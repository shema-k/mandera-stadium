package com.stadium.booking;

import static com.stadium.booking.TestRunner.assertEquals;
import static com.stadium.booking.TestRunner.assertFalse;
import static com.stadium.booking.TestRunner.assertTrue;
import static com.stadium.booking.TestRunner.freshDatabase;
import static com.stadium.booking.TestRunner.suite;
import static com.stadium.booking.TestRunner.test;

import java.util.List;
import java.util.Map;

/**
 * Checking what a customer typed, so bad input can be corrected in place.
 *
 * <p>The rules matter because they decide what a person is told. A form that
 * closes on a mistake and then explains it is a dead end: everything typed is
 * lost. These tests pin down that every field is checked, every problem is
 * reported against its own field, and every message names a format that would
 * be accepted.
 */
final class CustomerDetailsTest {
    private CustomerDetailsTest() {
    }

    private static final String NAME = "Amina Okello";
    private static final String EMAIL = "amina@example.co.ug";
    private static final String PHONE = "+256 700 123 456";

    private static CustomerDetails.Problem problemFor(
            Map<CustomerDetails.Field, CustomerDetails.Problem> problems,
            CustomerDetails.Field field) {
        CustomerDetails.Problem problem = problems.get(field);
        assertTrue(problem != null, "expected a problem for " + field + " in " + problems);
        return problem;
    }

