# Where things are

Every file in the project, and what it is for. Written to be read top to bottom
the first time, and dipped into afterwards.

## Start here

```bash
./run.sh          # start the application
./run-tests.sh    # check nothing is broken
```

Both compile everything first, so a mistake appears as an error message rather
than a strange screen. Nothing else is needed — no build tool, no installer.

## The two folders that matter

```
src/main/java/com/stadium/booking/    the application
src/test/java/com/stadium/booking/    the checks
```

Inside the application, files are grouped into five folders. **The folder a file
is in tells you what kind of thing it is**, and each folder explains itself in a
`package-info.java` you can open.

| Folder | Holds | Think of it as |
|---|---|---|
| `data/` | `Stadium`, `StadiumData`, `StadiumEvent`, `Seat`, `SeatKey`, and the small enums | The facts |
| `booking/` | `BookingService`, `CustomerDetails`, `Receipt`, `TicketBuilder`, `PaymentRecord` | The rules |
| `storage/` | `Database`, `BookingStore` | Saving to disk |
| `text/` | `Messages`, `VenueWords`, `Theme`, `ThemePreference` | Words and colours |
| `ui/` | `StadiumBookingApp`, `SeatMapPanel`, `DetailsFormPanel`, `StadiumPhotoPanel` | The screens |

Read them in that order and it makes sense: first what is true, then the rules
about it, then how it is kept, then how it looks and is worded.

---

## data/ — the facts

No decisions here. Change a value and every screen that uses it changes too.

| File | What it is |
|---|---|
| `Stadium.java` | One venue: name, city, capacity, address, shape. |
| `StadiumData.java` | **Edit this to add or change an event.** All eight Namboole events, the four ends with their prices, and the notices — written as plain readable lines. |
| `StadiumEvent.java` | One event: teams, date, kickoff, doors, and a factor that scales the prices. |
| `StadiumAnnouncement.java` | A notice on an event. Can stop bookings. |
| `StadiumDetails.java` | Answers "what is the price span?", "how full is it?" when a screen asks. |
| `StadiumShape.java` | Oval bowl or four straight stands. Two values. |
| `Seat.java` | One seat: end, row, number, price. |
| `SeatSection.java` | One end: rows, seats per row, front-row price. |
| `SeatKey.java` | A seat's address as one short value, so it can go in a set. |
| `SeatStatus.java` | Vacant, selected, held, booked, closed. |
| `EventType.java` | Game or concert. |
| `AnnouncementType.java` | Notice, cancellation, emergency, special request, schedule change. |

## booking/ — the rules

| File | What it is |
|---|---|
| `BookingService.java` | **The rulebook.** Prices by row, the booking fee, the seat cap, which seats are free, making a booking, finding bookings, saved selections. If you want to know what the app will allow, read this. |
| `CustomerDetails.java` | Checks a name, email and phone. Reports every bad field at once with an example of a format that works. |
| `FormRules.java` | Short rules for other typed text: label length, request length. |
| `Booking.java` | One reservation: reference, who, which seats, total, status. |
| `BookingStatus.java` | Confirmed or cancelled. |
| `SeatHoldService.java` | Seats picked but not paid for, with a timer. Stops two people buying the same seat. |
| `BookingCountdown.java` | Time left before bookings close. |
| `PaymentRecord.java` | One payment attempt. **Mobile money is simulated** — recorded locally and labelled as such. |
| `Receipt.java` | The itemised bill as text. Same text on screen, in the file and in print. |
| `TicketBuilder.java` | The e-ticket text and the CSV export. |
| `OccupancyReport.java` | How full the stadium is, overall and per end, per event. |

## storage/ — saving

| File | What it is |
|---|---|
| `Database.java` | Opens the database file. No password, because the system is open to anyone who books. |
| `BookingStore.java` | The three tables and all the SQL. `booking_seats` has a primary key on the seat, so the database itself refuses to sell it twice. |

## text/ — words and colours

| File | What it is |
|---|---|
| `Messages.java` | **Every label, in English, Luganda and Kiswahili.** The one file to change any wording. |
| `VenueWords.java` | Translates what comes from the venue data — the four ends, event kinds, the shape. |
| `Theme.java` | Light and dark palettes, and the contrast rules that keep text readable. |
| `ThemePreference.java` | Remembers light or dark between runs. |

