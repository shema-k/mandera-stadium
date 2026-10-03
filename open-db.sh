#!/usr/bin/env bash
# Open the stadium booking database for viewing.
#
#   ./open-db.sh          -> start the H2 web console and open the browser ALREADY
#                            connected to this project's database (shows the tables)
#   ./open-db.sh ro       -> same, but read-only (nothing can be changed)
#   ./open-db.sh tables   -> print the table list + row counts in this terminal
#   ./open-db.sh shell    -> open the text H2 shell in this terminal
#   ./open-db.sh stop     -> stop a console started by this script
#   ./open-db.sh sql "..." -> run one SQL statement and print the result
#                            (a backup is taken automatically before any change)
#   ./open-db.sh check    -> crosscheck the data: counts, status/seat mismatches,
#                            orphan seat rows and seats taken per event
#   ./open-db.sh backup   -> write a consistent backup zip into ./backups
#
# The database is the single file stadium-bookings.mv.db in this folder. There
# is no password (user "sa", blank) and no server to install.
#
# The viewer connects in H2 "automatic mixed mode" (AUTO_SERVER=TRUE). That lets
# the browser console, the shell and the tables command share the one file
# instead of locking each other out. Note: the Swing app opens the file directly
# (embedded), so while a viewer session is connected the app cannot open the same
# file - close the browser tab / run ./open-db.sh stop before using the app.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

H2_JAR="$ROOT_DIR/lib/h2-2.2.224.jar"
DB_FILE="$ROOT_DIR/stadium-bookings"     # H2 appends .mv.db itself
BASE_URL="jdbc:h2:file:$DB_FILE"
SHARE_URL="$BASE_URL;AUTO_SERVER=TRUE"  # shared, multi-connection URL
PORT="${H2_PORT:-8082}"
MODE="${1:-web}"

[[ -f "$H2_JAR" ]]       || { echo "H2 driver not found at $H2_JAR" >&2; exit 1; }
[[ -f "$DB_FILE.mv.db" ]] || { echo "Database not found at $DB_FILE.mv.db" >&2; exit 1; }

# Encode the characters that matter when a JDBC URL is put in a query string.
urlenc() {
  local s="$1"
  s="${s//%/%25}"; s="${s//:/%3A}"; s="${s//\//%2F}"; s="${s//;/%3B}"
  s="${s//=/%3D}"; s="${s//(/%28}"; s="${s//)/%29}"; s="${s// /%20}"
  printf '%s' "$s"
}

# True when an H2 web console is already answering on the port.
console_up() { curl -s -o /dev/null "http://localhost:$PORT/login.jsp" 2>/dev/null; }

# Explain a locked-database failure instead of dumping a Java stack trace.
locked_hint() {
  local out="$1"
  if grep -q 'already in use\|is locked' <<<"$out"; then
    echo "  The database is open by another process right now:" >&2
    echo "    - the H2 web console tab is still connected, or" >&2
    echo "    - the Stadium app is mid-save." >&2
    echo "  Disconnect the console (close the tab, or ./open-db.sh stop) and try again." >&2
    exit 1
  fi
}

# Write a consistent backup zip next to the project. H2 does this online, so it
# works even while a console/shell is using the file.
make_backup() {
  local dir="$ROOT_DIR/backups" stamp out
  mkdir -p "$dir"
  stamp="$(date +%Y%m%d-%H%M%S)"
  out="$dir/stadium-bookings-$stamp.zip"
  java -cp "$H2_JAR" org.h2.tools.Shell -url "$SHARE_URL" -user sa -password '' \
    -sql "BACKUP TO '$out'" >/dev/null
  printf '%s' "$out"
}

# Run one SQL statement and print the rows it returns, with a friendly message if
# the file is busy instead of a Java stack trace.
run_sql() {
  local out
  out="$(java -cp "$H2_JAR" org.h2.tools.Shell -url "$SHARE_URL" -user sa -password '' \
           -list -sql "$1" 2>&1 || true)"
  locked_hint "$out"
  printf '%s\n' "$out"
}

