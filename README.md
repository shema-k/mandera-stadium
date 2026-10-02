# Namboole Seat Booking — Java Swing

A professional desktop seat-booking application for one stadium: **Mandela National
Stadium (Namboole)**, Kampala, Uganda. Built with Java Swing, no frameworks and no
build tool required.

**Open to everyone who books.** There are no staff accounts, no PINs, no roles and
no sign-in. Every screen — the schedule, the seat map, your bookings, the occupancy
report, the booked-seats list — opens directly. There is nothing to log into and
nothing to unlock.

The flow a customer actually walks through:

**Pick an event → open its details → choose exact seats → confirm → pay → keep the receipt**

## What it does

**One stadium, opened straight away.** The application starts on Namboole itself with
its schedule and notices, rather than asking you to choose a venue from a list of one.
45,202 seats, the real published capacity, across four independent ends.

**Four ends, each named and separately priced.**

| End | Name | Rows × seats | Price from |
|---|---|---|---|
| A | VIP Box | 114 × 99 | UGX 250,000 |
| B | Main Stand | 127 × 89 | UGX 190,000 |
| C | Terrace | 127 × 89 | UGX 140,000 |
| D | Kampala End | 130 × 87 | UGX 95,000 |

Within every end the front rows cost most and the back rows least, on a four-tier
curve. An event's own price factor scales the whole grid, so a concert is priced
differently from a league game.

**Exact seats.** Tapping a seat holds that precise seat and nothing else. The
confirmation dialog repeats the seats you chose, and the booking contains those same
seats — there is no rule quietly moving you to a "tidier" spread of seats.

**Many seats in one booking.** Up to **20 seats** per reservation, so a family or a
club party is booked in one go. There is no limit on how many bookings one person
may make. A twenty-first seat is refused rather than trimmed, so nobody is charged
for a seat they did not get.

**Events with full detail.** Eight upcoming fixtures — Uganda Cranes, Vipers SC vs SC
Villa, KCCA vs Express, and concerts from Eddy Kenzo, Bobi Wine and Jose Chameleone.
Each event's details page gives the date, start and doors times, the booking deadline
with a live countdown, the sport or artist, both teams, the description, price from,
best available, current vacancy, and every notice affecting it. Tapping an event
anywhere opens that page, with the booking option on it.

**Receipts.** Every booking produces an itemised receipt: each seat on its own line
with its section, price tier and amount; a per-end subtotal showing the unit price it
was worked out at; the ticketing fee; and the total paid. It can be saved to a file or
printed, and reopened later from the booking history.

**Your bookings.** **My bookings** opens directly and lists every reservation, with
the reference, event, date, seats, total and status. Open any row for the full
details, its charges and its receipt, or to cancel it. Search by reference, event or
seat to find yours.

**Your seats are held while you decide.** Five-minute holds with a visible countdown,
so two people cannot pick the same seat and only find out at the payment step. A held
seat shows its own colour on the map and is turned away immediately for anyone else.

## Requirements

- JDK 17 or newer (developed and verified on OpenJDK 25)
- A desktop environment with Swing
- The bundled H2 driver in `lib/` — no database server to install

## Run it

```bash
cd /home/shema/Desktop/PROJECTS/stadium-seat-booking
./run.sh
```

## Build and test

```bash
./run-tests.sh
```

**224 tests, no build tool, no network access** — just a JDK and the bundled driver.

They cover the venue and event data, the four pricing ends and the row curve, exact
seat selection through the real click path, the stadium map and its size limits, the
booking button appearing and counting seats, the full booking flow from seat to
receipt, receipt arithmetic, finding a booking, that every screen opens with no
sign-in, persistence, seat holds, three languages, the occupancy report, keyboard
seat selection, dark mode and WCAG contrast.

Five of them exist because of defects found while building this:

- The confirmation step used to **reassign seats** to spread a booking across the four
  ends, so a customer could be quoted one price and given different seats. Exact seats
  are now authoritative, and the tests fail if anything moves them again.
- A seat that could not be booked said only "is not selectable", which told a customer
  nothing about whether to pick again or wait. It now names the reason.
- The seat limit shown on screen said six while the code enforced six by coincidence
  of an edit; the wording is now checked against the constant, in all three languages.
- The seat map sat in a `BorderLayout.CENTER` slot, which always stretches to fill and
  ignores a maximum size, so it grew down the whole screen and pushed the price
  outline off the bottom. It is now capped, shows about 30 rows at a time, and a test
  fails if the cap is removed.
- The confirm button was the event page's *south* component, so the seat map and the
  notices panel pushed it off the bottom of the window. It was in the layout but not on
  screen — the booking control existed and a customer could not see it. It now sits
  along the top of the page, and a test fails if it moves back down.

## How to use it

1. The stadium screen opens on the upcoming schedule and the notices panel.
2. Tap an event to open its details page, or use **What's on** for the whole list with
   date, type and search filters.
3. On the details page, choose seats. The map shows the four ends as tabs; tap a seat
   to hold it, or use the arrow keys and Enter.
4. Once you have picked a seat, a **Confirm N seats** button appears at the top of
   the page with the total beside it. Before you pick anything it reads "Choose one
   or more seats, then confirm here" instead, so there is no dead button to press.
5. Press it. A short dialog asks for your name, email and phone the first time and
   reuses them after that.
6. **Pay** by cash at the venue or mobile money.
7. The booking is taken and the receipt is issued straight away — save it or print it.
8. **My bookings** lists every reservation with no sign-in; open one to see its
   receipt again, or to cancel it.

The **Back** button in the header is the only one: it returns to wherever you came
from, so an event opened from the full schedule goes back there rather than always
to the stadium page.

## Notes

**Mobile money is simulated.** The payment step records an authorisation locally and
labels it as such. No money moves and no provider credentials ship with this build. A
real transfer needs a merchant account with a payment provider, wired in at
`PaymentRecord`.

**Customer details are stored in plain text, and anyone running the application can
read them.** Opening the system to bookers means there is no account boundary and no
PIN, so the booking file `stadium-bookings.dat` holds names, email addresses and
phone numbers that this build does not encrypt — H2 2.2.224 does not encrypt page
contents on write, so they are readable with a hex editor. The in-app screens show
these details openly by design, because they are the customer's own bookings. Two
consequences worth naming plainly:

- Treat access to the machine as access to every customer's contact details. Put the
  application somewhere only the box office can reach.
- If it is ever exposed beyond that, real protection needs either a database that
  encrypts its pages or the customer fields encrypted in the application itself.

The database password feature that used to guard this file has been removed along with
the staff accounts, so nothing prompts for a password at launch any more.

**Receipts are recomputed from current pricing.** Each seat's price on a receipt is
looked up from the same pricing curve the seat map charged, and the total is the
booking's own recorded total. That is exact here because prices are fixed per event. A
venue that repriced between sales would need the price stored on the booking line.

**Language.** English, Luganda and Swahili, switchable from the header, with the whole
interface — seat map, tables, dialogs, receipts — following the choice.

**Theme.** Light and dark, switched from the header and remembered between runs. One
palette drives everything, and the tests measure real WCAG contrast in both themes
rather than eyeballing it.