## ui/ — the screens

| File | What it is |
|---|---|
| `StadiumBookingApp.java` | **The window and every screen in it.** Divided into marked sections — stadium, stadium details, event details, saved seats, schedule, live schedules, booking, bookings history, occupancy, booked seats, then shared helpers. Search for a section marker to jump to one. |
| `SeatMapPanel.java` | Draws the seat map: four end tabs, seats, legend, price guide. Handles clicks and arrow keys. |
| `DetailsFormPanel.java` | The name/email/phone form. Stays open when something is wrong, marks each bad field, keeps what you typed. |
| `StadiumPhotoPanel.java` | The photograph or seating-plan area. |

---

## src/test/java/ — the checks

`./run-tests.sh` runs all of these. Each is named after what it checks, so a
failure tells you what broke. `TestRunner.java` is the list; a new test only runs
if it is registered there.

| File | Checks |
|---|---|
| `TestRunner.java` | Starts everything. Edit this when adding a test. |
| `StadiumDataTest` | The venue and event facts are right and in the future. |
| `StadiumDetailsTest` | Price spans, vacancy, seats on sale. |
| `BookingPricingTest` | Prices, the row curve, the fee, the seat cap. |
| `BookingFlowTest` | Booking from seats to receipt. |
| `ConfirmBookingTest` | The confirm button appears and counts correctly. |
| `DetailsFlowTest` | The whole booking screen journey. |
| `SeatAllocationTest` | Exactly the seats chosen are the seats booked. |
| `BookingPersistenceTest` | Bookings survive closing the app. |
| `SeatLedgerTest` | The booked-seats list. |
| `SavedSelectionTest` | Saving seats for later, and their labels. |
| `OccupancyReportTest` | The occupancy numbers. |
| `ReceiptTest` | The receipt adds up. |
| `BookingToolsTest` | The ticket text and the CSV export. |
| `CustomerLookupTest` | Finding a booking by email or phone. |
| `StadiumMapTest` | The seat map drawing and its limits. |
| `KeyboardSeatSelectionTest` | Choosing seats with the arrow keys. |
| `SearchAndFilterTest` | Searching and filtering the schedule. |
| `OpenAccessTest` | Every screen opens with no sign-in. |
| `ThemeTest` | Light and dark, and readable contrast. |
| `LocaleTest` | Numbers in the wording match the code. |
| `TranslationCoverageTest` | Every word exists in all three languages. |
| `CustomerDetailsTest`, `FormRulesTest` | The input rules. |
| `DetailsFormTest`, `PrefilledDetailsTest` | The form when input is wrong. |
| `VenueWordsTest`, `SeatMapRetranslationTest` | Venue data translated, map renamed on a switch. |
| `ScreenWording.java` | Reads the source files, so the translation checks can see what is written there. |
| `ScreenShots.java` | **Not part of the suite.** Run by hand to save the screens as PNGs when checking layout. |

## Files that are not your code

| File | What it is |
|---|---|
| `lib/h2-2.2.224.jar` | The database driver. Written by someone else. |
| `build/` | Compiled output, made by the scripts. Safe to delete. |
| `stadium-bookings.mv.db` | Your bookings. Made on first run. **Deleting it erases every booking.** |
| `photos/` | Photographs you supply. None are included. |

---

## Where to make a change

| You want to… | Open |
|---|---|
| Add or change an event | `data/StadiumData.java` |
| Change a seat price | `data/StadiumData.java`, in the `sections(...)` call |
| Change the booking fee or seat cap | `booking/BookingService.java` |
| Change what counts as a valid email | `booking/CustomerDetails.java` |
| Change any wording or translation | `text/Messages.java` |
| Change a colour | `text/Theme.java` |
| Change what a screen shows, or where | `ui/StadiumBookingApp.java` |
| Change how the seat map looks | `ui/SeatMapPanel.java` |
| Change what is saved | `storage/BookingStore.java` |

**The rule worth keeping:** prices, limits and other rules belong in
`booking/BookingService.java`, not in a screen. A number edited in
`ui/StadiumBookingApp.java` may be used by one screen and nowhere else, so the
next screen needing it will have a different value.
