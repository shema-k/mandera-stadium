package com.stadium.booking;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * All user-facing wording in one place, so the interface can run in English,
 * Luganda or Swahili.
 *
 * <p>An English fallback is always used for any key a translation has not
 * covered yet, so a missing translation can never leave a blank label.
 */
public final class Messages {
    /** The languages the interface offers. */
    public enum Language {
        ENGLISH("en", "English"),
        LUGANDA("lg", " Luganda"),
        SWAHILI("sw", "Kiswahili");

        private final String code;
        private final String label;

        Language(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static Language current = Language.ENGLISH;

    private Messages() {
    }

    public static void setLanguage(Language language) {
        current = language == null ? Language.ENGLISH : language;
    }

    public static Language getLanguage() {
        return current;
    }

    private static final Map<Language, Map<String, String>> TEXT = new LinkedHashMap<>();

    private static void put(Language language, String key, String value) {
        TEXT.computeIfAbsent(language, ignored -> new LinkedHashMap<>()).put(key, value);
    }

    static {
        // English is the reference wording.
        put(Language.ENGLISH, "app.title", "Namboole Seat Booking");
        put(Language.ENGLISH, "app.tagline", "NAMBOOLE SEAT BOOKING");
        put(Language.ENGLISH, "app.languageHint", "Change the interface language");
        put(Language.ENGLISH, "app.languageName", "Language");
        put(Language.ENGLISH, "app.languageDescription",
                "Choose English, Luganda or Kiswahili. The whole screen changes at once.");
        put(Language.ENGLISH, "nav.back", "← Back");
        put(Language.ENGLISH, "nav.venues", "Stadium");
        put(Language.ENGLISH, "nav.bookings", "My bookings");
        put(Language.ENGLISH, "nav.bookedSeats", "Booked seats");
        put(Language.ENGLISH, "nav.liveSchedules", "WHAT'S ON");
        put(Language.ENGLISH, "directory.title", "Mandela National Stadium");
        put(Language.ENGLISH, "directory.subtitle",
                "Choose an event, pick your seats, and pay at the venue.");
        put(Language.ENGLISH, "directory.hero.eyebrow", "MANDELA NATIONAL STADIUM");
        put(Language.ENGLISH, "directory.hero.title", "Book your seat at Namboole");
        put(Language.ENGLISH, "directory.hero.subtitle",
                "Games, concerts and national team fixtures, with the exact seat you choose.");
        put(Language.ENGLISH, "directory.findVenue", "FIND A VENUE");
        put(Language.ENGLISH, "directory.search.hint", "Search stadium, city, team or artist");
        put(Language.ENGLISH, "directory.venuesAvailable", "{0} venues available");
        put(Language.ENGLISH, "directory.venuesMatching", "{0} venues matching your search");
        put(Language.ENGLISH, "directory.open", "Open stadium");
        put(Language.ENGLISH, "directory.viewDetails", "View details");
        put(Language.ENGLISH, "directory.book", "Book seats");
        put(Language.ENGLISH, "directory.bookAt", "See the schedule and book seats");
        put(Language.ENGLISH, "directory.allEvents", "All upcoming events");
        put(Language.ENGLISH, "stadium.upcoming", "Upcoming schedule");
        put(Language.ENGLISH, "stadium.search.hint", "Search teams, artists, sport or date");
        put(Language.ENGLISH, "stadium.subtitle",
                "Choose a date, then select the game or concert you want to attend.");
        put(Language.ENGLISH, "stadium.notices", "Notices & requests");
        put(Language.ENGLISH, "stadium.specialRequest", "Submit a special request");
        put(Language.ENGLISH, "booking.title", "Book your seats");
        put(Language.ENGLISH, "booking.yourDetails", "Your details");
        put(Language.ENGLISH, "booking.threeDetails", "Just three details are needed");
        put(Language.ENGLISH, "booking.name", "Name");
        put(Language.ENGLISH, "booking.email", "Email");
        put(Language.ENGLISH, "booking.phone", "Phone");
        put(Language.ENGLISH, "booking.chooseSeats", "Choose your seats");
        put(Language.ENGLISH, "booking.reviewSeats", "Review seats and continue");
        put(Language.ENGLISH, "booking.confirmHere", "Confirm booking");
        put(Language.ENGLISH, "directory.chooseSeats", "Choose seats");
        put(Language.ENGLISH, "booking.selectSeat", "SELECT A SEAT");
        put(Language.ENGLISH, "booking.priceOutline", "Price outline");
        put(Language.ENGLISH, "booking.total", "TOTAL");
        put(Language.ENGLISH, "booking.vacancy", "VACANCY");
        put(Language.ENGLISH, "booking.seatsHeld", "SEATS HELD");
        put(Language.ENGLISH, "booking.yourSelection", "YOUR SELECTION");
        put(Language.ENGLISH, "booking.noSeats", "No seats selected");
        put(Language.ENGLISH, "booking.clearSeats", "Clear seats");
        put(Language.ENGLISH, "booking.confirm", "Confirm booked seats");
        put(Language.ENGLISH, "booking.viewBooked", "View booked seats");
        put(Language.ENGLISH, "booking.seatSubtotal", "Seat subtotal");
        put(Language.ENGLISH, "booking.bookingFee", "Booking fee (once)");
        put(Language.ENGLISH, "booking.totalDue", "Total due");
        put(Language.ENGLISH, "legend.vacant", "Vacant");
        put(Language.ENGLISH, "legend.selected", "Selected");
        put(Language.ENGLISH, "legend.held", "Held");
        put(Language.ENGLISH, "legend.booked", "Booked");
        put(Language.ENGLISH, "legend.closed", "Closed");
        put(Language.ENGLISH, "bookings.title", "Booking history");
        put(Language.ENGLISH, "bookings.subtitle",
                "Every confirmed or cancelled reservation in one place.");
        put(Language.ENGLISH, "bookings.search.hint", "Search bookings");
        put(Language.ENGLISH, "bookings.refresh", "Refresh");
        put(Language.ENGLISH, "bookings.cancel", "Cancel selected booking");
        put(Language.ENGLISH, "bookings.export", "Export CSV");
        put(Language.ENGLISH, "seats.title", "Seats booked so far");
        put(Language.ENGLISH, "seats.subtitle",
                "Every seat already taken for the event you pick.");
        put(Language.ENGLISH, "status.ready", "Ready");
        put(Language.ENGLISH, "status.chooseStadium", "Choose a stadium to begin");
        put(Language.ENGLISH, "common.back", "Back");
        put(Language.ENGLISH, "common.cancel", "Cancel");
        put(Language.ENGLISH, "common.close", "Close");
        put(Language.ENGLISH, "common.pay", "Pay");
        put(Language.ENGLISH, "common.print", "Print");
        put(Language.ENGLISH, "common.save", "Save as text");

        put(Language.ENGLISH, "booking.priceGuide", "Booked seats at this event");
        put(Language.ENGLISH, "booking.priceHint", "Seat prices vary by row and section.");
        put(Language.ENGLISH, "booking.feeHint", "One booking fee per reservation");
        // The number is a {0} placeholder, not a typed-in figure. It used to be
        // written out, so raising MAX_SEATS_PER_BOOKING left the screen claiming
        // the old limit: the code said 50 and the wording still said 20.
        put(Language.ENGLISH, "booking.limitHint",
                "No limit on bookings • up to {0} seats in one reservation");
        put(Language.ENGLISH, "booking.priceSubtitle", "Every charge is shown before you confirm");
        put(Language.ENGLISH, "bookings.hint",
                "Click any booking row to view the complete reservation and customer details.");
        put(Language.ENGLISH, "bookings.cancelSelected", "Cancel selected booking");
        put(Language.ENGLISH, "common.backToStadiums", "\u2190 Back to the stadium");
        put(Language.ENGLISH, "common.backToSchedule", "\u2190 Back to schedule");
        put(Language.ENGLISH, "nav.occupancy", "Occupancy");
        put(Language.ENGLISH, "nav.saved", "Saved");
        put(Language.ENGLISH, "saved.title", "Saved seats");
        put(Language.ENGLISH, "booking.saveSelection", "Save these seats");
        put(Language.ENGLISH, "occupancy.title", "Occupancy report");
        put(Language.ENGLISH, "occupancy.subtitle",
                "How full the ground is for each event, and how full each end is.");
        put(Language.ENGLISH, "occupancy.export", "Export occupancy CSV");
        put(Language.ENGLISH, "payment.title", "Payment");
        put(Language.ENGLISH, "payment.question", "How would you like to pay?");
        put(Language.ENGLISH, "common.totalDue", "Total due");
        put(Language.ENGLISH, "seatMap.hint",
                "Front rows are premium \u2022 tap a seat, or use the arrow keys");
        put(Language.ENGLISH, "seatMap.priceGuide", "Prices fall from front to back");
        put(Language.ENGLISH, "occupancy.allVenues", "How full each night is");
        put(Language.ENGLISH, "occupancy.note",
                "Confirmed seats only. Cancelled bookings free their seats.");
        put(Language.ENGLISH, "occupancy.vacancyNote",
                "Occupancy of the venue and event you are booking on.");
        put(Language.ENGLISH, "seats.everyConfirmed",
                "Every confirmed seat for the selected event, newest first.");
        put(Language.ENGLISH, "payment.simulatedNote",
                "mobile money is simulated, no money moves");
        put(Language.ENGLISH, "booking.seatPriceGuide", "Seat prices vary by row and section.");
        put(Language.ENGLISH, "booking.emptyPrice",
                "Select one or more vacant seats to see the full price outline.");
        put(Language.ENGLISH, "common.allVenues", "\u2190 Back to the stadium");

        // Wording that used to be typed straight into the screens. It lives here
        // now so the whole interface follows the chosen language and not just the
        // parts that happened to use a key.
        put(Language.ENGLISH, "common.allStadiums", "\u2190 All stadiums");
        put(Language.ENGLISH, "common.discard", "Discard");
        put(Language.ENGLISH, "directory.howItWorks", "HOW IT WORKS");
        put(Language.ENGLISH, "directory.seeAllSchedules", "See all live schedules");
        put(Language.ENGLISH, "stadium.about", "About this venue");
        put(Language.ENGLISH, "stadium.aboutButton", "About {0}");
        put(Language.ENGLISH, "stadium.atThisVenue", "At this venue");
        put(Language.ENGLISH, "stadium.sections", "Seating sections");
        put(Language.ENGLISH, "stadium.sectionsNote",
                "Front rows are the dearest in every section.");
        put(Language.ENGLISH, "stadium.info", "Stadium information");
        put(Language.ENGLISH, "stadium.infoTip",
                "Everything about the venue: capacity, seating, address");
        put(Language.ENGLISH, "stadium.seeBooked", "See seats already booked here");
        put(Language.ENGLISH, "stadium.tip.schedule",
                "Choose a date and event, then pick your seats at {0}");
        put(Language.ENGLISH, "stadium.noticesSubtitle", "Cancellations and emergency updates");
        put(Language.ENGLISH, "stadium.noNotices",
                "No active notices. Special requests can still be sent to the venue team.");
        put(Language.ENGLISH, "booking.beforeYouBook", "Before you book");
        put(Language.ENGLISH, "booking.noSeatsChosen", "No seats chosen yet.");
        put(Language.ENGLISH, "booking.totalInline", "Total {0}");
        put(Language.ENGLISH, "booking.feeNote", "plus a {0} ticketing fee");
        put(Language.ENGLISH, "booking.chooseSeatsHint",
                "Choose one or more seats, then confirm here");
        put(Language.ENGLISH, "booking.stopsIn", "Booking stops in {0}");
        put(Language.ENGLISH, "booking.referenceTitle", "Booking {0}");
        put(Language.ENGLISH, "booking.viewReceipt", "View itemised receipt");
        put(Language.ENGLISH, "booking.confirmTip",
                "Book your {0} for {1}, then show your receipt");
        put(Language.ENGLISH, "booking.detailsIntro", "Your details, so the booking is yours");
        put(Language.ENGLISH, "booking.statusHeader", "STATUS");
        put(Language.ENGLISH, "saved.labelHint", "Call it something you will recognise");
        put(Language.ENGLISH, "saved.findEvent", "Find an event to book");
        put(Language.ENGLISH, "saved.resume", "Resume and book");
        put(Language.ENGLISH, "saved.tip.keep",
                "Keep these seats and choose them later. This does not book or hold them");
        put(Language.ENGLISH, "events.allUpcoming", "All upcoming events");
        put(Language.ENGLISH, "events.allUpcomingSubtitle",
                "Soonest first, every game and concert at Namboole. Pick an event to choose seats.");
        put(Language.ENGLISH, "event.tip.open", "Open the details for {0}");
        put(Language.ENGLISH, "event.doors", "Doors {0}");
        put(Language.ENGLISH, "event.starts", "Starts {0}  \u2022  Doors {1}");
        put(Language.ENGLISH, "event.deadline", "Booking deadline: {0}");
        put(Language.ENGLISH, "ledger.tip", "See every seat already taken for this event");
        put(Language.ENGLISH, "bookings.tip.export", "Write every booking to a spreadsheet file");
        put(Language.ENGLISH, "request.type", "Request type");
        put(Language.ENGLISH, "request.details", "Details");
        put(Language.ENGLISH, "request.minHint",
                "At least {0} characters, so the venue team knows what you need.");

        // Luganda
        put(Language.LUGANDA, "nav.back", "← Mabega");
        put(Language.LUGANDA, "nav.venues", "Ekisaawe");
        put(Language.LUGANDA, "nav.bookings", "Buukinga");
        put(Language.LUGANDA, "nav.bookedSeats", "Buukingi zange");
        put(Language.LUGANDA, "nav.liveSchedules", "ENTEEKATEEKA EZILIWO");
        put(Language.LUGANDA, "directory.title", "Ekizimbe kya Mandela");
        put(Language.LUGANDA, "directory.hero.title", "Yatandika olootanda lwafo ku Namboole");
        put(Language.LUGANDA, "directory.findVenue", "LONDA EKIZIMBE");
        put(Language.LUGANDA, "directory.search.hint", "Shunja ekizimbe, egga, ekizibuBy'obujjuni");
        put(Language.LUGANDA, "directory.open", "Kikikizo");
        put(Language.LUGANDA, "directory.viewDetails", "Lola ebisinga by'obuddwa");
        put(Language.LUGANDA, "directory.book", "Yatandika ebizitansa");
        put(Language.LUGANDA, "directory.bookAt", "Lola lutundu era yatandika");
        put(Language.LUGANDA, "directory.allEvents", "Ebiro byonna ebiri");
        put(Language.LUGANDA, "booking.title", "Yatandika ebizitansa");
        put(Language.LUGANDA, "booking.yourDetails", "Ebikwata by'ekisobola");
        put(Language.LUGANDA, "booking.name", "Amalinnya");
        put(Language.LUGANDA, "booking.email", "Imeera y'okwandika");
        put(Language.LUGANDA, "booking.phone", "Ennimi");
        put(Language.LUGANDA, "booking.chooseSeats", "Londa ebizitansa");
        put(Language.LUGANDA, "booking.reviewSeats", "Kebera ebizitansa era gyeyo");
        put(Language.LUGANDA, "booking.confirmHere", "Kakasa ebizitansa");
        put(Language.LUGANDA, "directory.chooseSeats", "Londa ebizitansa");
        put(Language.LUGANDA, "booking.priceOutline", "Ennimiro y'omutunda");
        put(Language.LUGANDA, "booking.total", "TOTAL");
        put(Language.LUGANDA, "booking.vacancy", "EZZAALIRO");
        put(Language.LUGANDA, "booking.yourSelection", "OKATORODDA");
        put(Language.LUGANDA, "booking.noSeats", "Tewali buzitansa");
        put(Language.LUGANDA, "booking.clearSeats", "Ggiriza");
        put(Language.LUGANDA, "booking.confirm", "Wekakise ebizitansa");
        put(Language.LUGANDA, "legend.vacant", "Naludika");
        put(Language.LUGANDA, "legend.selected", "Waliwo");
        put(Language.LUGANDA, "legend.held", "Wamanyagiridwa");
        put(Language.LUGANDA, "legend.booked", "Wakibwa");
        put(Language.LUGANDA, "legend.closed", "Wafunye");
        put(Language.LUGANDA, "bookings.title", "Ebikwata byanjulo");
        put(Language.LUGANDA, "bookings.refresh", "Yakiza");
        put(Language.LUGANDA, "bookings.cancel", "Sikiza ekyali");
        put(Language.LUGANDA, "bookings.export", "Kikola CSV");
        put(Language.LUGANDA, "seats.title", "Ebizitansa ebikwata");

        // Swahili
        put(Language.SWAHILI, "nav.back", "← Rudi");
        put(Language.SWAHILI, "nav.venues", "Uwanja");
        put(Language.SWAHILI, "nav.bookings", "Reservations zangu");
        put(Language.SWAHILI, "nav.bookedSeats", "Viti vilivyookwa");
        put(Language.SWAHILI, "nav.liveSchedules", "KINACHOKUWA");
        put(Language.SWAHILI, "directory.title", "Uwanja wa Taifa wa Mandela");
        put(Language.SWAHILI, "directory.hero.title", "Booki kiti yako Namboole");
        put(Language.SWAHILI, "directory.findVenue", "TAFAUTA UWANJA");
        put(Language.SWAHILI, "directory.search.hint", "Tafuta uwanja, mji, timu au msanii");
        put(Language.SWAHILI, "directory.open", "Fungua uwanja");
        put(Language.SWAHILI, "directory.viewDetails", "Angalia maelezo");
        put(Language.SWAHILI, "directory.book", "Chagua viti");
        put(Language.SWAHILI, "directory.bookAt", "RUDI ratiba na uchague viti");
        put(Language.SWAHILI, "directory.allEvents", "Matukio yote yajayo");
        put(Language.SWAHILI, "booking.title", "Viti vyako");
        put(Language.SWAHILI, "booking.yourDetails", "Maelezo yako");
        put(Language.SWAHILI, "booking.name", "Jina");
        put(Language.SWAHILI, "booking.email", "Barua pepe");
        put(Language.SWAHILI, "booking.phone", "Namba ya simu");
        put(Language.SWAHILI, "booking.chooseSeats", "Chagua viti");
        put(Language.SWAHILI, "booking.reviewSeats", "Pitia viti na uendelee");
        put(Language.SWAHILI, "booking.confirmHere", "Thibitisha viti");
        put(Language.SWAHILI, "directory.chooseSeats", "Chagua viti");
        put(Language.SWAHILI, "booking.priceOutline", "Muhtasari wa bei");
        put(Language.SWAHILI, "booking.total", "JUMLA");
        put(Language.SWAHILI, "booking.vacancy", "NAFASI");
        put(Language.SWAHILI, "booking.yourSelection", "ULICHAGUA");
        put(Language.SWAHILI, "booking.noSeats", "Hakuna viti vilivyochaguliwa");
        put(Language.SWAHILI, "booking.clearSeats", "Futa");
        put(Language.SWAHILI, "booking.confirm", "Thibitisha viti");
        put(Language.SWAHILI, "legend.vacant", "Zilizo huria");
        put(Language.SWAHILI, "legend.selected", "Vimechaguliwa");
        put(Language.SWAHILI, "legend.held", "Vimeshikiliwa");
        put(Language.SWAHILI, "legend.booked", "Vimeuzwa");
        put(Language.SWAHILI, "legend.closed", "Yimefungwa");
        put(Language.SWAHILI, "bookings.title", "Historia ya reservations");
        put(Language.SWAHILI, "bookings.refresh", "Onyesha upya");
        put(Language.SWAHILI, "bookings.cancel", "Ghairi iliyochaguliwa");
        put(Language.SWAHILI, "bookings.export", "Hamisha CSV");
        put(Language.SWAHILI, "seats.title", "Viti vilivyookwa hadi sasa");

        put(Language.LUGANDA, "app.title", "Ebisitansa bya Namboole");
        put(Language.LUGANDA, "app.tagline", "EBISITANSA BYA NAMBOOLE");
        put(Language.LUGANDA, "app.languageHint", "Lunguza olulimi lw'ekikwata");
        put(Language.LUGANDA, "app.languageName", "Olulimi");
        put(Language.LUGANDA, "app.languageDescription",
                "Lunguza English, Luganda oba Kiswahili. Ekizimbe kyonna kikakyukuka ku kiseera kiso.");
        put(Language.LUGANDA, "directory.subtitle",
                "Katandika ekizimbe, mbeera olunaku lwa ekizibuBy'obujjuni oluyako.");
        put(Language.LUGANDA, "directory.hero.eyebrow", "EBIRO BY'AMABIRALI");
        put(Language.LUGANDA, "directory.hero.subtitle",
                "Lulambula amabirali, keka ebbaliwo, era yatandika ebizitansa nga bwe kisobola.");
        put(Language.LUGANDA, "directory.venuesAvailable", "Amabirali {0} awo aliwo");
        put(Language.LUGANDA, "directory.venuesMatching", "Amabirali {0} agasobola n'okunoonya");
        put(Language.LUGANDA, "stadium.upcoming", "Enteekateeka ezijja mu maaso");
        put(Language.LUGANDA, "stadium.search.hint",
                "Shunja ekizibuBy'obujjuni, omusani, sport n'obudde");
        put(Language.LUGANDA, "stadium.notices", "Ebikwata n'okusaba");
        put(Language.LUGANDA, "stadium.specialRequest", "Saba ekikwata ekikwateekero");
        put(Language.LUGANDA, "booking.threeDetails", "Ebituuka bibiri gusa birina");
        put(Language.LUGANDA, "booking.selectSeat", "LONDA EKIZITANSA");
        put(Language.LUGANDA, "booking.seatsHeld", "EBIZITANSA EBIRAMBIDDE");
        put(Language.LUGANDA, "booking.viewBooked", "Loola ebizitansa ebikwata");
        put(Language.LUGANDA, "booking.priceGuide", "Ebizitansa ebikwata ebisobola ku ngalo");
        put(Language.LUGANDA, "booking.priceHint", "Emirimu egendera ku lutundu n'ekizimbe.");
        put(Language.LUGANDA, "booking.feeHint", "Kikumi k'ekikwata kimwe ku nkwaateeka");
        put(Language.LUGANDA, "booking.limitHint",
                "Tewali kkano • kiwango kya vituuka {0} mu nkwaateeka emu");
        put(Language.LUGANDA, "booking.priceSubtitle", "Bwe buli ntikita bonynkusooka nga osazeeko");
        put(Language.LUGANDA, "bookings.subtitle", "Byonna ebikwata ebirimu n'ebitannyalukirwa mu kifo kina.");
        put(Language.LUGANDA, "bookings.hint",
                "Kikwata ekigyendererwa okulaba ekikwata by'ekintu n'ebisubiraby'omuntu.");
        put(Language.LUGANDA, "bookings.cancelSelected", "Sikiza ekikwata ekisobola");
        put(Language.LUGANDA, "seats.subtitle",
                "Loola kila kifo ekikwata, ku nzimbe n'ekigyendererwa.");
        put(Language.LUGANDA, "common.backToStadiums", "← Amabirali onoona");
        put(Language.LUGANDA, "common.backToSchedule", "← Subira ku lutundu");
        put(Language.LUGANDA, "nav.occupancy", "Ebikwata");
        put(Language.LUGANDA, "nav.saved", "Ebizitansa ebikumi");
        put(Language.LUGANDA, "saved.title", "Ebizitansa ebikumi");
        put(Language.LUGANDA, "booking.saveSelection", "Ghiriza ebizitansa");
        put(Language.LUGANDA, "occupancy.title", "Ebikwata by'ebizitansa");
        put(Language.LUGANDA, "occupancy.subtitle",
                "Ebizitansa ebikwata buli kizimbe n'ekizimbe, mu bweru bw'ekigyendererwa.");
        put(Language.LUGANDA, "occupancy.export", "Kikola CSV y'ebikwata");
        put(Language.LUGANDA, "payment.title", "Nn-payment");
        put(Language.LUGANDA, "payment.question", "Wandi wa mula okugy Payment?");
        put(Language.LUGANDA, "common.totalDue", "Amagazi a'okugy Payment");
        put(Language.LUGANDA, "seatMap.hint", "Emiganda esooka eri eby'omugaso • kikka kifo, oba kozooda endoboozi");
        put(Language.LUGANDA, "seatMap.priceGuide", "Emirimu ekkka ku ngalo kubanga ku ngalo");

        // Keys the English screen gained later. Without these the screen fell back
        // to English the moment the language was switched.
        put(Language.LUGANDA, "stadium.subtitle",
                "Londa olunaku, olwo londa omuzannyo oba konsati gw'oyagala okulaba.");
        put(Language.LUGANDA, "booking.seatSubtotal", "Omugatte gw'ebifo");
        put(Language.LUGANDA, "booking.bookingFee", "Ebisale by'okutanda (kamwe)");
        put(Language.LUGANDA, "booking.totalDue", "Omugatte ogusabiddwa");
        put(Language.LUGANDA, "booking.seatPriceGuide", "Ebisaale by'ebifo bitandika ku mulundi n'ekitundu.");
        put(Language.LUGANDA, "booking.emptyPrice",
                "Londa ekifo kimu oba ebisinga okulaba ebisale byonna.");
        put(Language.LUGANDA, "bookings.search.hint", "Noonya ebikwata");
        put(Language.LUGANDA, "seats.everyConfirmed",
                "Buli kifo ekikakasiddwa ku kiro ekyalondeddwa, ekisinga obupya okusooka.");
        put(Language.LUGANDA, "occupancy.allVenues", "Amatu ekifo buli kiro bwe kijjudde");
        put(Language.LUGANDA, "occupancy.note",
                "Ebifo ebikakasiddwa gusa. Ebikwata ebisaziddwaamu bireka ebifo wolle.");
        put(Language.LUGANDA, "occupancy.vacancyNote", "Obujjuvu bw'ekisaawe n'ekiro ky'otandirako.");
        put(Language.LUGANDA, "payment.simulatedNote",
                "ssente za simu zikozesebwa mu ngeri ey'ekifaananyi, tewali ssente ezitambula");
        put(Language.LUGANDA, "status.ready", "Wekyetegese");
        put(Language.LUGANDA, "status.chooseStadium", "Londa ekisaawe okutandika");
        put(Language.LUGANDA, "common.back", "Ddayo");
        put(Language.LUGANDA, "common.cancel", "Sazaamu");
        put(Language.LUGANDA, "common.close", "Ggalawo");
        put(Language.LUGANDA, "common.pay", "Sasula");
        put(Language.LUGANDA, "common.print", "Fulumya");
        put(Language.LUGANDA, "common.save", "Tereka nga ekiwandiiko");
        put(Language.LUGANDA, "common.allVenues", "\u2190 Ddayo ku kisaawe");

        // Wording moved out of the screens, translated so the whole interface
        // changes with the language.
        put(Language.LUGANDA, "common.allStadiums", "\u2190 Amabirali gonna");
        put(Language.LUGANDA, "common.discard", "Ggyawo");
        put(Language.LUGANDA, "directory.howItWorks", "ENGERI EKIKOLA");
        put(Language.LUGANDA, "directory.seeAllSchedules", "Laba enteekateeka zonna");
        put(Language.LUGANDA, "stadium.about", "Ebikwata ku kisaawe kino");
        put(Language.LUGANDA, "stadium.aboutButton", "Ebikwata ku {0}");
        put(Language.LUGANDA, "stadium.atThisVenue", "Ku kisaawe kino");
        put(Language.LUGANDA, "stadium.sections", "Ebitundu by'ebifo");
        put(Language.LUGANDA, "stadium.sectionsNote",
                "Emirundi egisooka gye gisinga obungi mu kitundu kyonna.");
        put(Language.LUGANDA, "stadium.info", "Amawulire g'ekisaawe");
        put(Language.LUGANDA, "stadium.infoTip",
                "Byonna ebikwata ku kisaawe: obunene, ebitundu n'endagiriro");
        put(Language.LUGANDA, "stadium.seeBooked", "Laba ebifo ebikwata ku kino");
        put(Language.LUGANDA, "stadium.tip.schedule",
                "Londa olunaku n'ekiro, olwo londa ebifo byo ku {0}");
        put(Language.LUGANDA, "stadium.noticesSubtitle",
                "Okusazaamu n'amawulire ag'ekyaaye");
        put(Language.LUGANDA, "stadium.noNotices",
                "Tewali biwandiiko. Okusaba okw'enjawulo kusobola okutumibwa ku ttiimu y'ekisaawe.");
        put(Language.LUGANDA, "booking.beforeYouBook", "Nga tonnatandika");
        put(Language.LUGANDA, "booking.noSeatsChosen", "Tewali kifo kirondeddwa.");
        put(Language.LUGANDA, "booking.totalInline", "Omugatte {0}");
        put(Language.LUGANDA, "booking.feeNote", "kwongera {0} ku bisale by'okutanda");
        put(Language.LUGANDA, "booking.chooseSeatsHint",
                "Londa ekifo kimu oba ebisinga, olwo kakasa wano");
        put(Language.LUGANDA, "booking.stopsIn", "Okutanda kukoma mu {0}");
        put(Language.LUGANDA, "booking.referenceTitle", "Okutanda {0}");
        put(Language.LUGANDA, "booking.viewReceipt", "Laba risiti ekkwatiriza");
        put(Language.LUGANDA, "booking.confirmTip",
                "Tanda {0} ku {1}, olwo laga risiti yo");
        put(Language.LUGANDA, "booking.detailsIntro",
                "Ebikukwatako, kisobozesa okutanda okubeera kyange");
        put(Language.LUGANDA, "booking.statusHeader", "EMBEERA");
        put(Language.LUGANDA, "saved.labelHint", "Kiyite ekintu ky'ojja okumanya");
        put(Language.LUGANDA, "saved.findEvent", "Noonya ekiro ky'okutandira");
        put(Language.LUGANDA, "saved.resume", "Ddamu otande");
        put(Language.LUGANDA, "saved.tip.keep",
                "Kuma ebifo bino era obirondere dda. Kino tekutanda wadde okukwata");
        put(Language.LUGANDA, "events.allUpcoming", "Ebiro byonna ebijja");
        put(Language.LUGANDA, "events.allUpcomingSubtitle",
                "Ekisinga okumpi okusooka, buli muzannyo ne konsati ku Namboole. Londa ekiro okulonda ebifo.");
        put(Language.LUGANDA, "event.tip.open", "Ggula ebisinga by'obuddwa ku {0}");
        put(Language.LUGANDA, "event.doors", "Amawungo {0}");
        put(Language.LUGANDA, "event.starts", "Bitandika {0}  \u2022  Amawungo {1}");
        put(Language.LUGANDA, "event.deadline", "Ekkomo ly'okutanda: {0}");
        put(Language.LUGANDA, "ledger.tip", "Laba buli kifo ekitwaliro ku kiro kino");
        put(Language.LUGANDA, "bookings.tip.export",
                "Wandiika buli kutanda mu fayiro ya sipuredi");
        put(Language.LUGANDA, "request.type", "Kika ky'okusaba");
        put(Language.LUGANDA, "request.details", "Ebisinga");
        put(Language.LUGANDA, "request.minHint",
                "Obuntu {0} oba okusinga, ttiimu y'ekisaawe emanye ky'oyagala.");

        put(Language.SWAHILI, "directory.subtitle",
                "Anza na uwanja, kisha chagua tarehe na matukio unayotaka.");
        put(Language.SWAHILI, "directory.hero.eyebrow", "OBDHA YA UWANJA");
        put(Language.SWAHILI, "directory.hero.subtitle",
                "Tembelea uwanja, angalia matukio yako, na hifadhi viti sahihi kwa hatua rahisi.");
        put(Language.SWAHILI, "directory.venuesAvailable", "Uwanja {0} zilizopo");
        put(Language.SWAHILI, "directory.venuesMatching", "Uwanja {0} zinalingana na utafutaji wako");
        put(Language.SWAHILI, "stadium.upcoming", "Ratiba ijayo");
        put(Language.SWAHILI, "stadium.search.hint", "Tafuta timu, msanii, mchezo au tarehe");
        put(Language.SWAHILI, "stadium.notices", "Taarifa na maombi");
        put(Language.SWAHILI, "stadium.specialRequest", "Wasilisha ombi maalum");
        put(Language.SWAHILI, "booking.threeDetails", "Maelezo matatu tu ndiyo yanayohitajika");
        put(Language.SWAHILI, "booking.selectSeat", "CHAGUA KITI");
        put(Language.SWAHILI, "booking.seatsHeld", "VITI VILIVYOSHIKILIWA");
        put(Language.SWAHILI, "booking.viewBooked", "Ona viti vilivyookwa");
        put(Language.SWAHILI, "booking.priceGuide", "Viti vilivyookwa zinaonyeshwa hapa");
        put(Language.SWAHILI, "booking.priceHint", "Bei za viti hutofautiana kwa safu na sehemu.");
        put(Language.SWAHILI, "booking.feeHint", "Kadi moja kwa kila reservation");
        put(Language.SWAHILI, "booking.limitHint",
                "Hakio kikomo • hadi viti {0} kwa kila utanuzi");
        put(Language.SWAHILI, "booking.priceSubtitle", "Kila malipo yanaonyeshwa kabla ya kuthibitisha");
        put(Language.SWAHILI, "bookings.subtitle",
                "Reservations zote zilizothibitishwa au zilizoghairiwa mahali pamoja.");
        put(Language.SWAHILI, "bookings.hint",
                "Bofya safu yoyote ili kuona reservation kamili na maelezo ya mteja.");
        put(Language.SWAHILI, "bookings.cancelSelected", "Ghairi iliyochaguliwa");
        put(Language.SWAHILI, "seats.subtitle",
                "Ona kila kiti kilichookwa, kwa uwanja na kwa matukio.");
        put(Language.SWAHILI, "common.backToStadiums", "← Uwanja zote");
        put(Language.SWAHILI, "common.backToSchedule", "← Rudi kwa ratiba");
        put(Language.SWAHILI, "nav.occupancy", "Kujaza");
        put(Language.SWAHILI, "nav.saved", "Viti vilivyohifadhiwa");
        put(Language.SWAHILI, "saved.title", "Viti vilivyohifadhiwa");
        put(Language.SWAHILI, "booking.saveSelection", "Hifadhi viti hivi");
        put(Language.SWAHILI, "occupancy.title", "Ripoti ya kujaza viti");
        put(Language.SWAHILI, "occupancy.subtitle",
                "Jinsi gani kila uwanja na sehemu ilivyojaa, katika matukio yote.");
        put(Language.SWAHILI, "occupancy.export", "Hamisha CSV ya kujaza");
        put(Language.SWAHILI, "payment.title", "Malipo");
        put(Language.SWAHILI, "payment.question", "Ungependa kulipa vipi?");
        put(Language.SWAHILI, "common.totalDue", "Inayotakiwa kulipa");
        put(Language.SWAHILI, "seatMap.hint",
                "Safu za mbele ni za kipekee • bofya kiti, au tumia vishale vya mishale");
        put(Language.SWAHILI, "app.title", "Kiti cha Namboole");
        put(Language.SWAHILI, "app.tagline", "KITI CHA NAMBOOLE");
        put(Language.SWAHILI, "app.languageHint", "Badilisha lugha ya kiolesura");
        put(Language.SWAHILI, "app.languageName", "Lugha");
        put(Language.SWAHILI, "app.languageDescription",
                "Chagua English, Luganda au Kiswahili. Skrini nzima hubadilika mara moja.");
        put(Language.SWAHILI, "seatMap.priceGuide", "Bei zinashuka kutoka mbele hadi nyuma");

        // Keys the English screen gained later, translated so switching language
        // never leaves part of the screen reading in English.
        put(Language.SWAHILI, "stadium.subtitle",
                "Chagua tarehe, kisha chagua mchezo au tamasha unalotaka kuhudhuria.");
        put(Language.SWAHILI, "booking.seatSubtotal", "Jumla ya viti");
        put(Language.SWAHILI, "booking.bookingFee", "Ada ya kuweka nafasi (mara moja)");
        put(Language.SWAHILI, "booking.totalDue", "Jumla inayotakiwa");
        put(Language.SWAHILI, "booking.seatPriceGuide", "Bei za viti hutofautiana kwa safu na sehemu.");
        put(Language.SWAHILI, "booking.emptyPrice",
                "Chagua kiti kimoja au zaidi vilivyo huria ili kuona muhtasari wa bei.");
        put(Language.SWAHILI, "bookings.search.hint", "Tafuta reservations");
        put(Language.SWAHILI, "seats.everyConfirmed",
                "Kila kiti kilichothibitishwa kwa tukio lililochaguliwa, vipya kwanza.");
        put(Language.SWAHILI, "occupancy.allVenues", "Jinsi kila usiku ulivyojaa");
        put(Language.SWAHILI, "occupancy.note",
                "Viti vilivyothibitishwa pekee. Ghairi hurudisha viti.");
        put(Language.SWAHILI, "occupancy.vacancyNote", "Kujaa kwa uwanja na tukio unaloweka nafasi.");
        put(Language.SWAHILI, "payment.simulatedNote",
                "pesa za simu zinaigwa, hakuna pesa zinazotoka");
        put(Language.SWAHILI, "status.ready", "Tayari");
        put(Language.SWAHILI, "status.chooseStadium", "Chagua uwanja ili kuanza");
        put(Language.SWAHILI, "common.back", "Rudi");
        put(Language.SWAHILI, "common.cancel", "Ghairi");
        put(Language.SWAHILI, "common.close", "Funga");
        put(Language.SWAHILI, "common.pay", "Lipa");
        put(Language.SWAHILI, "common.print", "Chapisha");
        put(Language.SWAHILI, "common.save", "Hifadhi kama maandishi");
        put(Language.SWAHILI, "common.allVenues", "\u2190 Rudi kwa uwanja");

        // Wording moved out of the screens, translated so the whole interface
        // changes with the language.
        put(Language.SWAHILI, "common.allStadiums", "\u2190 Viwanja vyote");
        put(Language.SWAHILI, "common.discard", "Tupa");
        put(Language.SWAHILI, "directory.howItWorks", "INAVYOFANYA KAZI");
        put(Language.SWAHILI, "directory.seeAllSchedules", "Ona ratiba zote");
        put(Language.SWAHILI, "stadium.about", "Kuhusu uwanja huu");
        put(Language.SWAHILI, "stadium.aboutButton", "Kuhusu {0}");
        put(Language.SWAHILI, "stadium.atThisVenue", "Katika uwanja huu");
        put(Language.SWAHILI, "stadium.sections", "Sehemu za viti");
        put(Language.SWAHILI, "stadium.sectionsNote",
                "Safu za mbele ni za bei kubwa katika kila sehemu.");
        put(Language.SWAHILI, "stadium.info", "Taarifa za uwanja");
        put(Language.SWAHILI, "stadium.infoTip",
                "Kila kitu kuhusu uwanja: uwezo, viti, anwani");
        put(Language.SWAHILI, "stadium.seeBooked", "Ona viti vilivyookwa hapa");
        put(Language.SWAHILI, "stadium.tip.schedule",
                "Chagua tarehe na tukio, kisha chagua viti vyako kwa {0}");
        put(Language.SWAHILI, "stadium.noticesSubtitle", "Ghairi na taarifa za dharura");
        put(Language.SWAHILI, "stadium.noNotices",
                "Hakuna taarifa. Maombi maalum yanaweza kutumwa kwa timu ya uwanja.");
        put(Language.SWAHILI, "booking.beforeYouBook", "Kabla ya kuweka nafasi");
        put(Language.SWAHILI, "booking.noSeatsChosen", "Hakuna kiti kilichochaguliwa.");
        put(Language.SWAHILI, "booking.totalInline", "Jumla {0}");
        put(Language.SWAHILI, "booking.feeNote", "pamoja na {0} ya ada");
        put(Language.SWAHILI, "booking.chooseSeatsHint",
                "Chagua kiti kimoja au zaidi, kisha thibitisha hapa");
        put(Language.SWAHILI, "booking.stopsIn", "Uwekaji nafasi unaisha baada ya {0}");
        put(Language.SWAHILI, "booking.referenceTitle", "Reservation {0}");
        put(Language.SWAHILI, "booking.viewReceipt", "Ona risiti ya vitu");
        put(Language.SWAHILI, "booking.confirmTip",
                "Weka {0} kwa {1}, kisha onyesha risiti yako");
        put(Language.SWAHILI, "booking.detailsIntro",
                "Maelezo yako, ili reservation iwe yako");
        put(Language.SWAHILI, "booking.statusHeader", "HALI");
        put(Language.SWAHILI, "saved.labelHint", "Ipe jina utakalolitambua");
        put(Language.SWAHILI, "saved.findEvent", "Tafuta tukio la kuweka nafasi");
        put(Language.SWAHILI, "saved.resume", "Endelea na uweke");
        put(Language.SWAHILI, "saved.tip.keep",
                "Hifadhi viti hivi na uchague baadaye. Hakuweki wala kushikilia");
        put(Language.SWAHILI, "events.allUpcoming", "Matukio yote yajayo");
        put(Language.SWAHILI, "events.allUpcomingSubtitle",
                "Yanayokaribia kwanza, kila mchezo na tamasha Namboole. Chagua tukio kuchagua viti.");
        put(Language.SWAHILI, "event.tip.open", "Fungua maelezo ya {0}");
        put(Language.SWAHILI, "event.doors", "Milango {0}");
        put(Language.SWAHILI, "event.starts", "Huanza {0}  \u2022  Milango {1}");
        put(Language.SWAHILI, "event.deadline", "Mwisho wa kuweka nafasi: {0}");
        put(Language.SWAHILI, "ledger.tip", "Ona kila kiti kilichookwa kwa tukio hili");
        put(Language.SWAHILI, "bookings.tip.export",
                "Andika kila reservation kwenye faili ya lahajedwali");
        put(Language.SWAHILI, "request.type", "Aina ya ombi");
        put(Language.SWAHILI, "request.details", "Maelezo");
        put(Language.SWAHILI, "request.minHint",
                "Herufi {0} au zaidi, ili timu ya uwanja ijue unachohitaji.");
    }

    /**
     * The wording for a key in the current language, falling back to English and
     * then to the key itself so nothing ever renders blank.
     */
    public static String get(String key) {
        Map<String, String> language = TEXT.get(current);
        if (language != null) {
            String value = language.get(key);
            if (value != null) {
                return value;
            }
        }
        String fallback = TEXT.get(Language.ENGLISH).get(key);
        return fallback == null ? key : fallback;
    }

    /** Every key the interface knows, in the order English defines them. */
    public static java.util.List<String> keys() {
        Map<String, String> english = TEXT.get(Language.ENGLISH);
        return english == null
                ? new java.util.ArrayList<>()
                : new java.util.ArrayList<>(english.keySet());
    }

    /**
     * Whether a language carries its own wording for a key. Used by the tests to
     * prove nothing is quietly falling back to English.
     */
    public static boolean has(Language language, String key) {
        Map<String, String> map = TEXT.get(language);
        return map != null && map.containsKey(key);
    }

    /** Wording with a single number substituted, for example "3 venues available". */
    public static String get(String key, Object... arguments) {
        String template = get(key);
        for (int index = 0; index < arguments.length; index++) {
            template = template.replace("{" + index + "}", String.valueOf(arguments[index]));
        }
        return template;
    }

    /**
     * Picks the singular or plural wording for a count.
     *
     * <p>Languages do not agree on how many plural forms they have, and a
     * translated screen that always reads "3 seat" or "1 seats" looks broken
     * whatever the language. Each language is given the two forms it needs, and
     * falls back to English for any that has not been split yet.
     *
     * @param key    prefix of the pair, for example {@code home.eventCount}
     * @param amount how many there are
     * @return the wording with the number already in it
     */
    public static String count(String key, int amount) {
        if (amount == 1) {
            return get(key, amount);
        }
        String plural = get(key + "Plural");
        // A missing plural form would otherwise show the key itself, so English
        // is used until the other languages carry the pair.
        return plural.equals(key + "Plural") ? get(key + "Plural", amount) : plural;
    }

    /**
     * Every key defined, with the wording each language gives it.
     *
     * <p>Exposed so the wording table can be checked rather than trusted. The
     * runtime fallback in {@link #get(String)} is deliberate — a missing
     * translation shows English rather than a blank — but it also means an
     * untranslated screen looks perfectly healthy while running, and nothing
     * would ever say so. These accessors let a test ask the table directly.
     *
     * @return key to language to wording, in the order the keys were defined
     */
    public static Map<String, Map<Language, String>> allWording() {
        Map<String, Map<Language, String>> table = new LinkedHashMap<>();
        for (Language language : Language.values()) {
            Map<String, String> byKey = TEXT.get(language);
            if (byKey == null) {
                continue;
            }
            for (Map.Entry<String, String> entry : byKey.entrySet()) {
                table.computeIfAbsent(entry.getKey(), ignored -> new LinkedHashMap<>())
                        .put(language, entry.getValue());
            }
        }
        return table;
    }

    /** The keys defined for one language, for a check that a key was translated. */
    public static boolean isDefined(Language language, String key) {
        Map<String, String> byKey = TEXT.get(language);
        return byKey != null && byKey.containsKey(key);
    }

    /** Shillings are always written as UGX so a translated screen still reads clearly. */
    public static String money(double amount) {
        return BookingService.formatMoney(amount);
    }

    public static String dateLabel(java.time.LocalDate date) {
        return date.format(java.time.format.DateTimeFormatter.ofPattern("EEE, d MMM yyyy",
                current == Language.SWAHILI ? Locale.forLanguageTag("sw") : Locale.ENGLISH));
    }


    // ------------------------------------------------------------------
    // Wording for the screens that had English written into the code itself.
    // Every language carries every key: a screen that falls back to English
    // after a switch is the defect this section exists to prevent.
    // ------------------------------------------------------------------
    static {
        put(Language.ENGLISH, "common.ok",
                "OK");
        put(Language.ENGLISH, "common.discardThem",
                "Discard them");
        put(Language.ENGLISH, "common.keepThem",
                "Keep them");
        put(Language.ENGLISH, "nav.back.desc",
                "Return to the previous screen");
        put(Language.ENGLISH, "nav.venues.desc",
                "The stadium, its schedule and its notices");
        put(Language.ENGLISH, "nav.bookings.desc",
                "Find a booking by the email or phone number on it");
        put(Language.ENGLISH, "nav.bookedSeats.desc",
                "See which seats are already taken for each event");
        put(Language.ENGLISH, "nav.occupancy.desc",
                "See how full the stadium is, and how full each end is");
        put(Language.ENGLISH, "nav.saved.desc",
                "Seats you chose earlier and can come back to book later");
        put(Language.ENGLISH, "home.howItWorks",
                "HOW IT WORKS");
        put(Language.ENGLISH, "home.step1",
                "Pick an event");
        put(Language.ENGLISH, "home.step2",
                "Choose your seats");
        put(Language.ENGLISH, "home.step3",
                "Pay and keep your receipt");
        put(Language.ENGLISH, "home.step4",
                "Show your receipt");
        put(Language.ENGLISH, "home.nextUp",
                "Next up");
        put(Language.ENGLISH, "home.noEvents",
                "No events are scheduled here yet.");
        put(Language.ENGLISH, "home.capacity",
                "Capacity");
        put(Language.ENGLISH, "home.capacityHint",
                "published figure");
        put(Language.ENGLISH, "home.shape",
                "Shape");
        put(Language.ENGLISH, "home.shapeOval",
                "continuous bowl");
        put(Language.ENGLISH, "home.shapeStraight",
                "four straight stands");
        put(Language.ENGLISH, "home.seatPrice",
                "Seat price");
        put(Language.ENGLISH, "home.seatPriceHint",
                "plus");
        put(Language.ENGLISH, "home.feeHint",
                "fee");
        put(Language.ENGLISH, "home.onSaleNow",
                "On sale now");
        put(Language.ENGLISH, "home.vacantPercent",
                "% vacant");
        put(Language.ENGLISH, "home.aboutVenue",
                "About this venue");
        put(Language.ENGLISH, "facts.where",
                "Where");
        put(Language.ENGLISH, "facts.city",
                "City");
        put(Language.ENGLISH, "facts.type",
                "Type");
        put(Language.ENGLISH, "facts.footprint",
                "Footprint");
        put(Language.ENGLISH, "facts.seatsOnSale",
                "Seats on sale");
        put(Language.ENGLISH, "facts.sections",
                "Sections");
        put(Language.ENGLISH, "facts.independent",
                "independent");
        put(Language.ENGLISH, "facts.perEvent",
                "per event");
        put(Language.ENGLISH, "stadium.whatIsOn",
                "What is on here");
        put(Language.ENGLISH, "stadium.gamesAndConcerts",
                "games,");
        put(Language.ENGLISH, "stadium.concerts",
                "concerts");
        put(Language.ENGLISH, "stadium.clubsAndTeams",
                "Clubs and teams:");
        put(Language.ENGLISH, "stadium.artists",
                "Artists:");
        put(Language.ENGLISH, "stadium.availability",
                "Availability");
        put(Language.ENGLISH, "stadium.allEventsOpen",
                "Every event here is open for booking.");
        put(Language.ENGLISH, "stadium.someEventsClosed",
                "event(s) cannot be booked");
        put(Language.ENGLISH, "stadium.someEventsClosedBecause",
                "because of a cancellation or an emergency notice, and are marked on the schedule.");
        put(Language.ENGLISH, "stadium.noticesPosted",
                "posted");
        put(Language.ENGLISH, "stadium.noticesHint",
                "Cancellations and emergency updates");
        put(Language.ENGLISH, "stadium.noActiveNotices",
                "No active notices. Special requests can still be sent to the venue team.");
        put(Language.ENGLISH, "stadium.moreNotices",
                "additional notice(s) available at the venue desk.");
        put(Language.ENGLISH, "seatMap.sections",
                "Seat sections");
        put(Language.ENGLISH, "seatMap.sectionsHint",
                "Four seating sections. Use the arrow keys to move between seats ");
        put(Language.ENGLISH, "seatMap.sectionsHintEnd",
                "and Enter to hold one.");
        put(Language.ENGLISH, "seatMap.keyUp",
                "UP");
        put(Language.ENGLISH, "seatMap.keyDown",
                "DOWN");
        put(Language.ENGLISH, "seatMap.keyLeft",
                "LEFT");
        put(Language.ENGLISH, "seatMap.keyRight",
                "RIGHT");
        put(Language.ENGLISH, "seatMap.keyEnter",
                "ENTER");
        put(Language.ENGLISH, "seatMap.keySpace",
                "SPACE");
        put(Language.ENGLISH, "seatMap.seatDesc",
                "Section %s %s, row %d, seat %d, %s, %s");
        put(Language.ENGLISH, "seatMap.seat",
                "Seat");
        put(Language.ENGLISH, "seatMap.is",
                "is");
        put(Language.ENGLISH, "seatMap.heldByOther",
                "is being held by another customer for a few more minutes");
        put(Language.ENGLISH, "seatMap.removed",
                "Removed");
        put(Language.ENGLISH, "seatMap.upTo",
                "Up to");
        put(Language.ENGLISH, "seatMap.perReservation",
                "seats per reservation; booking count is unlimited per person");
        put(Language.ENGLISH, "seatMap.heldForYou",
                "Held for you");
        put(Language.ENGLISH, "seatMap.booked",
                "Booked");
        put(Language.ENGLISH, "seatMap.unavailable",
                "Unavailable");
        put(Language.ENGLISH, "seatMap.selected",
                "Selected");
        put(Language.ENGLISH, "seatMap.held",
                "Held");
        put(Language.ENGLISH, "seatMap.vacant",
                "Vacant");
        put(Language.ENGLISH, "seatMap.pricesFall",
                "Prices fall from front to back");
        put(Language.ENGLISH, "seatMap.frontFrom",
                "Front from %s  •  Back from");
        put(Language.ENGLISH, "seatMap.name",
                "Seat map, section");
        put(Language.ENGLISH, "seatMap.nameHint",
                "Arrow keys move between seats, Enter or Space holds the seat.");
        put(Language.ENGLISH, "seatMap.frontPremium",
                "FRONT • PREMIUM");
        put(Language.ENGLISH, "seatMap.middleStandard",
                "MIDDLE • STANDARD");
        put(Language.ENGLISH, "seatMap.backValue",
                "BACK • VALUE");
        put(Language.ENGLISH, "seatMap.circular",
                "CIRCULAR");
        put(Language.ENGLISH, "seatMap.pitchStage",
                "PITCH / STAGE");
        put(Language.ENGLISH, "event.beforeYouBook",
                "Before you book");
        put(Language.ENGLISH, "event.date",
                "Date");
        put(Language.ENGLISH, "event.bookBy",
                "Book by");
        put(Language.ENGLISH, "event.seats",
                "Seats");
        put(Language.ENGLISH, "event.from",
                "From");
        put(Language.ENGLISH, "event.bestSeats",
                "Best seats");
        put(Language.ENGLISH, "event.inSectionARow1",
                "in section A row 1");
        put(Language.ENGLISH, "event.perReservation",
                "per reservation");
        put(Language.ENGLISH, "event.cannotBeBooked",
                "This event cannot be booked.");
        put(Language.ENGLISH, "event.seatsStillAvailable",
                "seats still available");
        put(Language.ENGLISH, "event.noSeatsChosen",
                "No seats chosen yet.");
        put(Language.ENGLISH, "event.heldFor",
                "•  held for");
        put(Language.ENGLISH, "event.noNotices",
                "Notices for this event");
        put(Language.ENGLISH, "event.nothingPosted",
                "Nothing has been posted about this event. The venue's other notices are on its details page.");
        put(Language.ENGLISH, "event.venueNoticesElsewhere",
                "The venue's other notices are on its details page.");
        put(Language.ENGLISH, "event.detailsAndBooking",
                "Details and booking");
        put(Language.ENGLISH, "event.whyItIsClosed",
                "Why it is closed");
        put(Language.ENGLISH, "event.unavailable",
                "Unavailable");
        put(Language.ENGLISH, "event.closed",
                "Closed");
        put(Language.ENGLISH, "event.bookingStopsIn",
                "Booking stops in");
        put(Language.ENGLISH, "event.eventCancelled",
                "Event cancelled");
        put(Language.ENGLISH, "event.emergencyNotice",
                "Emergency notice");
        put(Language.ENGLISH, "event.bookingClosed",
                "Booking closed");
        put(Language.ENGLISH, "event.openDetails",
                "Open the details for");
        put(Language.ENGLISH, "event.vacantShort",
                "of seats vacant");
        put(Language.ENGLISH, "event.seatsAndPrice",
                "•  %s seats");
        put(Language.ENGLISH, "event.beginsNow",
                "Booking opens now");
        put(Language.ENGLISH, "event.dateTimeLine",
                "Starts");
        put(Language.ENGLISH, "event.countdownLabel",
                "Booking deadline: ");
        put(Language.ENGLISH, "event.selectFirst",
                "Choose a date and event, then pick your seats at ");
        put(Language.ENGLISH, "booking.confirmSeats",
                "Confirm");
        put(Language.ENGLISH, "booking.needSeatToConfirm",
                "Choose at least one seat before confirming.");
        put(Language.ENGLISH, "booking.continue",
                "Continue");
        put(Language.ENGLISH, "booking.continueAndConfirm",
                "Continue and confirm");
        put(Language.ENGLISH, "booking.bookYour",
                "Book your");
        put(Language.ENGLISH, "booking.continuePrompt",
                "for");
        put(Language.ENGLISH, "booking.thenShowReceipt",
                ", then show your receipt");
        put(Language.ENGLISH, "booking.totalFor",
                "Total for");
        put(Language.ENGLISH, "booking.plusAFee",
                "plus a");
        put(Language.ENGLISH, "booking.ticketingFee",
                "ticketing fee");
        put(Language.ENGLISH, "booking.chooseFirst",
                "Choose one or more seats, then confirm here");
        put(Language.ENGLISH, "booking.about",
                "About");
        put(Language.ENGLISH, "booking.seeAllSchedules",
                "See all live schedules");
        put(Language.ENGLISH, "booking.saveHintLead",
                "Keep these seats and choose them later. ");
        put(Language.ENGLISH, "booking.saveHintEnd",
                "This does not book or hold them");
        put(Language.ENGLISH, "booking.needSeatToSave",
                "Choose at least one seat before saving it.");
        put(Language.ENGLISH, "booking.mySeats",
                "My seats");
        put(Language.ENGLISH, "booking.labelHint",
                "Label for this selection");
        put(Language.ENGLISH, "booking.labelHintDesc",
                "characters. Something you will ");
        put(Language.ENGLISH, "booking.labelHintDescEnd",
                "recognise later.");
        put(Language.ENGLISH, "booking.labelPrompt",
                "Call it something you will recognise");
        put(Language.ENGLISH, "booking.saveTheseSeats",
                "Save these seats for later");
        put(Language.ENGLISH, "booking.saved",
                "Saved");
        put(Language.ENGLISH, "booking.findUnderSaved",
                "•  find them under Saved seats");
        put(Language.ENGLISH, "booking.seatWord",
                "seat");
        put(Language.ENGLISH, "booking.seatsWord",
                "seats");
        put(Language.ENGLISH, "booking.notBookedYet",
                "These seats are not booked yet.");
        put(Language.ENGLISH, "booking.notBookedYetBody",
                "Saving them keeps your choice so you can come back later, but it does not hold them and does not sell them. Somebody else may take them, and this page will say so when that happens.");
        put(Language.ENGLISH, "booking.nothingSaved",
                "Nothing saved yet.");
        put(Language.ENGLISH, "booking.nothingSavedBody",
                "Pick the seats you want on an event, then press Save these seats on the booking screen. They will be here next time you open the application.");
        put(Language.ENGLISH, "booking.findEventToBook",
                "Find an event to book");
        put(Language.ENGLISH, "booking.cannotBeBookedPrefix",
                "Cannot be booked:");
        put(Language.ENGLISH, "booking.resumeAndBook",
                "Resume and book");
        put(Language.ENGLISH, "booking.pickedUp",
                "Picked up");
        put(Language.ENGLISH, "booking.savedSeatWord",
                "saved seat");
        put(Language.ENGLISH, "booking.discardQuestion",
                "Discard \"%s\"?");
        put(Language.ENGLISH, "booking.discardBody",
                "The saved seats are forgotten. Nothing was booked, so nothing is charged and the seats go back on sale.");
        put(Language.ENGLISH, "booking.discardSavedTitle",
                "Discard saved seats");
        put(Language.ENGLISH, "booking.discardSaved",
                "Discard saved seats");
        put(Language.ENGLISH, "booking.discarded",
                "Saved seats discarded");
        put(Language.ENGLISH, "booking.couldNotDiscard",
                "That selection could not be discarded. Please try again.");
        put(Language.ENGLISH, "booking.viewing",
                "Viewing the");
        put(Language.ENGLISH, "booking.viewingSchedule",
                "schedule");
        put(Language.ENGLISH, "booking.seatSelectionCleared",
                "Seat selection cleared");
        put(Language.ENGLISH, "booking.selectedSeat",
                "Selected seat");
        put(Language.ENGLISH, "booking.removedSeat",
                "Removed seat");
        put(Language.ENGLISH, "booking.seeEverySeatTaken",
                "See every seat already taken for this event");
        put(Language.ENGLISH, "booking.bySection",
                "By section:  ");
        put(Language.ENGLISH, "schedules.title",
                "Live schedules");
        put(Language.ENGLISH, "schedules.subtitle",
                "Every upcoming game and concert at Namboole, soonest first.");
        put(Language.ENGLISH, "schedules.allUpcoming",
                "All upcoming events");
        put(Language.ENGLISH, "schedules.soonestFirst",
                "Soonest first, every game and concert at Namboole. Pick an event to choose seats.");
        put(Language.ENGLISH, "schedules.allTypes",
                "All types");
        put(Language.ENGLISH, "schedules.games",
                "Games");
        put(Language.ENGLISH, "schedules.concerts",
                "Concerts");
        put(Language.ENGLISH, "schedules.dateColumn",
                "Date");
        put(Language.ENGLISH, "schedules.typeColumn",
                "Type");
        put(Language.ENGLISH, "schedules.allDates",
                "All dates");
        put(Language.ENGLISH, "schedules.today",
                "Today");
        put(Language.ENGLISH, "schedules.tomorrow",
                "Tomorrow");
        put(Language.ENGLISH, "schedules.noMatch",
                "No events match");
        put(Language.ENGLISH, "schedules.tryDifferent",
                "Try a different date, type or search word.");
        put(Language.ENGLISH, "schedules.eventWord",
                "event");
        put(Language.ENGLISH, "schedules.eventsWord",
                "events");
        put(Language.ENGLISH, "schedules.comingUpAt",
                "coming up at");
        put(Language.ENGLISH, "schedules.matching",
                "•  matching \"");
        put(Language.ENGLISH, "schedules.matchCount",
                "matches");
        put(Language.ENGLISH, "schedules.teamOrArtist",
                "Team or artist");
        put(Language.ENGLISH, "schedules.teamOrArtistHint",
                "Team or artist appearing at a venue");
        put(Language.ENGLISH, "schedules.enterForList",
                "Press Enter to see the full list");
        put(Language.ENGLISH, "schedules.doors",
                "Doors");
        put(Language.ENGLISH, "search.stadium",
                "Stadium");
        put(Language.ENGLISH, "search.stadiumHint",
                "Search teams, artists, venues or dates");
        put(Language.ENGLISH, "search.venue",
                "venue");
        put(Language.ENGLISH, "search.venues",
                "venues");
        put(Language.ENGLISH, "search.matches",
                "venue matches");
        put(Language.ENGLISH, "search.match",
                "venues match");
        put(Language.ENGLISH, "search.booking",
                "booking");
        put(Language.ENGLISH, "search.bookings",
                "bookings");
        put(Language.ENGLISH, "search.bookingMatches",
                "booking matches");
        put(Language.ENGLISH, "search.bookingMatch",
                "bookings match");
        put(Language.ENGLISH, "search.seatsHint",
                "Search reference, stadium, event or status");
        put(Language.ENGLISH, "search.seats",
                "Booked seats");
        put(Language.ENGLISH, "search.bookingTitleHint",
                "Search");
        put(Language.ENGLISH, "search.enterToChoose",
                "Type to search. Use the arrow keys and Enter to choose a suggestion.");
        put(Language.ENGLISH, "search.noMatches",
                "No matches");
        put(Language.ENGLISH, "search.tryDifferentWord",
                "Try a different word");
        put(Language.ENGLISH, "bookings.emptyLead",
                "Every reservation at");
        put(Language.ENGLISH, "bookings.emptyTail",
                ". Open one to see its seats, its charges and its receipt.");
        put(Language.ENGLISH, "bookings.exportHint",
                "Write every booking to a spreadsheet file");
        put(Language.ENGLISH, "bookings.selectFirst",
                "Select a booking from the table first.");
        put(Language.ENGLISH, "bookings.alreadyCancelled",
                "That booking is already cancelled.");
        put(Language.ENGLISH, "bookings.cancelTitle",
                "Cancel booking");
        put(Language.ENGLISH, "booking.cancelQuestion",
                "Cancel booking %s?");
        put(Language.ENGLISH, "bookings.cancelBody",
                "The seats will become available again.");
        put(Language.ENGLISH, "bookings.cancelled",
                "cancelled");
        put(Language.ENGLISH, "bookings.couldNotCancel",
                "The booking could not be cancelled.");
        put(Language.ENGLISH, "bookings.legacy",
                "Legacy venue");
        put(Language.ENGLISH, "bookings.table.reference",
                "Reference");
        put(Language.ENGLISH, "bookings.table.status",
                "Status");
        put(Language.ENGLISH, "bookings.table.created",
                "Created");
        put(Language.ENGLISH, "bookings.section.venue",
                "Venue");
        put(Language.ENGLISH, "bookings.section.legacy",
                "Legacy venue");
        put(Language.ENGLISH, "bookings.section.location",
                "Location");
        put(Language.ENGLISH, "bookings.section.address",
                "Address");
        put(Language.ENGLISH, "bookings.section.capacity",
                "Capacity");
        put(Language.ENGLISH, "bookings.section.bookedBy",
                "BOOKED BY");
        put(Language.ENGLISH, "bookings.section.paid",
                "SEATS AND PAYMENT");
        put(Language.ENGLISH, "bookings.field.fullName",
                "Full name");
        put(Language.ENGLISH, "bookings.field.email",
                "Email");
        put(Language.ENGLISH, "bookings.field.phone",
                "Phone");
        put(Language.ENGLISH, "bookings.field.bookedSeats",
                "Booked seats");
        put(Language.ENGLISH, "bookings.field.seatCount",
                "Seat count");
        put(Language.ENGLISH, "bookings.field.totalCharged",
                "Total charged");
        put(Language.ENGLISH, "bookings.viewReceipt",
                "View itemised receipt");
        put(Language.ENGLISH, "bookings.completeDetails",
                "Complete booking details");
        put(Language.ENGLISH, "payment.simulated",
                "•  mobile money is simulated, no money moves");
        put(Language.ENGLISH, "payment.line",
                "  Payment: ");
        put(Language.ENGLISH, "receipt.title",
                "Receipt");
        put(Language.ENGLISH, "receipt.save",
                "Save receipt");
        put(Language.ENGLISH, "receipt.savedTo",
                "Receipt saved to");
        put(Language.ENGLISH, "receipt.needsBooking",
                "A receipt needs a booking");
        put(Language.ENGLISH, "receipt.section",
                "Section");
        put(Language.ENGLISH, "receipt.filePrefix",
                "receipt-");
        put(Language.ENGLISH, "ticket.title",
                "Ticket");
        put(Language.ENGLISH, "ticket.viewReceipt",
                "View receipt");
        put(Language.ENGLISH, "ticket.saveAsText",
                "Save as text");
        put(Language.ENGLISH, "ticket.savedTo",
                "Ticket saved to");
        put(Language.ENGLISH, "ticket.filePrefix",
                "ticket-");
        put(Language.ENGLISH, "ticket.couldNotSave",
                "That could not be saved: ");
        put(Language.ENGLISH, "print.sentToPrinter",
                "sent to the printer");
        put(Language.ENGLISH, "export.noneYet",
                "There are no bookings to export yet.");
        put(Language.ENGLISH, "export.done",
                "bookings to");
        put(Language.ENGLISH, "export.couldNotWrite",
                "The export could not be written: ");
        put(Language.ENGLISH, "occupancy.allEvents",
                "All events");
        put(Language.ENGLISH, "occupancy.savedTo",
                "Occupancy report saved to");
        put(Language.ENGLISH, "occupancy.couldNotSave",
                "The report could not be saved: ");
        put(Language.ENGLISH, "occupancy.vacancy",
                "Vacancy");
        put(Language.ENGLISH, "occupancy.seatsBooked",
                "Seats booked");
        put(Language.ENGLISH, "occupancy.seatsOnSale",
                "Seats on sale");
        put(Language.ENGLISH, "ledger.seat",
                "Seat");
        put(Language.ENGLISH, "ledger.section",
                "Section");
        put(Language.ENGLISH, "ledger.number",
                "Number");
        put(Language.ENGLISH, "ledger.priceTier",
                "Price tier");
        put(Language.ENGLISH, "ledger.price",
                "Price");
        put(Language.ENGLISH, "ledger.bookedBy",
                "Booked by");
        put(Language.ENGLISH, "ledger.bookedOn",
                "Booked on");
        put(Language.ENGLISH, "ledger.bookedOf",
                " booked of ");
        put(Language.ENGLISH, "ledger.percentVacant",
                "% vacant  •  ");
        put(Language.ENGLISH, "request.typeCancellation",
                "Event cancellation");
        put(Language.ENGLISH, "request.typeEmergency",
                "Stadium emergency");
        put(Language.ENGLISH, "request.typeSafety",
                "Safety notice");
        put(Language.ENGLISH, "request.typeAccess",
                "Accessibility request");
        put(Language.ENGLISH, "request.typeOther",
                "Other request");
        put(Language.ENGLISH, "request.minLength",
                "characters, so the venue team knows what you need.");
        put(Language.ENGLISH, "request.submitTo",
                "Submit a special request to");
        put(Language.ENGLISH, "request.submit",
                "Submit request");
        put(Language.ENGLISH, "request.submittedTo",
                "Special request submitted to");
        put(Language.ENGLISH, "request.pickVenue",
                "Choose a valid stadium");
        put(Language.ENGLISH, "form.heading",
                "Your details, so the booking is yours");
        put(Language.ENGLISH, "form.name",
                "Name");
        put(Language.ENGLISH, "form.nameHint",
                "Your full name");
        put(Language.ENGLISH, "form.emailHint",
                "Email address");
        put(Language.ENGLISH, "form.phoneHint",
                "Phone number");
        put(Language.ENGLISH, "form.nameExample",
                "For example: Amina Okello");
        put(Language.ENGLISH, "form.emailExample",
                "For example: amina@example.co.ug");
        put(Language.ENGLISH, "form.phoneExample",
                "For example: +256 700 123 456");
        put(Language.ENGLISH, "form.labelName",
                "Name");
        put(Language.ENGLISH, "form.labelEmail",
                "Email");
        put(Language.ENGLISH, "form.labelPhone",
                "Phone");
        put(Language.ENGLISH, "form.hintName",
                "Full name of the person booking, at least two characters");
        put(Language.ENGLISH, "form.hintEmail",
                "Email address used for the booking");
        put(Language.ENGLISH, "form.hintPhone",
                "Phone number, digits, spaces and an optional leading plus");
        put(Language.ENGLISH, "form.email",
                "Email address used for the booking");
        put(Language.ENGLISH, "form.phone",
                "Phone number, digits, spaces and an optional leading plus");
        put(Language.ENGLISH, "status.checkBooking",
                "Please check your booking");
        put(Language.ENGLISH, "status.pressed",
                "pressed");
        put(Language.ENGLISH, "status.hover",
                "Hover: ");
        put(Language.ENGLISH, "status.clicking",
                "Clicking: ");
        put(Language.ENGLISH, "status.areYouSure",
                "are you sure?");
        put(Language.ENGLISH, "status.cancelled",
                "CANCELLED");
        put(Language.ENGLISH, "theme.switchToLight",
                "Switch to the light theme");
        put(Language.ENGLISH, "theme.switchToDark",
                "Switch to the dark theme");
        put(Language.ENGLISH, "theme.currentlyDark",
                "Currently the dark theme. Activate for the light theme.");
        put(Language.ENGLISH, "theme.currentlyLight",
                "Currently the light theme. Activate for the dark theme.");
        put(Language.ENGLISH, "theme.darkOn",
                "Dark mode on");
        put(Language.ENGLISH, "theme.lightOn",
                "Light mode on");
        put(Language.ENGLISH, "theme.darkLabel",
                "Dark");
        put(Language.ENGLISH, "theme.lightLabel",
                "Light");
        put(Language.ENGLISH, "theme.darkName",
                "Dark mode");
        put(Language.ENGLISH, "photo.photograph",
                "Photograph");
        put(Language.ENGLISH, "photo.aerialPlan",
                "Aerial seating plan");
        put(Language.ENGLISH, "photo.addFile",
                "add photos/%s.jpg for a photograph");
        put(Language.ENGLISH, "error.needSeatToSave",
                "Choose at least one seat to save");
        put(Language.ENGLISH, "error.eventGone",
                "That event is no longer on the schedule.");
        put(Language.ENGLISH, "error.venueGone",
                "That venue is no longer available.");
        put(Language.ENGLISH, "error.bookingClosed",
                "Booking is closed for this event");
        put(Language.ENGLISH, "error.nameTooShort",
                "Please enter your name");
        put(Language.ENGLISH, "error.emailInvalid",
                "Please enter a valid email address");
        put(Language.ENGLISH, "error.phoneInvalid",
                "Please enter a valid phone number");

        put(Language.LUGANDA, "common.ok",
                "Kikwateekero");
        put(Language.LUGANDA, "common.discardThem",
                "Sula byo");
        put(Language.LUGANDA, "common.keepThem",
                "Bikira byo");
        put(Language.LUGANDA, "nav.back.desc",
                "Doola ku ekizimbe ekyaliwo");
        put(Language.LUGANDA, "nav.venues.desc",
                "Amabirali, lutundu lwawo n'ebikwata byawo");
        put(Language.LUGANDA, "nav.bookings.desc",
                "Loola ekikwata ku email oba essimu eri ku kiso");
        put(Language.LUGANDA, "nav.bookedSeats.desc",
                "Loola ebizitansa ebikwata buli ekizibuBy'obujjuni");
        put(Language.LUGANDA, "nav.occupancy.desc",
                "Loola nga ekizimbe kizimbe kiwe nga kizimbe buli nsengwe");
        put(Language.LUGANDA, "nav.saved.desc",
                "Ebizitansa byo oyaliwo, wasobola okuzyoka byebuke");
        put(Language.LUGANDA, "home.howItWorks",
                "NG'OKUNOBA");
        put(Language.LUGANDA, "home.step1",
                "Loola ekizibuBy'obujjuni");
        put(Language.LUGANDA, "home.step2",
                "Lunguza ebizitansa byo");
        put(Language.LUGANDA, "home.step3",
                "Noola era ghiriza kasiita y'ekikwata");
        put(Language.LUGANDA, "home.step4",
                "Yoloka kasiita y'ekikwata");
        put(Language.LUGANDA, "home.nextUp",
                "Ekiruukira");
        put(Language.LUGANDA, "home.noEvents",
                "Tewali mizibiso etandikwa hano.");
        put(Language.LUGANDA, "home.capacity",
                "Bujja");
        put(Language.LUGANDA, "home.capacityHint",
                "namba eyakubikulwa");
        put(Language.LUGANDA, "home.shape",
                "Bujja");
        put(Language.LUGANDA, "home.shapeOval",
                "ekizimbe eky'olukalu");
        put(Language.LUGANDA, "home.shapeStraight",
                "nsengwe nna");
        put(Language.LUGANDA, "home.seatPrice",
                "Ezimbe ly'ekizitanse");
        put(Language.LUGANDA, "home.seatPriceHint",
                "kikumi");
        put(Language.LUGANDA, "home.feeHint",
                "ada");
        put(Language.LUGANDA, "home.onSaleNow",
                "Baliwo kaano");
        put(Language.LUGANDA, "home.vacantPercent",
                "% bwe muganda");
        put(Language.LUGANDA, "home.aboutVenue",
                "Ebikwata ekizimbe kino");
        put(Language.LUGANDA, "facts.where",
                "Egga");
        put(Language.LUGANDA, "facts.city",
                "Ekibuga");
        put(Language.LUGANDA, "facts.type",
                "Kind");
        put(Language.LUGANDA, "facts.footprint",
                "Obudde");
        put(Language.LUGANDA, "facts.seatsOnSale",
                "Ebizitansa ebiri ku katalo");
        put(Language.LUGANDA, "facts.sections",
                "Nsengwe");
        put(Language.LUGANDA, "facts.independent",
                "eziyi");
        put(Language.LUGANDA, "facts.perEvent",
                "kwa buli ekizibuBy'obujjuni");
        put(Language.LUGANDA, "stadium.whatIsOn",
                "Ebigambiro bino");
        put(Language.LUGANDA, "stadium.gamesAndConcerts",
                "emizibiso,");
        put(Language.LUGANDA, "stadium.concerts",
                "emuzeemu");
        put(Language.LUGANDA, "stadium.clubsAndTeams",
                "Ebuga n'eb teams:");
        put(Language.LUGANDA, "stadium.artists",
                "Abasinganyi:");
        put(Language.LUGANDA, "stadium.availability",
                "Ebya nokusinga");
        put(Language.LUGANDA, "stadium.allEventsOpen",
                "Biro byonna ebiri hano bisobola okutandikibwa.");
        put(Language.LUGANDA, "stadium.someEventsClosed",
                "ekizibuBy'obujjuni kisobola okutandikibwa");
        put(Language.LUGANDA, "stadium.someEventsClosedBecause",
                "olw'emuk cancellation oba olw'ebibikkulo by'okudda, era bisumulukwa ku lutundu.");
        put(Language.LUGANDA, "stadium.noticesPosted",
                "ebigambiro");
        put(Language.LUGANDA, "stadium.noticesHint",
                "Emutunda n'ebibikkulo");
        put(Language.LUGANDA, "stadium.noActiveNotices",
                "Tewali ebikwata bikola. Kusaba okusobola kumwetoolooda.");
        put(Language.LUGANDA, "stadium.moreNotices",
                "ebikwata ebirala ebiri ku desk.");
        put(Language.LUGANDA, "seatMap.sections",
                "Nsengwe z'ebizitansa");
        put(Language.LUGANDA, "seatMap.sectionsHint",
                "Nsengwe nna ezitindikwa. Kigyezi ebisobola okusobola okutambula ");
        put(Language.LUGANDA, "seatMap.sectionsHintEnd",
                "era Enter okuyimiriza kimu.");
        put(Language.LUGANDA, "seatMap.keyUp",
                "WANNUNDA");
        put(Language.LUGANDA, "seatMap.keyDown",
                "WASSI");
        put(Language.LUGANDA, "seatMap.keyLeft",
                "KUMARO");
        put(Language.LUGANDA, "seatMap.keyRight",
                "KUMALIRO");
        put(Language.LUGANDA, "seatMap.keyEnter",
                "ENTER");
        put(Language.LUGANDA, "seatMap.keySpace",
                "NAFAAKA");
        put(Language.LUGANDA, "seatMap.seatDesc",
                "Nsengwe %s %s, olulungi %d, ekizitanse %d, %s, %s");
        put(Language.LUGANDA, "seatMap.seat",
                "Kizitanse");
        put(Language.LUGANDA, "seatMap.is",
                "kikyo");
        put(Language.LUGANDA, "seatMap.heldByOther",
                "kirimidwa by'omuntu omu kuliko minnete");
        put(Language.LUGANDA, "seatMap.removed",
                "Gikyali");
        put(Language.LUGANDA, "seatMap.upTo",
                "Kisinga ku");
        put(Language.LUGANDA, "seatMap.perReservation",
                "kwa nkwaateeka; ekitundu temuli ku muntu");
        put(Language.LUGANDA, "seatMap.heldForYou",
                "Kirimidwa ku lwo");
        put(Language.LUGANDA, "seatMap.booked",
                "Kikwata");
        put(Language.LUGANDA, "seatMap.unavailable",
                "Tewali");
        put(Language.LUGANDA, "seatMap.selected",
                "Erisobola");
        put(Language.LUGANDA, "seatMap.held",
                "Kirimidwa");
        put(Language.LUGANDA, "seatMap.vacant",
                "Bwe muganda");
        put(Language.LUGANDA, "seatMap.pricesFall",
                "Ebisoboka bilisa okuva ebisajja nokuuma emaago");
        put(Language.LUGANDA, "seatMap.frontFrom",
                "Ebisajja okuva %s  •  Emyaago okuva");
        put(Language.LUGANDA, "seatMap.name",
                "Kuwoko ku bibizitansa, nsengwe");
        put(Language.LUGANDA, "seatMap.nameHint",
                "Vishale vya mishale bimutula, Enter oba Nafaaka bikirimiza kizitanse.");
        put(Language.LUGANDA, "seatMap.frontPremium",
                "EBISAJJA • NKO");
        put(Language.LUGANDA, "seatMap.middleStandard",
                "EKATAKATO • NORMAL");
        put(Language.LUGANDA, "seatMap.backValue",
                "EMYAAGO • OLUYIMU");
        put(Language.LUGANDA, "seatMap.circular",
                "LUZUNGA");
        put(Language.LUGANDA, "seatMap.pitchStage",
                "KABBULA / SAAZINI");
        put(Language.LUGANDA, "event.beforeYouBook",
                "Nga tannaku ekikwata");
        put(Language.LUGANDA, "event.date",
                "Olunaku");
        put(Language.LUGANDA, "event.bookBy",
                "Weesha bya");
        put(Language.LUGANDA, "event.seats",
                "Ebizitansa");
        put(Language.LUGANDA, "event.from",
                "Okuva");
        put(Language.LUGANDA, "event.bestSeats",
                "Ebizitansa ebisinga oba");
        put(Language.LUGANDA, "event.inSectionARow1",
                "mu nsengwe A olulungi 1");
        put(Language.LUGANDA, "event.perReservation",
                "kwa nkwaateeka");
        put(Language.LUGANDA, "event.cannotBeBooked",
                "EkizibuBy'obujjuni kino kisobola okutandikibwa");
        put(Language.LUGANDA, "event.seatsStillAvailable",
                "ebizitansa ebisobola");
        put(Language.LUGANDA, "event.noSeatsChosen",
                "Tewali kizitanse kisoboledwa.");
        put(Language.LUGANDA, "event.heldFor",
                "•  kirimidwa");
        put(Language.LUGANDA, "event.noNotices",
                "Ebikwata ekizibuBy'obujjuni kino");
        put(Language.LUGANDA, "event.nothingPosted",
                "Tewali kigambiro kya ekizibuBy'obujjuni kino.");
        put(Language.LUGANDA, "event.venueNoticesElsewhere",
                "Ebikwata ebirala bya ekizimbe biri ku lupagana lwawo.");
        put(Language.LUGANDA, "event.detailsAndBooking",
                "Ebisinga n'okutandika");
        put(Language.LUGANDA, "event.whyItIsClosed",
                "Lwaki kimaze");
        put(Language.LUGANDA, "event.unavailable",
                "Tewali");
        put(Language.LUGANDA, "event.closed",
                "Kiwaze");
        put(Language.LUGANDA, "event.bookingStopsIn",
                "Okutandika kuggwa mu");
        put(Language.LUGANDA, "event.eventCancelled",
                "EkizibuBy'obujjuni kimaze");
        put(Language.LUGANDA, "event.emergencyNotice",
                "Olunaku lw'okudda");
        put(Language.LUGANDA, "event.bookingClosed",
                "Okutandika kikaze");
        put(Language.LUGANDA, "event.openDetails",
                "Bikira ebisinga");
        put(Language.LUGANDA, "event.vacantShort",
                "ebizitansa bwe muganda");
        put(Language.LUGANDA, "event.seatsAndPrice",
                "•  ebizitansa %s");
        put(Language.LUGANDA, "event.beginsNow",
                "Okutandika katandika kaano");
        put(Language.LUGANDA, "event.dateTimeLine",
                "Tandika");
        put(Language.LUGANDA, "event.countdownLabel",
                "Olunaku lwokuggwa: ");
        put(Language.LUGANDA, "event.selectFirst",
                "Lunguza olunaku n'ekizibuBy'obujjuni, olwoko lumbire ebizitansa byo ku ");
        put(Language.LUGANDA, "booking.confirmSeats",
                "Kakasa");
        put(Language.LUGANDA, "booking.needSeatToConfirm",
                "Lunguza kizitanse kimu n'obwerali okukakasa.");
        put(Language.LUGANDA, "booking.continue",
                "Kandika");
        put(Language.LUGANDA, "booking.continueAndConfirm",
                "Kandika n'okukakasa");
        put(Language.LUGANDA, "booking.bookYour",
                "Yatandika ekizibuBy'obujjuni");
        put(Language.LUGANDA, "booking.continuePrompt",
                "lwaki");
        put(Language.LUGANDA, "booking.thenShowReceipt",
                ", olwoko olokezza kasiita yo");
        put(Language.LUGANDA, "booking.totalFor",
                "Jigger kwa");
        put(Language.LUGANDA, "booking.plusAFee",
                "kikumi");
        put(Language.LUGANDA, "booking.ticketingFee",
                "ada y'okutandika");
        put(Language.LUGANDA, "booking.chooseFirst",
                "Lunguza kizitanse kimu oba ebisinga, olwoko kakasa hano");
        put(Language.LUGANDA, "booking.about",
                "Ebikwata");
        put(Language.LUGANDA, "booking.seeAllSchedules",
                "Loola emizibiso yonna");
        put(Language.LUGANDA, "booking.saveHintLead",
                "Giriza ebizitansa bino era obibikkule buli lero. ");
        put(Language.LUGANDA, "booking.saveHintEnd",
                "Kino kisobola n'okutandika");
        put(Language.LUGANDA, "booking.needSeatToSave",
                "Lunguza kizitanse kimu n'obwerali okugiriza.");
        put(Language.LUGANDA, "booking.mySeats",
                "Ebizitansa bya nte");
        put(Language.LUGANDA, "booking.labelHint",
                "Amatundu ga ekisubizo kino");
        put(Language.LUGANDA, "booking.labelHintDesc",
                "ebimyango. Ekintu kisobola okutwala ");
        put(Language.LUGANDA, "booking.labelHintDescEnd",
                "mule.");
        put(Language.LUGANDA, "booking.labelPrompt",
                "Muganda ku ekintu kisobola okumanya");
        put(Language.LUGANDA, "booking.saveTheseSeats",
                "Ghiriza ebizitansa bino");
        put(Language.LUGANDA, "booking.saved",
                "Ekigiriwako");
        put(Language.LUGANDA, "booking.findUnderSaved",
                "•  boola ku Ebizitansa ebikumi");
        put(Language.LUGANDA, "booking.seatWord",
                "kizitanse");
        put(Language.LUGANDA, "booking.seatsWord",
                "ebizitansa");
        put(Language.LUGANDA, "booking.notBookedYet",
                "Ebizitansa bino si(byakibwa)");
        put(Language.LUGANDA, "booking.notBookedYetBody",
                "Okugiriza bikubikira okusubiza, naye tetwerako okubikkula.");
        put(Language.LUGANDA, "booking.nothingSaved",
                "Tewali kigiriwako.");
        put(Language.LUGANDA, "booking.nothingSavedBody",
                "Lunguza ebizitansa, olwoko geriiza ku ekizimbe.");
        put(Language.LUGANDA, "booking.findEventToBook",
                "Loola ekizibuBy'obujjuni ekya kutandika");
        put(Language.LUGANDA, "booking.cannotBeBookedPrefix",
                "Kisobola n'okutandika:");
        put(Language.LUGANDA, "booking.resumeAndBook",
                "Kandika n'okutandika");
        put(Language.LUGANDA, "booking.pickedUp",
                "Waggya");
        put(Language.LUGANDA, "booking.savedSeatWord",
                "kizitanse ekikumi");
        put(Language.LUGANDA, "booking.discardQuestion",
                "Sula \"%s\"?");
        put(Language.LUGANDA, "booking.discardBody",
                "Ebizitansa ebikumi bibyibulwa. Tewali ekikwata, naye tetwebwa ada.");
        put(Language.LUGANDA, "booking.discardSavedTitle",
                "Sula ebizitansa ebikumi");
        put(Language.LUGANDA, "booking.discardSaved",
                "Sula ebizitansa ebikumi");
        put(Language.LUGANDA, "booking.discarded",
                "Ebizitansa ebikumi bisulidwa");
        put(Language.LUGANDA, "booking.couldNotDiscard",
                "Ekisubizo kino kisobola n'okusula. Nkusaba ko omukeero.");
        put(Language.LUGANDA, "booking.viewing",
                "Loozebwa");
        put(Language.LUGANDA, "booking.viewingSchedule",
                "lutundu");
        put(Language.LUGANDA, "booking.seatSelectionCleared",
                "Okusooka kwebizitansa kuyooka");
        put(Language.LUGANDA, "booking.selectedSeat",
                "Kizitanse ekisoboledwa");
        put(Language.LUGANDA, "booking.removedSeat",
                "Kizitanse ekikyali");
        put(Language.LUGANDA, "booking.seeEverySeatTaken",
                "Loola buli kizitanse ekikwata ku ekizibuBy'obujjuni kino");
        put(Language.LUGANDA, "booking.bySection",
                "Ku nsengwe:  ");
        put(Language.LUGANDA, "schedules.title",
                "Emizibiso ebiwangako");
        put(Language.LUGANDA, "schedules.subtitle",
                "Biro n'emuzeemu byonna ebiri Namboole, ebikkumi n'obudde.");
        put(Language.LUGANDA, "schedules.allUpcoming",
                "Biro byonna ebiri");
        put(Language.LUGANDA, "schedules.soonestFirst",
                "Ebikkumi n'obudde, emizibiso n'emuzeemu byonna. Lunja ekizibuBy'obujjuni.");
        put(Language.LUGANDA, "schedules.allTypes",
                "Byonna ebika");
        put(Language.LUGANDA, "schedules.games",
                "Emizibiso");
        put(Language.LUGANDA, "schedules.concerts",
                "Emuzeemu");
        put(Language.LUGANDA, "schedules.dateColumn",
                "Olunaku");
        put(Language.LUGANDA, "schedules.typeColumn",
                "Kind");
        put(Language.LUGANDA, "schedules.allDates",
                "Bonna ebiro");
        put(Language.LUGANDA, "schedules.today",
                "Leero");
        put(Language.LUGANDA, "schedules.tomorrow",
                "Enkya");
        put(Language.LUGANDA, "schedules.noMatch",
                "Tewali ekizibuBy'obujjuni ekisobola");
        put(Language.LUGANDA, "schedules.tryDifferent",
                "Geragerako olunaku, kind oba igundu.");
        put(Language.LUGANDA, "schedules.eventWord",
                "ekizibuBy'obujjuni");
        put(Language.LUGANDA, "schedules.eventsWord",
                "emizibiso");
        put(Language.LUGANDA, "schedules.comingUpAt",
                "ebiri ku");
        put(Language.LUGANDA, "schedules.matching",
                "•  ekisobola \"");
        put(Language.LUGANDA, "schedules.matchCount",
                "bisobola");
        put(Language.LUGANDA, "schedules.teamOrArtist",
                "Team oba muzanyi");
        put(Language.LUGANDA, "schedules.teamOrArtistHint",
                "Team oba muzanyi alula ku ekizimbe");
        put(Language.LUGANDA, "schedules.enterForList",
                "Yogera Enter okulaba olunaku");
        put(Language.LUGANDA, "schedules.doors",
                "Emigateeza");
        put(Language.LUGANDA, "search.stadium",
                "Amabirali");
        put(Language.LUGANDA, "search.stadiumHint",
                "Shunja teams, abasinganyi, amabirali obo lunaku");
        put(Language.LUGANDA, "search.venue",
                "ekizimbe");
        put(Language.LUGANDA, "search.venues",
                "amabirali");
        put(Language.LUGANDA, "search.matches",
                "amabirali gasobola");
        put(Language.LUGANDA, "search.match",
                "amabirali gasobola");
        put(Language.LUGANDA, "search.booking",
                "ekikwata");
        put(Language.LUGANDA, "search.bookings",
                "ebikwata");
        put(Language.LUGANDA, "search.bookingMatches",
                "ebikwata bisobola");
        put(Language.LUGANDA, "search.bookingMatch",
                "ebikwata bisobola");
        put(Language.LUGANDA, "search.seatsHint",
                "Shunja kuwoko, ekizimbe, ekizibuBy'obujjuni oba ebika");
        put(Language.LUGANDA, "search.seats",
                "Ebizitansa ebikwata");
        put(Language.LUGANDA, "search.bookingTitleHint",
                "Shunja");
        put(Language.LUGANDA, "search.enterToChoose",
                "Andika okushunja. Kigyezi ebisobola okuyingira, Enter okusala.");
        put(Language.LUGANDA, "search.noMatches",
                "Tewali kisobola");
        put(Language.LUGANDA, "search.tryDifferentWord",
                "Andika akavunju");
        put(Language.LUGANDA, "bookings.emptyLead",
                "Bukwa ekikwata ekiri ku");
        put(Language.LUGANDA, "bookings.emptyTail",
                ". Bikira kimu okulaba ebizitansa n'amada n'kasiita.");
        put(Language.LUGANDA, "bookings.exportHint",
                "Andika buli ekikwata mu kipeekero");
        put(Language.LUGANDA, "bookings.selectFirst",
                "Sala ekikwata mu tabula n'obwerali.");
        put(Language.LUGANDA, "bookings.alreadyCancelled",
                "Ekikwata kino kisalreadyeza.");
        put(Language.LUGANDA, "bookings.cancelTitle",
                "Sikiza ekikwata");
        put(Language.LUGANDA, "booking.cancelQuestion",
                "Sikiza ekikwata %s?");
        put(Language.LUGANDA, "bookings.cancelBody",
                "Ebizitansa biri ku buto.");
        put(Language.LUGANDA, "bookings.cancelled",
                "ekisikizibwa");
        put(Language.LUGANDA, "bookings.couldNotCancel",
                "Ekikwata kisobola n'okusingizibwa.");
        put(Language.LUGANDA, "bookings.legacy",
                "Ekizimbe ekikadde");
        put(Language.LUGANDA, "bookings.table.reference",
                "Kuwoko");
        put(Language.LUGANDA, "bookings.table.status",
                "Ebika");
        put(Language.LUGANDA, "bookings.table.created",
                "Kikolekwa");
        put(Language.LUGANDA, "bookings.section.venue",
                "Ekizimbe");
        put(Language.LUGANDA, "bookings.section.legacy",
                "Ekizimbe ekikadde");
        put(Language.LUGANDA, "bookings.section.location",
                "Ekizimbe");
        put(Language.LUGANDA, "bookings.section.address",
                "Endereza");
        put(Language.LUGANDA, "bookings.section.capacity",
                "Bujja");
        put(Language.LUGANDA, "bookings.section.bookedBy",
                "EKIKWATWA N'");
        put(Language.LUGANDA, "bookings.section.paid",
                "EBIZITANSA N'OKUDULA");
        put(Language.LUGANDA, "bookings.field.fullName",
                "Amanina");
        put(Language.LUGANDA, "bookings.field.email",
                "Email");
        put(Language.LUGANDA, "bookings.field.phone",
                "Essimu");
        put(Language.LUGANDA, "bookings.field.bookedSeats",
                "Ebizitansa ebikwata");
        put(Language.LUGANDA, "bookings.field.seatCount",
                "Omujju");
        put(Language.LUGANDA, "bookings.field.totalCharged",
                "Jigger ekikwattisibwa");
        put(Language.LUGANDA, "bookings.viewReceipt",
                "Loola kasiita y'ebikumi");
        put(Language.LUGANDA, "bookings.completeDetails",
                "Ebisinga by'ekikwata");
        put(Language.LUGANDA, "payment.simulated",
                "•  essimu y'obujjuni alaba, tessuma tetigera");
        put(Language.LUGANDA, "payment.line",
                "  Okudula: ");
        put(Language.LUGANDA, "receipt.title",
                "Kasiita");
        put(Language.LUGANDA, "receipt.save",
                "Ghiriza kasiita");
        put(Language.LUGANDA, "receipt.savedTo",
                "Kasiita yaghirizwa ku");
        put(Language.LUGANDA, "receipt.needsBooking",
                "Kasiita yamala ekikwata");
        put(Language.LUGANDA, "receipt.section",
                "Nsengwe");
        put(Language.LUGANDA, "receipt.filePrefix",
                "kasiita-");
        put(Language.LUGANDA, "ticket.title",
                "Ekwata");
        put(Language.LUGANDA, "ticket.viewReceipt",
                "Loola kasiita");
        put(Language.LUGANDA, "ticket.saveAsText",
                "Ghiriza nkubyoko");
        put(Language.LUGANDA, "ticket.savedTo",
                "Ekwata yaghirizwa ku");
        put(Language.LUGANDA, "ticket.filePrefix",
                "kwata-");
        put(Language.LUGANDA, "ticket.couldNotSave",
                "Kino kisobola n'okugiriza: ");
        put(Language.LUGANDA, "print.sentToPrinter",
                "yatindika ku mashinery");
        put(Language.LUGANDA, "export.noneYet",
                "Tewali ebikwata bya kuumiza.");
        put(Language.LUGANDA, "export.done",
                "ebikwata ku");
        put(Language.LUGANDA, "export.couldNotWrite",
                "Ssimu tetowekeddwa: ");
        put(Language.LUGANDA, "occupancy.allEvents",
                "Biro byonna");
        put(Language.LUGANDA, "occupancy.savedTo",
                "Ebikwata ekigirizwa ku");
        put(Language.LUGANDA, "occupancy.couldNotSave",
                "Ssimu tetowekeddwa: ");
        put(Language.LUGANDA, "occupancy.vacancy",
                "Bwe muganda");
        put(Language.LUGANDA, "occupancy.seatsBooked",
                "Ebizitansa ebikwata");
        put(Language.LUGANDA, "occupancy.seatsOnSale",
                "Ebizitansa ebiri ku katalo");
        put(Language.LUGANDA, "ledger.seat",
                "Kizitanse");
        put(Language.LUGANDA, "ledger.section",
                "Nsengwe");
        put(Language.LUGANDA, "ledger.number",
                "Nomero");
        put(Language.LUGANDA, "ledger.priceTier",
                "Ebika");
        put(Language.LUGANDA, "ledger.price",
                "Bwezu");
        put(Language.LUGANDA, "ledger.bookedBy",
                "Kikwata n'");
        put(Language.LUGANDA, "ledger.bookedOn",
                "Kikwattisibwa ku");
        put(Language.LUGANDA, "ledger.bookedOf",
                " ebigiridwa ku ");
        put(Language.LUGANDA, "ledger.percentVacant",
                "% bwe muganda  •  ");
        put(Language.LUGANDA, "request.typeCancellation",
                "Emuk cancellation");
        put(Language.LUGANDA, "request.typeEmergency",
                "Olunaku lw'ekizimbe");
        put(Language.LUGANDA, "request.typeSafety",
                "Olunaku lw'obutebeera");
        put(Language.LUGANDA, "request.typeAccess",
                "Olusaba l'obunyangamugayo");
        put(Language.LUGANDA, "request.typeOther",
                "Olusaba olunaku");
        put(Language.LUGANDA, "request.minLength",
                "ebimyango, nga team y'ekizimbe okumanya ekyo owagenda.");
        put(Language.LUGANDA, "request.submitTo",
                "Yatindika olusaba ku");
        put(Language.LUGANDA, "request.submit",
                "Yatindika olusaba");
        put(Language.LUGANDA, "request.submittedTo",
                "Olusaba lwatindikwa ku");
        put(Language.LUGANDA, "request.pickVenue",
                "Lunguza ekizimbe");
        put(Language.LUGANDA, "form.heading",
                "Ebikwata byo, nga ekikwata kikwata kikyo");
        put(Language.LUGANDA, "form.name",
                "Amanina");
        put(Language.LUGANDA, "form.nameHint",
                "Amanina yo olu");
        put(Language.LUGANDA, "form.emailHint",
                "Endereza y'email");
        put(Language.LUGANDA, "form.phoneHint",
                "Essimu");
        put(Language.LUGANDA, "form.nameExample",
                "Egzample: Amina Okello");
        put(Language.LUGANDA, "form.emailExample",
                "Egzample: amina@example.co.ug");
        put(Language.LUGANDA, "form.phoneExample",
                "Egzample: +256 700 123 456");
        put(Language.LUGANDA, "form.labelName",
                "Amanina");
        put(Language.LUGANDA, "form.labelEmail",
                "Email");
        put(Language.LUGANDA, "form.labelPhone",
                "Essimu");
        put(Language.LUGANDA, "form.hintName",
                "Amanina omuzimbe akatandika, ebimyango ebiri");
        put(Language.LUGANDA, "form.hintEmail",
                "Email ekozesedwa mu ekikwata");
        put(Language.LUGANDA, "form.hintPhone",
                "Essimu, manumba, spaces n'ek Plus ku ntandikwa");
        put(Language.LUGANDA, "form.email",
                "Email ekozesedwa mu ekikwata");
        put(Language.LUGANDA, "form.phone",
                "Essimu, manumba, spaces n'ek Plus ku ntandikwa");
        put(Language.LUGANDA, "status.checkBooking",
                "Nkusaba ko ocheckye ekikwata");
        put(Language.LUGANDA, "status.pressed",
                "asookedwa");
        put(Language.LUGANDA, "status.hover",
                "Fuumuza: ");
        put(Language.LUGANDA, "status.clicking",
                "Kikwata: ");
        put(Language.LUGANDA, "status.areYouSure",
                "wagomba");
        put(Language.LUGANDA, "status.cancelled",
                "EKISIKIZIBWA");
        put(Language.LUGANDA, "theme.switchToLight",
                "Swoola ku thema ya maawe");
        put(Language.LUGANDA, "theme.switchToDark",
                "Swoola ku thema y'ekiro");
        put(Language.LUGANDA, "theme.currentlyDark",
                "Wekuza thema y'ekiro. Katanda okuyingira thema ya maawe.");
        put(Language.LUGANDA, "theme.currentlyLight",
                "Wekuza thema ya maawe. Katanda okuyingira thema y'ekiro.");
        put(Language.LUGANDA, "theme.darkOn",
                "Thema y'ekiro kutandikwa");
        put(Language.LUGANDA, "theme.lightOn",
                "Thema ya maawe kutandikwa");
        put(Language.LUGANDA, "theme.darkLabel",
                "Ekiro");
        put(Language.LUGANDA, "theme.lightLabel",
                "Maawe");
        put(Language.LUGANDA, "theme.darkName",
                "Thema y'ekiro");
        put(Language.LUGANDA, "photo.photograph",
                "Ekifoto");
        put(Language.LUGANDA, "photo.aerialPlan",
                "Ebbaliwo ekiggya ekizimbe");
        put(Language.LUGANDA, "photo.addFile",
                "yongera photos/%s.jpg");
        put(Language.LUGANDA, "error.needSeatToSave",
                "Lunguza kizitanse kimu");
        put(Language.LUGANDA, "error.eventGone",
                "EkizibuBy'obujjuni kino kisibulukidde ku lutundu.");
        put(Language.LUGANDA, "error.venueGone",
                "Ekizimbe kino kisibulukidde.");
        put(Language.LUGANDA, "error.bookingClosed",
                "Okutandika kikaze");
        put(Language.LUGANDA, "error.nameTooShort",
                "Andika amanina");
        put(Language.LUGANDA, "error.emailInvalid",
                "Andika email esobola");
        put(Language.LUGANDA, "error.phoneInvalid",
                "Andika essimu esobola");

        put(Language.SWAHILI, "common.ok",
                "Sawa");
        put(Language.SWAHILI, "common.discardThem",
                "Tupa");
        put(Language.SWAHILI, "common.keepThem",
                "Weka");
        put(Language.SWAHILI, "nav.back.desc",
                "Rudi kwenye skrini iliyotangulia");
        put(Language.SWAHILI, "nav.venues.desc",
                "Uwanja, ratiba yake na taarifa zake");
        put(Language.SWAHILI, "nav.bookings.desc",
                "Tafuta booking kwa barua pepe au nambari ya simu iliyo kwenye hilo");
        put(Language.SWAHILI, "nav.bookedSeats.desc",
                "Angalia viti vilivyookwa kwa kila tukio");
        put(Language.SWAHILI, "nav.occupancy.desc",
                "Angalia uwanja umejaa kiasi na kila upande umejaa kiasi");
        put(Language.SWAHILI, "nav.saved.desc",
                "Viti ulivyochagua awali na unaweza kurudi kuvizire");
        put(Language.SWAHILI, "home.howItWorks",
                "JINSI INAVYOFANYA KAZI");
        put(Language.SWAHILI, "home.step1",
                "Chagua tukio");
        put(Language.SWAHILI, "home.step2",
                "Chagua viti vyako");
        put(Language.SWAHILI, "home.step3",
                "Lipa na hifadhi risiti yako");
        put(Language.SWAHILI, "home.step4",
                "Onyesha risiti yako");
        put(Language.SWAHILI, "home.nextUp",
                "Ifuatayo");
        put(Language.SWAHILI, "home.noEvents",
                "Hakuna matukio yaliyopangwa hapa bado.");
        put(Language.SWAHILI, "home.capacity",
                "Uwezekano");
        put(Language.SWAHILI, "home.capacityHint",
                "namba iliyochapishwa");
        put(Language.SWAHILI, "home.shape",
                "Mfumo");
        put(Language.SWAHILI, "home.shapeOval",
                "bazi la mzunguko");
        put(Language.SWAHILI, "home.shapeStraight",
                "pande nne za moja kwa moja");
        put(Language.SWAHILI, "home.seatPrice",
                "Bei ya kiti");
        put(Language.SWAHILI, "home.seatPriceHint",
                "zaidi ya");
        put(Language.SWAHILI, "home.feeHint",
                "ada");
        put(Language.SWAHILI, "home.onSaleNow",
                "Ukipo sasa");
        put(Language.SWAHILI, "home.vacantPercent",
                "% hazitoshiwi");
        put(Language.SWAHILI, "home.aboutVenue",
                "Kuhusu uwanja huu");
        put(Language.SWAHILI, "facts.where",
                "Wapi");
        put(Language.SWAHILI, "facts.city",
                "Mji");
        put(Language.SWAHILI, "facts.type",
                "Aina");
        put(Language.SWAHILI, "facts.footprint",
                "Inyakwa");
        put(Language.SWAHILI, "facts.seatsOnSale",
                "Viti vinavyouzwa");
        put(Language.SWAHILI, "facts.sections",
                "Pande");
        put(Language.SWAHILI, "facts.independent",
                "huru");
        put(Language.SWAHILI, "facts.perEvent",
                "kwa kila tukio");
        put(Language.SWAHILI, "stadium.whatIsOn",
                "Kilichomo hapa");
        put(Language.SWAHILI, "stadium.gamesAndConcerts",
                "michezo,");
        put(Language.SWAHILI, "stadium.concerts",
                "tamasha");
        put(Language.SWAHILI, "stadium.clubsAndTeams",
                "Mabano na timu:");
        put(Language.SWAHILI, "stadium.artists",
                "Wasanii:");
        put(Language.SWAHILI, "stadium.availability",
                "Upatikanaji");
        put(Language.SWAHILI, "stadium.allEventsOpen",
                "Matukio yote hapa yanafunguliwa kwa bookings.");
        put(Language.SWAHILI, "stadium.someEventsClosed",
                "tukio haliwezi kuhudhuriwa");
        put(Language.SWAHILI, "stadium.someEventsClosedBecause",
                "kwa sababu ya kufutwa au taarifa ya dharura, na huo vimewekewa alama kwenye ratiba.");
        put(Language.SWAHILI, "stadium.noticesPosted",
                "zilizotolewa");
        put(Language.SWAHILI, "stadium.noticesHint",
                "Kufutwa na taarifa za dharura");
        put(Language.SWAHILI, "stadium.noActiveNotices",
                "Hakuna taarifa zinazotumika. Unaweza bado kutuma maombi kwa timu ya uwanja.");
        put(Language.SWAHILI, "stadium.moreNotices",
                "taarifa zaidi zinapatikana kwa kdesk ya uwanja.");
        put(Language.SWAHILI, "seatMap.sections",
                "Pande za viti");
        put(Language.SWAHILI, "seatMap.sectionsHint",
                "Pande nne za viti. Tumia vishale vya mishale kuhamia kati ya viti ");
        put(Language.SWAHILI, "seatMap.sectionsHintEnd",
                "na Enter kuzishikilia mmoja.");
        put(Language.SWAHILI, "seatMap.keyUp",
                "JUU");
        put(Language.SWAHILI, "seatMap.keyDown",
                "CHINI");
        put(Language.SWAHILI, "seatMap.keyLeft",
                "KUSHOTO");
        put(Language.SWAHILI, "seatMap.keyRight",
                "KULIA");
        put(Language.SWAHILI, "seatMap.keyEnter",
                "ENTER");
        put(Language.SWAHILI, "seatMap.keySpace",
                "NAFAAKA");
        put(Language.SWAHILI, "seatMap.seatDesc",
                "Pande %s %s, safu %d, kiti %d, %s, %s");
        put(Language.SWAHILI, "seatMap.seat",
                "Kiti");
        put(Language.SWAHILI, "seatMap.is",
                "ni");
        put(Language.SWAHILI, "seatMap.heldByOther",
                "kinashikiliwa na mteja mwingine kwa dakika chache zaidi");
        put(Language.SWAHILI, "seatMap.removed",
                "Imeondolewa");
        put(Language.SWAHILI, "seatMap.upTo",
                "Hata");
        put(Language.SWAHILI, "seatMap.perReservation",
                "kwa kila utunuzi; idadi ya bookings haipatikani kwa mtu");
        put(Language.SWAHILI, "seatMap.heldForYou",
                "Imeshikiliwa kwako");
        put(Language.SWAHILI, "seatMap.booked",
                "Imebookwa");
        put(Language.SWAHILI, "seatMap.unavailable",
                "Haipatikani");
        put(Language.SWAHILI, "seatMap.selected",
                "Imechaguliwa");
        put(Language.SWAHILI, "seatMap.held",
                "Imeshikiliwa");
        put(Language.SWAHILI, "seatMap.vacant",
                "Haitumiki");
        put(Language.SWAHILI, "seatMap.pricesFall",
                "Bei zinashuka kutoka mbele hadi nyuma");
        put(Language.SWAHILI, "seatMap.frontFrom",
                "Mbele kuanzia %s  •  Nyuma kuanzia");
        put(Language.SWAHILI, "seatMap.name",
                "Ramani ya viti, pande");
        put(Language.SWAHILI, "seatMap.nameHint",
                "Vishale vya mishale vinahamisha, Enter au Nafaaka vinashikili kiti.");
        put(Language.SWAHILI, "seatMap.frontPremium",
                "MBELE • ZA JU'U");
        put(Language.SWAHILI, "seatMap.middleStandard",
                "WAKATI • KAWAIDA");
        put(Language.SWAHILI, "seatMap.backValue",
                "NYUMA • RAHISI");
        put(Language.SWAHILI, "seatMap.circular",
                "MIZUNGUKO");
        put(Language.SWAHILI, "seatMap.pitchStage",
                "UMEWA / KIWAKO");
        put(Language.SWAHILI, "event.beforeYouBook",
                "Kabla ya kuhudhuria");
        put(Language.SWAHILI, "event.date",
                "Tarehe");
        put(Language.SWAHILI, "event.bookBy",
                "Amrishia");
        put(Language.SWAHILI, "event.seats",
                "Viti");
        put(Language.SWAHILI, "event.from",
                "Kuanzia");
        put(Language.SWAHILI, "event.bestSeats",
                "Viti bora");
        put(Language.SWAHILI, "event.inSectionARow1",
                "katika pande A safu 1");
        put(Language.SWAHILI, "event.perReservation",
                "kwa kila utunuzi");
        put(Language.SWAHILI, "event.cannotBeBooked",
                "Tukio hili haliwezi kuhudhuriwa");
        put(Language.SWAHILI, "event.seatsStillAvailable",
                "viti bado zinapatikana");
        put(Language.SWAHILI, "event.noSeatsChosen",
                "Hakuna kiti bado huchaguliwa.");
        put(Language.SWAHILI, "event.heldFor",
                "•  imeshikiliwa kwa");
        put(Language.SWAHILI, "event.noNotices",
                "Taarifa za tukio hili");
        put(Language.SWAHILI, "event.nothingPosted",
                "Hakuna kilichotolewa kuhusu tukio hili.");
        put(Language.SWAHILI, "event.venueNoticesElsewhere",
                "Taarifa nyingine za uwanja zipo kwenye ukurasa wake.");
        put(Language.SWAHILI, "event.detailsAndBooking",
                "Maelezo na booking");
        put(Language.SWAHILI, "event.whyItIsClosed",
                "Kwa nini imefungwa");
        put(Language.SWAHILI, "event.unavailable",
                "Haipatikani");
        put(Language.SWAHILI, "event.closed",
                "Imefungwa");
        put(Language.SWAHILI, "event.bookingStopsIn",
                "Bookings zinakatika baada ya");
        put(Language.SWAHILI, "event.eventCancelled",
                "Tukio limefutwa");
        put(Language.SWAHILI, "event.emergencyNotice",
                "Taarifa ya dharura");
        put(Language.SWAHILI, "event.bookingClosed",
                "Bookings zimefungwa");
        put(Language.SWAHILI, "event.openDetails",
                "Fungua maelezo ya");
        put(Language.SWAHILI, "event.vacantShort",
                "viti hazitoshiwi");
        put(Language.SWAHILI, "event.seatsAndPrice",
                "•  viti %s");
        put(Language.SWAHILI, "event.beginsNow",
                "Bookings zinafunguliwa sasa");
        put(Language.SWAHILI, "event.dateTimeLine",
                "Inaanza");
        put(Language.SWAHILI, "event.countdownLabel",
                "Mwisho wa booking: ");
        put(Language.SWAHILI, "event.selectFirst",
                "Chagua tarehe na tukio, kisha chagua viti vyako");
        put(Language.SWAHILI, "booking.confirmSeats",
                "Thibitisha");
        put(Language.SWAHILI, "booking.needSeatToConfirm",
                "Chagua angalau kiti moja kabla ya kuthibitisha.");
        put(Language.SWAHILI, "booking.continue",
                "Endelea");
        put(Language.SWAHILI, "booking.continueAndConfirm",
                "Endelea na uthibitisho");
        put(Language.SWAHILI, "booking.bookYour",
                "Bookia tukio");
        put(Language.SWAHILI, "booking.continuePrompt",
                "la");
        put(Language.SWAHILI, "booking.thenShowReceipt",
                ", kisha onyesha risiti yako");
        put(Language.SWAHILI, "booking.totalFor",
                "Jumla kwa");
        put(Language.SWAHILI, "booking.plusAFee",
                "pamoja na");
        put(Language.SWAHILI, "booking.ticketingFee",
                "ada ya tikiti");
        put(Language.SWAHILI, "booking.chooseFirst",
                "Chagua kiti moja au zaidi, kisha Thibitisha hapa");
        put(Language.SWAHILI, "booking.about",
                "Kuhusu");
        put(Language.SWAHILI, "booking.seeAllSchedules",
                "Angalia ratiba zote");
        put(Language.SWAHILI, "booking.saveHintLead",
                "Weka viti hivi vyake na uvichague baadaye. ");
        put(Language.SWAHILI, "booking.saveHintEnd",
                "Hivi haviui viti");
        put(Language.SWAHILI, "booking.needSeatToSave",
                "Chagua angalau kiti moja kabla ya kuhifadhi.");
        put(Language.SWAHILI, "booking.mySeats",
                "Viti vyangu");
        put(Language.SWAHILI, "booking.labelHint",
                "Jina la uteuzi huu");
        put(Language.SWAHILI, "booking.labelHintDesc",
                "herufi. Jambo utakayokumbuka ");
        put(Language.SWAHILI, "booking.labelHintDescEnd",
                "baadaye.");
        put(Language.SWAHILI, "booking.labelPrompt",
                "Niite jina utakayokumbuka");
        put(Language.SWAHILI, "booking.saveTheseSeats",
                "Hifadhi viti hivi kwa baadaye");
        put(Language.SWAHILI, "booking.saved",
                "Imehifadhiwa");
        put(Language.SWAHILI, "booking.findUnderSaved",
                "•  pata kwenye Viti vilivyohifadhiwa");
        put(Language.SWAHILI, "booking.seatWord",
                "kiti");
        put(Language.SWAHILI, "booking.seatsWord",
                "viti");
        put(Language.SWAHILI, "booking.notBookedYet",
                "Viti hivi havijabookiwa bado.");
        put(Language.SWAHILI, "booking.notBookedYetBody",
                "Kuhifadhi huku hukuhifadhi uchaguzi wako ili urudi, lakini hakushikili viti wala kuviuza. Mtu mwingine anaweza kuvichukua, na ukurasa huu utaeleza wakati huo.");
        put(Language.SWAHILI, "booking.nothingSaved",
                "Hakuna kimehifadhiwa bado.");
        put(Language.SWAHILI, "booking.nothingSavedBody",
                "Chagua viti unavyotaka kwenye tukio, kisha bonyeza Hifadhi viti hivi. Vitita hapa unapofungua programu.");
        put(Language.SWAHILI, "booking.findEventToBook",
                "Tafuta tukio la kuhudhuria");
        put(Language.SWAHILI, "booking.cannotBeBookedPrefix",
                "Haiwezi kuhudhuriwa:");
        put(Language.SWAHILI, "booking.resumeAndBook",
                "Endelea na uweke");
        put(Language.SWAHILI, "booking.pickedUp",
                "Imechukuliwa");
        put(Language.SWAHILI, "booking.savedSeatWord",
                "kiti kilichohifadhiwa");
        put(Language.SWAHILI, "booking.discardQuestion",
                "Tupa \"%s\"?");
        put(Language.SWAHILI, "booking.discardBody",
                "Viti vilivyohifadhiwa vitasahauwa. Hakuna kilichobookiwa, kwa hivyo hakuna ada na viti hurudi kuuzwa.");
        put(Language.SWAHILI, "booking.discardSavedTitle",
                "Tupa viti vilivyohifadhiwa");
        put(Language.SWAHILI, "booking.discardSaved",
                "Tupa viti vilivyohifadhiwa");
        put(Language.SWAHILI, "booking.discarded",
                "Viti vilivyohifadhiwa vitimetwa");
        put(Language.SWAHILI, "booking.couldNotDiscard",
                "Uteuzi huu haukuweza kutupwa. Tafuta tena.");
        put(Language.SWAHILI, "booking.viewing",
                "Inaangaliwa");
        put(Language.SWAHILI, "booking.viewingSchedule",
                "ratiba");
        put(Language.SWAHILI, "booking.seatSelectionCleared",
                "Utuzi wa viti umefutwa");
        put(Language.SWAHILI, "booking.selectedSeat",
                "Kiti kimechaguliwa");
        put(Language.SWAHILI, "booking.removedSeat",
                "Kiti kimeondolewa");
        put(Language.SWAHILI, "booking.seeEverySeatTaken",
                "Angalia kila kiti kilichookwa kwa tukio hili");
        put(Language.SWAHILI, "booking.bySection",
                "Kwa pande:  ");
        put(Language.SWAHILI, "schedules.title",
                "Ratiba za moja kwa moja");
        put(Language.SWAHILI, "schedules.subtitle",
                "Kila mchezo na tamasha ya Namboole, yaliyo karibu zaidi kwanza.");
        put(Language.SWAHILI, "schedules.allUpcoming",
                "Matukio yote yaliyo karibu");
        put(Language.SWAHILI, "schedules.soonestFirst",
                "Karibu zaidi kwanza, kila mchezo na tamasha. Chagua tukio ili uchague viti.");
        put(Language.SWAHILI, "schedules.allTypes",
                "Aina zote");
        put(Language.SWAHILI, "schedules.games",
                "Michezo");
        put(Language.SWAHILI, "schedules.concerts",
                "Tamasha");
        put(Language.SWAHILI, "schedules.dateColumn",
                "Tarehe");
        put(Language.SWAHILI, "schedules.typeColumn",
                "Aina");
        put(Language.SWAHILI, "schedules.allDates",
                "Tarehe zote");
        put(Language.SWAHILI, "schedules.today",
                "Leo");
        put(Language.SWAHILI, "schedules.tomorrow",
                "Kesho");
        put(Language.SWAHILI, "schedules.noMatch",
                "Hakuna tukio linalofaa");
        put(Language.SWAHILI, "schedules.tryDifferent",
                "Jaribu tarehe, aina au neno lingine.");
        put(Language.SWAHILI, "schedules.eventWord",
                "tukio");
        put(Language.SWAHILI, "schedules.eventsWord",
                "matukio");
        put(Language.SWAHILI, "schedules.comingUpAt",
                "inayokuja kwenye");
        put(Language.SWAHILI, "schedules.matching",
                "•  inayolingana na \"");
        put(Language.SWAHILI, "schedules.matchCount",
                "zinalolingana");
        put(Language.SWAHILI, "schedules.teamOrArtist",
                "Timu au msanii");
        put(Language.SWAHILI, "schedules.teamOrArtistHint",
                "Timu au msanii anayepatikana uwanja");
        put(Language.SWAHILI, "schedules.enterForList",
                "Bonyeza Enter kuona orodha kamili");
        put(Language.SWAHILI, "schedules.doors",
                "Lango");
        put(Language.SWAHILI, "search.stadium",
                "Uwanja");
        put(Language.SWAHILI, "search.stadiumHint",
                "Tafuta timu, wasanii, uwanja au tarehe");
        put(Language.SWAHILI, "search.venue",
                "uwanja");
        put(Language.SWAHILI, "search.venues",
                "majengo");
        put(Language.SWAHILI, "search.matches",
                "majengo yanayolingana");
        put(Language.SWAHILI, "search.match",
                "majengo yanayolingana");
        put(Language.SWAHILI, "search.booking",
                "booking");
        put(Language.SWAHILI, "search.bookings",
                "bookings");
        put(Language.SWAHILI, "search.bookingMatches",
                "bookings zinalolingana");
        put(Language.SWAHILI, "search.bookingMatch",
                "bookings zinalolingana");
        put(Language.SWAHILI, "search.seatsHint",
                "Tafuta kumbukumbu, uwanja, tukio au hali");
        put(Language.SWAHILI, "search.seats",
                "Viti vilivyookwa");
        put(Language.SWAHILI, "search.bookingTitleHint",
                "Tafuta");
        put(Language.SWAHILI, "search.enterToChoose",
                "Andika kutafuta. Tumia vishale na Enter kuchagua.");
        put(Language.SWAHILI, "search.noMatches",
                "Hakuna kinacholingana");
        put(Language.SWAHILI, "search.tryDifferentWord",
                "Jaribu neno lingine");
        put(Language.SWAHILI, "bookings.emptyLead",
                "Kila utunuzi kwenye");
        put(Language.SWAHILI, "bookings.emptyTail",
                "Fungua mmoja ili kuona viti, malipo na risiti yake.");
        put(Language.SWAHILI, "bookings.exportHint",
                "Andika kila booking kwenye faili ya spreadsheet");
        put(Language.SWAHILI, "bookings.selectFirst",
                "Chagua booking kutoka kwenye jedwali kwanza.");
        put(Language.SWAHILI, "bookings.alreadyCancelled",
                "Booking hii tayari imeghairiwa.");
        put(Language.SWAHILI, "bookings.cancelTitle",
                "Ghairi booking");
        put(Language.SWAHILI, "booking.cancelQuestion",
                "Ghairi booking %s?");
        put(Language.SWAHILI, "bookings.cancelBody",
                "Viti vitapatikana tena.");
        put(Language.SWAHILI, "bookings.cancelled",
                "imeghairiwa");
        put(Language.SWAHILI, "bookings.couldNotCancel",
                "Booking haikuweza kughairiwa.");
        put(Language.SWAHILI, "bookings.legacy",
                "Uwanja wa zamani");
        put(Language.SWAHILI, "bookings.table.reference",
                "Kumbukumbu");
        put(Language.SWAHILI, "bookings.table.status",
                "Hali");
        put(Language.SWAHILI, "bookings.table.created",
                "Ilichanganywa");
        put(Language.SWAHILI, "bookings.section.venue",
                "Uwanja");
        put(Language.SWAHILI, "bookings.section.legacy",
                "Uwanja wa zamani");
        put(Language.SWAHILI, "bookings.section.location",
                "Mahali");
        put(Language.SWAHILI, "bookings.section.address",
                "Anwani");
        put(Language.SWAHILI, "bookings.section.capacity",
                "Uwezekano");
        put(Language.SWAHILI, "bookings.section.bookedBy",
                "IMEBOOKWA NA");
        put(Language.SWAHILI, "bookings.section.paid",
                "VITI NA MALIPO");
        put(Language.SWAHILI, "bookings.field.fullName",
                "Jina kamili");
        put(Language.SWAHILI, "bookings.field.email",
                "Barua pepe");
        put(Language.SWAHILI, "bookings.field.phone",
                "Simu");
        put(Language.SWAHILI, "bookings.field.bookedSeats",
                "Viti vilivyookwa");
        put(Language.SWAHILI, "bookings.field.seatCount",
                "Idadi ya viti");
        put(Language.SWAHILI, "bookings.field.totalCharged",
                "Jumla iliyochukuliwa");
        put(Language.SWAHILI, "bookings.viewReceipt",
                "Angalia risiti ya bidhaa");
        put(Language.SWAHILI, "bookings.completeDetails",
                "Maelezo kamili ya booking");
        put(Language.SWAHILI, "payment.simulated",
                "•  malipo ya simu ni ya kuonyesha tu, pesa hazitishukiki");
        put(Language.SWAHILI, "payment.line",
                "  Malipo: ");
        put(Language.SWAHILI, "receipt.title",
                "Risiti");
        put(Language.SWAHILI, "receipt.save",
                "Hifadhi risiti");
        put(Language.SWAHILI, "receipt.savedTo",
                "Risiti imehifadhiwa kwenye");
        put(Language.SWAHILI, "receipt.needsBooking",
                "Risiti inahitaji booking");
        put(Language.SWAHILI, "receipt.section",
                "Pande");
        put(Language.SWAHILI, "receipt.filePrefix",
                "risiti-");
        put(Language.SWAHILI, "ticket.title",
                "Tikiti");
        put(Language.SWAHILI, "ticket.viewReceipt",
                "Angalia risiti");
        put(Language.SWAHILI, "ticket.saveAsText",
                "Hifadhi kama maandishi");
        put(Language.SWAHILI, "ticket.savedTo",
                "Tikiti imehifadhiwa kwenye");
        put(Language.SWAHILI, "ticket.filePrefix",
                "tikiti-");
        put(Language.SWAHILI, "ticket.couldNotSave",
                "Hiki haikuweza kuhifadhiwa: ");
        put(Language.SWAHILI, "print.sentToPrinter",
                "imetumwa kwa kichapishaji");
        put(Language.SWAHILI, "export.noneYet",
                "Hakuna bookings za kuuza bado.");
        put(Language.SWAHILI, "export.done",
                "bookings kwenye");
        put(Language.SWAHILI, "export.couldNotWrite",
                "Uuza haukuweza kuandikwa: ");
        put(Language.SWAHILI, "occupancy.allEvents",
                "Matukio yote");
        put(Language.SWAHILI, "occupancy.savedTo",
                "Ripoti imehifadhiwa kwenye");
        put(Language.SWAHILI, "occupancy.couldNotSave",
                "Ripoti haikuweza kuhifadhiwa: ");
        put(Language.SWAHILI, "occupancy.vacancy",
                "Uzio");
        put(Language.SWAHILI, "occupancy.seatsBooked",
                "Viti vilivyookwa");
        put(Language.SWAHILI, "occupancy.seatsOnSale",
                "Viti vinavyouzwa");
        put(Language.SWAHILI, "ledger.seat",
                "Kiti");
        put(Language.SWAHILI, "ledger.section",
                "Pande");
        put(Language.SWAHILI, "ledger.number",
                "Namba");
        put(Language.SWAHILI, "ledger.priceTier",
                "Tabaka la bei");
        put(Language.SWAHILI, "ledger.price",
                "Bei");
        put(Language.SWAHILI, "ledger.bookedBy",
                "Imebookwa na");
        put(Language.SWAHILI, "ledger.bookedOn",
                "Ilibookwa tarehe");
        put(Language.SWAHILI, "ledger.bookedOf",
                " viliyookwa kati ya ");
        put(Language.SWAHILI, "ledger.percentVacant",
                "% hazitoshiwi  •  ");
        put(Language.SWAHILI, "request.typeCancellation",
                "Kufutwa kwa tukio");
        put(Language.SWAHILI, "request.typeEmergency",
                "Dharura ya uwanja");
        put(Language.SWAHILI, "request.typeSafety",
                "Taarifa ya usalama");
        put(Language.SWAHILI, "request.typeAccess",
                "Ombi la ufikivu");
        put(Language.SWAHILI, "request.typeOther",
                "Ombi lingine");
        put(Language.SWAHILI, "request.minLength",
                "herufi, ili timu ya uwanja ijue unachohitaji.");
        put(Language.SWAHILI, "request.submitTo",
                "Wasilisha ombi maalum kwa");
        put(Language.SWAHILI, "request.submit",
                "Wasilisha ombi");
        put(Language.SWAHILI, "request.submittedTo",
                "Ombi maalum limewasilishwa kwa");
        put(Language.SWAHILI, "request.pickVenue",
                "Chagua uwanja");
        put(Language.SWAHILI, "form.heading",
                "Maelezo yako, ili booking iwe yako");
        put(Language.SWAHILI, "form.name",
                "Jina");
        put(Language.SWAHILI, "form.nameHint",
                "Jina lako kamili");
        put(Language.SWAHILI, "form.emailHint",
                "Anwani ya barua pepe");
        put(Language.SWAHILI, "form.phoneHint",
                "Nambari ya simu");
        put(Language.SWAHILI, "form.nameExample",
                "Kwa mfano: Amina Okello");
        put(Language.SWAHILI, "form.emailExample",
                "Kwa mfano: amina@example.co.ug");
        put(Language.SWAHILI, "form.phoneExample",
                "Kwa mfano: +256 700 123 456");
        put(Language.SWAHILI, "form.labelName",
                "Jina");
        put(Language.SWAHILI, "form.labelEmail",
                "Barua pepe");
        put(Language.SWAHILI, "form.labelPhone",
                "Simu");
        put(Language.SWAHILI, "form.hintName",
                "Jina kamili la mteja, angalau herufi mbili");
        put(Language.SWAHILI, "form.hintEmail",
                "Anwani ya pepe iliyotumika kwenye booking");
        put(Language.SWAHILI, "form.hintPhone",
                "Nambari ya simu, tarakimu, nafasi na alama ya plus");
        put(Language.SWAHILI, "form.email",
                "Anwani ya pepe iliyotumika kwenye booking");
        put(Language.SWAHILI, "form.phone",
                "Nambari ya simu, tarakimu, nafasi na alama ya plus");
        put(Language.SWAHILI, "status.checkBooking",
                "Tafuta booking yako");
        put(Language.SWAHILI, "status.pressed",
                "imebofywa");
        put(Language.SWAHILI, "status.hover",
                "Chuma: ");
        put(Language.SWAHILI, "status.clicking",
                "Bofya: ");
        put(Language.SWAHILI, "status.areYouSure",
                "una uhakika?");
        put(Language.SWAHILI, "status.cancelled",
                "IMEGHAIRIWA");
        put(Language.SWAHILI, "theme.switchToLight",
                "Badilisha kwa mandhari ya mwanga");
        put(Language.SWAHILI, "theme.switchToDark",
                "Badilisha kwa mandhari ya giza");
        put(Language.SWAHILI, "theme.currentlyDark",
                "Sasa ni mandhari ya giza. Bofya kupata mandhari ya mwanga.");
        put(Language.SWAHILI, "theme.currentlyLight",
                "Sasa ni mandhari ya mwanga. Bofya kupata mandhari ya giza.");
        put(Language.SWAHILI, "theme.darkOn",
                "Hali ya giza imewashwa");
        put(Language.SWAHILI, "theme.lightOn",
                "Hali ya mwanga imewashwa");
        put(Language.SWAHILI, "theme.darkLabel",
                "Giza");
        put(Language.SWAHILI, "theme.lightLabel",
                "Mwanga");
        put(Language.SWAHILI, "theme.darkName",
                "Hali ya giza");
        put(Language.SWAHILI, "photo.photograph",
                "Picha");
        put(Language.SWAHILI, "photo.aerialPlan",
                "Ramani ya viti ya uwanja");
        put(Language.SWAHILI, "photo.addFile",
                "ongeza photos/%s.jpg");
        put(Language.SWAHILI, "error.needSeatToSave",
                "Chagua angalau kiti moja kuhifadhi");
        put(Language.SWAHILI, "error.eventGone",
                "Tukio hilo halipo tena kwenye ratiba.");
        put(Language.SWAHILI, "error.venueGone",
                "Uwanja hao haupatikani tena.");
        put(Language.SWAHILI, "error.bookingClosed",
                "Bookings zimefungwa kwa tukio hili");
        put(Language.SWAHILI, "error.nameTooShort",
                "Andika jina lako");
        put(Language.SWAHILI, "error.emailInvalid",
                "Andika anwani ya pepe inayofaa");
        put(Language.SWAHILI, "error.phoneInvalid",
                "Andika nambari ya simu inayofaa");

        put(Language.ENGLISH, "home.step1Desc",
                "Open it and book");
        put(Language.ENGLISH, "home.step2Desc",
                "Pick your exact seats on the map");
        put(Language.ENGLISH, "home.step3Desc",
                "Cash at the venue or mobile money");
        put(Language.ENGLISH, "home.step4Desc",
                "Issued the moment it is booked");

        put(Language.LUGANDA, "home.step1Desc",
                "Bikira era oyate");
        put(Language.LUGANDA, "home.step2Desc",
                "Lunguza ebizitansa byo ku kuwoko");
        put(Language.LUGANDA, "home.step3Desc",
                "Amasada ku ekizimbe oba essimu");
        put(Language.LUGANDA, "home.step4Desc",
                "Ebisalwa ku mwanvu nga bikwata");

        put(Language.SWAHILI, "home.step1Desc",
                "Fungua na uweke");
        put(Language.SWAHILI, "home.step2Desc",
                "Chagua viti vyako hasa kwenye ramani");
        put(Language.SWAHILI, "home.step3Desc",
                "Fedha tasnimoni au simu");
        put(Language.SWAHILI, "home.step4Desc",
                "Hutolewa mara tu inapobookiwa");


        put(Language.ENGLISH, "home.bookingCloses",
                "Booking closes");
        put(Language.ENGLISH, "home.seatLimit",
                "{0} seats per reservation, and no");
        put(Language.ENGLISH, "home.noBookingLimit",
                "limit on how many reservations one person may hold.");

        put(Language.LUGANDA, "home.bookingCloses",
                "Okutandika kuggwa");
        put(Language.LUGANDA, "home.seatLimit",
                "{0} ebizitansa kwa nkwaateeka, era naba");
        put(Language.LUGANDA, "home.noBookingLimit",
                "kano ku nga muntu esobola okutandika nkwaateeka zingi.");

        put(Language.SWAHILI, "home.bookingCloses",
                "Bookings zinakatika");
        put(Language.SWAHILI, "home.seatLimit",
                "{0} viti kwa kila utunuzi, na hakuna");
        put(Language.SWAHILI, "home.noBookingLimit",
                "kikomo cha idadi ya bookings mtu mmoja aweza kufunga.");


        put(Language.ENGLISH, "home.eventCount",
                "%d event");
        put(Language.ENGLISH, "home.eventCountPlural",
                "%d events");
        put(Language.ENGLISH, "home.vacantWord",
                "vacant");

        put(Language.LUGANDA, "home.eventCount",
                "%d ekizibuBy'obujjuni");
        put(Language.LUGANDA, "home.eventCountPlural",
                "%d emizibiso");
        put(Language.LUGANDA, "home.vacantWord",
                "bwe muganda");

        put(Language.SWAHILI, "home.eventCount",
                "%d tukio");
        put(Language.SWAHILI, "home.eventCountPlural",
                "%d matukio");
        put(Language.SWAHILI, "home.vacantWord",
                "hazitoshiwi");


        put(Language.ENGLISH, "booking.bookingFeeOnce",
                "Booking fee (once)");
        put(Language.ENGLISH, "bookings.field.bookingFee",
                "Booking fee");
        put(Language.ENGLISH, "event.bookingDeadline",
                "Booking deadline");
        put(Language.ENGLISH, "event.dateAndTime",
                "Date and time");
        put(Language.ENGLISH, "ledger.pricePerSeat",
                "Price per seat");
        put(Language.ENGLISH, "ledger.rows",
                "Rows");
        put(Language.ENGLISH, "ledger.seatsPerRow",
                "Seats per row");
        put(Language.ENGLISH, "seatMap.bookingClosed",
                "Booking closed");
        put(Language.ENGLISH, "receipt.sectionLine",
                "Section");
        put(Language.ENGLISH, "receipt.header",
                "NAMBOOLE STADIUM  -  BOOKING RECEIPT");
        put(Language.ENGLISH, "receipt.receiptNo",
                "Receipt no.");
        put(Language.ENGLISH, "receipt.issued",
                "Issued");
        put(Language.ENGLISH, "receipt.statusLine",
                "Status");
        put(Language.ENGLISH, "receipt.eventHeading",
                "EVENT");
        put(Language.ENGLISH, "receipt.typeLine",
                "Type");
        put(Language.ENGLISH, "receipt.seatsHeading",
                "SEATS BOOKED");
        put(Language.ENGLISH, "receipt.colSeat",
                "SEAT");
        put(Language.ENGLISH, "receipt.colSection",
                "SECTION");
        put(Language.ENGLISH, "receipt.colTier",
                "PRICE TIER");
        put(Language.ENGLISH, "receipt.colAmount",
                "AMOUNT");
        put(Language.ENGLISH, "receipt.chargesHeading",
                "CHARGES");
        put(Language.ENGLISH, "receipt.seatsLine",
                "Seats (");
        put(Language.ENGLISH, "receipt.feeLine",
                "Ticketing fee");
        put(Language.ENGLISH, "receipt.totalPaid",
                "TOTAL PAID");
        put(Language.ENGLISH, "receipt.billedTo",
                "BILLED TO");
        put(Language.ENGLISH, "receipt.keepNote",
                "Keep this receipt. Quote the receipt number at the gate.");
        put(Language.ENGLISH, "receipt.holdNote",
                "Seats are held under this number only.");
        put(Language.ENGLISH, "ticket.heading",
                "STADIUM SELECT  •  E-TICKET");
        put(Language.ENGLISH, "ticket.arriveNote",
                "Please arrive before the doors close. This ticket is the only");
        put(Language.ENGLISH, "ticket.arriveNoteEnd",
                "record of your seat; quote the reference at the entrance.");
        put(Language.ENGLISH, "photo.aerialPlanHint",
                "Aerial seating plan  •  add photos/");

        put(Language.LUGANDA, "booking.bookingFeeOnce",
                "Ada y'ekikwata (kikumi)");
        put(Language.LUGANDA, "bookings.field.bookingFee",
                "Ada y'ekikwata");
        put(Language.LUGANDA, "event.bookingDeadline",
                "Olunaku lwokuggwa");
        put(Language.LUGANDA, "event.dateAndTime",
                "Olunaku n'obudde");
        put(Language.LUGANDA, "ledger.pricePerSeat",
                "Ezimbe ly'ekizitanse");
        put(Language.LUGANDA, "ledger.rows",
                "Emizimbe");
        put(Language.LUGANDA, "ledger.seatsPerRow",
                "EBizitansa buli lulungi");
        put(Language.LUGANDA, "seatMap.bookingClosed",
                "Okutandika kikaze");
        put(Language.LUGANDA, "receipt.sectionLine",
                "Nsengwe");
        put(Language.LUGANDA, "receipt.header",
                "NAMBOOLE  -  KASIITA Y'ekIKWATA");

        put(Language.LUGANDA, "receipt.receiptNo",
                "Kasiita namba");
        put(Language.LUGANDA, "receipt.issued",
                "Ebisalwa");
        put(Language.LUGANDA, "receipt.statusLine",
                "Ebika");
        put(Language.LUGANDA, "receipt.eventHeading",
                "EKIZIBUBy'OBUJJUNI");
        put(Language.LUGANDA, "receipt.typeLine",
                "Kind");
        put(Language.LUGANDA, "receipt.seatsHeading",
                "EBIZITANSA EBIKWATA");
        put(Language.LUGANDA, "receipt.colSeat",
                "KIZITANSA");
        put(Language.LUGANDA, "receipt.colSection",
                "NSENGWE");
        put(Language.LUGANDA, "receipt.colTier",
                "EBIKA");
        put(Language.LUGANDA, "receipt.colAmount",
                "KATUZI");
        put(Language.LUGANDA, "receipt.chargesHeading",
                "ADA");
        put(Language.LUGANDA, "receipt.seatsLine",
                "EBizitansa (");
        put(Language.LUGANDA, "receipt.feeLine",
                "Ada y'okutandika");
        put(Language.LUGANDA, "receipt.totalPaid",
                "JIGGER EKATUZI");
        put(Language.LUGANDA, "receipt.billedTo",
                "AMAKWATTA");
        put(Language.LUGANDA, "receipt.keepNote",
                "Giriza kasiita kino. Mannya kasiita ku nnimiro.");
        put(Language.LUGANDA, "receipt.holdNote",
                "EBizitansa biri ku namba eno era.");
        put(Language.LUGANDA, "ticket.heading",
                "NAMBOOLE  •  EKWATA");
        put(Language.LUGANDA, "ticket.arriveNote",
                "Endereera nga emigateeza telatuma. Ekwata kino kikwale");
        put(Language.LUGANDA, "ticket.arriveNoteEnd",
                "yakwata ekizitansa; nannya kuwoko ku nnimiro.");
        put(Language.LUGANDA, "photo.aerialPlanHint",
                "Ebbaliwo ekiggya ekizimbe  •  yongera photos/");

        put(Language.SWAHILI, "booking.bookingFeeOnce",
                "Ada ya booking (mara moja)");
        put(Language.SWAHILI, "bookings.field.bookingFee",
                "Ada ya booking");
        put(Language.SWAHILI, "event.bookingDeadline",
                "Mwisho wa booking");
        put(Language.SWAHILI, "event.dateAndTime",
                "Tarehe na wakati");
        put(Language.SWAHILI, "ledger.pricePerSeat",
                "Bei kwa kiti");
        put(Language.SWAHILI, "ledger.rows",
                "Safu");
        put(Language.SWAHILI, "ledger.seatsPerRow",
                "Viti kwa safu");
        put(Language.SWAHILI, "seatMap.bookingClosed",
                "Bookings zimefungwa");
        put(Language.SWAHILI, "receipt.sectionLine",
                "Pande");
        put(Language.SWAHILI, "receipt.header",
                "NAMBOOLE  -  RISITI YA BOOKING");

        put(Language.SWAHILI, "receipt.receiptNo",
                "Risiti namba");
        put(Language.SWAHILI, "receipt.issued",
                "Isitolewa");
        put(Language.SWAHILI, "receipt.statusLine",
                "Hali");
        put(Language.SWAHILI, "receipt.eventHeading",
                "TUKIO");
        put(Language.SWAHILI, "receipt.typeLine",
                "Aina");
        put(Language.SWAHILI, "receipt.seatsHeading",
                "VITI VILIVYOOZWA");
        put(Language.SWAHILI, "receipt.colSeat",
                "KITI");
        put(Language.SWAHILI, "receipt.colSection",
                "PANDE");
        put(Language.SWAHILI, "receipt.colTier",
                "TABAKA LA BEI");
        put(Language.SWAHILI, "receipt.colAmount",
                "KIASI");
        put(Language.SWAHILI, "receipt.chargesHeading",
                "MALIPO");
        put(Language.SWAHILI, "receipt.seatsLine",
                "Viti (");
        put(Language.SWAHILI, "receipt.feeLine",
                "Ada ya tikiti");
        put(Language.SWAHILI, "receipt.totalPaid",
                "JUMLA ILIYOPOKWA");
        put(Language.SWAHILI, "receipt.billedTo",
                "ANwani ya");
        put(Language.SWAHILI, "receipt.keepNote",
                "Weka risiti hii. Namba ya risiti ionyeshe kwenye mlango.");
        put(Language.SWAHILI, "receipt.holdNote",
                "Viti vinasikiliwa kwa namba hii pekee.");
        put(Language.SWAHILI, "ticket.heading",
                "NAMBOOLE  •  TIKITI");
        put(Language.SWAHILI, "ticket.arriveNote",
                "Karibu kabla ya lango kufungwa. Tikiti hii ndiyo pekee");
        put(Language.SWAHILI, "ticket.arriveNoteEnd",
                "kumbukumbu ya kiti yako; namba ionyeshe mlangoni.");
        put(Language.SWAHILI, "photo.aerialPlanHint",
                "Ramani ya viti  •  ongeza photos/");


        put(Language.ENGLISH, "nav.savedTitle",
                "Saved seats");
        put(Language.ENGLISH, "nav.occupancyTitle",
                "Occupancy");
        put(Language.ENGLISH, "booking.seatOne",
                "1 seat");
        put(Language.ENGLISH, "booking.seatSubtotalLabel",
                "Seat subtotal");
        put(Language.ENGLISH, "booking.totalDueLabel",
                "Total due");
        put(Language.ENGLISH, "bookings.table.stadium",
                "Stadium");
        put(Language.ENGLISH, "bookings.table.event",
                "Event");
        put(Language.ENGLISH, "bookings.table.when",
                "When");
        put(Language.ENGLISH, "ledger.seats",
                "Seats");
        put(Language.ENGLISH, "bookings.table.total",
                "Total");
        put(Language.ENGLISH, "facts.whereUpper",
                "LOCATION");
        put(Language.ENGLISH, "home.capacityUpper",
                "CAPACITY");
        put(Language.ENGLISH, "home.nextEvent",
                "NEXT EVENT");
        put(Language.ENGLISH, "schedules.tryAnother",
                "Try another date or clear the search field.");
        put(Language.ENGLISH, "event.bookingWord",
                "Booking");
        put(Language.ENGLISH, "schedules.eventCountSingular",
                "event");
        put(Language.ENGLISH, "schedules.eventCountPlural",
                "events");
        put(Language.ENGLISH, "schedules.matchingLabel",
                "•  matching");
        put(Language.ENGLISH, "schedules.pressEnter",
                "Press Enter to see the full list");
        put(Language.ENGLISH, "event.selected",
                "Event selected");
        put(Language.ENGLISH, "event.heldForLabel",
                "•  held for");
        put(Language.ENGLISH, "export.exported",
                "Exported");
        put(Language.ENGLISH, "export.bookingsTo",
                "bookings to");
        put(Language.ENGLISH, "error.bookingGone",
                "That booking is no longer listed");
        put(Language.ENGLISH, "bookings.section.bookingUpper",
                "BOOKING");
        put(Language.ENGLISH, "bookings.section.venueUpper",
                "STADIUM");
        put(Language.ENGLISH, "booking.discardQuestionLabel",
                "Discard");
        put(Language.ENGLISH, "booking.findUnderSavedLabel",
                "•  find them under Saved seats");
        put(Language.ENGLISH, "booking.savedSeatsNotBookedBody",
                "Saving them keeps your choice so you can come back later, but it does not hold them and does not sell them. Somebody else may take them, and this page will say so when that happens.");
        put(Language.ENGLISH, "booking.discardSavedBody",
                "The saved seats are forgotten. Nothing was booked, so nothing is charged and the seats go back on sale.");
        put(Language.ENGLISH, "booking.labelHintDescMid",
                "characters. Something you will ");
        put(Language.ENGLISH, "booking.noSeatsLabel",
                "No notices");
        put(Language.ENGLISH, "theme.lightWord",
                "Light");
        put(Language.ENGLISH, "theme.darkWord",
                "Dark");
        put(Language.ENGLISH, "ledger.seatsPerRowLabel",
                "Seats per row");
        put(Language.ENGLISH, "event.perReservationLabel",
                " per reservation");

        put(Language.LUGANDA, "nav.savedTitle",
                "Viti vyalikumi");
        put(Language.LUGANDA, "nav.occupancyTitle",
                "Ujaa");
        put(Language.LUGANDA, "booking.seatOne",
                "kizitanse 1");
        put(Language.LUGANDA, "booking.seatSubtotalLabel",
                "Jigger ly'ebizitansa");
        put(Language.LUGANDA, "booking.totalDueLabel",
                "Jigger esobola");
        put(Language.LUGANDA, "bookings.table.stadium",
                "Ekizimbe");
        put(Language.LUGANDA, "bookings.table.event",
                "EkizibuBy'obujjuni");
        put(Language.LUGANDA, "bookings.table.when",
                "Olunaku n'obudde");
        put(Language.LUGANDA, "ledger.seats",
                "Ebizitansa");
        put(Language.LUGANDA, "bookings.table.total",
                "Jigger");
        put(Language.LUGANDA, "facts.whereUpper",
                "EFUULU");
        put(Language.LUGANDA, "home.capacityUpper",
                "BUJJA");
        put(Language.LUGANDA, "home.nextEvent",
                "EKIZIBUBy'OBUJJUNI KIRU");
        put(Language.LUGANDA, "schedules.tryAnother",
                "Geru olunaku olu lunaku kumiriza oba buli nnyo.");
        put(Language.LUGANDA, "event.bookingWord",
                "Ekikwata");
        put(Language.LUGANDA, "schedules.eventCountSingular",
                "ekizibuBy'obujjuni");
        put(Language.LUGANDA, "schedules.eventCountPlural",
                "emizibiso");
        put(Language.LUGANDA, "schedules.matchingLabel",
                "•  ekisobola");
        put(Language.LUGANDA, "schedules.pressEnter",
                "Yogera Enter okulaba olunaku lwa bwera");
        put(Language.LUGANDA, "event.selected",
                "EkizibuBy'obujjuni ekisoboledwa");
        put(Language.LUGANDA, "event.heldForLabel",
                "•  kirimidwa ku");
        put(Language.LUGANDA, "export.exported",
                "Yaumuzibwa");
        put(Language.LUGANDA, "export.bookingsTo",
                "ebikwata ku");
        put(Language.LUGANDA, "error.bookingGone",
                "Ekikwata kino kisibulukidde ku lulimi.");
        put(Language.LUGANDA, "bookings.section.bookingUpper",
                "EKIKWATA");
        put(Language.LUGANDA, "bookings.section.venueUpper",
                "EKIZIMBE");
        put(Language.LUGANDA, "booking.discardQuestionLabel",
                "Sula");
        put(Language.LUGANDA, "booking.findUnderSavedLabel",
                "•  boola ku Viti vyalikumi");
        put(Language.LUGANDA, "booking.savedSeatsNotBookedBody",
                "Okugiriza bikubikira okusubiza naye tetwerako okubikkula. Muntu omu alaba asobola okutwala, era luno ku lupagana luzannyeuka olw'ekyo.");
        put(Language.LUGANDA, "booking.discardSavedBody",
                "Ebizitansa ebikumi bibyibulwa. Tewali ekikwata, naye tetwebwa ada.");
        put(Language.LUGANDA, "booking.labelHintDescMid",
                "ebimyango. Ekintu kisobola okutwala ");
        put(Language.LUGANDA, "booking.noSeatsLabel",
                "Tewali kikwateekero");
        put(Language.LUGANDA, "theme.lightWord",
                "Maawe");
        put(Language.LUGANDA, "theme.darkWord",
                "Ekiro");
        put(Language.LUGANDA, "ledger.seatsPerRowLabel",
                "EBizitansa buli lulungi");
        put(Language.LUGANDA, "event.perReservationLabel",
                " kwa nkwaateeka");

        put(Language.SWAHILI, "nav.savedTitle",
                "Viti vilivyohifadhiwa");
        put(Language.SWAHILI, "nav.occupancyTitle",
                "Uwezekano");
        put(Language.SWAHILI, "booking.seatOne",
                "kiti 1");
        put(Language.SWAHILI, "booking.seatSubtotalLabel",
                "Jumla ya viti");
        put(Language.SWAHILI, "booking.totalDueLabel",
                "Inayotakiwa kulipa");
        put(Language.SWAHILI, "bookings.table.stadium",
                "Uwanja");
        put(Language.SWAHILI, "bookings.table.event",
                "Tukio");
        put(Language.SWAHILI, "bookings.table.when",
                "Tarehe");
        put(Language.SWAHILI, "ledger.seats",
                "Viti");
        put(Language.SWAHILI, "bookings.table.total",
                "Jumla");
        put(Language.SWAHILI, "facts.whereUpper",
                "MAHALI");
        put(Language.SWAHILI, "home.capacityUpper",
                "UWEZEKANO");
        put(Language.SWAHILI, "home.nextEvent",
                "TIKIO KIJAYO");
        put(Language.SWAHILI, "schedules.tryAnother",
                "Jaribu tarehe nyingine au futa utafutaji.");
        put(Language.SWAHILI, "event.bookingWord",
                "Booking");
        put(Language.SWAHILI, "schedules.eventCountSingular",
                "tukio");
        put(Language.SWAHILI, "schedules.eventCountPlural",
                "matukio");
        put(Language.SWAHILI, "schedules.matchingLabel",
                "•  inayolingana");
        put(Language.SWAHILI, "schedules.pressEnter",
                "Bonyeza Enter kuona orodha kamili");
        put(Language.SWAHILI, "event.selected",
                "Tukio limechaguliwa");
        put(Language.SWAHILI, "event.heldForLabel",
                "•  imeshikiliwa kwa");
        put(Language.SWAHILI, "export.exported",
                "Imehamishwa");
        put(Language.SWAHILI, "export.bookingsTo",
                "bookings kwenye");
        put(Language.SWAHILI, "error.bookingGone",
                "Booking hiyo haipo tena kwenye orodha.");
        put(Language.SWAHILI, "bookings.section.bookingUpper",
                "BOOKING");
        put(Language.SWAHILI, "bookings.section.venueUpper",
                "UWANJA");
        put(Language.SWAHILI, "booking.discardQuestionLabel",
                "Tupa");
        put(Language.SWAHILI, "booking.findUnderSavedLabel",
                "•  pata kwenye Viti vilivyohifadhiwa");
        put(Language.SWAHILI, "booking.savedSeatsNotBookedBody",
                "Kuhifadhi hukuhifadhi uchaguzi wako ili urudi, lakini hakushikili viti wala kuviuza. Mtu mwingine anaweza kuvichukua, na ukurasa huu utaeleza wakati huo.");
        put(Language.SWAHILI, "booking.discardSavedBody",
                "Viti vilivyohifadhiwa vitasahauwa. Hakuna kilichobookiwa, kwa hivyo hakuna ada na viti hurudi kuuzwa.");
        put(Language.SWAHILI, "booking.labelHintDescMid",
                "herufi. Jambo utakayokumbuka ");
        put(Language.SWAHILI, "booking.noSeatsLabel",
                "Hakuna taarifa");
        put(Language.SWAHILI, "theme.lightWord",
                "Mwanga");
        put(Language.SWAHILI, "theme.darkWord",
                "Giza");
        put(Language.SWAHILI, "ledger.seatsPerRowLabel",
                "Viti kwa safu");
        put(Language.SWAHILI, "event.perReservationLabel",
                " kwa kila utunuzi");


        put(Language.ENGLISH, "saved.subtitle",
                "Seats you chose earlier, kept until you book them or discard them.");
        put(Language.ENGLISH, "ticket.csvEvent",
                "Event");
        put(Language.ENGLISH, "ticket.csvCustomer",
                "Customer");
        put(Language.ENGLISH, "ticket.csvBookedOn",
                "BookedOn");

        put(Language.LUGANDA, "saved.subtitle",
                "Viti owaliwo, bitawulidwa nga tebikwata oba osula.");
        put(Language.LUGANDA, "ticket.csvEvent",
                "Event");
        put(Language.LUGANDA, "ticket.csvCustomer",
                "Mteja");
        put(Language.LUGANDA, "ticket.csvBookedOn",
                "BookedOn");

        put(Language.SWAHILI, "saved.subtitle",
                "Viti ulivyochagua awali, zinabaki hadi uzihudhurie au kuzitupa.");
        put(Language.SWAHILI, "ticket.csvEvent",
                "Tukio");
        put(Language.SWAHILI, "ticket.csvCustomer",
                "Mteja");
        put(Language.SWAHILI, "ticket.csvBookedOn",
                "IliyookwaTarehe");


        put(Language.ENGLISH, "nav.backArrow",
                "← Back");
        put(Language.ENGLISH, "booking.seatLimitLabel",
                "%d seats");
        put(Language.ENGLISH, "booking.savedOnLabel",
                "  •  saved");
        put(Language.ENGLISH, "payment.mobileNote",
                "•  mobile money is simulated, no money moves");
        put(Language.ENGLISH, "payment.lineLabel",
                "  Payment: ");
        put(Language.ENGLISH, "print.sentLabel",
                " sent to the printer");
        put(Language.ENGLISH, "bookings.section.locationLabel",
                "Location");
        put(Language.ENGLISH, "bookings.section.addressLabel",
                "Address");
        put(Language.ENGLISH, "bookings.section.capacityLabel",
                "Capacity");
        put(Language.ENGLISH, "bookings.section.eventUpper",
                "EVENT");
        put(Language.ENGLISH, "bookings.section.eventLabel",
                "Event");
        put(Language.ENGLISH, "bookings.section.bookedByUpper",
                "BOOKED BY");
        put(Language.ENGLISH, "bookings.section.paidUpper",
                "SEATS AND PAYMENT");
        put(Language.ENGLISH, "bookings.seatSubtotalLabel",
                "Seat subtotal");
        put(Language.ENGLISH, "bookings.statusCancelled",
                "CANCELLED");
        put(Language.ENGLISH, "occupancy.endLabel",
                "End");
        put(Language.ENGLISH, "occupancy.allEventsLabel",
                "All events");
        put(Language.ENGLISH, "ledger.rowLabel",
                "Row");
        put(Language.ENGLISH, "ledger.bookedOfLabel",
                " booked of ");
        put(Language.ENGLISH, "ledger.bySectionLabel",
                "By section: ");
        put(Language.ENGLISH, "ledger.totalLabel",
                "Total");
        put(Language.ENGLISH, "search.typeToChoose",
                "Type to search. Use the arrow keys and Enter to choose a suggestion.");
        put(Language.ENGLISH, "search.venueMatchesLabel",
                " venue matches");
        put(Language.ENGLISH, "search.venuesMatchLabel",
                " venues match");
        put(Language.ENGLISH, "search.eventMatchesLabel",
                " event matches");
        put(Language.ENGLISH, "search.eventsMatchLabel",
                " events match");
        put(Language.ENGLISH, "search.nextLabel",
                "  •  next ");
        put(Language.ENGLISH, "saved.seatSavedLabel",
                "saved seat");
        put(Language.ENGLISH, "saved.pickupLabel",
                "Picked up ");
        put(Language.ENGLISH, "saved.chooseLabel",
                "Choose");
        put(Language.ENGLISH, "schedules.comingUpLabel",
                " coming up at ");
        put(Language.ENGLISH, "schedules.savedTimeLabel",
                "  •  saved ");
        put(Language.ENGLISH, "receipt.prefix",
                "Receipt ");
        put(Language.ENGLISH, "ticket.prefix",
                "Ticket ");
        put(Language.ENGLISH, "event.doorsLabel",
                "Doors ");

        put(Language.LUGANDA, "nav.backArrow",
                "← Subira");
        put(Language.LUGANDA, "booking.seatLimitLabel",
                "%d ebizitansa");
        put(Language.LUGANDA, "booking.savedOnLabel",
                "  •  ekikumi");
        put(Language.LUGANDA, "payment.mobileNote",
                "•  essimu y'obujjuni alaba, tessuma tetigera");
        put(Language.LUGANDA, "payment.lineLabel",
                "  Okudula: ");
        put(Language.LUGANDA, "print.sentLabel",
                " yatindika ku mashinery");
        put(Language.LUGANDA, "bookings.section.locationLabel",
                "Ekizimbe");
        put(Language.LUGANDA, "bookings.section.addressLabel",
                "Endereza");
        put(Language.LUGANDA, "bookings.section.capacityLabel",
                "Bujja");
        put(Language.LUGANDA, "bookings.section.eventUpper",
                "EKIZIBUBy'OBUJJUNI");
        put(Language.LUGANDA, "bookings.section.eventLabel",
                "EkizibuBy'obujjuni");
        put(Language.LUGANDA, "bookings.section.bookedByUpper",
                "EKIKWATWA N'");
        put(Language.LUGANDA, "bookings.section.paidUpper",
                "EBIZITANSA N'OKUDULA");
        put(Language.LUGANDA, "bookings.seatSubtotalLabel",
                "Jigger ly'ebizitansa");
        put(Language.LUGANDA, "bookings.statusCancelled",
                "EKISIKIZIBWA");
        put(Language.LUGANDA, "occupancy.endLabel",
                "Nsengwe");
        put(Language.LUGANDA, "occupancy.allEventsLabel",
                "Biro byonna");
        put(Language.LUGANDA, "ledger.rowLabel",
                "Olulungi");
        put(Language.LUGANDA, "ledger.bookedOfLabel",
                " ebigiridwa ku ");
        put(Language.LUGANDA, "ledger.bySectionLabel",
                "Ku nsengwe:  ");
        put(Language.LUGANDA, "ledger.totalLabel",
                "Jigger");
        put(Language.LUGANDA, "search.typeToChoose",
                "Andika okushunja. Kigyezi bimutula, era Enter osala okusala.");
        put(Language.LUGANDA, "search.venueMatchesLabel",
                " amabirali gasobola");
        put(Language.LUGANDA, "search.venuesMatchLabel",
                " amabirali gasobola");
        put(Language.LUGANDA, "search.eventMatchesLabel",
                " ekizibuBy'obujjuni kisobola");
        put(Language.LUGANDA, "search.eventsMatchLabel",
                " emizibiso gisobola");
        put(Language.LUGANDA, "search.nextLabel",
                "  •  kiri ");
        put(Language.LUGANDA, "saved.seatSavedLabel",
                "kizitanse ekikumi");
        put(Language.LUGANDA, "saved.pickupLabel",
                "Kiweeka ");
        put(Language.LUGANDA, "saved.chooseLabel",
                "Lunda");
        put(Language.LUGANDA, "schedules.comingUpLabel",
                " ebiri ");
        put(Language.LUGANDA, "schedules.savedTimeLabel",
                "  •  ekikumi ");
        put(Language.LUGANDA, "receipt.prefix",
                "Kasiita ");
        put(Language.LUGANDA, "ticket.prefix",
                "Ekwata ");
        put(Language.LUGANDA, "event.doorsLabel",
                "Emigateeza ");

        put(Language.SWAHILI, "nav.backArrow",
                "← Rudi");
        put(Language.SWAHILI, "booking.seatLimitLabel",
                "%d viti");
        put(Language.SWAHILI, "booking.savedOnLabel",
                "  •  imehifadhiwa");
        put(Language.SWAHILI, "payment.mobileNote",
                "•  malipo ya simu ni ya kuonyesha tu, pesa hazitishukiki");
        put(Language.SWAHILI, "payment.lineLabel",
                "  Malipo: ");
        put(Language.SWAHILI, "print.sentLabel",
                " imetumwa kwa kichapishaji");
        put(Language.SWAHILI, "bookings.section.locationLabel",
                "Mahali");
        put(Language.SWAHILI, "bookings.section.addressLabel",
                "Anwani");
        put(Language.SWAHILI, "bookings.section.capacityLabel",
                "Uwezekano");
        put(Language.SWAHILI, "bookings.section.eventUpper",
                "TUKIO");
        put(Language.SWAHILI, "bookings.section.eventLabel",
                "Tukio");
        put(Language.SWAHILI, "bookings.section.bookedByUpper",
                "IMEBOOKWA NA");
        put(Language.SWAHILI, "bookings.section.paidUpper",
                "VITI NA MALIPO");
        put(Language.SWAHILI, "bookings.seatSubtotalLabel",
                "Jumla ya viti");
        put(Language.SWAHILI, "bookings.statusCancelled",
                "IMEGHAIRIWA");
        put(Language.SWAHILI, "occupancy.endLabel",
                "Pande");
        put(Language.SWAHILI, "occupancy.allEventsLabel",
                "Matukio yote");
        put(Language.SWAHILI, "ledger.rowLabel",
                "Safu");
        put(Language.SWAHILI, "ledger.bookedOfLabel",
                " viliyookwa kati ya ");
        put(Language.SWAHILI, "ledger.bySectionLabel",
                "Kwa pande:  ");
        put(Language.SWAHILI, "ledger.totalLabel",
                "Jumla");
        put(Language.SWAHILI, "search.typeToChoose",
                "Andika kutafuta. Tumia vishale na Enter kuchagua.");
        put(Language.SWAHILI, "search.venueMatchesLabel",
                " majengo yanayolingana");
        put(Language.SWAHILI, "search.venuesMatchLabel",
                " majengo yanayolingana");
        put(Language.SWAHILI, "search.eventMatchesLabel",
                " tukio linalolingana");
        put(Language.SWAHILI, "search.eventsMatchLabel",
                " matukio yanayolingana");
        put(Language.SWAHILI, "search.nextLabel",
                "  •  zijazo ");
        put(Language.SWAHILI, "saved.seatSavedLabel",
                "kiti kilichohifadhiwa");
        put(Language.SWAHILI, "saved.pickupLabel",
                "Imepelekwa ");
        put(Language.SWAHILI, "saved.chooseLabel",
                "Chagua");
        put(Language.SWAHILI, "schedules.comingUpLabel",
                " inayokuja kwenye ");
        put(Language.SWAHILI, "schedules.savedTimeLabel",
                "  •  imehifadhiwa ");
        put(Language.SWAHILI, "receipt.prefix",
                "Risiti ");
        put(Language.SWAHILI, "ticket.prefix",
                "Tikiti ");
        put(Language.SWAHILI, "event.doorsLabel",
                "Lango ");

    }
}
