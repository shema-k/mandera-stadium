# How the tests work

Everything in `src/test/java/` checks that the program still behaves the way it
should. Run them with:

```bash
./run-tests.sh
```

It prints one line per check and a count at the end. All 299 must pass.

## The shape of a test file

Every test file has a `register()` method holding its checks. Each check says
what should be true, then checks it:

```java
static void register() {
    suite("Pricing and booking rules");            // a named group

    test("the booking fee is added once", () -> {  // one check
        BookingService service = new BookingService();
        service.selectEvent(StadiumData.getEvents().get(0));
        assertEquals(55000, service.getTotalCharge(pick(service, 2)),
                "two seats at 25,000 plus one 5,000 fee");
    });
}
```

- `suite(...)` names a group, purely so the output is readable.
- `test(name, ...)` runs one check. The `->` is Java's shorthand for "the code
  to run" — the same thing as writing a whole method out.
- `assertEquals`, `assertTrue`, `assertFalse`, `assertClose` and `assertThrows`
  are the five ways of saying "this did not match what I expected".

All of these live in `TestRunner.java`, which is a hand-written harness. Most
Java projects use JUnit, but that needs downloading and configuring, and this
project has no build tool — so the small part of it that we need is written out
by hand instead.

## Adding a test

1. Write the check inside an existing `register()` method, or create a new file
   with one.
2. **If you created a new file, add its `register()` call to `main()` in
   `TestRunner.java`.** Nothing else finds it, and nothing warns you that it
   never ran — it would simply never fail either.

## The asserts, and when to use each

| Assert | Use it for | Watch out for |
|---|---|---|
| `assertEquals` | Two values that should match | Compares `Integer` to `Integer`, not `1` to `1L` |
| `assertTrue` / `assertFalse` | A yes/no question | — |
| `assertClose` | Money and percentages | Give it a tolerance; division is never exact |
| `assertThrows` | Code that should say no | It only checks that *something* was thrown |

Write the message as if for a stranger. A failure prints it verbatim, and the
test name alone rarely says what went wrong:

```java
// poor: the reader cannot tell what was wrong
assertEquals(3, count, "wrong count");

// better: says what was expected and what the code actually did
assertEquals(3, count, "one line per seat, so 3 seats means 3 lines");
```

## What each file covers

| File | Checks |
|---|---|
| `TestRunner` | The harness. Not a test itself — it runs the rest |
| `StadiumDataTest` | The venue and event facts are right and in the future |
| `StadiumDetailsTest` | Price spans, vacancy, seats on sale |
| `BookingPricingTest` | Prices, the row curve, the fee, the seat cap |
| `OccupancyReportTest` | The occupancy figures |
| `BookingPersistenceTest` | Bookings survive closing the app |
| `SeatAllocationTest` | Exactly the seats chosen are the seats booked |
| `StadiumMapTest` | The seat map drawing and its size limits |
| `KeyboardSeatSelectionTest` | Choosing seats with the arrow keys |
| `BookingFlowTest` | Booking from seats to receipt |
| `DetailsFlowTest` | The whole booking screen journey |
| `ConfirmBookingTest` | The confirm button appears and counts correctly |
| `DetailsFormTest` | The form when input is wrong |
| `PrefilledDetailsTest` | Details already typed before the form opens |
| `OpenAccessTest` | Every screen opens with no sign-in |
| `SeatLedgerTest` | The booked-seats list |
| `SavedSelectionTest` | Saving seats for later, and their labels |
| `CustomerLookupTest` | Finding a booking by email or phone |
| `SearchAndFilterTest` | Searching and filtering the schedule |
| `BookingToolsTest` | Seat holds, the ticket, mobile money |
| `ReceiptTest` | The receipt adds up |
| `LocaleTest` | Numbers in the wording match the code |
| `TranslationCoverageTest` | Every word exists in all three languages |
| `VenueWordsTest` | Venue data translated, and the right words for it |
| `SeatMapRetranslationTest` | The seat map is renamed on a language switch |
| `CustomerDetailsTest` | What counts as a valid name, email and phone |
| `FormRulesTest` | The rules for other typed text |
| `ThemeTest` | Light and dark, and readable contrast |

### Two files that are not tests

- **`ScreenWording.java`** — reads the *source files* so the translation checks
  can see what is written in them. It looks for particular method names to
  confirm a language switch still redraws every screen, which cannot be
  observed from the running program.
- **`ScreenShots.java`** — saves the screens as PNG files. Run by hand when you
  have changed a layout. It is deliberately **not** in the suite, because it
  needs a display and would slow every run down.

## Tests that exist because of a bug

A few of these are not obvious from their names. Each one is here because the
thing it checks went wrong at some point, and the fix was not enough on its own:

- **`SeatAllocationTest`** — the confirmation step used to quietly reassign seats
  to spread a booking across the four ends. A customer could be quoted one price
  and given different seats.
- **`DetailsFlowTest`** — the confirm button was in the layout but off the bottom
  of the window, so it existed and could not be seen.
- **`PrefilledDetailsTest`** — details that were present but unusable counted as
  answered, so a wrong email was waved through and the booking then refused.
- **`SeatMapRetranslationTest`** — the seat map's tabs stayed in English whatever
  language was selected, and nothing caught it because the words live in the
  data rather than in the wording table.
- **`LocaleTest`** — the seat limit was typed into a sentence, so raising it left
  the screen claiming the old number.

When you fix a bug, add the test that would have caught it. A test that has
never failed is not testing anything.

## When a test fails

Read the message, not the stack trace. The message says what was expected and
what happened. Then:

1. Is the code wrong, or is the test wrong? Sometimes a change is intentional
   and the test needs updating — but say why in the commit.
2. If it is the code, fix the code, not the test. Changing a test to match broken
   behaviour is how a bug quietly becomes permanent.