case "$MODE" in
  tables)
    echo "Tables in $DB_FILE.mv.db:"
    OUT="$(printf 'SHOW TABLES;\n' | java -cp "$H2_JAR" org.h2.tools.Shell \
             -url "$SHARE_URL" -user sa -password '' 2>&1 || true)"
    locked_hint "$OUT"
    printf '%s\n' "$OUT" | sed -n '/sql> /,$p' | sed 's/^sql> //' \
      | grep -v -E '^(Aborted|Connection closed)$' || true
    ;;

  shell)
    echo "H2 text shell -> $SHARE_URL   (user sa, blank password). SQL ends with ';', quit to exit."
    exec java -cp "$H2_JAR" org.h2.tools.Shell -url "$SHARE_URL" -user sa -password ''
    ;;

  stop)
    if pkill -f "org.h2.tools.Server -web -webPort $PORT"; then
      echo "Stopped the H2 console on port $PORT."
    else
      echo "No H2 console is running on port $PORT."
    fi
    ;;

  ro|web)
    if [[ "$MODE" == "ro" ]]; then
      # Read-only cannot be combined with AUTO_SERVER, so it opens the file directly.
      URL="$BASE_URL;ACCESS_MODE_DATA=r"
    else
      URL="$SHARE_URL"
    fi

    STARTED=0
    if console_up; then
      echo "An H2 console is already running on port $PORT - reusing it."
    else
      echo "Starting the H2 web console on port $PORT ..."
      nohup java -cp "$H2_JAR" org.h2.tools.Server -web -webPort "$PORT" \
        >/tmp/stadium-h2-console.log 2>&1 &
      disown 2>/dev/null || true
      STARTED=1
      for _ in $(seq 1 40); do
        if console_up; then break; fi
        sleep 0.25
      done
      if ! console_up; then
        echo "Console failed to start. Log: /tmp/stadium-h2-console.log" >&2
        tail -n 20 /tmp/stadium-h2-console.log >&2 || true
        exit 1
      fi
    fi

    # Build a link that connects immediately - H2's login.do accepts the
    # connection details - so the browser lands on the table list, not a form.
    SID="$(curl -s "http://localhost:$PORT/login.jsp" \
            | grep -m1 -o 'jsessionid=[a-f0-9]*' | cut -d= -f2 || true)"
    LINK="http://localhost:$PORT/login.do?jsessionid=$SID&language=en"
    LINK="$LINK&setting=$(urlenc 'Generic H2 (Embedded)')&driver=org.h2.Driver"
    LINK="$LINK&url=$(urlenc "$URL")&user=sa&password="

    echo
    echo "Opening your browser already connected - the tables show on the left:"
    echo "  BOOKINGS, BOOKING_SEATS, SAVED_SELECTIONS"
    echo
    echo "If the browser did not open, paste this link:"
    echo "  $LINK"
    echo
    echo "Manual login page (http://localhost:$PORT/) uses:"
    echo "  JDBC URL : $URL"
    echo "  User     : sa     Password : (leave blank)"
    echo
    echo "While this is connected the database file is locked; stop it with"
    echo "  ./open-db.sh stop   (or close the tab) before you save from the app."
    ;;

  backup)
    echo "Backup written: $(make_backup)"
    ;;

  sql)
    STMT="${2:-}"
    if [[ -z "$STMT" ]]; then
      echo 'usage: ./open-db.sh sql "SELECT * FROM BOOKINGS;"' >&2
      exit 2
    fi
    FIRST="$(printf '%s' "$STMT" | sed -E 's/^[[:space:]]+//' | cut -d' ' -f1 \
              | tr '[:lower:]' '[:upper:]')"
    case "$FIRST" in
      SELECT|SHOW|EXPLAIN) ;;                     # read-only, no backup needed
      *) echo "This changes data. Backup: $(make_backup)" ;;
    esac
    run_sql "$STMT"
    ;;

  check)
    echo "===== counts (bookings / seat rows / saved selections) ====="
    run_sql "SELECT (SELECT COUNT(*) FROM BOOKINGS) AS BOOKINGS, (SELECT COUNT(*) FROM BOOKING_SEATS) AS SEAT_ROWS, (SELECT COUNT(*) FROM SAVED_SELECTIONS) AS SELECTIONS"
    echo
    echo "===== bookings whose seat rows disagree with their status (should be empty) ====="
    run_sql "SELECT b.REFERENCE, b.STATUS, (LENGTH(b.SEATS)-LENGTH(REPLACE(b.SEATS,';',''))+1) AS SEATS_LISTED, (SELECT COUNT(*) FROM BOOKING_SEATS s WHERE s.REFERENCE=b.REFERENCE) AS SEAT_ROWS FROM BOOKINGS b WHERE (b.STATUS='CONFIRMED' AND (SELECT COUNT(*) FROM BOOKING_SEATS s WHERE s.REFERENCE=b.REFERENCE) <> (LENGTH(b.SEATS)-LENGTH(REPLACE(b.SEATS,';',''))+1)) OR (b.STATUS='CANCELLED' AND EXISTS (SELECT 1 FROM BOOKING_SEATS s WHERE s.REFERENCE=b.REFERENCE))"
    echo
    echo "===== orphan seat rows, i.e. booked seats with no parent booking (should be empty) ====="
    run_sql "SELECT s.EVENT_ID, s.SECTION, s.SEAT_ROW, s.SEAT_NUMBER, s.REFERENCE FROM BOOKING_SEATS s LEFT JOIN BOOKINGS b ON b.REFERENCE=s.REFERENCE WHERE b.REFERENCE IS NULL"
    echo
    echo "===== seats taken per event ====="
    run_sql "SELECT EVENT_ID, COUNT(*) AS SEATS_TAKEN FROM BOOKING_SEATS GROUP BY EVENT_ID ORDER BY EVENT_ID"
    echo
    echo "===== bookings, newest first ====="
    run_sql "SELECT REFERENCE, EVENT_NAME, CUSTOMER_NAME, TOTAL, STATUS FROM BOOKINGS ORDER BY CREATED_AT DESC"
    ;;

  *)
    echo "Unknown mode '$MODE'. Use one of: (none) | ro | tables | shell | sql | check | backup | stop" >&2
    exit 2
    ;;
esac