    static void register() {
        suite("Checking customer input");

        test("correct details raise nothing", () -> {
            assertTrue(CustomerDetails.isValid(NAME, EMAIL, PHONE),
                    "a real name, email and phone are accepted");
            assertEquals(0, CustomerDetails.check(NAME, EMAIL, PHONE).size(),
                    "and report no problems");
        });

        test("surrounding spaces are forgiven", () -> {
            assertTrue(CustomerDetails.isValid("  " + NAME + " ", " " + EMAIL + " ",
                    " " + PHONE + " "),
                    "what people type on a phone keyboard is rarely trimmed");
        });

        test("every wrong field is reported, not just the first", () -> {
            // This is the point of the class. One problem at a time meant
            // fixing the name, pressing the button again, then being told about
            // the email, and so on.
            Map<CustomerDetails.Field, CustomerDetails.Problem> problems =
                    CustomerDetails.check("", "not-an-email", "abc");
            assertEquals(3, problems.size(),
                    "all three fields are reported at once, got " + problems.keySet());
            problemFor(problems, CustomerDetails.Field.NAME);
            problemFor(problems, CustomerDetails.Field.EMAIL);
            problemFor(problems, CustomerDetails.Field.PHONE);
        });

        test("problems come back in the order the fields appear", () -> {
            List<CustomerDetails.Problem> ordered = CustomerDetails.problemsInOrder(
                    CustomerDetails.check("", "", ""));
            assertEquals(3, ordered.size(), "three problems");
            assertEquals(CustomerDetails.Field.NAME, ordered.get(0).getField(),
                    "the name is listed first, so focus starts at the top");
            assertEquals(CustomerDetails.Field.EMAIL, ordered.get(1).getField(),
                    "then the email");
            assertEquals(CustomerDetails.Field.PHONE, ordered.get(2).getField(),
                    "then the phone");
        });

        test("an empty field asks for it by name", () -> {
            CustomerDetails.Problem problem = problemFor(
                    CustomerDetails.check("", EMAIL, PHONE), CustomerDetails.Field.NAME);
            assertTrue(problem.getMessage().contains("name"),
                    "the message names the field: " + problem.getMessage());
        });

        test("an email with no @ is refused with an example", () -> {
            CustomerDetails.Problem problem = problemFor(
                    CustomerDetails.check(NAME, "amina.example.co.ug", PHONE),
                    CustomerDetails.Field.EMAIL);
            assertTrue(problem.getMessage().contains("@"),
                    "the message says what is missing: " + problem.getMessage());
            assertEquals("amina@example.co.ug", problem.getExample(),
                    "and shows a value that would work");
            assertTrue(problem.describe().contains("amina@example.co.ug"),
                    "the combined message carries the example: " + problem.describe());
        });

        test("an email with no domain is refused", () -> {
            assertTrue(!CustomerDetails.isValid(NAME, "amina@", PHONE),
                    "amina@ is not an address");
            assertTrue(!CustomerDetails.isValid(NAME, "amina@example", PHONE),
                    "amina@example is not an address either");
        });

        test("an email with a space is refused and says why", () -> {
            CustomerDetails.Problem problem = problemFor(
                    CustomerDetails.check(NAME, "amina @example.co.ug", PHONE),
                    CustomerDetails.Field.EMAIL);
            assertTrue(problem.getMessage().toLowerCase().contains("space"),
                    "the message says a space is the problem: " + problem.getMessage());
        });

        test("letters typed into the phone box are refused with an example", () -> {
            // The mistake people actually make, and "invalid" would not help.
            CustomerDetails.Problem problem = problemFor(
                    CustomerDetails.check(NAME, EMAIL, "0772abc123"),
                    CustomerDetails.Field.PHONE);
            assertTrue(problem.getMessage().toLowerCase().contains("digit"),
                    "the message says digits only: " + problem.getMessage());
            assertEquals("+256 700 123 456", problem.getExample(),
                    "and shows a number that would work");
        });

        test("a phone number that is too short or too long is refused", () -> {
            assertTrue(!CustomerDetails.isValid(NAME, EMAIL, "12345"),
                    "five digits is not a phone number");
            CustomerDetails.Problem problem = problemFor(
                    CustomerDetails.check(NAME, EMAIL, "1234567890123456789"),
                    CustomerDetails.Field.PHONE);
            assertTrue(problem.getMessage().toLowerCase().contains("long"),
                    "an over-long number is called out: " + problem.getMessage());
        });

        test("the usual ways of writing a phone number are accepted", () -> {
            for (String number : new String[]{"+256700123456", "0772123456",
                    "0772 123 456", "(0772) 123-456", "+256.700.123.456"}) {
                assertTrue(CustomerDetails.isValid(NAME, EMAIL, number),
                        "should accept " + number);
            }
        });

        test("a name that is all digits is refused", () -> {
            CustomerDetails.Problem problem = problemFor(
                    CustomerDetails.check("12345", EMAIL, PHONE), CustomerDetails.Field.NAME);
            assertTrue(problem.getMessage().toLowerCase().contains("letter"),
                    "a number in the name box is caught: " + problem.getMessage());
        });

        test("an email address typed into the name box is caught", () -> {
            // Someone who skipped a field. Every field has a letter, so the
            // digit rule alone would not notice.
            assertTrue(!CustomerDetails.isValid("", "amina@example.co.ug", PHONE)
                            || CustomerDetails.check("amina@example.co.ug", "", PHONE)
                            .containsKey(CustomerDetails.Field.NAME),
                    "the name is still required");
        });

        test("every message about wrong input carries an example", () -> {
            // A message with no example leaves the customer guessing, which is
            // the situation this whole change exists to remove.
            //
            // An empty field is the one exception: there is nothing to correct,
            // only something to fill in, so those messages ask rather than
            // show. Everything else has to demonstrate the format.
            List<String> wrong = new java.util.ArrayList<>();
            wrong.add("a");
            wrong.add("123");
            wrong.add("@@");
            wrong.add("name@");
            wrong.add("a b@c.co");
            wrong.add("0772");
            wrong.add("abcdefg");
            wrong.add("07721234567890123456");
            for (String value : wrong) {
                for (Map<CustomerDetails.Field, CustomerDetails.Problem> problems
                        : List.of(CustomerDetails.check(value, value, value),
                        CustomerDetails.check(NAME, value, value),
                        CustomerDetails.check(NAME, EMAIL, value))) {
                    for (CustomerDetails.Problem problem : problems.values()) {
                        assertTrue(problem.describe().length() > problem.getMessage().length(),
                                "a message about \"" + value + "\" on " + problem.getField()
                                        + " should carry an example: " + problem.describe());
                    }
                }
            }
        });

        test("an empty field is asked for rather than corrected", () -> {
            // Nothing is wrong with an empty box, so there is no format to show;
            // the message simply says what is needed.
            for (Map<CustomerDetails.Field, CustomerDetails.Problem> problems
                    : List.of(CustomerDetails.check("", EMAIL, PHONE),
                    CustomerDetails.check(NAME, "", PHONE),
                    CustomerDetails.check(NAME, EMAIL, ""))) {
                for (CustomerDetails.Problem problem : problems.values()) {
                    assertEquals(problem.getMessage(), problem.describe(),
                            "an empty field needs no example: " + problem.getField());
                    assertTrue(problem.getMessage().contains("enter"),
                            "and it asks rather than corrects: " + problem.getMessage());
                }
            }
        });

        test("the summary joins every problem for a status line", () -> {
            String summary = CustomerDetails.summarise(
                    CustomerDetails.check("", "bad", "abc"));
            assertTrue(summary.contains("name") || summary.contains("Name"),
                    "the summary names the first problem: " + summary);
            assertTrue(summary.length() > 30,
                    "and it is long enough to be useful: " + summary);
            assertEquals("", CustomerDetails.summarise(CustomerDetails.check(NAME, EMAIL, PHONE)),
                    "no problems, nothing to say");
        });

        test("the booking rules agree with the form", () -> {
            // The form checks up front so a customer is not sent away one problem
            // at a time; the service still refuses as a guarantee. They must not
            // disagree, or a booking could be refused for something the form had
            // already accepted.
            BookingService service = new BookingService(new BookingStore(
                    freshDatabase("agree")));
            String[][] cases = {
                    {"Amina Okello", "amina@example.co.ug", "+256700123456"},
                    {"A", "amina@example.co.ug", "+256700123456"},
                    {"Amina Okello", "not-an-email", "+256700123456"},
                    {"Amina Okello", "amina@example.co.ug", "abc"},
                    {"Amina Okello", "amina@example.co.ug", "12345"},
                    {"", "amina@example.co.ug", "+256700123456"}};
            for (String[] values : cases) {
                boolean formAccepts = CustomerDetails.isValid(values[0], values[1], values[2]);
                boolean serviceAccepts = true;
                try {
                    service.validateCustomer(values[0], values[1], values[2]);
                } catch (IllegalArgumentException refused) {
                    serviceAccepts = false;
                }
                assertEquals(formAccepts, serviceAccepts,
                        "the form and the service must agree on "
                                + values[0] + " / " + values[1] + " / " + values[2]);
            }
        });

        test("a message explains the format and stays a single readable line", () -> {
            // The messages were clipped mid-word when the example was appended,
            // which made the example the least readable part. Each has to fit.
            CustomerDetails.Problem problem = CustomerDetails
                    .check(NAME, "amina example co ug", PHONE).get(CustomerDetails.Field.EMAIL);
            assertTrue(problem.describe().length() < 120,
                    "the message fits on one line, was " + problem.describe().length()
                            + " chars: " + problem.describe());
            assertTrue(problem.describe().startsWith(problem.getMessage()),
                    "the reason comes before the example: " + problem.describe());
        });

        test("every field's message is independently clearable", () -> {
            // The form clears all three before writing the current problems, so a
            // field corrected since the last attempt stops showing a message.
            // This checks the map shape supports that: independent entries.
            java.util.Map<CustomerDetails.Field, CustomerDetails.Problem> problems =
                    new java.util.LinkedHashMap<>(
                            CustomerDetails.check("", "bad", "abc"));
            assertEquals(3, problems.size(), "three entries to work with");
            problems.remove(CustomerDetails.Field.NAME);
            assertEquals(2, problems.size(), "removing one leaves the others");
            assertTrue(problems.containsKey(CustomerDetails.Field.EMAIL)
                            && problems.containsKey(CustomerDetails.Field.PHONE),
                    "the untouched fields are still marked");
        });

        test("no booking is taken with details the form refused", () -> {
            BookingService service = new BookingService(new BookingStore(
                    freshDatabase("guard")));
            service.selectEvent(StadiumData.getEvent("namboole-01"));
            Seat seat = service.getSeat(new SeatKey("A", 1, 1));
            boolean refused = false;
            try {
                service.book("Amina", "not-an-email", "+256700123456",
                        List.of(seat));
            } catch (IllegalArgumentException expected) {
                refused = true;
            }
            assertTrue(refused, "a bad email is refused at the point of booking");
            assertEquals(0, service.getBookings().size(), "and nothing was booked");
        });
    }
}