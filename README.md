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

**Many seats in one booking.** Up to **50 seats** per reservation, so a family or a
club party is booked in one go. There is no limit on how many bookings one person
may make. A seat past the limit is refused rather than trimmed, so nobody is charged
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

**299 tests, no build tool, no network access** — just a JDK and the bundled driver.

They cover the venue and event data, the four pricing ends and the row curve, exact
seat selection through the real click path, the stadium map and its size limits, the
booking button appearing and counting seats, the full booking flow from seat to
receipt, receipt arithmetic, finding a booking, that every screen opens with no
sign-in, persistence, seat holds, three languages, the occupancy report, keyboard
seat selection, dark mode and WCAG contrast.

Ten of them exist because of defects found while building this:

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
- **Wrong input was a dead end.** Every form read the boxes, let the dialog close, and
  only then checked them — so a mistake closed the form, put a warning on top of it,
  and left the customer with an empty form and a warning they could not act on. Now a
  form stays open, marks each wrong field with what to type and a format that would be
  accepted, keeps everything already typed, and puts the cursor in the first field that
  needs fixing. Four separate forms had this fault and each is now checked.
- The details form reported every problem and then **cleared its messages** before
  showing itself again, so it came back with the same mistakes and no explanation of
  them — exactly the dead end above, one layer in. A test now fails if the clearing
  comes back.
- Its rows were stepped wrongly, so the heading was **drawn over the name box**. The
  form looked like it was missing its first field. A test lays the form out for real
  and fails if any two parts of it overlap.
- Details that were **present but unusable were treated as answered.** The check was
  "are the boxes non-empty", so a wrong email typed on the booking screen was waved
  through and the booking was then refused, with the customer holding a warning and no
  way to correct it. The form now opens for it, marks the wrong field immediately, and
  the two rules are checked against each other so they cannot drift apart.
- A saved selection's label over 120 characters was **silently cut** when it was saved.
  A customer could type a name, watch the save succeed, and find later that the start of
  it was gone. It is now refused with both numbers in the message — how long the label
  was, and how short it has to be.
- **Changing the language only changed some of the screen.** The header and the booking
  page followed the choice, and the schedule, the bookings list, the occupancy report and
  the saved seats carried on reading in the old language underneath a translated header.
  Every screen is now redrawn on a switch, and three widgets built once when the
  application opens — the search boxes, the status line and the seat map's tabs — are
  renamed by hand, since no screen rebuild reaches them.
- **The seat map's four ends stayed in English** whatever language was selected. "VIP
  Box" and "Main Stand" are facts about the place rather than wording the interface
  supplies, so nothing in the translation table held them. `VenueWords` now names them
  in all three languages, and the tabs are renamed on a switch rather than keeping the
  name they were built with.

## How to use it

1. The stadium screen opens on the upcoming schedule and the notices panel.
2. Tap an event to open its details page, or use **What's on** for the whole list with
   date, type and search filters.
3. On the details page, choose seats. The map shows the four ends as tabs; tap a seat
   to hold it, or use the arrow keys and Enter.
4. Once you have picked a seat, a **Confirm N seats** button appears at the top of
   the page with the total beside it. Before you pick anything it reads "Choose one
   or more seats, then confirm here" instead, so there is no dead button to press.
5. Press it. A dialog asks for your name, email and phone. If something is missing or
   malformed it stays open: each wrong field is marked with what to type and a format
   that would work, the cursor goes to the first one, and everything you already typed
   is kept. Correct them and press the button again.
6. **Pay** by cash at the venue or mobile money.
7. The booking is taken and the receipt is issued straight away — save it or print it.
8. **My bookings** lists every reservation with no sign-in; open one to see its
   receipt again, or to cancel it.

The **Back** button in the header is the only one: it returns to wherever you came
from, so an event opened from the full schedule goes back there rather than always
to the stadium page.

The other two places that ask for something typed — **Saved** for a selection's name
and **Submit a special request** — behave the same way as the details form. A name
over 120 characters or a request under 5 is marked under its own field, with the
accepted length stated on the form beforehand, and what you wrote stays in the box.

## Notes

**Input is checked before it is acted on, not after.** The rules live apart from the
screens so they can be tested without a window: `CustomerDetails` for the three contact
fields, `FormRules` for the label and request wording. `DetailsFormPanel` is the form
itself — built once, shown again for as long as the entry is wrong, and responsible for
marking fields and moving the cursor. `BookingService.validateCustomer` still refuses
bad details as a last line of defence, so a booking can never be taken with something
the form would have refused; a test asserts the two never disagree.

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
interface following the choice: every screen is redrawn, the seat map's four ends and
the event badges are renamed, and the search boxes, status line and map tabs — all
built once at startup — are renamed by hand. Every one of the 463 keys exists in all
three languages, and a test fails if one does not, so a screen cannot quietly show
English after the switch.

Where the wording lives:

- `Messages.java` holds every label. All three languages carry every key; the English
  fallback in `get()` is for safety at runtime, not a substitute for translating.
- `VenueWords.java` translates what comes from the venue data rather than the
  interface — the four ends, the kinds of event, and the shape of the ground.
- Numbers that describe a rule — the seat limit, the booking fee, prices — are passed
  into the wording rather than typed into it, so changing the constant changes every
  screen that mentions it.
- `Messages.count(key, n)` picks the singular or plural form, so a screen never reads
  "3 seat" or "1 seats".

**Theme.** Light and dark, switched from the header and remembered between runs. One
palette drives everything, and the tests measure real WCAG contrast in both themes
rather than eyeballing it.
