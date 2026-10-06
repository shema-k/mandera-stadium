package com.stadium.booking.ui;

import com.stadium.booking.booking.Booking;
import com.stadium.booking.booking.BookingService;
import com.stadium.booking.booking.CustomerDetails;
import com.stadium.booking.booking.FormRules;
import com.stadium.booking.booking.OccupancyReport;
import com.stadium.booking.booking.PaymentRecord;
import com.stadium.booking.booking.Receipt;
import com.stadium.booking.booking.TicketBuilder;
import com.stadium.booking.data.AnnouncementType;
import com.stadium.booking.data.Seat;
import com.stadium.booking.data.SeatKey;
import com.stadium.booking.data.SeatSection;
import com.stadium.booking.data.Stadium;
import com.stadium.booking.data.StadiumAnnouncement;
import com.stadium.booking.data.StadiumData;
import com.stadium.booking.data.StadiumDetails;
import com.stadium.booking.data.StadiumEvent;
import com.stadium.booking.data.StadiumShape;
import com.stadium.booking.storage.BookingStore;
import com.stadium.booking.storage.Database;
import com.stadium.booking.text.Messages;
import com.stadium.booking.text.Theme;
import com.stadium.booking.text.ThemePreference;
import com.stadium.booking.text.VenueWords;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
/**
 * Professional desktop venue directory and stadium seat-booking application
 * built entirely with Java Swing.
 *
 * <p>The user flow is deliberately linear: choose a stadium, choose a date,
 * choose a game or concert, choose seats, and confirm the booking.</p>
 */
@SuppressWarnings("serial")
public final class StadiumBookingApp extends JFrame {
    // These are repainted in place by applyThemeColours() when dark mode is
    // switched on or off, so the several hundred places that use them need no
    // change and no second version of themselves.
    private static Color NAVY;
    private static Color NAVY_SOFT;
    private static Color BLUE;
    private static Color BLUE_DARK;
    private static Color PURPLE;
    private static Color TEAL;
    private static Color SKY;
    private static Color PAGE;
    private static Color WHITE;
    private static Color TEXT;
    private static Color MUTED;
    private static Color BORDER;
    private static Color SUCCESS_DARK;
    private static Color SUCCESS_FOREGROUND;
    private static Color WARNING_FOREGROUND;
    private static Color CANCELLED;
    private static Color FIELD;
    private static Color DANGER;

    /** Copies the active palette into the names the screens already use. */
    private static void applyThemeColours() {
        Theme.Palette palette = Theme.current();
        NAVY = palette.header();
        NAVY_SOFT = palette.headerSoft();
        BLUE = palette.accent();
        BLUE_DARK = palette.accentDark();
        PURPLE = palette.accentDark();
        TEAL = palette.success();
        SKY = palette.tableHeader();
        PAGE = palette.page();
        WHITE = palette.card();
        TEXT = palette.text();
        MUTED = palette.muted();
        BORDER = palette.border();
        SUCCESS_DARK = palette.success();
        SUCCESS_FOREGROUND = palette.success();
        WARNING_FOREGROUND = palette.warning();
        CANCELLED = palette.cancelled();
        FIELD = palette.field();
        DANGER = palette.danger();
    }

    private static final DateTimeFormatter CREATED_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
                    .withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    private final BookingService bookingService;
    private final SeatMapPanel seatMapPanel;
    private final JPanel contentHost = new JPanel(new BorderLayout());
    private final JLabel headerTitle = new JLabel();
    private final JLabel headerSubtitle = new JLabel();
    private final JLabel statusValue = new JLabel(Messages.get("status.chooseStadium"));
    private final SearchField eventSearchField =
            new SearchField(Messages.get("stadium.search.hint"));
    private final SearchField bookingSearchField =
            new SearchField(Messages.get("search.seatsHint"));
    private final SearchField liveSearchField =
            new SearchField(Messages.get("search.stadiumHint"));
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JLabel selectedSeatsValue = new JLabel(text("booking.noSeats"));
    private final JLabel totalValue = new JLabel("$0.00");
    private final JLabel bookingAvailabilityValue = new JLabel();
    private final JLabel bookingCountdownValue = new JLabel();
    private JPanel pricingBreakdownHost;
    private final Map<JLabel, StadiumEvent> countdownLabels = new LinkedHashMap<>();
    private final Map<JButton, StadiumEvent> eventActionButtons = new LinkedHashMap<>();
    private final DefaultTableModel bookingTableModel;
    private final JTable bookingTable;
    private JComboBox<Messages.Language> languageCombo;
    private final FeedbackButton darkModeButton = new FeedbackButton("☾  Light");
    private final JButton backNavButton = new FeedbackButton(Messages.get("nav.back"));
    private final JButton stadiumNavButton = new FeedbackButton(Messages.get("nav.venues"));
    private final JButton bookingsNavButton = new FeedbackButton(Messages.get("nav.bookings"));
    private final JButton seatLedgerNavButton = new FeedbackButton(Messages.get("nav.bookedSeats"));
    private final JButton occupancyNavButton = new FeedbackButton(Messages.get("nav.occupancy"));
    private final JButton savedNavButton = new FeedbackButton(Messages.get("nav.saved"));

    private Stadium selectedStadium;
    private StadiumEvent selectedEvent;
    private LocalDate selectedScheduleDate;
    private List<LocalDate> scheduleDates = new ArrayList<>();
    private List<StadiumEvent> ledgerEventOptions = new ArrayList<>();
    private JComboBox<String> liveDateCombo;
    private JComboBox<String> liveTypeCombo;
    private JPanel liveListHost;
    private JLabel liveResultLabel;
    private JPanel scheduleListHost;
    private JLabel scheduleResultLabel;
    private JComboBox<String> dateCombo;
    private JButton clearSelectionButton;
    private JButton confirmBookingButton;
    private JButton cancelBookingButton;
    /**
     * The booking button on the event page. The same button is reached as both
     * fields, since it is one control rather than a pair of near-duplicates.
     */
    private JButton reviewSeatsButton;
    private JButton eventConfirmButton;
    /**
     * Holds the event page's action row so it can be rebuilt as seats are chosen.
     * The booking button only exists once there is a selection, so it cannot be
     * simply enabled or disabled.
     */
    private JPanel eventActionsHost;
    /** The two live figures on the event page, rewritten on every seat change. */
    private JLabel eventAvailabilityLabel;
    private JLabel eventSelectionLabel;
    private JComboBox<String> ledgerStadiumCombo;
    private JComboBox<String> ledgerEventCombo;
    private DefaultTableModel ledgerTableModel;
    private JTable ledgerTable;
    private JLabel ledgerSummaryLabel;
    private JLabel ledgerSectionLabel;
    private String currentScreen = "directory";
    /**
     * Which screen the booking screen was opened from, so Back returns to the
     * seat picker the customer just used rather than skipping past it.
     */
    private String bookingReturnScreen = "";
    /** Where the bookings live, beside the application. */
    private static final java.nio.file.Path BOOKING_DATABASE =
            new java.io.File("stadium-bookings.dat").toPath();
    private final Database database;
    private JLabel statusHeading;
    private JPanel statusBar;
    private JPanel headerPanel;
    private JPanel root;
    private final String sessionOwner = UUID.randomUUID().toString();
    private JLabel holdCountdownValue;
    private String ledgerReturnScreen = "directory";
    private String eventReturnScreen = "stadium";
    /** Ticks once a second to keep countdowns and seat holds current. */
    private final Timer countdownTimer;
    private final Timer liveSearchTimer = new Timer(200, event -> {
        if ("schedules".equals(currentScreen)) {
            refreshLiveSchedules();
        }
    });

    public StadiumBookingApp() {
        super(text("app.title"));
        // Colours have to be in place before the first screen is painted.
        ThemePreference.restore();
        applyThemeColours();
        liveSearchTimer.setRepeats(false);
        // One embedded database holds the bookings, opened directly in the
        // working directory. No account, no password, nothing to unlock.
        database = new Database(BOOKING_DATABASE);
        bookingService = new BookingService(database);
        seatMapPanel = new SeatMapPanel(bookingService, this::onSeatToggled,
                this::updateBookingSummary, this::showStatus);
        seatMapPanel.setHoldOwner(sessionOwner);
        bookingTableModel = new DefaultTableModel(
                new Object[]{Messages.get("bookings.table.reference"), "Stadium", "Event", "When", "Seats", "Total", Messages.get("bookings.table.status")}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingTable = new JTable(bookingTableModel);
        bookingTable.getSelectionModel().addListSelectionListener(event -> updateCancelButton());
        bookingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (!SwingUtilities.isLeftMouseButton(event)) {
                    return;
                }
                int row = bookingTable.rowAtPoint(event.getPoint());
                if (row >= 0) {
                    bookingTable.setRowSelectionInterval(row, row);
                    showBookingDetails(row);
                }
            }
        });

        countdownTimer = new Timer(1_000, event -> {
            updateCountdownDisplays();
            seatMapPanel.getHoldService().purgeExpired();
            seatMapPanel.refreshStatuses();
            updateHoldCountdown();
        });
        countdownTimer.setRepeats(true);

        configureWindow();
        installSearchListeners();
        buildShell();
        countdownTimer.start();
        showStadiumDirectory();
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 740));
        setSize(1320, 900);
        setLocationRelativeTo(null);
        rethemeChrome();
    }

    /**
     * Repaints the window furniture that is built once and lives outside any
     * screen: the frame, the page host and the status bar. Without this the light
     * margins and status bar stayed light after dark mode was switched on.
     */
    private void rethemeChrome() {
        if (getContentPane() != null) {
            getContentPane().setBackground(PAGE);
        }
        if (root != null) {
            root.setBackground(PAGE);
            root.setBorder(BorderFactory.createLineBorder(BORDER));
        }
        if (contentHost != null) {
            contentHost.setBackground(PAGE);
        }
        if (statusBar != null) {
            statusBar.setBackground(WHITE);
            statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        }
        if (statusHeading != null) {
            statusHeading.setForeground(MUTED);
        }
        if (statusValue != null) {
            statusValue.setForeground(SUCCESS_DARK);
        }
        if (headerPanel != null) {
            headerPanel.setBackground(NAVY);
        }
        for (JTextField field : new JTextField[]{eventSearchField,
                bookingSearchField, liveSearchField, nameField, emailField, phoneField}) {
            if (field == null) {
                continue;
            }
            field.setForeground(TEXT);
            field.setBackground(FIELD);
            field.setCaretColor(BLUE);
        }
    }

    private void installSearchListeners() {
        // There is no venue picker to filter any more, so the stadium search field and
        // its suggestion popup are gone with it.
        addDocumentListener(eventSearchField, this::refreshScheduleIfVisible);
        addDocumentListener(bookingSearchField, this::refreshBookingsIfVisible);
        // Live suggestion lists that refresh as each field is typed into.
        new SearchSuggestions(eventSearchField, this::eventSuggestions);
        new SearchSuggestions(bookingSearchField, this::bookingSuggestions);
        new SearchSuggestions(liveSearchField, this::eventSuggestions);
        addDocumentListener(liveSearchField, liveSearchTimer::restart);
    }

    private void addDocumentListener(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                action.run();
            }
        });
    }

    /** Rebuilt whenever the language changes. */
    private JLabel brandLabel;

    private void buildShell() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE);
        this.root = root;
        root.add(buildHeader(), BorderLayout.NORTH);
        contentHost.setBackground(PAGE);
        contentHost.setBorder(new EmptyBorder(0, 24, 0, 24));
        root.add(contentHost, BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildHeader() {
        // A two column grid rather than west/east regions: the title then takes
        // whatever space is left instead of being overlapped by the navigation
        // once there are enough buttons to fill the bar.
        JPanel header = new JPanel(new GridBagLayout());
        headerPanel = header;
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(49, 73, 108)),
                new EmptyBorder(18, 28, 18, 28)));

        JPanel titleBlock = new JPanel(new GridBagLayout());
        titleBlock.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.WEST;
        JLabel brand = new JLabel(text("app.tagline"));
        brandLabel = brand;
        brand.setForeground(new Color(147, 197, 253));
        brand.setFont(brand.getFont().deriveFont(Font.BOLD, 10f));
        titleBlock.add(brand, constraints);

        headerTitle.setForeground(WHITE);
        headerTitle.setFont(headerTitle.getFont().deriveFont(Font.BOLD, 23f));
        constraints.gridy = 1;
        constraints.insets = new Insets(3, 0, 0, 0);
        titleBlock.add(headerTitle, constraints);

        headerSubtitle.setForeground(new Color(190, 207, 232));
        headerSubtitle.setFont(headerSubtitle.getFont().deriveFont(Font.PLAIN, 12f));
        constraints.gridy = 2;
        constraints.insets = new Insets(3, 0, 0, 0);
        titleBlock.add(headerSubtitle, constraints);

        languageCombo = new JComboBox<>(Messages.Language.values());
        languageCombo.setSelectedItem(Messages.getLanguage());
        languageCombo.setFont(languageCombo.getFont().deriveFont(Font.BOLD, 10f));
        languageCombo.setFocusable(false);
        languageCombo.setPreferredSize(new Dimension(110, 28));
        languageCombo.setToolTipText(text("app.languageHint"));
        describe(languageCombo, text("app.languageName"), text("app.languageDescription"));
        languageCombo.addActionListener(event -> {
            Messages.setLanguage((Messages.Language) languageCombo.getSelectedItem());
            applyLanguage();
        });

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 17));
        navigation.setOpaque(false);
        navigation.add(languageCombo);
        styleHeaderButton(darkModeButton);
        darkModeButton.setPreferredSize(new Dimension(96, 30));
        darkModeButton.addActionListener(event -> toggleDarkMode());
        describe(darkModeButton, Messages.get("theme.darkName"),
                Messages.get("nav.venues.desc"));
        navigation.add(darkModeButton);
        styleHeaderButton(backNavButton);
        styleHeaderButton(stadiumNavButton);
        styleHeaderButton(bookingsNavButton);
        styleHeaderButton(seatLedgerNavButton);
        styleHeaderButton(occupancyNavButton);
        styleHeaderButton(savedNavButton);
        backNavButton.addActionListener(event -> goBack());
        stadiumNavButton.addActionListener(event -> showStadiumDirectory());
        bookingsNavButton.addActionListener(event -> showBookings());
        seatLedgerNavButton.addActionListener(event -> showSeatLedger());
        occupancyNavButton.addActionListener(event -> showOccupancyReport());
        savedNavButton.addActionListener(event -> showSavedSeats());
        describe(backNavButton, "Back", Messages.get("nav.back.desc"));
        describe(stadiumNavButton, "Stadium", Messages.get("nav.venues.desc"));
        describe(bookingsNavButton, "My bookings",
                Messages.get("nav.bookings.desc"));
        describe(seatLedgerNavButton, Messages.get("bookings.field.bookedSeats"),
                Messages.get("nav.bookedSeats.desc"));
        describe(occupancyNavButton, "Occupancy",
                Messages.get("nav.occupancy.desc"));
        describe(savedNavButton, "Saved seats",
                Messages.get("nav.saved.desc"));
        navigation.add(backNavButton);
        navigation.add(stadiumNavButton);
        navigation.add(bookingsNavButton);
        navigation.add(seatLedgerNavButton);
        navigation.add(occupancyNavButton);
        navigation.add(savedNavButton);

        GridBagConstraints headerConstraints = new GridBagConstraints();
        headerConstraints.gridx = 0;
        headerConstraints.weightx = 1;
        headerConstraints.fill = GridBagConstraints.HORIZONTAL;
        headerConstraints.anchor = GridBagConstraints.LINE_START;
        header.add(titleBlock, headerConstraints);
        // No forced size on the navigation: it sets its own height, and imposing
        // one cut the buttons in half.
        headerConstraints.gridx = 1;
        headerConstraints.weightx = 0;
        headerConstraints.fill = GridBagConstraints.NONE;
        headerConstraints.insets = new Insets(0, 16, 0, 0);
        header.add(navigation, headerConstraints);
        return header;
    }

    private void styleHeaderButton(JButton button) {
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11f));
        button.setForeground(new Color(219, 234, 254));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        // Keeps the navy header visible through the button; the outline and text
        // carry the hover and pressed feedback instead of a background fill.
        button.setOpaque(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(76, 112, 164)),
                new EmptyBorder(8, 12, 8, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addInteractionFeedback(button);
    }

    private JPanel buildStatusBar() {
        JPanel status = new JPanel(new BorderLayout());
        statusBar = status;
        status.setBackground(WHITE);
        status.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(7, 24, 7, 24)));
        JLabel label = new JLabel(text("booking.statusHeader"));
        statusHeading = label;
        label.setForeground(MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10f));
        statusValue.setForeground(SUCCESS_DARK);
        statusValue.setFont(statusValue.getFont().deriveFont(Font.PLAIN, 11f));
        statusValue.setBorder(new EmptyBorder(0, 14, 0, 0));
        status.add(label, BorderLayout.WEST);
        status.add(statusValue, BorderLayout.CENTER);
        return status;
    }

    /** Shorthand for the current language's wording. */
    private static String text(String key) {
        return Messages.get(key);
    }

    private static String text(String key, Object... arguments) {
        return Messages.get(key, arguments);
    }

    /**
     * Re-applies the current language to the header, then redraws whatever screen
     * is showing.
     *
     * <p>Every screen is redrawn, not just the booking one. It used to redraw
     * only that, so picking another language left the schedule, the bookings
     * list, the occupancy report and the saved seats reading in the old language
     * while the header and the buttons changed. Whichever screen the customer is
     * on is the one they are looking at, so that is the one that has to follow.
     */
    private void applyLanguage() {
        if (brandLabel != null) {
            brandLabel.setText(text("app.tagline"));
        }
        backNavButton.setText(text("nav.back"));
        stadiumNavButton.setText(text("nav.venues"));
        bookingsNavButton.setText(text("nav.bookings"));
        seatLedgerNavButton.setText(text("nav.bookedSeats"));
        occupancyNavButton.setText(text("nav.occupancy"));
        savedNavButton.setText(text("nav.saved"));
        languageCombo.setToolTipText(text("app.languageHint"));
        describe(languageCombo, text("app.languageName"), text("app.languageDescription"));
        // The search boxes were given their placeholder when the application was
        // built, so without this they keep the word they were first given. They
        // are long-lived components rather than part of any one screen, which is
        // why the redraw below does not reach them.
        eventSearchField.setHint(text("stadium.search.hint"));
        bookingSearchField.setHint(text("search.seatsHint"));
        liveSearchField.setHint(text("search.stadiumHint"));
        // Likewise the two figures that sit in the header from the moment the
        // application opens, before any screen has been drawn.
        if (Messages.get("booking.noSeats").equals(selectedSeatsValue.getText())) {
            selectedSeatsValue.setText(text("booking.noSeats"));
        }
        statusValue.setText(Messages.get("status.chooseStadium"));
        updateDarkModeButton();
        seatMapPanel.retranslate();
        setTitle(text("app.title"));
        redrawCurrentScreen();
    }

    /**
     * Rebuilds the screen that is showing, in the language now selected.
     *
     * <p>Each screen is rebuilt by calling the same method that opened it, so
     * there is one way to build a screen rather than a second one kept in step by
     * hand. Anything a screen cannot rebuild without losing the customer's place
     * — the seat map mid-selection, for instance — is left as it is, because
     * changing language must never discard the seats someone has chosen.
     */
    private void redrawCurrentScreen() {
        switch (currentScreen == null ? "" : currentScreen) {
            case "stadium":
                showStadiumDirectory();
                break;
            case "stadium-details":
                if (selectedStadium != null) {
                    showStadiumDetails(selectedStadium);
                }
                break;
            case "schedules":
                showLiveSchedules();
                break;
            case "event-details":
            case "booking":
                if (selectedEvent != null) {
                    showEventDetails(selectedEvent);
                }
                break;
            case "bookings":
                showBookings();
                break;
            case "seats":
                showSeatLedger();
                break;
            case "occupancy":
                showOccupancyReport();
                break;
            case "saved-seats":
                showSavedSeats();
                break;
            default:
                // The booking screen shares the seat map with the event screen and
                // holds the chosen seats, so it is redrawn in place rather than
                // rebuilt. Losing a selection because someone switched language
                // would be far worse than a few labels reading in the old one.
                if (selectedEvent != null && selectedStadium != null) {
                    refreshBookingScreen();
                }
                break;
        }
    }

    /** Rebuilds the booking screen so its wording follows the language. */
    private void refreshBookingScreen() {
        if (selectedEvent == null || selectedStadium == null) {
            return;
        }
        setHeader(text("booking.title"),
                selectedStadium.getName() + "  •  " + selectedEvent.getHeadline());
        contentHost.removeAll();
        contentHost.add(buildBookingScreen(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        updateBookingSummary();
    }

    private void setHeader(String title, String subtitle) {
        headerTitle.setText(clipHeaderTitle(title));
        headerSubtitle.setText(clipHeaderTitle(subtitle));
        // The stadium screen is the front door, so there is nowhere to go back to from
// it and the button is disabled rather than left as a control that does nothing.
        backNavButton.setEnabled(!"stadium".equals(currentScreen));
    }

    /**
     * Keeps a long venue or event name from running under the navigation. The bar
     * holds nine buttons, so the title has a fixed ceiling rather than whatever
     * width the text happens to want.
     */
    private String clipHeaderTitle(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= 52 ? text : text.substring(0, 50).trim() + "\u2026";
    }

    /**
     * The wording of the "back" button on a dialog.
     *
     * <p>Built from the table rather than typed in, so the dialog button follows
     * the language along with everything else. A dialog offering "← Back" in
     * English while the screen behind it is in Luganda is the sort of thing that
     * makes an interface feel half finished.
     */
    private static String backLabel() {
        return "← " + Messages.get("common.back");
    }

    /**
     * Shows a modal dialog with custom buttons and returns the label of the button the
     * user pressed.
     *
     * <p>{@link JOptionPane#showOptionDialog} returns the <em>index</em> of the chosen
     * option rather than the option object itself, so the index is mapped back to its
     * label here. Without this mapping every custom button would compare unequal to its
     * own label and the action would be silently discarded. Closing the dialog with the
     * window button returns {@link JOptionPane#CLOSED_OPTION} and yields {@code null}.
     */
    private String showDialogChoice(Component parent, Object message, String title,
                                    int messageType, String initialChoice, String... choices) {
        int index = JOptionPane.showOptionDialog(parent, message, title,
                JOptionPane.DEFAULT_OPTION, messageType, null, choices, initialChoice);
        if (index < 0 || index >= choices.length) {
            return null;
        }
        return choices[index];
    }

    private void goBack() {
        switch (currentScreen == null ? "" : currentScreen) {
            case "booking":
                // Back from the review step returns to the seat picker the
                // customer just used, rather than skipping past their seats.
                if ("event-details".equals(bookingReturnScreen) && selectedEvent != null) {
                    showEventDetails(selectedEvent);
                } else if (selectedStadium != null) {
                    openStadium(selectedStadium);
                } else {
                    showStadiumDirectory();
                }
                break;
            case "seats":
                if ("booking".equals(ledgerReturnScreen) && selectedEvent != null) {
                    openEvent(selectedEvent);
                } else {
                    showStadiumDirectory();
                }
                break;
            case "event-details":
                // Back to wherever the event was opened from, which is not always
                // the venue's own schedule.
                if ("schedules".equals(eventReturnScreen) || selectedStadium == null) {
                    showLiveSchedules();
                } else {
                    openStadium(selectedStadium);
                }
                break;
            case "stadium-details":
                showStadiumDirectory();
                break;
            case "stadium":
            case "bookings":
            case "schedules":
            case "occupancy":
            case "saved-seats":
                showStadiumDirectory();
                break;
            default:
                showStadiumDirectory();
                break;
        }
    }

    // ---------------------------------------------------------------------
    // Stadium
    // ---------------------------------------------------------------------

    /**
     * The front screen. This system books one stadium, so rather than asking the
     * customer to choose a venue from a list, it opens Namboole directly with
     * its schedule, its notices and the way into its seat map.
     */
    private void showStadiumDirectory() {
        openStadium(StadiumData.getStadiums().get(0));
    }

    private JPanel createBookingSteps() {
        JPanel steps = createCard();
        steps.setLayout(new BorderLayout(20, 0));
        steps.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 18, 14, 18)));
        JLabel heading = new JLabel(text("directory.howItWorks"));
        heading.setForeground(MUTED);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 10f));
        steps.add(heading, BorderLayout.WEST);
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        // There is no venue to choose: the stadium is fixed, so the first step is
        // finding what is on rather than picking where to go.
        row.add(createStep("1", text("home.step1"), text("home.step1Desc")));
        row.add(createStep("2", text("home.step2"), text("home.step2Desc")));
        row.add(createStep("3", text("home.step3"), text("home.step3Desc")));
        row.add(createStep("4", text("home.step4"), text("home.step4Desc")));
        steps.add(row, BorderLayout.CENTER);
        return steps;
    }

    /**
     * One numbered step in the "how it works" strip.
     *
     * @param title    the step itself, read at a glance
     * @param detail   the sentence under it, so the step says what to do rather
     *                 than only naming the thing being done
     */
    private JPanel createStep(String number, String title, String detail) {
        JPanel step = new JPanel(new BorderLayout(8, 0));
        step.setOpaque(false);
        JLabel circle = new JLabel(number, SwingConstants.CENTER);
        circle.setOpaque(true);
        circle.setBackground(SKY);
        circle.setForeground(BLUE_DARK);
        circle.setFont(circle.getFont().deriveFont(Font.BOLD, 11f));
        circle.setPreferredSize(new Dimension(25, 25));
        circle.setVerticalAlignment(SwingConstants.TOP);

        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 11f));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        copy.add(titleLabel);
        if (detail != null && !detail.isEmpty()) {
            JLabel detailLabel = new JLabel(detail);
            detailLabel.setForeground(MUTED);
            detailLabel.setFont(detailLabel.getFont().deriveFont(Font.PLAIN, 10f));
            detailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            copy.add(detailLabel);
        }
        step.add(circle, BorderLayout.WEST);
        step.add(copy, BorderLayout.CENTER);
        return step;
    }

    // ---------------------------------------------------------------------
    // Stadium details
    // ---------------------------------------------------------------------

    /**
     * Everything the application knows about one venue, in one screen.
     *
     * <p>The picture, the address, the description, the four sections with their
     * real prices, what is on there and how full it is. The schedule and the seat
     * map are one click away, so choosing a venue still leads to booking.
     */
    private void showStadiumDetails(Stadium stadium) {
        currentScreen = "stadium-details";
        selectedStadium = stadium;
        StadiumDetails details = StadiumDetails.of(stadium, bookingService);
        setHeader(stadium.getName(),
                details.getSummary());
        contentHost.removeAll();
        contentHost.add(buildStadiumDetailsContent(stadium, details), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        showStatus(stadium.getName() + "  •  " + stadium.getLocation());
    }

    private JPanel buildStadiumDetailsContent(Stadium stadium, StadiumDetails details) {
        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(16, 0, 20, 0));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JButton back = createOutlineButton(text("common.allStadiums"), BLUE);
        back.addActionListener(event -> showStadiumDirectory());
        top.add(back);
        page.add(top, BorderLayout.NORTH);

        JPanel middle = new JPanel();
        middle.setOpaque(false);
        middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));

        // The venue plan used to sit beside these figures. It is gone: the page
        // now leads with the numbers and the way to pick seats, and the freed
        // width goes to the facts instead of leaving a gap.
        JPanel factsCard = createCard();
        factsCard.setLayout(new BorderLayout(0, 10));
        factsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 14, 12, 14)));
        factsCard.add(buildQuickFacts(stadium, details), BorderLayout.CENTER);
        factsCard.add(buildHighlights(details), BorderLayout.SOUTH);
        // Without a ceiling the card stretched to the full height of the page once
        // the picture stopped sharing the row, which spread four short figures
        // across a tall empty band. Held to its own height instead.
        factsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                factsCard.getPreferredSize().height));
        middle.add(factsCard);

        middle.add(Box.createVerticalStrut(12));

        JPanel lower = new JPanel(new BorderLayout(12, 0));
        lower.setOpaque(false);
        JPanel about = buildAboutCard(stadium);
        about.setPreferredSize(new Dimension(320, 100));
        JPanel sections = buildSectionsCard(details);
        sections.setPreferredSize(new Dimension(430, 100));
        JPanel side = buildSideCard(details, stadium);
        side.setPreferredSize(new Dimension(400, 100));
        lower.add(about, BorderLayout.WEST);
        lower.add(sections, BorderLayout.CENTER);
        lower.add(side, BorderLayout.EAST);
        middle.add(lower);

        page.add(middle, BorderLayout.CENTER);
        page.add(buildDetailsActions(stadium), BorderLayout.SOUTH);
        return page;
    }

    /** The handful of facts worth reading first, across the top of the page. */
    private JPanel buildHighlights(StadiumDetails details) {
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        StadiumEvent next = details.getNextEvent();
        if (next != null) {
            text.add(sideText("<b>" + text("home.nextUp") + "</b>  " + next.getHeadline(),
                    "#1e293b", 12f));
            text.add(sideText(Messages.dateLabel(next.getDate()) + "  •  "
                    + Messages.get("event.starts", next.getTimeLabel(), next.getDoorsLabel()),
                    "#64748b", 10f));
        } else {
            text.add(sideText(text("home.noEvents"), "#64748b", 11f));
        }
        text.add(Box.createVerticalStrut(8));
        // The limit is read from the constant rather than written out. It said
        // "Six seats" here while the code enforced a different number, so the
        // page quietly contradicted itself.
        text.add(sideText(text("home.bookingCloses") + " " + (next == null ? "\u2014"
                : next.getBookingDeadlineLabel()) + ". " + text("home.seatLimit", BookingService.MAX_SEATS_PER_BOOKING) + ", " + text("home.noBookingLimit"),
                "#64748b", 10f));
        return text;
    }

    /** Capacity, shape, venue type and price span, across the top. */
    private JPanel buildQuickFacts(Stadium stadium, StadiumDetails details) {
        // One row of four rather than two rows of two: the plan that used to sit
        // beside these left the full page width for them.
        JPanel facts = new JPanel(new GridLayout(1, 4, 18, 0));
        facts.setOpaque(false);
        facts.add(factTile(text("home.capacity"),
                StadiumPhotoPanel.compact(stadium.getCapacity()),
                text("home.capacityHint")));
        // The shape is described by the shape itself, so the hint underneath names
        // the same thing in the language of the screen rather than repeating it.
        facts.add(factTile(text("home.shape"),
                VenueWords.shape(stadium.getShape(), stadium.getShapeLabel()),
                stadium.getShape() == StadiumShape.BOX
                        ? text("home.shapeStraight") : text("home.shapeOval")));
        facts.add(factTile(text("home.seatPrice"), details.getPriceSpan(), text("home.seatPriceHint")
                + " " + BookingService.formatMoney(details.getBookingFee()) + " "
                + text("home.feeHint")));
        facts.add(factTile(text("home.onSaleNow"),
                        Messages.count("home.eventCount", details.getEventCount()),
                String.format(Locale.US, "%.2f%%", details.getVacancyPercentage())
                        + " " + text("home.vacantWord")));
        return facts;
    }

    private JPanel factTile(String heading, String value, String note) {
        JPanel tile = new JPanel(new GridBagLayout());
        tile.setOpaque(false);
        tile.setBorder(new EmptyBorder(4, 0, 4, 0));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel label = new JLabel(heading.toUpperCase(Locale.ENGLISH));
        label.setForeground(MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 9f));
        tile.add(label, constraints);
        constraints.gridy = 1;
        constraints.insets = new Insets(2, 0, 0, 0);
        JLabel amount = new JLabel(value);
        amount.setForeground(TEXT);
        amount.setFont(amount.getFont().deriveFont(Font.BOLD, 13f));
        tile.add(amount, constraints);
        constraints.gridy = 2;
        constraints.insets = new Insets(1, 0, 0, 0);
        JLabel sub = new JLabel(note);
        sub.setForeground(MUTED);
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 9f));
        tile.add(sub, constraints);
        return tile;
    }

    private JPanel buildAboutCard(Stadium stadium) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 14, 12, 14)));

        JLabel title = new JLabel(text("stadium.about"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        card.add(title, BorderLayout.NORTH);

        // One HTML document rather than a stack of labels: the wrapping, the
        // alignment and the column widths are then the editor pane's problem and
        // not a row of panels fighting over a fixed height.
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:SansSerif; margin:0;'>");
        detailRow(html, Messages.get("facts.where"), stadium.getAddress());
        detailRow(html, Messages.get("facts.city"), stadium.getCity() + ", " + stadium.getCountry());
        detailRow(html, Messages.get("facts.type"), stadium.getVenueType());
        detailRow(html, Messages.get("facts.footprint"), VenueWords.shape(stadium.getShape(), stadium.getShapeLabel()));
        detailRow(html, Messages.get("facts.seatsOnSale"), String.valueOf(stadium.getSeatCount()));
        detailRow(html, Messages.get("facts.sections"), stadium.getSections().size() + " independent");
        html.append("<p style='margin-top:10px; color:#1e293b; font-size:11px;'>")
                .append(stadium.getDescription()).append("</p>");
        html.append("</body></html>");
        card.add(htmlScroll(html.toString(), 11f), BorderLayout.CENTER);
        return card;
    }

    /** One label-and-value line in the About card. */
    private void detailRow(StringBuilder html, String heading, String value) {
        html.append("<div style='margin-bottom:5px;'>")
                .append("<span style='color:#64748b; font-size:10px;'>").append(heading)
                .append("</span><br>")
                .append("<span style='color:#1e293b; font-size:11px;'>").append(value)
                .append("</span></div>");
    }

    /**
     * A read-only HTML pane that wraps its content to the width it is given.
     * Used instead of stacked labels, which is what was clipping the text.
     */
    private JComponent htmlScroll(String html, float baseSize) {
        JEditorPane pane = new JEditorPane("text/html", html);
        pane.setEditable(false);
        pane.setOpaque(false);
        pane.setBorder(BorderFactory.createEmptyBorder());
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        pane.setFont(pane.getFont().deriveFont(Font.PLAIN, baseSize));
        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getHorizontalScrollBar().setVisible(false);
        // An HTML pane opens showing the bottom of its document, which hid the
        // first lines of the About card. Pin it back to the top once sized.
        pane.setCaretPosition(0);
        // An HTML pane sizes itself to its longest line, so it has to be pinned to
        // the viewport width or the text runs off the side instead of wrapping.
        scroll.getViewport().addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent event) {
                java.awt.Dimension extent = scroll.getViewport().getExtentSize();
                pane.setSize(new java.awt.Dimension(extent.width, Short.MAX_VALUE));
            }
        });
        SwingUtilities.invokeLater(() -> {
            pane.setCaretPosition(0);
            java.awt.Dimension extent = scroll.getViewport().getExtentSize();
            pane.setSize(new java.awt.Dimension(extent.width, Short.MAX_VALUE));
            scroll.getViewport().setViewPosition(new java.awt.Point(0, 0));
        });
        return scroll;
    }

    /**
     * What is on, availability and notices, in one scrolling column. Split across
     * two cards they were squeezed to nothing by the height left under the picture.
     */
    private JPanel buildSideCard(StadiumDetails details, Stadium stadium) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 14, 12, 14)));

        JLabel title = new JLabel(text("stadium.atThisVenue"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        card.add(title, BorderLayout.NORTH);

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family:SansSerif; margin:0;'>");

        htmlHeading(html, Messages.get("stadium.whatIsOn"), details.getEventCount() + " upcoming");
        html.append("<div style='color:#64748b; font-size:9px; margin:-2px 0 4px 0;'>")
                .append(details.getGameCount()).append(" games, ")
                .append(details.getConcertCount()).append(" concerts</div>");
        html.append("<table width='100%' cellpadding='0' cellspacing='0'>");
        for (StadiumEvent event : details.getEvents()) {
            boolean open = bookingService.isBookingOpen(event);
            html.append("<tr><td width='106' valign='top' style='color:#64748b; font-size:9px;'>")
                    .append(Messages.dateLabel(event.getDate())).append("</td>")
                    .append("<td valign='top' style='font-size:10px; color:")
                    .append(open ? "#1e293b" : "#94a3b8").append(";'>")
                    .append(event.getHeadline());
            if (!open) {
                html.append(" <span style='color:#b91c1c; font-weight:bold;'>closed</span>");
            }
            html.append("</td></tr>");
        }
        html.append("</table>");

        if (!details.getTeams().isEmpty()) {
            html.append("<p style='margin:8px 0 0 0; color:#64748b; font-size:10px;'>")
                    .append("<b>Clubs and teams:</b> ")
                    .append(joinNames(details.getTeams())).append("</p>");
        }
        if (!details.getArtists().isEmpty()) {
            html.append("<p style='margin:6px 0 0 0; color:#64748b; font-size:10px;'>")
                    .append("<b>Artists:</b> ")
                    .append(joinNames(details.getArtists())).append("</p>");
        }

        htmlHeading(html, Messages.get("stadium.availability"),
                String.format(Locale.US, "%.2f%% vacant", details.getVacancyPercentage()));
        html.append("<div style='color:#475569; font-size:10px;'>")
                .append(String.format(Locale.US, "%s of %s seats sold across the %d events.",
                        String.valueOf(details.getSeatsBooked()),
                        String.valueOf(details.getSeatsOnSale()),
                        details.getEventCount())).append("</div>");
        if (details.getBlockedEvents().isEmpty()) {
            html.append("<div style='color:#15803d; font-size:10px;'>")
                    .append("Every event here is open for booking.</div>");
        } else {
            html.append("<div style='color:#b91c1c; font-size:10px;'><b>")
                    .append(details.getBlockedEvents().size())
                    .append(" event(s) cannot be booked</b> because of a cancellation or an ")
                    .append("emergency notice, and are marked on the schedule.</div>");
        }

        if (details.hasNotices()) {
            htmlHeading(html, Messages.get("stadium.notices"), details.getNotices().size() + " posted");
            for (StadiumAnnouncement notice : details.getNotices()) {
                html.append("<div style='color:#475569; font-size:10px; margin-bottom:6px;'>")
                        .append("<b>").append(notice.getTitle()).append("</b><br>")
                        .append(notice.getMessage()).append("</div>");
            }
        }
        html.append("</body></html>");

        card.add(htmlScroll(html.toString(), 11f), BorderLayout.CENTER);

        JButton request = createSecondaryButton(text("stadium.specialRequest"));
        request.addActionListener(event -> showSpecialRequestDialog(stadium));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(request);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private void htmlHeading(StringBuilder html, String title, String note) {
        html.append("<div style='margin-top:12px; margin-bottom:2px;'>")
                .append("<span style='color:#1e293b; font-size:11px; font-weight:bold;'>")
                .append(title).append("</span> ")
                .append("<span style='color:#94a3b8; font-size:9px;'>").append(note)
                .append("</span></div>");
    }

    private String joinNames(List<String> names) {
        return String.join(", ", names);
    }

    /** The four sections with their real geometry and real prices. */
    private JPanel buildSectionsCard(StadiumDetails details) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 14, 12, 14)));

        JLabel title = new JLabel(text("stadium.sections"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        JLabel note = new JLabel(text("stadium.sectionsNote"));
        note.setForeground(MUTED);
        note.setFont(note.getFont().deriveFont(Font.PLAIN, 10f));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(note, BorderLayout.SOUTH);
        card.add(heading, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"", Messages.get("ledger.section"), Messages.get("ledger.rows"), Messages.get("ledger.seatsPerRow"), "Seats", Messages.get("ledger.pricePerSeat")},
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Stadium stadium = details.getStadium();
        Color accent = StadiumPhotoPanel.colorOf(stadium.getAccentColor(), BLUE);
        for (StadiumDetails.SectionFacts facts : details.getSections()) {
            model.addRow(new Object[]{"", facts.getId() + "  " + facts.getLabel(),
                    facts.getRows(), facts.getSeatsPerRow(), facts.getSeatCount(),
                    facts.getPriceRange()});
        }
        JTable table = new JTable(model);
        table.setBackground(WHITE);
        table.setForeground(TEXT);
        table.setRowHeight(30);
        table.setGridColor(new Color(235, 240, 247));
        table.getTableHeader().setBackground(SKY);
        table.getTableHeader().setForeground(BLUE_DARK);
        table.getTableHeader().setFont(table.getTableHeader().getFont()
                .deriveFont(Font.BOLD, 11f));
        int[] widths = {24, 138, 54, 92, 70, 196};
        for (int column = 0; column < widths.length; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }
        // A colour chip per section, matching the picture above it.
        table.getColumnModel().getColumn(0).setCellRenderer(
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public java.awt.Component getTableCellRendererComponent(JTable owner,
                                                                           Object value,
                                                                           boolean selected,
                                                                           boolean focused,
                                                                           int row,
                                                                           int column) {
                        java.awt.Component component =
                                super.getTableCellRendererComponent(owner, value, selected,
                                        focused, row, column);
                        component.setBackground(StadiumPhotoPanel.sectionColor(row, accent));
                        return component;
                    }
                });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        card.add(scroll, BorderLayout.CENTER);

        JLabel totals = new JLabel(String.format(Locale.US,
                "%s seats in total across four sections  •  booking fee %s per reservation",
                String.valueOf(stadium.getSeatCount()),
                BookingService.formatMoney(details.getBookingFee())));
        totals.setForeground(MUTED);
        totals.setFont(totals.getFont().deriveFont(Font.PLAIN, 10f));
        card.add(totals, BorderLayout.SOUTH);
        return card;
    }

    /** Shortens text that would run past the right edge. */
    private static String clip(Graphics2D g, String text, int maxWidth) {
        if (text == null) {
            return "";
        }
        if (g.getFontMetrics().stringWidth(text) <= maxWidth) {
            return text;
        }
        String trimmed = text;
        while (trimmed.length() > 3
                && g.getFontMetrics().stringWidth(trimmed + "…") > maxWidth) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + "…";
    }

    /**
     * A colour as a CSS hex string. getRGB() includes the alpha byte, so it cannot
     * be used directly: it produced eight digits and Color.decode rejected it.
     */
    private static String hex(Color colour) {
        return String.format(Locale.ROOT, "#%02x%02x%02x",
                colour.getRed(), colour.getGreen(), colour.getBlue());
    }

    /** A short wrapped line, pinned left, for the block beside the picture. */
    private JLabel sideText(String html, String colour, float size) {
        JLabel label = new JLabel("<html><div style='width:560px'>" + html + "</div></html>");
        label.setForeground(Color.decode(colour));
        label.setFont(label.getFont().deriveFont(Font.PLAIN, size));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /** The way through to the schedule and the seat map. */
    private JPanel buildDetailsActions(Stadium stadium) {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        actions.setOpaque(false);
        JButton schedule = createPrimaryButton(Messages.get("directory.chooseSeats"));
        schedule.setToolTipText(text("stadium.tip.schedule", stadium.getName()));
        schedule.addActionListener(event -> openStadium(stadium));
        actions.add(schedule);
        JButton events = createSecondaryButton(Messages.get("directory.allEvents"));
        events.addActionListener(ignored -> showLiveSchedules());
        actions.add(events);
        JButton ledger = createSecondaryButton(text("stadium.seeBooked"));
        ledger.addActionListener(event -> {
            selectedStadium = stadium;
            showSeatLedger();
        });
        actions.add(ledger);
        return actions;
    }

    // ---------------------------------------------------------------------
    // Event details
    // ---------------------------------------------------------------------

    /**
     * Everything about one event, with the way to book it.
     *
     * <p>Reached by tapping an event in the venue schedule, in the live schedules,
     * or in the list on a venue's details page. Choosing an event used to go
     * straight to the seat map, which left nowhere to read what the event actually
     * was before committing to it.
     */
    private void showEventDetails(StadiumEvent event) {
        if (event == null) {
            return;
        }
        // Remember where we came from, so Back returns there rather than always
        // assuming the venue's own schedule.
        if (!"event-details".equals(currentScreen)) {
            eventReturnScreen = currentScreen;
        }
        currentScreen = "event-details";
        selectedEvent = event;
        Stadium stadium = StadiumData.getStadium(event.getStadiumId());
        // The seat picker draws from the selected event, and the venue too, so
        // both are set before the page is built. Coming from the live schedules
        // there is no chosen venue yet, and the event carries its own.
        selectedStadium = stadium;
        bookingService.selectEvent(event);
        seatMapPanel.refreshStatuses();
        setHeader(event.getHeadline(),
                stadium.getName() + "  •  " + VenueWords.eventType(event.getType().getLabel()));
        contentHost.removeAll();
        contentHost.add(buildEventDetailsContent(event), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        // Keeps the review button and the price panel in step with the seats
        // already chosen, including ones carried back from the review step.
        updateBookingSummary();
        showStatus(event.getHeadline());
    }

    private JPanel buildEventDetailsContent(StadiumEvent event) {
        Stadium stadium = StadiumData.getStadium(event.getStadiumId());
        boolean open = bookingService.isBookingOpen(event);
        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(16, 0, 20, 0));

        // The confirm button sits at the top of the page, not the bottom. It used
        // to be the page's south component, which meant the seat map and the
        // notices panel pushed it off the bottom of the window: the booking
        // control was on the page, but a customer could not see it. Putting it
        // first means it is on show whatever height the rest of the page needs.
        eventActionsHost = new JPanel(new BorderLayout());
        eventActionsHost.setOpaque(false);
        eventActionsHost.add(buildEventActions(event, open), BorderLayout.CENTER);
        page.add(eventActionsHost, BorderLayout.NORTH);

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        // Capped so the page is never taller than the window, whatever the
        // notices panel would like to be.
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 520));
        // The plan is replaced by the seat picker itself, so seats are chosen
        // here rather than after another jump. The picker is the live panel, so
        // it has to be detached from whichever screen held it last.
        if (open) {
            row.add(buildSeatMapCard(), BorderLayout.CENTER);
        } else {
            row.add(buildEventFactsCard(stadium, event, open), BorderLayout.CENTER);
        }
        JPanel side = new JPanel(new BorderLayout(0, 12));
        side.setOpaque(false);
        side.add(buildEventPriceCard(stadium, event), BorderLayout.NORTH);
        side.add(buildEventNoticesCard(stadium, event), BorderLayout.CENTER);
        side.setPreferredSize(new Dimension(360, 100));
        side.setMaximumSize(new Dimension(380, 520));
        row.add(side, BorderLayout.EAST);
        page.add(row, BorderLayout.CENTER);
        return page;
    }

    /**
     * Who is playing and what it is. One HTML block rather than separate labels,
     * because a long fixture name is wider than the card and a JLabel will not
     * wrap it however wide a div is asked to be.
     */
    private JPanel buildEventFactsCard(Stadium stadium, StadiumEvent event, boolean open) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 16, 14, 16)));

        String accent = hex(open ? BLUE_DARK : DANGER);
        StringBuilder html = new StringBuilder("<html><body style='font-family:SansSerif; margin:0;'>");
        html.append("<div style='font-size:10px; font-weight:bold; color:").append(accent)
                .append(";'>").append(VenueWords.eventType(event.getType().getLabel(), true))
                .append("</div>");
        html.append("<div style='font-size:19px; font-weight:bold; color:").append(hex(TEXT))
                .append("; margin-top:4px;'>").append(event.getHeadline()).append("</div>");
        html.append("<div style='font-size:11px; color:").append(hex(MUTED))
                .append("; margin-top:3px;'>").append(stadium.getName()).append(" &bull; ")
                .append(stadium.getLocation()).append("</div>");
        if (event.isGame() && event.getSport() != null) {
            html.append("<div style='font-size:11px; color:#475569; margin-top:12px;'>")
                    .append("<b>Sport:</b> ").append(event.getSport())
                    .append(" &nbsp;&bull;&nbsp; <b>Home:</b> ").append(event.getTeamOne())
                    .append(" &nbsp;&bull;&nbsp; <b>Away:</b> ").append(event.getTeamTwo())
                    .append("</div>");
        } else if (event.getArtist() != null) {
            html.append("<div style='font-size:11px; color:#475569; margin-top:12px;'>")
                    .append("<b>Artist:</b> ").append(event.getArtist()).append("</div>");
        }
        html.append("<div style='font-size:11px; color:#475569; margin-top:8px;'>")
                .append(event.getDescription()).append("</div>");
        html.append("</body></html>");
        card.add(htmlScroll(html.toString(), 11f), BorderLayout.CENTER);
        return card;
    }

    /** The numbers a customer checks before booking: when, by when, and how full. */
    private JPanel buildEventPriceCard(Stadium stadium, StadiumEvent event) {
        boolean open = bookingService.isBookingOpen(event);
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 14, 12, 14)));

        JLabel title = new JLabel(text("booking.beforeYouBook"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        card.add(title, BorderLayout.NORTH);

        double front = cheapestSeatPrice(event, true);
        double back = cheapestSeatPrice(event, false);
        int total = bookingService.getTotalSeatCount(event);

        StringBuilder html = new StringBuilder("<html><body style='font-family:SansSerif; margin:0;'>");
        row(html, Messages.get("event.date"), Messages.dateLabel(event.getDate()));
        row(html, Messages.get("event.starts", event.getTimeLabel()), "");
        row(html, Messages.get("event.doors", event.getDoorsLabel()), "");
        row(html, Messages.get("event.bookBy"), event.getBookingDeadlineLabel());
        row(html, Messages.get("event.seats"), String.valueOf(total));
        row(html, Messages.get("event.from"), BookingService.formatMoney(back));
        row(html, Messages.get("event.bestSeats"), BookingService.formatMoney(front)
                + Messages.get("event.inSectionARow1"));
        row(html, Messages.get("booking.bookingFee"), BookingService.formatMoney(BookingService.BOOKING_FEE)
                + Messages.get("event.perReservation"));
        html.append("</body></html>");

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setOpaque(false);
        body.add(htmlScroll(html.toString(), 11f), BorderLayout.NORTH);

        // Availability and the running total are kept out of the HTML above and
        // into labels of their own. The old figures were written once when the
        // page was built, so they still showed the full house after seats had
        // been chosen, and nothing on the page reacted to a seat being picked.
        eventAvailabilityLabel = new JLabel();
        eventAvailabilityLabel.setFont(eventAvailabilityLabel.getFont().deriveFont(Font.BOLD, 11f));
        eventSelectionLabel = new JLabel();
        eventSelectionLabel.setFont(eventSelectionLabel.getFont().deriveFont(Font.PLAIN, 10f));
        eventSelectionLabel.setForeground(MUTED);
        JPanel live = new JPanel(new GridBagLayout());
        live.setOpaque(false);
        GridBagConstraints liveConstraints = new GridBagConstraints();
        liveConstraints.anchor = GridBagConstraints.WEST;
        liveConstraints.fill = GridBagConstraints.HORIZONTAL;
        liveConstraints.weightx = 1;
        liveConstraints.insets = new Insets(8, 0, 0, 0);
        live.add(eventAvailabilityLabel, liveConstraints);
        liveConstraints.gridy = 1;
        liveConstraints.insets = new Insets(2, 0, 0, 0);
        live.add(eventSelectionLabel, liveConstraints);
        body.add(live, BorderLayout.CENTER);

        if (!open) {
            JLabel closed = new JLabel("<html><div style='width:250px; font-size:11px; color:"
                    + hex(DANGER) + ";'><b>This event cannot be booked.</b><br>"
                    + htmlEscape(bookingService.getBookingRestrictionMessage(event))
                    + "</div></html>");
            closed.setForeground(DANGER);
            closed.setFont(closed.getFont().deriveFont(Font.BOLD, 11f));
            body.add(closed, BorderLayout.SOUTH);
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    /**
     * Rewrites the availability and selection figures on the event page.
     *
     * <p>Called whenever the chosen seats change, so the vacancy falls and the
     * total rises as seats are added rather than sitting at whatever it said
     * when the page opened.
     */
    private void updateEventAvailability() {
        if (eventAvailabilityLabel == null || selectedEvent == null) {
            return;
        }
        int total = bookingService.getTotalSeatCount(selectedEvent);
        int sold = bookingService.getBookedSeatKeys(selectedEvent).size();
        int mine = seatMapPanel.getSelectedSeats().size();
        // Seats the customer is holding right now are not available to anybody
        // else, so they leave the available figure as soon as they are picked.
        int available = Math.max(0, total - sold - mine);
        double vacancy = total == 0 ? 0.0 : available * 100.0 / total;

        eventAvailabilityLabel.setText(String.format(Locale.US,
                "%s of %s seats still available  (%.3f%%)",
                formatThousands(available), formatThousands(total), vacancy));
        eventAvailabilityLabel.setForeground(vacancy <= 10.0 ? WARNING_FOREGROUND : SUCCESS_FOREGROUND);

        if (mine == 0) {
            eventSelectionLabel.setText(text("booking.noSeatsChosen"));
        } else {
            // The hold countdown is spoken in words so it cannot be mistaken for
            // the total, which is money.
            long seconds = 0;
            for (Seat seat : seatMapPanel.getSelectedSeats()) {
                seconds = Math.max(seconds, seatMapPanel.holdSecondsRemaining(seat.getKey()));
            }
            eventSelectionLabel.setText(mine + " seat" + (mine == 1 ? "" : "s") + " chosen  •  "
                    + currency(bookingService.getTotalCharge(seatMapPanel.getSelectedSeats()))
                    + "  •  held for " + (seconds / 60) + "m " + (seconds % 60) + "s");
        }
    }

    /** Thousands separators, so a 45,202-seat venue does not read as 45202. */
    private String formatThousands(int value) {
        return String.format(Locale.US, "%,d", value);
    }

    /** Escapes the few characters that would otherwise break an HTML label. */
    private String htmlEscape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void row(StringBuilder html, String heading, String value) {
        html.append("<div style='margin-bottom:4px;'>")
                .append("<span style='color:#64748b; font-size:10px;'>").append(heading)
                .append("</span>  ")
                .append("<span style='color:#1e293b; font-size:11px;'>").append(value)
                .append("</span></div>");
    }

    /** The cheapest and dearest seat for this event, priced the way the map prices. */
    private double cheapestSeatPrice(StadiumEvent event, boolean front) {
        double lowest = Double.MAX_VALUE;
        double highest = 0;
        for (SeatSection section : StadiumData.getStadium(event.getStadiumId()).getSections()) {
            int row = front ? 1 : section.getRows();
            double price = bookingService.getSeatPrice(event,
                    new SeatKey(section.getId(), row, 1));
            lowest = Math.min(lowest, price);
            highest = Math.max(highest, price);
        }
        return front ? highest : (lowest == Double.MAX_VALUE ? 0 : lowest);
    }

    /** Notices that stop this event being booked, or the empty state if none do. */
    private JPanel buildEventNoticesCard(Stadium stadium, StadiumEvent event) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 14, 12, 14)));

        // Keep the notices that belong to this event. A notice with no event id
        // is about the venue as a whole, so it belongs on every event.
        List<StadiumAnnouncement> notices = new ArrayList<>();
        for (StadiumAnnouncement notice : StadiumData.getAnnouncements(stadium.getId())) {
            boolean aboutWholeVenue = notice.getEventId() == null
                    || notice.getEventId().isEmpty();
            if (aboutWholeVenue || notice.getEventId().equals(event.getId())) {
                notices.add(notice);
            }
        }

        JLabel title = new JLabel(notices.isEmpty() ? "No notices" : Messages.get("event.noNotices"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        card.add(title, BorderLayout.NORTH);

        StringBuilder html = new StringBuilder("<html><body style='font-family:SansSerif; margin:0;'>");
        if (notices.isEmpty()) {
            html.append("<div style='color:#64748b; font-size:11px;'>Nothing has been posted "
                    + "about this event. The venue's other notices are on its details page."
                    + "</div>");
        } else {
            for (StadiumAnnouncement notice : notices) {
                // Only a blocking notice is shown in the alarm colour.
                Color colour = notice.getType().isBlocking() ? DANGER : new Color(71, 85, 105);
                html.append("<div style='margin-bottom:7px; font-size:10px; color:")
                        .append(hex(colour)).append(";'>")
                        .append("<b>").append(notice.getTitle()).append("</b><br>")
                        .append(notice.getMessage()).append("</div>");
            }
        }
        html.append("</body></html>");
        card.add(htmlScroll(html.toString(), 11f), BorderLayout.CENTER);
        return card;
    }

    /**
     * The label on the confirmation button, counting the seats it will book so a
     * customer can see what they are agreeing to pay for.
     */
    private String confirmLabel(int seats) {
        return "Confirm " + seatCount(seats);
    }

    /** "1 seat" and "3 seats", so a button never reads "1 seats". */
    private static String seatCount(int seats) {
        return seats == 1 ? "1 seat" : seats + " seats";
    }

    /**
 * Books the seats chosen on the event page, in one press.
 *
 * <p>The button is on the seat-picking page, so the contact details have not been
 * asked for yet. Rather than send the customer to another screen to type them and
 * then come back, they are collected here if they are missing. After that the
 * booking is taken and the receipt is issued, which is what the button promises.
 */
private void confirmBookingHere(StadiumEvent event) {
        List<Seat> chosen = seatMapPanel.getSelectedSeats();
        if (chosen.isEmpty()) {
            showWarning(Messages.get("booking.needSeatToConfirm"));
            return;
        }
        if (!collectContactDetailsIfNeeded()) {
            return;
        }
        selectedEvent = event;
        confirmBooking();
    }

    /**
     * Asks for name, email and phone the first time, and reuses them after that.
     *
     * @return true when the details are filled in and the booking may proceed
     */
    private boolean collectContactDetailsIfNeeded() {
        // Already entered and usable, so the customer is not asked again. Checked
        // for correctness and not merely for being non-empty: three boxes holding
        // something unusable would otherwise skip the form and be refused later,
        // which is the dead end this method exists to prevent.
        if (!CustomerDetails.needsAsking(nameField.getText(), emailField.getText(),
                phoneField.getText())) {
            return true;
        }
        return showDetailsForm();
    }

    /**
     * Asks for the contact details and keeps the form open until they are right.
     *
     * <p>This replaces a dead end. It used to read the fields, let the dialog
     * close, and only then check them — so a mistake closed the form, put a
     * warning on top of it, and left the customer with an empty form and a
     * warning telling them what they had got wrong. Now nothing is checked until
     * the form is ready to close: an unusable entry marks that field, says what
     * to type, and keeps everything already typed so it can be corrected in
     * place.
     *
     * @return true when the details are usable, false if the customer backed out
     */
    private boolean showDetailsForm() {
        DetailsFormPanel form = new DetailsFormPanel(nameField, emailField, phoneField);
        // The same form object is shown on every pass, so what has been typed
        // survives a failed attempt. Rebuilding it each time would wipe the
        // entry and put the customer back where they started.
        //
        // Anything already in the boxes is marked before the first showing. The
        // boxes are the booking screen's own, so they can arrive here holding a
        // bad entry, and opening a full form with a wrong entry in it and no
        // complaint leaves the customer no reason to think it needs changing.
        java.util.Map<CustomerDetails.Field, CustomerDetails.Problem> already =
                form.check();
        if (!already.isEmpty()) {
            form.showProblems(already);
        }
        while (true) {
            String choice = showDialogChoice(this, form.panel(), Messages.get("booking.yourDetails"),
                    JOptionPane.PLAIN_MESSAGE, Messages.get("booking.continue"),
                    Messages.get("booking.continueAndConfirm"), backLabel());
            if (!Messages.get("booking.continueAndConfirm").equals(choice)) {
                return false;
            }
            java.util.Map<CustomerDetails.Field, CustomerDetails.Problem> problems =
                    form.check();
            if (problems.isEmpty()) {
                // The one last check, so a booking can never be taken with
                // details the rules would refuse.
                try {
                    bookingService.validateCustomer(nameField.getText(), emailField.getText(),
                            phoneField.getText());
                    return true;
                } catch (IllegalArgumentException problem) {
                    // Reached only if the form and the service ever disagree,
                    // which a test guards against. Marked on the form like any
                    // other problem, rather than raised over the top of it.
                    form.markAll(problem.getMessage());
                    continue;
                }
            }
            form.showProblems(problems);
        }
    }

    /**
 * The booking controls for the event page: the confirm button and the total,
 * with the quieter navigation links underneath.
 *
     * <p>Built fresh whenever the selection changes, so the button only exists
     * when there is something to book and always counts what is chosen.
     */
    private JPanel buildEventActions(StadiumEvent event, boolean open) {
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        JPanel confirmRow = new JPanel(new BorderLayout(14, 0));
        confirmRow.setOpaque(false);
        confirmRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));
        if (open) {
            // Seats are chosen on this page, so this is where the booking is
            // confirmed. The button is here rather than only on the next screen
            // because a customer who has just picked their seats should not have
            // to hunt for where to go next.
            List<Seat> chosen = seatMapPanel.getSelectedSeats();
            if (!chosen.isEmpty()) {
                String amount = currency(bookingService.getTotalCharge(chosen));
                // One button that books: it asks for the contact details, takes
                // the payment and issues the receipt, all in one press.
                JButton confirm = createPrimaryButton(confirmLabel(chosen.size()));
                confirm.setToolTipText(text("booking.confirmTip",
                        seatCount(chosen.size()), amount));
                confirm.addActionListener(ignored -> confirmBookingHere(event));
                confirm.setPreferredSize(new Dimension(210, 40));
                eventConfirmButton = confirm;
                reviewSeatsButton = confirm;

                JPanel summary = new JPanel();
                summary.setOpaque(false);
                summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
                JLabel due = new JLabel(text("booking.totalInline", amount));
                due.setForeground(TEXT);
                due.setFont(due.getFont().deriveFont(Font.BOLD, 13f));
                summary.add(due);
                JLabel seats = new JLabel(text("booking.feeNote", currency(BookingService.BOOKING_FEE)));
                seats.setForeground(MUTED);
                seats.setFont(seats.getFont().deriveFont(Font.PLAIN, 10f));
                summary.add(seats);
                confirmRow.add(summary, BorderLayout.WEST);
                confirmRow.add(confirm, BorderLayout.EAST);
            } else {
                JLabel hint = new JLabel(text("booking.chooseSeatsHint"));
                hint.setForeground(MUTED);
                hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 11f));
                confirmRow.add(hint, BorderLayout.WEST);
            }
        } else {
            JLabel closed = new JLabel(bookingService.getBookingRestrictionMessage(event));
            closed.setForeground(DANGER);
            closed.setFont(closed.getFont().deriveFont(Font.BOLD, 11f));
            confirmRow.add(closed, BorderLayout.WEST);
        }
        actions.add(confirmRow);

        // These two are quiet navigation, so they sit on their own line below
        // the confirm button rather than competing with it.
        JPanel links = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        links.setOpaque(false);
        JButton venue = createSecondaryButton(text("stadium.aboutButton",
                StadiumData.getStadium(event.getStadiumId()).getName()));
        venue.addActionListener(ignored -> showStadiumDetails(
                StadiumData.getStadium(event.getStadiumId())));
        links.add(venue);
        JButton schedules = createSecondaryButton(text("directory.seeAllSchedules"));
        schedules.addActionListener(ignored -> showLiveSchedules());
        links.add(schedules);
        actions.add(links);
        return actions;
    }

    // ---------------------------------------------------------------------
    // Saved selections: choose now, finish later
    // ---------------------------------------------------------------------

    /**
     * Saves the seats currently chosen so they can be picked up again later.
     *
     * <p>Without this there is nowhere to put "these are the ones I want" short of
     * buying them, which is why picking seats and choosing them later was not
     * possible at all.
     */
    private void saveCurrentSelection() {
        List<Seat> chosen = seatMapPanel.getSelectedSeats();
        if (chosen.isEmpty()) {
            showWarning(Messages.get("booking.needSeatToSave"));
            return;
        }
        JTextField label = new JTextField(18);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        label.setText(selectedEvent == null ? Messages.get("booking.mySeats") : selectedEvent.getHeadline());
        // The limit is in the field's own description as well as in the form, so
        // it is known before anything is refused rather than discovered by being
        // told the label is too long.
        describe(label, "Label for this selection",
                "Up to " + FormRules.MAX_LABEL_LENGTH + " characters. Something you will "
                        + "recognise later.");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(4, 0, 4, 10);
        form.add(new JLabel(text("saved.labelHint")), constraints);
        constraints.gridy = 1;
        form.add(label, constraints);

        JLabel labelError = new JLabel();
        labelError.setForeground(DANGER);
        labelError.setFont(labelError.getFont().deriveFont(Font.PLAIN, 10f));
        labelError.setPreferredSize(new Dimension(300, 14));
        constraints.gridy = 2;
        form.add(labelError, constraints);

        // The message is not cleared at the top of the loop, so a refusal keeps
        // explaining itself while the customer works out what to change.
        while (true) {
            String choice = showDialogChoice(this, form, Messages.get("booking.saveTheseSeats"),
                    JOptionPane.PLAIN_MESSAGE, Messages.get("common.save"),
                    Messages.get("booking.saveSelection"), backLabel());
            if (!Messages.get("booking.saveSelection").equals(choice)) {
                return;
            }
            String labelProblem = FormRules.labelProblem(label.getText());
            if (labelProblem != null) {
                labelError.setText(labelProblem);
                label.selectAll();
                SwingUtilities.invokeLater(label::requestFocusInWindow);
                continue;
            }
            try {
                BookingStore.SavedSelection saved = bookingService.saveSelection(
                        label.getText(), chosen);
                seatMapPanel.clearSelection();
                showStatus(Messages.get("booking.saved") + " " + saved.getSeats().size() + " seat"
                        + (saved.getSeats().size() == 1 ? "" : "s") + " as " + saved.getId()
                        + "  •  find them under Saved seats");
                return;
            } catch (IllegalArgumentException | IllegalStateException exception) {
                // Stays open with the label still in the box. A refused save
                // used to close the dialog and leave nothing behind.
                labelError.setText(exception.getMessage());
                label.selectAll();
                SwingUtilities.invokeLater(label::requestFocusInWindow);
            }
        }
    }

    /** The saved selections, and the way back into each one. */
    private void showSavedSeats() {
        currentScreen = "saved-seats";
        setHeader(Messages.get("saved.title"),
                "Seats you chose earlier, kept until you book them or discard them.");
        contentHost.removeAll();
        contentHost.add(buildSavedSeatsContent(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        showStatus("Saved seats");
    }

    private JPanel buildSavedSeatsContent() {
        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(16, 0, 20, 0));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        JButton back = createOutlineButton(text("common.allStadiums"), BLUE);
        back.addActionListener(ignored -> showStadiumDirectory());
        top.add(back);
        page.add(top, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel notice = createCard();
        notice.setLayout(new BorderLayout(12, 0));
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WARNING_FOREGROUND), new EmptyBorder(12, 14, 12, 14)));
        notice.add(sideText("<b>These seats are not booked yet.</b> Saving them keeps your choice "
                        + "so you can come back later, but it does not hold them and does not "
                        + "sell them. Somebody else may take them, and this page will say so when "
                        + "that happens.", "#92400e", 11f), BorderLayout.CENTER);
        notice.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                notice.getPreferredSize().height));
        body.add(notice);
        body.add(Box.createVerticalStrut(12));

        List<BookingStore.SavedSelection> saved = bookingService.getSavedSelections();
        if (saved.isEmpty()) {
            JPanel empty = createCard();
            empty.setLayout(new BorderLayout(0, 8));
            empty.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER), new EmptyBorder(18, 16, 18, 16)));
            empty.add(sideText("<b>Nothing saved yet.</b><br>Pick the seats you want on an event, "
                    + "then press <b>Save these seats</b> on the booking screen. They will be here "
                    + "next time you open the application.", "#475569", 11f), BorderLayout.CENTER);
            JButton find = createPrimaryButton(text("saved.findEvent"));
            find.addActionListener(ignored -> showStadiumDirectory());
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            actions.setOpaque(false);
            actions.add(find);
            empty.add(actions, BorderLayout.SOUTH);
            empty.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    empty.getPreferredSize().height));
            body.add(empty);
        } else {
            for (BookingStore.SavedSelection selection : saved) {
                body.add(buildSavedSelectionCard(selection));
                body.add(Box.createVerticalStrut(10));
            }
        }
        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildSavedSelectionCard(BookingStore.SavedSelection selection) {
        String problem = bookingService.whySelectionCannotBeUsed(selection);
        Stadium stadium = StadiumData.getStadium(selection.getStadiumId());
        JPanel card = createCard();
        card.setLayout(new BorderLayout(14, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(problem == null ? BORDER : WARNING_FOREGROUND),
                new EmptyBorder(12, 14, 12, 14)));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(sideText("<b>" + selection.getLabel() + "</b>", "#1e293b", 13f));
        text.add(sideText(selection.getEventName()
                + (stadium == null ? "" : "<br>" + stadium.getName()), "#475569", 11f));
        // The seats as one readable line: "B4-03, B4-04, B4-05"
        StringBuilder seatText = new StringBuilder();
        for (SeatKey seat : selection.getSeats()) {
            if (seatText.length() > 0) {
                seatText.append(", ");
            }
            seatText.append(seat.display());
        }
        text.add(sideText(seatText.toString(), "#1e293b", 11f));
        text.add(sideText(selection.getSeats().size() + " seat"
                + (selection.getSeats().size() == 1 ? "" : "s") + "  \u2022  "
                + BookingService.formatMoney(selection.getTotal())
                + "  \u2022  saved "
                + java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy HH:mm", Locale.ENGLISH)
                        .withZone(java.time.ZoneId.systemDefault())
                        .format(selection.getCreatedAt())
                + "  \u2022  " + selection.getId(), "#64748b", 10f));
        if (problem != null) {
            text.add(sideText("<b>Cannot be booked:</b> " + problem, "#b45309", 10f));
        }
        card.add(text, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton resume = createPrimaryButton(text("saved.resume"));
        resume.setEnabled(problem == null);
        if (problem != null) {
            resume.setToolTipText(problem);
        }
        resume.addActionListener(ignored -> resumeSelection(selection));
        JButton discard = createSecondaryButton(text("common.discard"),
                problem == null ? new Color(185, 28, 28) : MUTED);
        discard.addActionListener(ignored -> discardSelection(selection));
        actions.add(resume);
        actions.add(discard);
        card.add(actions, BorderLayout.EAST);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                card.getPreferredSize().height));
        return card;
    }

    /** Opens an event with the saved seats already chosen. */
    private void resumeSelection(BookingStore.SavedSelection selection) {
        StadiumEvent event = StadiumData.getEvent(selection.getEventId());
        if (event == null) {
            showWarning(Messages.get("error.eventGone"));
            return;
        }
        String problem = bookingService.whySelectionCannotBeUsed(selection);
        if (problem != null) {
            showWarning(problem);
            return;
        }
        // Open the event first. It clears the seat map, so choosing the seats has
        // to come after it or the selection is wiped on the way in.
        selectedStadium = StadiumData.getStadium(selection.getStadiumId());
        openEvent(event);
        int restored = seatMapPanel.setSelectedKeys(selection.getSeats());
        updateBookingSummary();
        seatMapPanel.repaint();
        showStatus("Picked up " + restored + " saved seat"
                + (restored == 1 ? "" : "s"));
    }

    private void discardSelection(BookingStore.SavedSelection selection) {
        String choice = showDialogChoice(this,
                Messages.get("booking.discardQuestion", selection.getLabel())
                        + "\\n\\n" + Messages.get("booking.discardBody"),
                Messages.get("booking.discardSaved"), JOptionPane.QUESTION_MESSAGE,
                Messages.get("common.discardThem"), Messages.get("common.keepThem"));
        if (!Messages.get("common.discardThem").equals(choice)) {
            return;
        }
        if (bookingService.deleteSavedSelection(selection.getId())) {
            showStatus(Messages.get("booking.discarded"));
        } else {
            showWarning(Messages.get("booking.couldNotDiscard"));
        }
        contentHost.removeAll();
        contentHost.add(buildSavedSeatsContent(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
    }

    private void openStadium(Stadium stadium) {
        selectedStadium = stadium;
        selectedEvent = null;
        selectedScheduleDate = null;
        scheduleListHost = null;
        scheduleResultLabel = null;
        pricingBreakdownHost = null;
        currentScreen = "directory";
        eventSearchField.setText("");
        currentScreen = "stadium";
        setHeader(stadium.getName(), stadium.getLocation() + "  •  " + stadium.getVenueType());
        contentHost.removeAll();
        contentHost.add(buildStadiumDashboard(stadium), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        showStatus("Viewing the " + stadium.getName() + " schedule");
    }

    // ---------------------------------------------------------------------
    // Stadium schedule
    // ---------------------------------------------------------------------

    private JPanel buildStadiumDashboard(Stadium stadium) {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(20, 0, 22, 0));
        page.add(buildStadiumHero(stadium), BorderLayout.NORTH);

        JPanel dashboardBody = new JPanel(new BorderLayout(14, 0));
        dashboardBody.setOpaque(false);
        dashboardBody.add(buildScheduleCard(stadium), BorderLayout.CENTER);
        JPanel notices = buildAnnouncementsCard(stadium);
        notices.setPreferredSize(new Dimension(410, 100));
        notices.setMinimumSize(new Dimension(360, 100));
        dashboardBody.add(notices, BorderLayout.EAST);
        page.add(dashboardBody, BorderLayout.CENTER);
        page.add(createBookingSteps(), BorderLayout.SOUTH);
        return page;
    }

    private JPanel buildStadiumHero(Stadium stadium) {
        Color accent = colorFromHex(stadium.getAccentColor(), BLUE);
        JPanel hero = createCard();
        hero.setLayout(new BorderLayout(18, 0));
        hero.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(17, 20, 17, 20)));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        JButton back = createOutlineButton(text("stadium.info"), accent);
        back.setToolTipText(text("stadium.infoTip"));
        back.addActionListener(event -> showStadiumDetails(stadium));
        top.add(back, BorderLayout.WEST);
        JLabel venueType = new JLabel(stadium.getVenueType().toUpperCase(Locale.ENGLISH));
        venueType.setForeground(accent.darker());
        venueType.setFont(venueType.getFont().deriveFont(Font.BOLD, 10f));
        top.add(venueType, BorderLayout.EAST);
        hero.add(top, BorderLayout.NORTH);

        JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        JLabel title = new JLabel(stadium.getName());
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        copy.add(title, constraints);
        JLabel description = new JLabel(stadium.getDescription());
        description.setForeground(MUTED);
        description.setFont(description.getFont().deriveFont(Font.PLAIN, 12f));
        constraints.gridy = 1;
        constraints.insets = new Insets(5, 0, 0, 0);
        copy.add(description, constraints);
        JLabel address = new JLabel(stadium.getAddress());
        address.setForeground(MUTED);
        address.setFont(address.getFont().deriveFont(Font.PLAIN, 10f));
        constraints.gridy = 2;
        constraints.insets = new Insets(4, 0, 0, 0);
        copy.add(address, constraints);
        hero.add(copy, BorderLayout.CENTER);

        JPanel stats = new JPanel(new GridLayout(1, 3, 10, 0));
        stats.setOpaque(false);
        List<StadiumEvent> events = bookingService.getEvents(stadium.getId());
        StadiumEvent next = events.isEmpty() ? null : events.get(0);
        stats.add(createStat("LOCATION", stadium.getLocation()));
        stats.add(createStat("CAPACITY", formatCapacity(stadium.getCapacity())));
        stats.add(createStat("NEXT EVENT", next == null ? "—" : next.getDateLabel()));
        hero.add(stats, BorderLayout.EAST);
        return hero;
    }

    private JPanel createStat(String label, String value) {
        JPanel stat = new JPanel(new GridBagLayout());
        stat.setOpaque(false);
        stat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(224, 232, 242)),
                new EmptyBorder(0, 16, 0, 0)));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel caption = new JLabel(label);
        caption.setForeground(MUTED);
        caption.setFont(caption.getFont().deriveFont(Font.BOLD, 9f));
        stat.add(caption, constraints);
        JLabel content = new JLabel(value);
        content.setForeground(TEXT);
        content.setFont(content.getFont().deriveFont(Font.BOLD, 12f));
        constraints.gridy = 1;
        constraints.insets = new Insets(4, 0, 0, 0);
        stat.add(content, constraints);
        return stat;
    }

    private JPanel buildAnnouncementsCard(Stadium stadium) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(12, 16, 12, 16)));

        JPanel heading = new JPanel(new BorderLayout(10, 0));
        heading.setOpaque(false);
        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel title = new JLabel(Messages.get("stadium.notices"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        titleBox.add(title, constraints);
        JLabel subtitle = new JLabel(text("stadium.noticesSubtitle"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 10f));
        constraints.gridy = 1;
        constraints.insets = new Insets(3, 0, 0, 0);
        titleBox.add(subtitle, constraints);
        heading.add(titleBox, BorderLayout.CENTER);
        JButton request = createOutlineButton(Messages.get("stadium.specialRequest"), BLUE);
        request.addActionListener(event -> showSpecialRequestDialog(stadium));
        heading.add(request, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        List<StadiumAnnouncement> announcements = bookingService.getAnnouncements(stadium.getId());
        int visibleCount = 0;
        for (StadiumAnnouncement announcement : announcements) {
            if (!announcement.isActive()) {
                continue;
            }
            list.add(createAnnouncementRow(announcement));
            list.add(Box.createVerticalStrut(6));
            visibleCount++;
            if (visibleCount == 3) {
                break;
            }
        }
        if (visibleCount == 0) {
            JLabel empty = new JLabel(text("stadium.noNotices"));
            empty.setForeground(MUTED);
            empty.setFont(empty.getFont().deriveFont(Font.PLAIN, 10f));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            list.add(empty);
        } else if (announcements.size() > visibleCount) {
            JLabel more = new JLabel((announcements.size() - visibleCount) + " additional notice(s) available at the venue desk.");
            more.setForeground(MUTED);
            more.setFont(more.getFont().deriveFont(Font.PLAIN, 9f));
            more.setAlignmentX(Component.LEFT_ALIGNMENT);
            list.add(more);
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAnnouncementRow(StadiumAnnouncement announcement) {
        Color accent = announcementColor(announcement.getType());
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accent), new EmptyBorder(2, 9, 2, 4)));

        JPanel copy = new JPanel(new GridBagLayout());
        copy.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel type = new JLabel(VenueWords.noticeType(announcement.getType().getLabel(), true));
        type.setForeground(accent.darker());
        type.setFont(type.getFont().deriveFont(Font.BOLD, 8f));
        copy.add(type, constraints);
        JLabel title = new JLabel("<html><div style='width:225px'>" + htmlText(announcement.getTitle()) + "</div></html>");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 11f));
        constraints.gridx = 1;
        constraints.insets = new Insets(0, 8, 0, 0);
        copy.add(title, constraints);
        JLabel message = new JLabel("<html><div style='width:250px'>" + htmlText(announcement.getMessage()) + "</div></html>");
        message.setForeground(MUTED);
        message.setFont(message.getFont().deriveFont(Font.PLAIN, 9f));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(2, 0, 0, 0);
        copy.add(message, constraints);
        row.add(copy, BorderLayout.CENTER);

        StadiumEvent event = announcement.getEventId().isEmpty() ? null
                : StadiumData.getEvent(announcement.getEventId());
        if (event != null) {
            JLabel eventLabel = new JLabel(event.getDateLabel() + "  •  " + event.getTimeLabel());
            eventLabel.setForeground(accent.darker());
            eventLabel.setFont(eventLabel.getFont().deriveFont(Font.BOLD, 9f));
            row.add(eventLabel, BorderLayout.EAST);
        }
        return row;
    }

    private Color announcementColor(AnnouncementType type) {
        if (type == AnnouncementType.CANCELLATION) {
            return new Color(185, 28, 28);
        }
        if (type == AnnouncementType.EMERGENCY) {
            return new Color(180, 83, 9);
        }
        if (type == AnnouncementType.SPECIAL_REQUEST) {
            return PURPLE;
        }
        return BLUE;
    }

    private void showSpecialRequestDialog(Stadium stadium) {
        JComboBox<String> category = new JComboBox<>(new String[]{
                Messages.get("request.typeCancellation"), Messages.get("request.typeEmergency"), Messages.get("request.typeSafety"),
                Messages.get("request.typeAccess"), Messages.get("request.typeOther")});
        JTextArea message = new JTextArea(5, 42);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setFont(message.getFont().deriveFont(Font.PLAIN, 12f));
        message.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(8, 8, 8, 8));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(4, 4, 4, 4);
        JLabel categoryLabel = new JLabel(text("request.type"));
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 11f));
        form.add(categoryLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        form.add(category, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.fill = GridBagConstraints.NONE;
        JLabel messageLabel = new JLabel(text("request.details"));
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD, 11f));
        form.add(messageLabel, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        form.add(message, constraints);

        // The message box is the one field here that can be refused, and the
        // rule is not visible from the box itself, so the accepted length is
        // shown rather than left for the customer to discover by being refused.
        JLabel hint = new JLabel(text("request.minHint", FormRules.MIN_REQUEST_LENGTH));
        hint.setForeground(MUTED);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        form.add(hint, constraints);

        JLabel messageError = new JLabel();
        messageError.setForeground(DANGER);
        messageError.setFont(messageError.getFont().deriveFont(Font.PLAIN, 10f));
        messageError.setPreferredSize(new Dimension(420, 14));
        constraints.gridy = 3;
        form.add(messageError, constraints);

        // The message is not cleared at the top of the loop. It is set after a
        // refused attempt and has to survive into the next pass, or the customer
        // is shown the same problem with no explanation of it.
        while (true) {
            String result = showDialogChoice(this, form,
                    Messages.get("request.submitTo") + " " + stadium.getName(),
                    JOptionPane.PLAIN_MESSAGE, Messages.get("request.submit"),
                    backLabel(), Messages.get("request.submit"));
            if (!Messages.get("request.submit").equals(result)) {
                return;
            }
            String requestProblem = FormRules.requestProblem(message.getText());
            if (requestProblem != null) {
                messageError.setText(requestProblem);
                message.selectAll();
                SwingUtilities.invokeLater(message::requestFocusInWindow);
                continue;
            }
            try {
                bookingService.addSpecialRequest(stadium.getId(),
                        String.valueOf(category.getSelectedItem()), message.getText());
                openStadium(stadium);
                showStatus(Messages.get("request.submittedTo") + " " + stadium.getName());
                return;
            } catch (IllegalArgumentException exception) {
                // The form stays open with the message still in the box, so the
                // customer extends what they wrote rather than retyping it.
                messageError.setText(exception.getMessage());
                final JTextArea box = message;
                SwingUtilities.invokeLater(box::requestFocusInWindow);
            }
        }
    }

    private JPanel buildScheduleCard(Stadium stadium) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(16, 18, 16, 18)));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel headingCopy = new JPanel(new GridBagLayout());
        headingCopy.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel title = new JLabel(Messages.get("stadium.upcoming"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 17f));
        headingCopy.add(title, constraints);
        JLabel subtitle = new JLabel(Messages.get("stadium.subtitle"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
        constraints.gridy = 1;
        constraints.insets = new Insets(3, 0, 0, 0);
        headingCopy.add(subtitle, constraints);
        heading.add(headingCopy, BorderLayout.WEST);
        scheduleResultLabel = new JLabel();
        scheduleResultLabel.setForeground(BLUE_DARK);
        scheduleResultLabel.setFont(scheduleResultLabel.getFont().deriveFont(Font.BOLD, 11f));
        heading.add(scheduleResultLabel, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        JPanel filters = new JPanel(new BorderLayout(12, 0));
        filters.setOpaque(false);
        filters.add(buildSearchBar(eventSearchField, text("stadium.search.hint")), BorderLayout.CENTER);
        scheduleDates = new ArrayList<>(StadiumData.getDates(stadium.getId()));
        String[] dateChoices = new String[scheduleDates.size() + 1];
        dateChoices[0] = Messages.get("schedules.allDates");
        for (int index = 0; index < scheduleDates.size(); index++) {
            LocalDate date = scheduleDates.get(index);
            int eventCount = 0;
            for (StadiumEvent event : bookingService.getEvents(stadium.getId())) {
                if (date.equals(event.getDate())) {
                    eventCount++;
                }
            }
            dateChoices[index + 1] = date.format(DATE_FORMATTER) + "  •  "
                    + eventCount + (eventCount == 1 ? " event" : " events");
        }
        dateCombo = new JComboBox<>(dateChoices);
        dateCombo.setFont(dateCombo.getFont().deriveFont(Font.PLAIN, 12f));
        dateCombo.setPreferredSize(new Dimension(280, 38));
        dateCombo.setBackground(WHITE);
        dateCombo.addActionListener(event -> {
            int index = dateCombo.getSelectedIndex();
            selectedScheduleDate = index <= 0 ? null : scheduleDates.get(index - 1);
            refreshScheduleList();
        });
        filters.add(dateCombo, BorderLayout.EAST);

        JPanel centerStack = new JPanel(new BorderLayout(0, 10));
        centerStack.setOpaque(false);
        centerStack.add(filters, BorderLayout.NORTH);
        scheduleListHost = new JPanel(new BorderLayout());
        scheduleListHost.setOpaque(false);
        centerStack.add(scheduleListHost, BorderLayout.CENTER);
        card.add(centerStack, BorderLayout.CENTER);
        refreshScheduleList();
        return card;
    }

    private void refreshScheduleIfVisible() {
        if ("stadium".equals(currentScreen)) {
            refreshScheduleList();
        }
    }

    private void refreshScheduleList() {
        if (scheduleListHost == null || selectedStadium == null) {
            return;
        }
        List<StadiumEvent> events = new ArrayList<>();
        String query = eventSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
        for (StadiumEvent event : bookingService.getEvents(selectedStadium.getId())) {
            boolean dateMatches = selectedScheduleDate == null || event.getDate().equals(selectedScheduleDate);
            boolean queryMatches = query.isEmpty() || event.searchableText().contains(query)
                    || selectedStadium.searchableText().contains(query);
            if (dateMatches && queryMatches) {
                events.add(event);
            }
        }

        scheduleListHost.removeAll();
        countdownLabels.clear();
        eventActionButtons.clear();
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        if (events.isEmpty()) {
            list.add(buildEmptyState(Messages.get("schedules.noMatch"), "Try another date or clear the search field."));
        } else {
            for (StadiumEvent event : events) {
                list.add(createEventCard(event));
                list.add(Box.createVerticalStrut(10));
            }
        }
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scheduleListHost.add(scroll, BorderLayout.CENTER);
        String dateText = selectedScheduleDate == null ? "all dates" : selectedScheduleDate.format(DATE_FORMATTER);
        scheduleResultLabel.setText(events.size() + " event" + (events.size() == 1 ? "" : "s")
                + " • " + dateText);
        scheduleListHost.revalidate();
        scheduleListHost.repaint();
    }

    private String eventCountdownText(StadiumEvent event) {
        StadiumAnnouncement blocking = bookingService.getBlockingAnnouncement(event);
        if (blocking != null) {
            return "Unavailable • " + blocking.getTitle();
        }
        if (!event.isBookingOpen()) {
            return "Closed • " + event.getBookingDeadlineLabel();
        }
        return "Booking stops in " + event.getCountdownLabel();
    }

    /**
     * The label on an event's button. The countdown refreshes this every second,
     * so the wording has to live here or it gets overwritten each tick.
     */
    private String eventActionText(StadiumEvent event) {
        StadiumAnnouncement blocking = bookingService.getBlockingAnnouncement(event);
        if (blocking != null) {
            if (blocking.getType() == AnnouncementType.CANCELLATION) {
                return Messages.get("event.eventCancelled");
            }
            if (blocking.getType() == AnnouncementType.EMERGENCY) {
                return Messages.get("event.emergencyNotice");
            }
        }
        if (!event.isBookingOpen()) {
            return Messages.get("seatMap.bookingClosed");
        }
        return Messages.get("event.detailsAndBooking");
    }

    /** Shows how long the customer's held seats stay reserved for them. */
    private void updateHoldCountdown() {
        if (holdCountdownValue == null) {
            return;
        }
        List<Seat> selected = seatMapPanel.getSelectedSeats();
        if (selected.isEmpty()) {
            holdCountdownValue.setText("—");
            return;
        }
        long seconds = 0;
        for (Seat seat : selected) {
            seconds = Math.max(seconds, seatMapPanel.holdSecondsRemaining(seat.getKey()));
        }
        long minutes = seconds / 60;
        long remainder = seconds % 60;
        holdCountdownValue.setText(String.format(Locale.US, "%d:%02d", minutes, remainder));
        holdCountdownValue.setForeground(seconds < 60
                ? new Color(253, 186, 116) : new Color(253, 230, 138));
    }

    private void updateCountdownDisplays() {
        for (Map.Entry<JLabel, StadiumEvent> entry : countdownLabels.entrySet()) {
            StadiumEvent event = entry.getValue();
            entry.getKey().setText(eventCountdownText(event));
            entry.getKey().setForeground(bookingService.isBookingOpen(event) ? MUTED : WARNING_FOREGROUND);
        }
        for (Map.Entry<JButton, StadiumEvent> entry : eventActionButtons.entrySet()) {
            StadiumEvent event = entry.getValue();
            entry.getKey().setText(eventActionText(event));
            // Left enabled on purpose: a cancelled or postponed event still has a
            // details page, and that page is where the reason is explained. The
            // label beside it already says what is wrong.
        }
        if (selectedEvent != null) {
            bookingCountdownValue.setText(text("booking.stopsIn", selectedEvent.getCountdownLabel()));
        }
    }

    private JPanel createEventCard(StadiumEvent event) {
        return createEventCard(event, false);
    }

    /**
     * @param showVenue adds the venue name, needed when events from more than
     *                 one stadium are listed together
     */
    private JPanel createEventCard(StadiumEvent event, boolean showVenue) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(18, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 16, 14, 16)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        // Tapping anywhere on an event opens its details, where the booking button is.
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setToolTipText(text("event.tip.open", event.getHeadline()));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent click) {
                if (SwingUtilities.isLeftMouseButton(click)) {
                    showEventDetails(event);
                }
            }
        });

        JPanel time = new JPanel(new GridBagLayout());
        time.setOpaque(false);
        time.setPreferredSize(new Dimension(112, 90));
        GridBagConstraints timeConstraints = new GridBagConstraints();
        timeConstraints.anchor = GridBagConstraints.WEST;
        JLabel start = new JLabel(event.getTimeLabel());
        start.setForeground(event.isGame() ? BLUE_DARK : PURPLE);
        start.setFont(start.getFont().deriveFont(Font.BOLD, 20f));
        time.add(start, timeConstraints);
        JLabel date = new JLabel(event.getDateLabel());
        date.setForeground(MUTED);
        date.setFont(date.getFont().deriveFont(Font.PLAIN, 10f));
        timeConstraints.gridy = 1;
        timeConstraints.insets = new Insets(2, 0, 0, 0);
        time.add(date, timeConstraints);
        JLabel doors = new JLabel(text("event.doors", event.getDoorsLabel()));
        doors.setForeground(MUTED);
        doors.setFont(doors.getFont().deriveFont(Font.PLAIN, 10f));
        timeConstraints.gridy = 2;
        timeConstraints.insets = new Insets(5, 0, 0, 0);
        time.add(doors, timeConstraints);
        card.add(time, BorderLayout.WEST);

        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        GridBagConstraints detailConstraints = new GridBagConstraints();
        detailConstraints.anchor = GridBagConstraints.WEST;
        JLabel type = new JLabel(VenueWords.eventType(event.getType().getLabel(), true));
        type.setForeground(event.isGame() ? BLUE_DARK : PURPLE);
        type.setFont(type.getFont().deriveFont(Font.BOLD, 9f));
        details.add(type, detailConstraints);
        JLabel title = new JLabel(event.getHeadline());
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        detailConstraints.gridy = 1;
        detailConstraints.insets = new Insets(3, 0, 0, 0);
        details.add(title, detailConstraints);
        String detailText = event.getEventDetails();
        Stadium venue = showVenue ? StadiumData.getStadium(event.getStadiumId()) : null;
        if (venue != null) {
            detailText = detailText + "  •  " + venue.getName() + ", " + venue.getCity();
        }
        JLabel eventDetails = new JLabel(detailText);
        eventDetails.setForeground(MUTED);
        eventDetails.setFont(eventDetails.getFont().deriveFont(Font.PLAIN, 11f));
        detailConstraints.gridy = 2;
        detailConstraints.insets = new Insets(3, 0, 0, 0);
        details.add(eventDetails, detailConstraints);
        card.add(details, BorderLayout.CENTER);

        JPanel action = new JPanel(new GridBagLayout());
        action.setOpaque(false);
        boolean open = bookingService.isBookingOpen(event);
        JLabel availability = new JLabel(String.format(Locale.US, "%.3f%%",
                bookingService.getVacancyPercentage(event))
                + " " + Messages.get("event.vacantWord") + "  •  "
                + Messages.get("event.seatsAndPrice",
                        formatCapacity(bookingService.getAvailableSeatCount(event))));
        availability.setForeground(open ? SUCCESS_DARK : WARNING_FOREGROUND);
        availability.setFont(availability.getFont().deriveFont(Font.BOLD, 10f));
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridy = 0;
        actionConstraints.anchor = GridBagConstraints.EAST;
        action.add(availability, actionConstraints);
        JLabel deadline = new JLabel(eventCountdownText(event));
        deadline.setForeground(open ? MUTED : WARNING_FOREGROUND);
        deadline.setFont(deadline.getFont().deriveFont(Font.PLAIN, 9f));
        countdownLabels.put(deadline, event);
        actionConstraints.gridy = 1;
        actionConstraints.insets = new Insets(3, 0, 0, 0);
        action.add(deadline, actionConstraints);
        // A closed event is still worth opening: its details page explains why,
        // where the straight-to-seat-map path used to just refuse.
        JButton choose = createPrimaryButton(open ? Messages.get("event.detailsAndBooking") : Messages.get("event.whyItIsClosed"));
        eventActionButtons.put(choose, event);
        choose.addActionListener(ignored -> showEventDetails(event));
        actionConstraints.gridy = 2;
        actionConstraints.insets = new Insets(7, 0, 0, 0);
        action.add(choose, actionConstraints);
        card.add(action, BorderLayout.EAST);
        return card;
    }

    // ---------------------------------------------------------------------
    // Live schedules (cross-venue)
    // ---------------------------------------------------------------------

    /**
     * Shows every upcoming game and concert at Namboole, soonest first, so a
     * customer can find something to attend without filtering the main schedule.
     */
    private void showLiveSchedules() {
        currentScreen = "schedules";
        setHeader(text("nav.liveSchedules"), Messages.get("schedules.subtitle"));
        contentHost.removeAll();
        contentHost.add(buildLiveSchedulesContent(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        loadLiveFilters();
        refreshLiveSchedules();
        showStatus(Messages.get("schedules.title"));
    }

    private JPanel buildLiveSchedulesContent() {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(20, 0, 22, 0));

        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setOpaque(false);

        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);

        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.anchor = GridBagConstraints.WEST;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        titleConstraints.weightx = 1;
        JLabel title = new JLabel(text("events.allUpcoming"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        titleConstraints.gridy = 1;
        titleConstraints.insets = new Insets(7, 0, 0, 0);
        titleBox.add(title, titleConstraints);
        JLabel subtitle = new JLabel(text("events.allUpcomingSubtitle"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
        titleConstraints.gridy = 2;
        titleConstraints.insets = new Insets(3, 0, 0, 0);
        titleBox.add(subtitle, titleConstraints);
        top.add(titleBox, BorderLayout.WEST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filters.setOpaque(false);
        JPanel searchWrap = new JPanel(new BorderLayout(0, 3));
        searchWrap.setOpaque(false);
        searchWrap.setPreferredSize(new Dimension(320, 56));
        searchWrap.add(buildSearchBar(liveSearchField, text("stadium.search.hint")),
                BorderLayout.CENTER);
        filters.add(searchWrap);
        liveDateCombo = new JComboBox<>();
        liveDateCombo.setPreferredSize(new Dimension(180, 38));
        liveDateCombo.setBackground(WHITE);
        liveDateCombo.addActionListener(event -> refreshLiveSchedules());
        liveTypeCombo = new JComboBox<>(new String[]{Messages.get("schedules.allTypes"), Messages.get("schedules.games"), Messages.get("schedules.concerts")});
        liveTypeCombo.setPreferredSize(new Dimension(130, 38));
        liveTypeCombo.setBackground(WHITE);
        liveTypeCombo.addActionListener(event -> refreshLiveSchedules());
        filters.add(labelled(Messages.get("event.date"), liveDateCombo));
        filters.add(labelled(Messages.get("facts.type"), liveTypeCombo));
        top.add(filters, BorderLayout.EAST);
        page.add(top, BorderLayout.NORTH);

        JPanel listCard = createCard();
        listCard.setLayout(new BorderLayout(0, 10));
        listCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 14, 14, 14)));
        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setOpaque(false);
        liveResultLabel = new JLabel("—");
        liveResultLabel.setForeground(MUTED);
        liveResultLabel.setFont(liveResultLabel.getFont().deriveFont(Font.PLAIN, 10f));
        listHeader.add(liveResultLabel, BorderLayout.WEST);
        listCard.add(listHeader, BorderLayout.NORTH);
        liveListHost = new JPanel(new BorderLayout());
        liveListHost.setOpaque(false);
        listCard.add(liveListHost, BorderLayout.CENTER);
        page.add(listCard, BorderLayout.CENTER);
        return page;
    }

    /** Fills the date filter with every date that has at least one event. */
    private void loadLiveFilters() {
        if (liveDateCombo == null) {
            return;
        }
        liveDateCombo.removeAllItems();
        liveDateCombo.addItem(Messages.get("schedules.allDates"));
        List<LocalDate> dates = new ArrayList<>();
        for (StadiumEvent event : StadiumData.getEvents()) {
            if (!dates.contains(event.getDate())) {
                dates.add(event.getDate());
            }
        }
        java.util.Collections.sort(dates);
        for (LocalDate date : dates) {
            liveDateCombo.addItem(dayLabel(date));
        }
        liveDateCombo.setSelectedIndex(0);
    }

    /** Messages.get("schedules.today") / Messages.get("schedules.tomorrow") / "Sat, 3 Oct 2026". */
    private String dayLabel(LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.equals(today)) {
            return "Today · " + date.format(DATE_FORMATTER);
        }
        if (date.equals(today.plusDays(1))) {
            return "Tomorrow · " + date.format(DATE_FORMATTER);
        }
        return date.format(DATE_FORMATTER);
    }

    private void refreshLiveSchedules() {
        if (liveListHost == null) {
            return;
        }
        String query = liveSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
        int dateIndex = liveDateCombo == null ? 0 : liveDateCombo.getSelectedIndex();
        int typeIndex = liveTypeCombo == null ? 0 : liveTypeCombo.getSelectedIndex();
        LocalDate dateFilter = null;
        if (dateIndex > 0) {
            List<LocalDate> dates = new ArrayList<>();
            for (StadiumEvent event : StadiumData.getEvents()) {
                if (!dates.contains(event.getDate())) {
                    dates.add(event.getDate());
                }
            }
            java.util.Collections.sort(dates);
            dateFilter = dates.get(dateIndex - 1);
        }

        List<StadiumEvent> events = new ArrayList<>();
        for (StadiumEvent event : StadiumData.getEvents()) {
            if (dateFilter != null && !event.getDate().equals(dateFilter)) {
                continue;
            }
            if (typeIndex == 1 && !event.isGame()) {
                continue;
            }
            if (typeIndex == 2 && event.isGame()) {
                continue;
            }
            if (!query.isEmpty() && !matchesLiveQuery(event, query)) {
                continue;
            }
            events.add(event);
        }
        events.sort(java.util.Comparator.comparing(StadiumEvent::getDate)
                .thenComparing(StadiumEvent::getStartTime)
                .thenComparing(StadiumEvent::getId));

        countdownLabels.clear();
        eventActionButtons.clear();
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        if (events.isEmpty()) {
            list.add(buildEmptyState(Messages.get("schedules.noMatch"),
                    Messages.get("schedules.tryDifferent")));
        } else {
            LocalDate current = null;
            for (StadiumEvent event : events) {
                if (!event.getDate().equals(current)) {
                    current = event.getDate();
                    list.add(buildDayHeader(current));
                    list.add(Box.createVerticalStrut(2));
                }
                list.add(createEventCard(event, true));
                list.add(Box.createVerticalStrut(10));
            }
        }
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        liveListHost.removeAll();
        liveListHost.add(scroll, BorderLayout.CENTER);
        liveResultLabel.setText(events.size() + " event" + (events.size() == 1 ? "" : "s")
                + " coming up at "
                + StadiumData.getStadiums().get(0).getName()
                + (query.isEmpty() ? "" : "  •  matching \"" + liveSearchField.getText().trim() + "\""));
        liveListHost.revalidate();
        liveListHost.repaint();
    }

    private boolean matchesLiveQuery(StadiumEvent event, String query) {
        if (event.searchableText().contains(query)) {
            return true;
        }
        return venueMatches(event, query);
    }

    /**
     * Whether a venue should match a search word. Deliberately ignores the venue
     * description, which mentions words such as "Cranes" and would otherwise drag
     * in every event at that ground when the user searched for a team.
     */
    private boolean venueMatches(StadiumEvent event, String query) {
        Stadium stadium = StadiumData.getStadium(event.getStadiumId());
        if (stadium == null) {
            return false;
        }
        return (stadium.getName() + " " + stadium.getCity() + " " + stadium.getCountry()
                + " " + stadium.getVenueType()).toLowerCase(Locale.ENGLISH).contains(query);
    }

    private JPanel buildDayHeader(LocalDate date) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel label = new JLabel(dayLabel(date));
        label.setForeground(BLUE_DARK);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        header.add(label);
        return header;
    }

    /** Opens an event picked from the cross-venue schedule. */
    private void openEventFromSchedule(StadiumEvent event) {
        Stadium stadium = StadiumData.getStadium(event.getStadiumId());
        if (stadium == null) {
            showWarning(Messages.get("error.venueGone"));
            return;
        }
        selectedStadium = stadium;
        openEvent(event);
    }

    // ---------------------------------------------------------------------
    // Booking screen
    // ---------------------------------------------------------------------

    private void openEvent(StadiumEvent event) {
        openEvent(event, false);
    }

    /**
     * Opens the booking screen for an event.
     *
     * @param keepSelection when true the seats already chosen are carried over,
     *     which is what the event page's review button needs. Opening an event
     *     fresh still clears them, so a second booking never inherits the first
     *     one's seats by accident.
     */
    private void openEvent(StadiumEvent event, boolean keepSelection) {
        if (event == null) {
            return;
        }
        // The event can be opened from the live schedules, where no venue has
        // been picked yet, so the venue comes from the event itself.
        Stadium stadium = StadiumData.getStadium(event.getStadiumId());
        if (stadium != null) {
            selectedStadium = stadium;
        }
        if (selectedStadium == null) {
            return;
        }
        if (!bookingService.isBookingOpen(event)) {
            showWarning(bookingService.getBookingRestrictionMessage(event));
            return;
        }
        if (!"booking".equals(currentScreen)) {
            bookingReturnScreen = currentScreen;
        }
        selectedEvent = event;
        if (!keepSelection) {
            seatMapPanel.clearSelection();
        }
        bookingService.selectEvent(event);
        seatMapPanel.refreshStatuses();
        currentScreen = "booking";
        setHeader(Messages.get("booking.title"), selectedStadium.getName() + "  •  " + event.getHeadline());
        contentHost.removeAll();
        contentHost.add(buildBookingScreen(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        updateBookingSummary();
        showStatus("Event selected: " + event.getHeadline());
    }

    private JPanel buildBookingScreen() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(20, 0, 22, 0));
        page.add(buildEventOverview(), BorderLayout.NORTH);
        page.add(buildBookingWorkspace(), BorderLayout.CENTER);
        return page;
    }

    private JPanel buildEventOverview() {
        Color accent = colorFromHex(selectedStadium.getAccentColor(), BLUE);
        JPanel overview = createCard();
        overview.setLayout(new BorderLayout(18, 0));
        overview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(15, 20, 15, 20)));
        JPanel left = new JPanel(new GridBagLayout());
        left.setOpaque(false);
        // No back button here: the header's Back does this and knows where the
        // event was opened from. A second one on the page duplicated it.
        // The map lives on this page too, so it needs a floor of its own or
        // BorderLayout.CENTER stretches it to fill whatever height is left.
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.weightx = 1;
        JLabel stadium = new JLabel(selectedStadium.getName() + "  /  " + selectedEvent.getType().getLabel());
        stadium.setForeground(accent.darker());
        stadium.setFont(stadium.getFont().deriveFont(Font.BOLD, 10f));
        labelConstraints.gridy = 1;
        labelConstraints.insets = new Insets(8, 0, 0, 0);
        left.add(stadium, labelConstraints);
        JLabel title = new JLabel(selectedEvent.getHeadline());
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        labelConstraints.gridy = 2;
        labelConstraints.insets = new Insets(4, 0, 0, 0);
        left.add(title, labelConstraints);
        JLabel details = new JLabel(selectedEvent.getEventDetails());
        details.setForeground(MUTED);
        details.setFont(details.getFont().deriveFont(Font.PLAIN, 12f));
        labelConstraints.gridy = 3;
        labelConstraints.insets = new Insets(3, 0, 0, 0);
        left.add(details, labelConstraints);
        JLabel description = new JLabel("<html><div style='width:600px'>" +
                selectedEvent.getDescription() + "</div></html>");
        description.setForeground(MUTED);
        description.setFont(description.getFont().deriveFont(Font.PLAIN, 10f));
        labelConstraints.gridy = 4;
        labelConstraints.insets = new Insets(4, 0, 0, 0);
        left.add(description, labelConstraints);

        JPanel when = new JPanel(new GridBagLayout());
        when.setOpaque(false);
        when.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER), new EmptyBorder(0, 20, 0, 0)));
        GridBagConstraints whenConstraints = new GridBagConstraints();
        whenConstraints.anchor = GridBagConstraints.EAST;
        JLabel date = new JLabel(selectedEvent.getDateLabel());
        date.setForeground(TEXT);
        date.setFont(date.getFont().deriveFont(Font.BOLD, 14f));
        when.add(date, whenConstraints);
        JLabel time = new JLabel(text("event.starts", selectedEvent.getTimeLabel(), selectedEvent.getDoorsLabel()));
        time.setForeground(MUTED);
        time.setFont(time.getFont().deriveFont(Font.PLAIN, 11f));
        whenConstraints.gridy = 1;
        whenConstraints.insets = new Insets(4, 0, 0, 0);
        when.add(time, whenConstraints);
        JLabel deadline = new JLabel(text("event.deadline", selectedEvent.getBookingDeadlineLabel()));
        deadline.setForeground(bookingService.isBookingOpen(selectedEvent) ? MUTED : WARNING_FOREGROUND);
        deadline.setFont(deadline.getFont().deriveFont(Font.PLAIN, 10f));
        whenConstraints.gridy = 2;
        whenConstraints.insets = new Insets(5, 0, 0, 0);
        when.add(deadline, whenConstraints);
        JLabel vacancy = new JLabel(String.format(Locale.US, "%.3f%% of seats vacant",
                bookingService.getVacancyPercentage(selectedEvent)));
        vacancy.setForeground(SUCCESS_DARK);
        vacancy.setFont(vacancy.getFont().deriveFont(Font.BOLD, 11f));
        whenConstraints.gridy = 3;
        whenConstraints.insets = new Insets(3, 0, 0, 0);
        when.add(vacancy, whenConstraints);
        bookingCountdownValue.setForeground(BLUE_DARK);
        bookingCountdownValue.setFont(bookingCountdownValue.getFont().deriveFont(Font.BOLD, 11f));
        bookingCountdownValue.setText(text("booking.stopsIn", selectedEvent.getCountdownLabel()));
        whenConstraints.gridy = 4;
        whenConstraints.insets = new Insets(4, 0, 0, 0);
        when.add(bookingCountdownValue, whenConstraints);
        overview.add(left, BorderLayout.CENTER);
        overview.add(when, BorderLayout.EAST);
        return overview;
    }

    private JPanel buildBookingWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(0, 14));
        workspace.setOpaque(false);
        workspace.add(buildContactCard(), BorderLayout.NORTH);

        JPanel bookingBody = new JPanel(new BorderLayout(14, 0));
        bookingBody.setOpaque(false);
        bookingBody.add(buildSeatMapCard(), BorderLayout.CENTER);
        JPanel pricing = buildPricingBreakdownCard();
        pricing.setPreferredSize(new Dimension(350, 100));
        pricing.setMinimumSize(new Dimension(320, 100));
        bookingBody.add(pricing, BorderLayout.EAST);
        workspace.add(bookingBody, BorderLayout.CENTER);
        workspace.add(buildBookingSummary(), BorderLayout.SOUTH);
        return workspace;
    }

    private JPanel buildContactCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 18, 14, 18)));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel(text("booking.yourDetails"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        heading.add(title, BorderLayout.WEST);
        JLabel hint = new JLabel(text("booking.threeDetails"));
        hint.setForeground(MUTED);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        heading.add(hint, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        addField(fields, 0, Messages.get("form.name"), nameField);
        addField(fields, 1, Messages.get("bookings.field.email"), emailField);
        addField(fields, 2, Messages.get("bookings.field.phone"), phoneField);
        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    private void addField(JPanel parent, int column, String labelText, JTextField field) {
        describe(field, labelText, describeField(labelText));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, column == 0 ? 0 : 12, 0, 0);
        JPanel fieldBox = new JPanel(new BorderLayout(0, 5));
        fieldBox.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 10f));
        fieldBox.add(label, BorderLayout.NORTH);
        styleTextField(field);
        fieldBox.add(field, BorderLayout.CENTER);
        parent.add(fieldBox, constraints);
    }

    private JPanel buildSeatMapCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 18, 14, 18)));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = new JLabel(text("booking.chooseSeats"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
        heading.add(title, BorderLayout.WEST);
        // The limit is passed in rather than written into the wording, so the
        // screen cannot claim a different number from the one the code enforces.
        JLabel hint = new JLabel(Messages.get("booking.limitHint",
                BookingService.MAX_SEATS_PER_BOOKING));
        hint.setForeground(MUTED);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 11f));
        heading.add(hint, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);
        // The picker is one live component shared by the event page and the
        // booking page. It can only have one parent, so it is detached from the
        // previous screen before being attached here; otherwise the second use
        // would leave it stranded and the seat grid would vanish.
        if (seatMapPanel.getParent() != null) {
            seatMapPanel.getParent().remove(seatMapPanel);
        }
        card.add(seatMapPanel, BorderLayout.CENTER);
        // The card sits in a BorderLayout.CENTER slot that hands it all the
        // height left over, which would stretch the map down the screen. A
        // capped maximum stops that, leaving the rest for the price outline.
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 460));
        card.setPreferredSize(new Dimension(720, 420));
        return card;
    }

    private JPanel buildPricingBreakdownCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(13, 18, 12, 18)));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.anchor = GridBagConstraints.WEST;
        JLabel title = new JLabel(text("booking.priceOutline"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        titleBox.add(title, titleConstraints);
        JLabel subtitle = new JLabel(Messages.get("booking.priceSubtitle"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 10f));
        titleConstraints.gridy = 1;
        titleConstraints.insets = new Insets(3, 0, 0, 0);
        titleBox.add(subtitle, titleConstraints);
        heading.add(titleBox, BorderLayout.WEST);
        JLabel feeHint = new JLabel(Messages.get("booking.feeHint"));
        feeHint.setForeground(MUTED);
        feeHint.setFont(feeHint.getFont().deriveFont(Font.PLAIN, 10f));
        heading.add(feeHint, BorderLayout.EAST);
        card.add(heading, BorderLayout.NORTH);

        pricingBreakdownHost = new JPanel();
        pricingBreakdownHost.setOpaque(false);
        pricingBreakdownHost.setLayout(new BoxLayout(pricingBreakdownHost, BoxLayout.Y_AXIS));
        card.add(pricingBreakdownHost, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(10, 0));
        actions.setOpaque(false);
        JLabel note = new JLabel(Messages.get("booking.priceHint"));
        note.setForeground(MUTED);
        note.setFont(note.getFont().deriveFont(Font.PLAIN, 10f));
        actions.add(note, BorderLayout.CENTER);
        confirmBookingButton = createPrimaryButton(text("booking.confirm"));
        confirmBookingButton.addActionListener(event -> confirmBooking());
        actions.add(confirmBookingButton, BorderLayout.EAST);
        card.add(actions, BorderLayout.SOUTH);
        // The label counts the seats being booked, so it is set once the seats
        // that arrived from the seat map are known rather than at construction.
        updateBookButtons(seatMapPanel.getSelectedSeats());
        return card;
    }

    private JPanel buildBookingSummary() {
        JPanel summary = new JPanel(new BorderLayout(18, 0));
        summary.setBackground(NAVY);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NAVY_SOFT), new EmptyBorder(13, 18, 13, 18)));

        JPanel selection = new JPanel(new GridBagLayout());
        selection.setOpaque(false);
        selection.setPreferredSize(new Dimension(380, 52));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        JLabel selectedHeading = new JLabel(Messages.get("booking.yourSelection"));
        selectedHeading.setForeground(new Color(170, 195, 229));
        selectedHeading.setFont(selectedHeading.getFont().deriveFont(Font.BOLD, 10f));
        selection.add(selectedHeading, constraints);
        selectedSeatsValue.setForeground(WHITE);
        selectedSeatsValue.setFont(selectedSeatsValue.getFont().deriveFont(Font.BOLD, 14f));
        constraints.gridy = 1;
        constraints.insets = new Insets(4, 0, 0, 0);
        selection.add(selectedSeatsValue, constraints);

        JPanel total = new JPanel(new GridBagLayout());
        total.setOpaque(false);
        total.setPreferredSize(new Dimension(140, 52));
        GridBagConstraints totalConstraints = new GridBagConstraints();
        totalConstraints.anchor = GridBagConstraints.EAST;
        JLabel totalHeading = new JLabel(text("booking.total"));
        totalHeading.setForeground(new Color(170, 195, 229));
        totalHeading.setFont(totalHeading.getFont().deriveFont(Font.BOLD, 10f));
        total.add(totalHeading, totalConstraints);
        totalValue.setForeground(new Color(147, 197, 253));
        totalValue.setFont(totalValue.getFont().deriveFont(Font.BOLD, 21f));
        totalConstraints.gridy = 1;
        totalConstraints.insets = new Insets(4, 0, 0, 0);
        total.add(totalValue, totalConstraints);

        JPanel availability = new JPanel(new GridBagLayout());
        availability.setOpaque(false);
        availability.setPreferredSize(new Dimension(120, 52));
        GridBagConstraints availabilityConstraints = new GridBagConstraints();
        availabilityConstraints.anchor = GridBagConstraints.EAST;
        JLabel availableHeading = new JLabel(text("booking.vacancy"));
        availableHeading.setForeground(new Color(170, 195, 229));
        availableHeading.setFont(availableHeading.getFont().deriveFont(Font.BOLD, 10f));
        availability.add(availableHeading, availabilityConstraints);
        bookingAvailabilityValue.setForeground(new Color(167, 243, 208));
        bookingAvailabilityValue.setFont(bookingAvailabilityValue.getFont().deriveFont(Font.BOLD, 16f));
        availabilityConstraints.gridy = 1;
        availabilityConstraints.insets = new Insets(4, 0, 0, 0);
        availability.add(bookingAvailabilityValue, availabilityConstraints);

        JPanel holds = new JPanel(new GridBagLayout());
        holds.setOpaque(false);
        holds.setPreferredSize(new Dimension(150, 52));
        GridBagConstraints holdConstraints = new GridBagConstraints();
        holdConstraints.anchor = GridBagConstraints.EAST;
        JLabel holdHeading = new JLabel(text("booking.seatsHeld"));
        holdHeading.setForeground(new Color(170, 195, 229));
        holdHeading.setFont(holdHeading.getFont().deriveFont(Font.BOLD, 10f));
        holds.add(holdHeading, holdConstraints);
        holdCountdownValue = new JLabel("—");
        holdCountdownValue.setForeground(new Color(253, 230, 138));
        holdCountdownValue.setFont(holdCountdownValue.getFont().deriveFont(Font.BOLD, 16f));
        holdConstraints.gridy = 1;
        holdConstraints.insets = new Insets(4, 0, 0, 0);
        holds.add(holdCountdownValue, holdConstraints);

        JPanel rightSide = new JPanel(new GridBagLayout());
        rightSide.setOpaque(false);
        GridBagConstraints rightConstraints = new GridBagConstraints();
        rightSide.add(availability, rightConstraints);
        rightConstraints.gridx = 1;
        rightSide.add(holds, rightConstraints);

        JPanel middle = new JPanel(new BorderLayout(15, 0));
        middle.setOpaque(false);
        middle.add(selection, BorderLayout.WEST);
        middle.add(total, BorderLayout.CENTER);
        middle.add(rightSide, BorderLayout.EAST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 3));
        actions.setOpaque(false);
        clearSelectionButton = createSecondaryButton(text("booking.clearSeats"));
        clearSelectionButton.addActionListener(event -> clearSelection());
        JButton saveSelection = createSecondaryButton(Messages.get("booking.saveSelection"));
        saveSelection.setToolTipText(text("saved.tip.keep"));
        saveSelection.addActionListener(ignored -> saveCurrentSelection());
        JButton viewBooked = createSecondaryButton(Messages.get("booking.viewBooked"));
        viewBooked.setToolTipText(text("ledger.tip"));
        viewBooked.addActionListener(event -> showSeatLedger());
        actions.add(clearSelectionButton);
        actions.add(saveSelection);
        actions.add(viewBooked);
        summary.add(middle, BorderLayout.CENTER);
        summary.add(actions, BorderLayout.EAST);
        return summary;
    }

    /**
     * Called every time somebody clicks a seat, to say what just happened in the
     * status line at the bottom of the screen.
     *
     * <p>We have to work out whether the click added the seat or took it away.
     * The only reliable way is to look at the selection afterwards: if this seat
     * is in it, the click added it.
     */
    private void onSeatToggled(Seat seat) {
        boolean selected = false;

        for (Seat item : seatMapPanel.getSelectedSeats()) {
            if (item.getKey().equals(seat.getKey())) {
                selected = true;
                break;
            }
        }

        showStatus((selected ? "Selected seat " : "Removed seat ") + seat.display());
    }

    private void refreshPricingBreakdown() {
        List<Seat> selectedSeats = seatMapPanel.getSelectedSeats();
        // The booking buttons are refreshed even when this screen has no price
        // card, because the event page carries those buttons and no price card.
        updateBookButtons(selectedSeats);
        if (pricingBreakdownHost == null) {
            return;
        }
        pricingBreakdownHost.removeAll();
        if (selectedSeats.isEmpty()) {
            JLabel empty = new JLabel(Messages.get("booking.emptyPrice"));
            empty.setForeground(MUTED);
            empty.setFont(empty.getFont().deriveFont(Font.PLAIN, 11f));
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            pricingBreakdownHost.add(empty);
        } else {
            for (Seat seat : selectedSeats) {
                pricingBreakdownHost.add(createPriceRow(
                        seat.display() + "  •  " + bookingService.getRowTier(seat.getKey()),
                        currency(seat.getPrice()), false));
            }
            pricingBreakdownHost.add(Box.createVerticalStrut(4));
            pricingBreakdownHost.add(createPriceRow("Seat subtotal",
                    currency(bookingService.totalFor(selectedSeats)), false));
            pricingBreakdownHost.add(createPriceRow(Messages.get("booking.bookingFeeOnce"),
                    currency(bookingService.getBookingFee()), false));
            pricingBreakdownHost.add(createPriceRow("Total due",
                    currency(bookingService.getTotalCharge(selectedSeats)), true));
        }
        pricingBreakdownHost.revalidate();
        pricingBreakdownHost.repaint();
    }

    /**
     * Keeps the booking buttons in step with the current selection.
     *
     * <p>The button on the seat map page only exists once seats are chosen, and
     * its label counts them, so it has to be rebuilt rather than merely enabled.
     * Rebuilding the page's action row on every seat change keeps the button,
     * its count and the quoted total together.
     */
    private void updateBookButtons(List<Seat> selectedSeats) {
        boolean canBook = !selectedSeats.isEmpty() && bookingService.isBookingOpen();
        if (confirmBookingButton != null) {
            confirmBookingButton.setEnabled(canBook);
            // "1 seats" was wrong, and a customer reading the button before
            // pressing it should never see a grammatical error on the control
            // that charges them.
            confirmBookingButton.setText(selectedSeats.isEmpty()
                    ? text("booking.confirm") : confirmLabel(selectedSeats.size()));
        }
        if (selectedEvent == null || !"event-details".equals(currentScreen)) {
            return;
        }
        if (eventActionsHost == null) {
            return;
        }
        eventActionsHost.removeAll();
        eventActionsHost.add(buildEventActions(selectedEvent,
                bookingService.isBookingOpen(selectedEvent)));
        eventActionsHost.revalidate();
        eventActionsHost.repaint();
    }

    private JPanel createPriceRow(String label, String value, boolean emphasis) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, emphasis ? 29 : 24));
        JLabel left = new JLabel(label);
        left.setForeground(emphasis ? TEXT : MUTED);
        left.setFont(left.getFont().deriveFont(emphasis ? Font.BOLD : Font.PLAIN,
                emphasis ? 12f : 11f));
        JLabel right = new JLabel(value);
        right.setForeground(emphasis ? BLUE_DARK : TEXT);
        right.setFont(right.getFont().deriveFont(Font.BOLD, emphasis ? 13f : 11f));
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        row.add(left, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private void updateBookingSummary() {
        if (selectedEvent == null) {
            return;
        }
        List<Seat> selectedSeats = seatMapPanel.getSelectedSeats();
        selectedSeatsValue.setText(selectedSeats.isEmpty()
                ? Messages.get("booking.noSeats") : joinSeatNames(selectedSeats));
        totalValue.setText(currency(bookingService.getTotalCharge(selectedSeats)));
        bookingAvailabilityValue.setText(String.format(Locale.US, "%.3f%%",
                bookingService.getVacancyPercentage()));
        bookingAvailabilityValue.setForeground(bookingService.getVacancyPercentage() <= 10.0
                ? WARNING_FOREGROUND : new Color(167, 243, 208));
        if (clearSelectionButton != null) {
            clearSelectionButton.setEnabled(!selectedSeats.isEmpty());
        }
        refreshPricingBreakdown();
        updateEventAvailability();
        updateCountdownDisplays();
        updateHoldCountdown();
    }

    private void clearSelection() {
        seatMapPanel.clearSelection();
        showStatus(Messages.get("booking.seatSelectionCleared"));
    }

    /**
     * Takes the booking for the seats currently chosen, then issues the receipt.
 *
     * <p>The confirmation has already happened by the time this runs: the button
     * that was pressed is labelled with the seat count and has the total printed
     * beside it, and the contact details are collected before it is pressed. So
     * there is no second "are you sure?" dialog here — asking twice meant two
     * confirmations for one booking, and the second one had nothing new in it.
     * Payment is still asked for, so there is a way out before any money moves.
     */
    private void confirmBooking() {
        if (selectedEvent == null) {
            return;
        }
        List<Seat> selectedSeats = seatMapPanel.getSelectedSeats();
        if (selectedSeats.isEmpty()) {
            showWarning(Messages.get("booking.needSeatToConfirm"));
            return;
        }
        try {
            // The seats booked are exactly the seats the customer picked. Nothing
            // is reassigned to make a booking look tidier across the ends: the
            // seat a customer chose, and the price quoted for it, is what they get.
            Booking booking = bookingService.book(nameField.getText(), emailField.getText(),
                    phoneField.getText(), selectedSeats);
            showBookingReceipt(booking, selectedEvent);
        } catch (IllegalArgumentException exception) {
            // The details were checked before the booking was attempted, so
            // arriving here means something other than what was typed went
            // wrong. It is still shown as a warning, but the customer is put back
            // on the details form so they can check and correct them rather than
            // being left with a warning and no way to act on it.
            if (!collectContactDetailsIfNeeded()) {
                return;
            }
            showWarning(exception.getMessage());
        }
    }

    /**
     * Clears the seat selection and issues the receipt for a booking that was
     * just taken.
     *
     * <p>Separate from {@link #confirmBooking()} so the success path reads in
     * order: the seats are released, the screen is told, and the receipt is
     * issued. A failure part-way leaves the holds in place, so the customer has
     * not lost their seats to an error on the way to the receipt.
     */
    private void showBookingReceipt(Booking booking, StadiumEvent event) {
        seatMapPanel.getHoldService().releaseAllFor(sessionOwner);
        seatMapPanel.clearSelection();
        seatMapPanel.refreshStatuses();
        clearContactFields();
        updateBookingSummary();
        showStatus("Seat" + (booking.getSeats().size() == 1 ? "" : "s") + " booked: "
                + booking.getSeatDisplay());
        settleAndIssueTicket(booking, event);
    }

    private boolean sameSeats(List<Seat> first, List<Seat> second) {
        if (first.size() != second.size()) {
            return false;
        }
        for (int index = 0; index < first.size(); index++) {
            if (!first.get(index).getKey().equals(second.get(index).getKey())) {
                return false;
            }
        }
        return true;
    }

    /** Lists how the chosen seats divide up between sections A to D. */
    private String sectionBreakdown(List<Seat> seats) {
        Map<String, Integer> perSection = new LinkedHashMap<>();
        for (Seat seat : seats) {
            perSection.merge(seat.getKey().getSection(), 1, Integer::sum);
        }
        if (perSection.size() <= 1) {
            return "";
        }
        StringBuilder text = new StringBuilder("By section:  ");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : perSection.entrySet()) {
            if (!first) {
                text.append("   ");
            }
            text.append(entry.getKey()).append(' ').append(entry.getValue());
            first = false;
        }
        return text.toString();
    }

    /**
     * Asks how the booking will be paid for, then settles it and shows the ticket.
     * Cash at the venue needs nothing extra; mobile money is clearly marked as a
     * simulation because no provider credentials ship with this build.
     */
    private void settleAndIssueTicket(Booking booking, StadiumEvent event) {
        JComboBox<PaymentRecord.Method> methods =
                new JComboBox<>(PaymentRecord.Method.values());
        methods.setSelectedItem(PaymentRecord.Method.CASH_AT_VENUE);
        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.add(new JLabel(Messages.get("payment.question")), BorderLayout.NORTH);
        form.add(methods, BorderLayout.CENTER);
        JLabel note = new JLabel(Messages.get("common.totalDue") + ": " + currency(booking.getTotal())
                + (PaymentRecord.isRealProviderConfigured()
                ? "" : "  •  mobile money is simulated, no money moves"));
        note.setForeground(MUTED);
        note.setFont(note.getFont().deriveFont(Font.PLAIN, 10f));
        form.add(note, BorderLayout.SOUTH);

        String choice = showDialogChoice(this, form, Messages.get("payment.title"),
                JOptionPane.PLAIN_MESSAGE, Messages.get("common.pay"),
                Messages.get("common.pay"), backLabel());
        if (!"Pay".equals(choice)) {
            return;
        }
        PaymentRecord.Method method = (PaymentRecord.Method) methods.getSelectedItem();
        PaymentRecord payment = method == PaymentRecord.Method.MOBILE_MONEY
                ? PaymentRecord.simulatedMobileMoney(booking.getTotal(), phoneField.getText())
                : PaymentRecord.cashAtVenue(booking.getTotal());

        showStatus("Booking " + booking.getReference() + " • " + payment.describe());
        // The receipt is what the customer asked for: an itemised bill for the
        // booking. The e-ticket is still one button away inside it, for anyone
        // who needs the gate document rather than the bill.
        showReceipt(booking, payment);
        refreshBookings();
    }

    /**
     * Shows the receipt straight after a booking is paid for.
     *
     * <p>This is the itemised bill: every seat on its own line with its price,
     * the charge per section, the ticketing fee and the total. The text is
     * laid out in a monospaced font so the columns line up, which is the whole
     * point of a receipt.
     */
    private void showReceipt(Booking booking, PaymentRecord payment) {
        Receipt receipt = Receipt.forBooking(booking, bookingService);
        // A receipt reopened from the booking history has no payment record to
        // quote, so that line is only shown when one was actually settled.
        String header = payment == null ? "" : "  Payment: " + payment.describe() + "\n\n";
        JTextArea area = new JTextArea(header + receipt.toText(), 34, 70);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        area.setCaretPosition(0);
        area.setBackground(new Color(250, 252, 255));
        area.setMargin(new java.awt.Insets(10, 12, 10, 12));

        String choice = showDialogChoice(this, new JScrollPane(area),
                "Receipt " + booking.getReference(), JOptionPane.INFORMATION_MESSAGE,
                Messages.get("receipt.save"), Messages.get("receipt.save"), Messages.get("common.print"), "Back");
        if (Messages.get("receipt.save").equals(choice)) {
            saveTextToFile("receipt-" + booking.getReference() + ".txt",
                    "Receipt " + booking.getReference(), header + receipt.toText(), "Receipt saved to ");
        } else if (Messages.get("common.print").equals(choice)) {
            printTicket("Receipt " + booking.getReference(), header + receipt.toText());
        }
    }

    private void showBookingTicket(Booking booking, StadiumEvent event, PaymentRecord payment) {
        Stadium stadium = StadiumData.getStadium(booking.getStadiumId());
        JTextArea ticket = new JTextArea(
                TicketBuilder.text(booking, stadium, event) + "\n  Payment: " + payment.describe() + "\n",
                24, 58);
        ticket.setEditable(false);
        ticket.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        ticket.setCaretPosition(0);
        ticket.setBackground(new Color(250, 252, 255));

        String choice = showDialogChoice(this, new JScrollPane(ticket),
                "Ticket " + booking.getReference(), JOptionPane.INFORMATION_MESSAGE,
                Messages.get("ticket.viewReceipt"), Messages.get("ticket.viewReceipt"), Messages.get("ticket.saveAsText"), Messages.get("common.print"), backLabel());
        if (Messages.get("ticket.viewReceipt").equals(choice)) {
            showReceipt(booking, payment);
        } else if (Messages.get("ticket.saveAsText").equals(choice)) {
            saveTextToFile("ticket-" + booking.getReference() + ".txt",
                    "Ticket " + booking.getReference(), ticket.getText(), "Ticket saved to ");
        } else if (Messages.get("common.print").equals(choice)) {
            printTicket("Ticket " + booking.getReference(), ticket.getText());
        }
    }

    /**
     * Offers to save a piece of generated text, such as a receipt or a ticket.
     *
     * @param suggestedName the file name to offer
     * @param jobTitle      the title for any print job and for the status line
     * @param contents      the text to write
     * @param statusPrefix  wording for the confirmation, so the message names the
     *                      document rather than always saying "ticket"
     */
    private void saveTextToFile(String suggestedName, String jobTitle,
                                String contents, String statusPrefix) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(suggestedName));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            java.nio.file.Files.writeString(chooser.getSelectedFile().toPath(), contents);
            showStatus(statusPrefix + chooser.getSelectedFile().getName());
        } catch (java.io.IOException exception) {
            showWarning(Messages.get("ticket.couldNotSave") + " " + exception.getMessage());
        }
    }

    private void printTicket(String jobTitle, String contents) {
        java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
        job.setJobName(jobTitle);
        job.setPrintable(new java.awt.print.Printable() {
            @Override
            public int print(java.awt.Graphics graphics, java.awt.print.PageFormat format,
                             int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D copy = (Graphics2D) graphics;
                copy.translate(format.getImageableX(), format.getImageableY());
                java.util.List<String> lines = java.util.Arrays.asList(contents.split("\n"));
                java.awt.FontMetrics metrics = copy.getFontMetrics();
                int y = 0;
                for (String line : lines) {
                    copy.drawString(line, 0, y);
                    y += metrics.getHeight();
                }
                return PAGE_EXISTS;
            }
        });
        if (job.printDialog()) {
            showStatus(jobTitle + " sent to the printer");
        }
    }

    /** Writes every booking to a CSV file the user chooses. */
    private void exportBookingsToCsv() {
        List<Booking> bookings = bookingService.getBookings();
        if (bookings.isEmpty()) {
            showWarning(Messages.get("export.noneYet"));
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("bookings.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            java.nio.file.Files.writeString(chooser.getSelectedFile().toPath(),
                    TicketBuilder.csv(bookings));
            showStatus("Exported " + bookings.size() + " bookings to "
                    + chooser.getSelectedFile().getName());
        } catch (java.io.IOException exception) {
            showWarning(Messages.get("export.couldNotWrite") + " " + exception.getMessage());
        }
    }

    private void clearContactFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }

    // ---------------------------------------------------------------------
    // Booking history
    // ---------------------------------------------------------------------

    private void showBookings() {
        // No sign-in and no PIN: anyone using the application reaches their bookings
        // directly, because the system is open to the people who book.
        bookingSearchField.setText("");
        currentScreen = "bookings";
        setHeader(text("nav.bookings"), bookingHistorySubtitle());
        contentHost.removeAll();
        contentHost.add(buildBookingsContent(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        refreshBookings();
        showStatus(Messages.get("bookings.title"));
    }

    private void refreshBookingsIfVisible() {
        if ("bookings".equals(currentScreen)) {
            refreshBookings();
        }
    }

    private String bookingHistorySubtitle() {
        return Messages.get("bookings.emptyLead") + " " + StadiumData.getStadiums().get(0).getName()
                + ". Open one to see its seats, its charges and its receipt.";
    }

    private JPanel buildBookingsContent() {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(20, 0, 22, 0));

        JPanel top = new JPanel(new GridBagLayout());
        top.setOpaque(false);
        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);

        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.anchor = GridBagConstraints.WEST;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        titleConstraints.weightx = 1;
        JLabel title = new JLabel(text("bookings.title"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        titleConstraints.gridy = 1;
        titleConstraints.insets = new Insets(7, 0, 0, 0);
        titleBox.add(title, titleConstraints);
        JLabel subtitle = new JLabel(Messages.get("bookings.subtitle"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
        titleConstraints.gridy = 2;
        titleConstraints.insets = new Insets(3, 0, 0, 0);
        titleBox.add(subtitle, titleConstraints);

        GridBagConstraints topTitleConstraints = new GridBagConstraints();
        topTitleConstraints.gridx = 0;
        topTitleConstraints.weightx = 1;
        topTitleConstraints.fill = GridBagConstraints.HORIZONTAL;
        topTitleConstraints.anchor = GridBagConstraints.WEST;
        top.add(titleBox, topTitleConstraints);

        JPanel search = new JPanel(new BorderLayout(10, 0));
        search.setOpaque(false);
        search.setPreferredSize(new Dimension(390, 42));
        search.add(buildSearchBar(bookingSearchField, text("bookings.search.hint")), BorderLayout.CENTER);
        GridBagConstraints topSearchConstraints = new GridBagConstraints();
        topSearchConstraints.gridx = 1;
        topSearchConstraints.anchor = GridBagConstraints.NORTHEAST;
        topSearchConstraints.fill = GridBagConstraints.NONE;
        top.add(search, topSearchConstraints);
        page.add(top, BorderLayout.NORTH);

        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 14, 14, 14)));
        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        JLabel tableHint = new JLabel(Messages.get("bookings.hint"));
        tableHint.setForeground(MUTED);
        tableHint.setFont(tableHint.getFont().deriveFont(Font.PLAIN, 10f));
        tableHeader.add(tableHint, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        configureBookingTable();
        JScrollPane tableScroll = new JScrollPane(bookingTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(WHITE);
        tableScroll.getVerticalScrollBar().setUnitIncrement(18);
        tableCard.add(tableScroll, BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);
        JButton refresh = createSecondaryButton(text("bookings.refresh"));
        refresh.addActionListener(event -> refreshBookings());
        JButton export = createSecondaryButton(text("bookings.export"));
        export.setToolTipText(text("bookings.tip.export"));
        export.addActionListener(event -> exportBookingsToCsv());
        cancelBookingButton = createSecondaryButton(Messages.get("bookings.cancelSelected"), new Color(185, 28, 28));
        cancelBookingButton.addActionListener(event -> cancelSelectedBooking());
        bottom.add(refresh);
        bottom.add(export);
        bottom.add(cancelBookingButton);
        page.add(bottom, BorderLayout.SOUTH);
        return page;
    }

    private void configureBookingTable() {
        bookingTable.setBackground(WHITE);
        bookingTable.setForeground(TEXT);
        bookingTable.setRowHeight(34);
        bookingTable.setShowVerticalLines(false);
        bookingTable.setGridColor(new Color(235, 240, 247));
        bookingTable.setIntercellSpacing(new Dimension(0, 1));
        bookingTable.setSelectionBackground(new Color(219, 234, 254));
        bookingTable.setSelectionForeground(TEXT);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        bookingTable.setFillsViewportHeight(true);
        JTableHeader header = bookingTable.getTableHeader();
        header.setBackground(SKY);
        header.setForeground(BLUE_DARK);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 11f));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        TableCellRenderer renderer = new BookingTableRenderer();
        for (int column = 0; column < bookingTable.getColumnCount(); column++) {
            bookingTable.getColumnModel().getColumn(column).setCellRenderer(renderer);
        }
        int[] widths = {100, 175, 270, 190, 190, 95, 120};
        for (int column = 0; column < widths.length; column++) {
            bookingTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }
    }

    /**
     * The bookings on screen.
     *
     * <p>The system is open to the people who book, so there is nobody signed in
     * and nothing withheld: this is every reservation, filtered only by whatever
     * has been typed in the search box.
     */
    private List<Booking> visibleBookings() {
        return bookingService.getBookings();
    }

    private void refreshBookings() {
        if (bookingTableModel == null) {
            return;
        }
        String query = bookingSearchField.getText().trim().toLowerCase(Locale.ENGLISH);
        List<Booking> visible = new ArrayList<>();
        for (Booking booking : visibleBookings()) {
            if (query.isEmpty() || bookingSearchText(booking).contains(query)) {
                visible.add(booking);
            }
        }
        bookingTableModel.setRowCount(0);
        for (Booking booking : visible) {
            StadiumEvent event = StadiumData.getEvent(booking.getEventId());
            Stadium stadium = StadiumData.getStadium(booking.getStadiumId());
            bookingTableModel.addRow(new Object[]{
                    booking.getReference(),
                    stadium == null ? Messages.get("bookings.section.legacy") : stadium.getName(),
                    event == null ? booking.getEvent() : event.getHeadline(),
                    bookingWhenLabel(booking, event),
                    booking.getSeatDisplay(),
                    currency(booking.getTotal()),
                    booking.getStatus().name()
            });
        }
        updateCancelButton();
    }

    private String bookingSearchText(Booking booking) {
        Stadium stadium = StadiumData.getStadium(booking.getStadiumId());
        StadiumEvent event = StadiumData.getEvent(booking.getEventId());
        String eventText = event == null ? booking.getEvent()
                : event.getHeadline() + " " + event.getEventDetails() + " " + bookingWhenLabel(booking, event);
        return String.join(" ", booking.getReference(), stadium == null ? "" : stadium.getName(),
                eventText, booking.getSeatDisplay(), booking.getStatus().name()).toLowerCase(Locale.ENGLISH);
    }

    private String bookingWhenLabel(Booking booking, StadiumEvent event) {
        if (booking.getEventDate() != null && booking.getEventStartTime() != null) {
            return booking.getEventDate().format(DATE_FORMATTER) + "  •  "
                    + booking.getEventStartTime().format(TIME_FORMATTER);
        }
        return event == null ? "—" : event.getWhenLabel();
    }

    private void showBookingDetails(int row) {
        if (row < 0 || row >= bookingTableModel.getRowCount()) {
            return;
        }
        String reference = String.valueOf(bookingTableModel.getValueAt(row, 0));
        Booking booking = null;
        for (Booking candidate : visibleBookings()) {
            if (candidate.getReference().equals(reference)) {
                booking = candidate;
                break;
            }
        }
        if (booking == null) {
            // The row has gone since the table was drawn, so the detail screen
            // has nothing to show rather than showing something stale.
            showStatus("That booking is no longer listed");
            return;
        }
        final Booking found = booking;

        Stadium stadium = StadiumData.getStadium(booking.getStadiumId());
        StadiumEvent event = StadiumData.getEvent(booking.getEventId());
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(8, 10, 8, 10));

        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        JLabel title = new JLabel(text("booking.referenceTitle", booking.getReference()));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        header.add(title, BorderLayout.WEST);
        JLabel status = new JLabel(booking.getStatus().name());
        status.setForeground(booking.isConfirmed() ? SUCCESS_DARK : CANCELLED);
        status.setFont(status.getFont().deriveFont(Font.BOLD, 11f));
        header.add(status, BorderLayout.EAST);
        content.add(header);
        content.add(Box.createVerticalStrut(6));

        addDetailSection(content, "BOOKING");
        addDetailRow(content, Messages.get("bookings.table.reference"), booking.getReference());
        addDetailRow(content, Messages.get("bookings.table.status"), booking.getStatus().name());
        addDetailRow(content, Messages.get("bookings.table.created"), CREATED_FORMATTER.format(booking.getCreatedAt()));

        addDetailSection(content, "STADIUM");
        addDetailRow(content, "Venue", stadium == null ? Messages.get("bookings.section.legacy") : stadium.getName());
        addDetailRow(content, "Location", stadium == null ? "—" : stadium.getLocation());
        addDetailRow(content, "Address", stadium == null ? "—" : stadium.getAddress());
        addDetailRow(content, "Capacity", stadium == null ? "—"
                : formatCapacity(stadium.getSeatCount()) + " seats");

        addDetailSection(content, "EVENT");
        addDetailRow(content, "Event", event == null ? booking.getEvent() : event.getHeadline());
        addDetailRow(content, Messages.get("facts.type"), event == null ? "—" : VenueWords.eventType(event.getType().getLabel()));
        addDetailRow(content, Messages.get("request.details"), event == null ? "—" : event.getEventDetails());
        addDetailRow(content, Messages.get("event.dateAndTime"), bookingWhenLabel(booking, event));
        addDetailRow(content, event == null ? Messages.get("event.doors", "—")
                : Messages.get("event.doors", event.getDoorsLabel()), "");
        addDetailRow(content, Messages.get("event.bookingDeadline"), event == null ? "—"
                : event.getBookingDeadlineLabel());

        addDetailSection(content, "BOOKED BY");
        addDetailRow(content, Messages.get("bookings.field.fullName"), booking.getCustomerName());
        addDetailRow(content, Messages.get("bookings.field.email"), booking.getEmail());
        addDetailRow(content, Messages.get("bookings.field.phone"), booking.getPhone());

        addDetailSection(content, "SEATS AND PAYMENT");
        addDetailRow(content, Messages.get("bookings.field.bookedSeats"), booking.getSeatDisplay());
        addDetailRow(content, Messages.get("bookings.field.seatCount"), String.valueOf(booking.getSeats().size()));
        double seatSubtotal = Math.max(0.0, booking.getTotal() - BookingService.BOOKING_FEE);
        addDetailRow(content, Messages.get("booking.seatSubtotal"), currency(seatSubtotal));
        addDetailRow(content, Messages.get("booking.bookingFee"), currency(BookingService.BOOKING_FEE));
        addDetailRow(content, Messages.get("bookings.field.totalCharged"), currency(booking.getTotal()));

        // The itemised receipt, one screen down, so a past booking can be
        // billed again without the customer having to remember a reference.
        JButton receiptButton = createPrimaryButton(text("booking.viewReceipt"));
        receiptButton.addActionListener(ignored -> showReceipt(found, null));
        JPanel receiptRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        receiptRow.setOpaque(false);
        receiptRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        receiptRow.add(receiptButton);
        content.add(receiptRow);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.setPreferredSize(new Dimension(650, 620));
        showDialogChoice(this, scroll, Messages.get("bookings.completeDetails"),
                JOptionPane.INFORMATION_MESSAGE, backLabel(), backLabel());
    }

    private void addDetailSection(JPanel parent, String title) {
        JLabel section = new JLabel(title);
        section.setForeground(BLUE_DARK);
        section.setFont(section.getFont().deriveFont(Font.BOLD, 10f));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setBorder(new EmptyBorder(10, 0, 4, 0));
        parent.add(section);
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        JLabel key = new JLabel(label);
        key.setForeground(MUTED);
        key.setFont(key.getFont().deriveFont(Font.BOLD, 10f));
        key.setPreferredSize(new Dimension(145, 25));
        JLabel content = new JLabel("<html><div style='width:330px'>"
                + htmlText(value == null ? "—" : value) + "</div></html>");
        content.setForeground(TEXT);
        content.setFont(content.getFont().deriveFont(Font.PLAIN, 11f));
        row.add(key, BorderLayout.WEST);
        row.add(content, BorderLayout.CENTER);
        parent.add(row);
    }

    private void updateCancelButton() {
        if (cancelBookingButton != null) {
            cancelBookingButton.setEnabled(bookingTable.getSelectedRow() >= 0);
        }
    }

    private void cancelSelectedBooking() {
        int row = bookingTable.getSelectedRow();
        if (row < 0) {
            showWarning(Messages.get("bookings.selectFirst"));
            return;
        }
        String reference = String.valueOf(bookingTableModel.getValueAt(row, 0));
        String status = String.valueOf(bookingTableModel.getValueAt(row, 6));
        if ("CANCELLED".equals(status)) {
            showWarning(Messages.get("bookings.alreadyCancelled"));
            return;
        }
        String choice = showDialogChoice(this,
                Messages.get("bookings.cancelQuestion", reference) + "\n\n"
                        + Messages.get("bookings.cancelBody"),
                Messages.get("bookings.cancelTitle"), JOptionPane.WARNING_MESSAGE,
                Messages.get("bookings.cancelTitle"), backLabel(),
                Messages.get("bookings.cancelTitle"));
        if (!Messages.get("bookings.cancelTitle").equals(choice)) {
            return;
        }
        if (bookingService.cancel(reference)) {
            seatMapPanel.refreshStatuses();
            refreshBookings();
            updateBookingSummary();
            showStatus("Booking " + reference + " cancelled");
        } else {
            showWarning(Messages.get("bookings.couldNotCancel"));
        }
    }

    // ---------------------------------------------------------------------
    // Occupancy report
    // ---------------------------------------------------------------------

    /**
     * Reports how full each venue is, section by section, across every event.
     * This is the figure a venue manager actually needs, so it is computed from
     * the database rather than from the current screen.
     */
    private void showOccupancyReport() {
        currentScreen = "occupancy";
        setHeader(Messages.get("occupancy.title"), Messages.get("occupancy.subtitle"));
        contentHost.removeAll();
        contentHost.add(buildOccupancyContent(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        showStatus(Messages.get("occupancy.title"));
    }

    private JPanel buildOccupancyContent() {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(20, 0, 22, 0));

        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setOpaque(false);
        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);
        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.anchor = GridBagConstraints.WEST;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        titleConstraints.weightx = 1;
        JLabel title = new JLabel(Messages.get("occupancy.allVenues"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        titleConstraints.gridy = 1;
        titleConstraints.insets = new Insets(7, 0, 0, 0);
        titleBox.add(title, titleConstraints);
        JLabel subtitle = new JLabel(Messages.get("occupancy.note"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
        titleConstraints.gridy = 2;
        titleConstraints.insets = new Insets(3, 0, 0, 0);
        titleBox.add(subtitle, titleConstraints);
        top.add(titleBox, BorderLayout.WEST);
        page.add(top, BorderLayout.NORTH);

        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 14, 14, 14)));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Event", Messages.get("facts.type"), Messages.get("event.date"), Messages.get("facts.seatsOnSale"), Messages.get("occupancy.seatsBooked"),
                        Messages.get("occupancy.vacancy"), "End A", "End B", "End C", "End D"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setBackground(WHITE);
        table.setForeground(TEXT);
        table.setRowHeight(30);
        table.setGridColor(new Color(235, 240, 247));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setBackground(SKY);
        table.getTableHeader().setForeground(BLUE_DARK);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD, 11f));
        int[] widths = {330, 80, 110, 110, 110, 85, 70, 70, 70, 70};
        for (int column = 0; column < widths.length; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }

        // One row per event, because occupancy is a question about a single night: how
        // full will this match or concert be. A stadium-level row would divide
        // seats sold across eight events by eight times the seat count, which
        // reads as a near-empty ground no matter how busy each night is.
        OccupancyReport report = occupancyReport();
        int[] byEnd = new int[4];
        for (OccupancyReport.Row row : report.getRows()) {
            for (int index = 0; index < byEnd.length; index++) {
                byEnd[index] += row.getSeatsInEnd(String.valueOf((char) ('A' + index)));
            }
            model.addRow(new Object[]{
                    row.getEvent().getHeadline(), row.getEvent().getType().getLabel(),
                    row.getEvent().getDateLabel(),
                    formatCapacity(row.getSeatsOnSale()), formatCapacity(row.getSeatsBooked()),
                    vacancy(row.getSeatsOnSale(), row.getSeatsBooked()),
                    row.getSeatsInEnd("A"), row.getSeatsInEnd("B"),
                    row.getSeatsInEnd("C"), row.getSeatsInEnd("D")});
        }
        // The total row compares like with like: seats sold across the whole season
        // against the seats on sale across the whole season.
        model.addRow(new Object[]{"All events", "", report.getEventCount() + " events",
                formatCapacity(report.getSeasonSeatsOnSale()),
                formatCapacity(report.getSeasonSeatsBooked()),
                vacancy(report.getSeasonSeatsOnSale(), report.getSeasonSeatsBooked()),
                byEnd[0], byEnd[1], byEnd[2], byEnd[3]});

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        tableCard.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);
        JButton export = createSecondaryButton(Messages.get("occupancy.export"));
        export.addActionListener(event -> exportOccupancyCsv());
        bottom.add(export);
        tableCard.add(bottom, BorderLayout.SOUTH);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    /** Share of a set of seats that is still free, as a percentage. */
    private static String vacancy(int onSale, int booked) {
        if (onSale <= 0) {
            return "0.00%";
        }
        return String.format(Locale.US, "%.2f%%", (onSale - booked) * 100.0 / onSale);
    }

    /** The occupancy being reported: this stadium, from the current bookings. */
    private OccupancyReport occupancyReport() {
        Stadium stadium = selectedStadium != null
                ? selectedStadium : StadiumData.getStadiums().get(0);
        return OccupancyReport.compute(bookingService, stadium);
    }

    /**
     * Writes the same per-event occupancy the screen shows, so the file and the
     * screen can never disagree about which night is nearly full.
     */
    private void exportOccupancyCsv() {
        StringBuilder sheet = new StringBuilder(
                "Event,Type,Date,SeatsOnSale,SeatsBooked,VacancyPercent,EndA,EndB,EndC,EndD\n");
        for (OccupancyReport.Row row : occupancyReport().getRows()) {
            sheet.append('"').append(row.getEvent().getHeadline()).append("\",")
                    .append('"').append(row.getEvent().getType().getLabel()).append("\",")
                    .append('"').append(row.getEvent().getDate()).append("\",")
                    .append(row.getSeatsOnSale()).append(',')
                    .append(row.getSeatsBooked()).append(',')
                    .append(String.format(Locale.US, "%.2f", row.getVacancyPercentage()))
                    .append(',')
                    .append(row.getSeatsInEnd("A")).append(',')
                    .append(row.getSeatsInEnd("B")).append(',')
                    .append(row.getSeatsInEnd("C")).append(',')
                    .append(row.getSeatsInEnd("D"))
                    .append('\n');
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("occupancy.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            java.nio.file.Files.writeString(chooser.getSelectedFile().toPath(), sheet.toString());
            showStatus(Messages.get("occupancy.savedTo") + " " + chooser.getSelectedFile().getName());
        } catch (java.io.IOException exception) {
            showWarning(Messages.get("occupancy.couldNotSave") + " " + exception.getMessage());
        }
    }

    // ---------------------------------------------------------------------
    // Booked seats ledger
    // ---------------------------------------------------------------------

    /**
     * Shows every seat already taken, per stadium and per event, so a customer
     * can see the occupancy of the venue they are booking on.
     */
    private void showSeatLedger() {
        Stadium target = selectedStadium != null ? selectedStadium : StadiumData.getStadium("namboole");
        StadiumEvent targetEvent = selectedEvent != null && target != null
                && target.getId().equals(selectedEvent.getStadiumId())
                ? selectedEvent : firstEventFor(target);
        ledgerReturnScreen = "booking".equals(currentScreen) ? "booking" : "directory";
        currentScreen = "seats";
        setHeader(text("nav.bookedSeats"), text("seats.subtitle"));
        contentHost.removeAll();
        contentHost.add(buildSeatLedgerContent(), BorderLayout.CENTER);
        contentHost.revalidate();
        contentHost.repaint();
        selectLedgerStadium(target);
        selectLedgerEvent(targetEvent);
        refreshSeatLedger();
        showStatus(Messages.get("bookings.field.bookedSeats"));
    }

    private StadiumEvent firstEventFor(Stadium stadium) {
        List<StadiumEvent> events = StadiumData.getEvents(stadium.getId());
        return events.isEmpty() ? StadiumData.getEvents().get(0) : events.get(0);
    }

    private JPanel buildSeatLedgerContent() {
        JPanel page = new JPanel(new BorderLayout(0, 14));
        page.setBackground(PAGE);
        page.setBorder(new EmptyBorder(20, 0, 22, 0));

        // BorderLayout has a single NORTH slot, so the title row and the summary
        // card are stacked in one wrapper instead of overlapping each other.
        JPanel north = new JPanel(new GridBagLayout());
        north.setOpaque(false);
        GridBagConstraints northConstraints = new GridBagConstraints();
        northConstraints.gridx = 0;
        northConstraints.gridy = 0;
        northConstraints.weightx = 1;
        northConstraints.fill = GridBagConstraints.HORIZONTAL;
        northConstraints.anchor = GridBagConstraints.NORTH;

        JPanel top = new JPanel(new BorderLayout(16, 0));
        top.setOpaque(false);

        JPanel titleBox = new JPanel(new GridBagLayout());
        titleBox.setOpaque(false);

        GridBagConstraints titleConstraints = new GridBagConstraints();
        titleConstraints.gridx = 0;
        titleConstraints.anchor = GridBagConstraints.WEST;
        titleConstraints.fill = GridBagConstraints.HORIZONTAL;
        titleConstraints.weightx = 1;
        JLabel title = new JLabel(text("seats.title"));
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        titleConstraints.gridy = 1;
        titleConstraints.insets = new Insets(7, 0, 0, 0);
        titleBox.add(title, titleConstraints);
        JLabel subtitle = new JLabel(Messages.get("occupancy.vacancyNote"));
        subtitle.setForeground(MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 11f));
        titleConstraints.gridy = 2;
        titleConstraints.insets = new Insets(3, 0, 0, 0);
        titleBox.add(subtitle, titleConstraints);
        top.add(titleBox, BorderLayout.WEST);

        JPanel pickers = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pickers.setOpaque(false);
        ledgerStadiumCombo = new JComboBox<>();
        for (Stadium stadium : StadiumData.getStadiums()) {
            ledgerStadiumCombo.addItem(stadium.getName());
        }
        ledgerStadiumCombo.setPreferredSize(new Dimension(280, 38));
        ledgerStadiumCombo.setBackground(WHITE);
        styleCombo(ledgerStadiumCombo);
        ledgerStadiumCombo.addActionListener(event -> {
            Stadium stadium = StadiumData.getStadium(ledgerStadiumCombo.getSelectedIndex() >= 0
                    ? StadiumData.getStadiums().get(ledgerStadiumCombo.getSelectedIndex()).getId() : null);
            if (stadium != null) {
                loadLedgerEvents(stadium);
            }
        });
        ledgerEventCombo = new JComboBox<>();
        ledgerEventCombo.setPreferredSize(new Dimension(340, 38));
        ledgerEventCombo.setBackground(WHITE);
        styleCombo(ledgerEventCombo);
        ledgerEventCombo.addActionListener(event -> refreshSeatLedger());
        pickers.add(labelled("Stadium", ledgerStadiumCombo));
        pickers.add(labelled("Event", ledgerEventCombo));
        top.add(pickers, BorderLayout.EAST);
        north.add(top, northConstraints);

        JPanel summaryCard = createCard();
        summaryCard.setLayout(new BorderLayout(0, 8));
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 16, 14, 16)));
        ledgerSummaryLabel = new JLabel("—");
        ledgerSummaryLabel.setForeground(TEXT);
        ledgerSummaryLabel.setFont(ledgerSummaryLabel.getFont().deriveFont(Font.BOLD, 14f));
        ledgerSectionLabel = new JLabel("—");
        ledgerSectionLabel.setForeground(MUTED);
        ledgerSectionLabel.setFont(ledgerSectionLabel.getFont().deriveFont(Font.PLAIN, 11f));
        summaryCard.add(ledgerSummaryLabel, BorderLayout.NORTH);
        summaryCard.add(ledgerSectionLabel, BorderLayout.CENTER);
        northConstraints.gridy = 1;
        northConstraints.insets = new Insets(14, 0, 0, 0);
        north.add(summaryCard, northConstraints);
        page.add(north, BorderLayout.NORTH);

        ledgerTableModel = new DefaultTableModel(
                new Object[]{"Seat", Messages.get("ledger.section"), "Row", Messages.get("ledger.number"), Messages.get("ledger.priceTier"), Messages.get("ledger.price"),
                        Messages.get("bookings.table.reference"), Messages.get("ledger.bookedBy"), Messages.get("ledger.bookedOn")}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ledgerTable = new JTable(ledgerTableModel);
        configureLedgerTable();
        JScrollPane scroll = new JScrollPane(ledgerTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(14, 14, 14, 14)));
        JLabel hint = new JLabel(Messages.get("seats.everyConfirmed"));
        hint.setForeground(MUTED);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        tableCard.add(hint, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);
        page.add(tableCard, BorderLayout.CENTER);
        return page;
    }

    private JPanel labelled(String text, JComponent field) {
        JPanel box = new JPanel(new BorderLayout(0, 3));
        box.setOpaque(false);
        JLabel caption = new JLabel(text);
        caption.setForeground(MUTED);
        caption.setFont(caption.getFont().deriveFont(Font.BOLD, 10f));
        box.add(caption, BorderLayout.NORTH);
        box.add(field, BorderLayout.CENTER);
        return box;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(combo.getFont().deriveFont(Font.PLAIN, 11f));
        combo.setFocusable(false);
    }

    private void configureLedgerTable() {
        ledgerTable.setBackground(WHITE);
        ledgerTable.setForeground(TEXT);
        ledgerTable.setRowHeight(32);
        ledgerTable.setShowVerticalLines(false);
        ledgerTable.setGridColor(new Color(235, 240, 247));
        ledgerTable.setIntercellSpacing(new Dimension(0, 1));
        ledgerTable.setSelectionBackground(new Color(219, 234, 254));
        ledgerTable.setSelectionForeground(TEXT);
        ledgerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ledgerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        ledgerTable.setFillsViewportHeight(true);
        JTableHeader header = ledgerTable.getTableHeader();
        header.setBackground(SKY);
        header.setForeground(BLUE_DARK);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 11f));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        int[] widths = {95, 135, 60, 80, 120, 120, 100, 150, 150};
        for (int column = 0; column < widths.length; column++) {
            ledgerTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }
    }

    private void selectLedgerStadium(Stadium stadium) {
        if (ledgerStadiumCombo == null || stadium == null) {
            return;
        }
        List<Stadium> stadiums = StadiumData.getStadiums();
        int index = stadiums.indexOf(stadium);
        if (index >= 0) {
            ledgerStadiumCombo.setSelectedIndex(index);
            loadLedgerEvents(stadium);
        }
    }

    private void loadLedgerEvents(Stadium stadium) {
        if (ledgerEventCombo == null) {
            return;
        }
        int previousIndex = ledgerEventCombo.getSelectedIndex();
        String previousId = previousIndex >= 0 && previousIndex < ledgerEventOptions.size()
                ? ledgerEventOptions.get(previousIndex).getId() : null;
        ledgerEventCombo.removeAllItems();
        ledgerEventOptions = new ArrayList<>();
        for (StadiumEvent event : StadiumData.getEvents(stadium.getId())) {
            ledgerEventOptions.add(event);
            ledgerEventCombo.addItem(ledgerEventLabel(event));
        }
        int restore = -1;
        for (int index = 0; index < ledgerEventOptions.size(); index++) {
            if (ledgerEventOptions.get(index).getId().equals(previousId)) {
                restore = index;
                break;
            }
        }
        if (restore >= 0) {
            ledgerEventCombo.setSelectedIndex(restore);
        } else if (ledgerEventCombo.getItemCount() > 0) {
            ledgerEventCombo.setSelectedIndex(0);
        }
    }

    private String ledgerEventLabel(StadiumEvent event) {
        return event.getHeadline() + "  •  " + event.getWhenLabel();
    }

    private void selectLedgerEvent(StadiumEvent event) {
        if (ledgerEventCombo == null || event == null) {
            return;
        }
        for (int index = 0; index < ledgerEventOptions.size(); index++) {
            if (event.getId().equals(ledgerEventOptions.get(index).getId())) {
                ledgerEventCombo.setSelectedIndex(index);
                return;
            }
        }
    }

    /** Rebuilds the ledger table and its summary for the chosen stadium and event. */
    private void refreshSeatLedger() {
        if (ledgerTableModel == null || ledgerStadiumCombo == null) {
            return;
        }
        int stadiumIndex = ledgerStadiumCombo.getSelectedIndex();
        if (stadiumIndex < 0) {
            return;
        }
        Stadium stadium = StadiumData.getStadiums().get(stadiumIndex);
        int eventIndex = ledgerEventCombo == null ? -1 : ledgerEventCombo.getSelectedIndex();
        StadiumEvent event = eventIndex >= 0 && eventIndex < ledgerEventOptions.size()
                ? ledgerEventOptions.get(eventIndex) : null;

        Map<SeatKey, Booking> owner = new LinkedHashMap<>();
        for (Booking booking : bookingService.getBookingsForEvent(event)) {
            for (SeatKey key : booking.getSeats()) {
                owner.putIfAbsent(key, booking);
            }
        }
        List<SeatKey> booked = new ArrayList<>(bookingService.getBookedSeatKeys(event));
        booked.sort(SeatKey::compareTo);

        int total = bookingService.getTotalSeatCount(event);
        ledgerTableModel.setRowCount(0);
        Map<String, Integer> perSection = new LinkedHashMap<>();
        for (SeatKey key : booked) {
            Booking booking = owner.get(key);
            Stadium venue = StadiumData.getStadium(stadium.getId());
            SeatSection section = venue == null ? null : venue.getSection(key.getSection());
            perSection.merge(key.getSection(), 1, Integer::sum);
            ledgerTableModel.addRow(new Object[]{
                    key.display(),
                    section == null ? key.getSection() : section.getLabel(),
                    key.getRow(),
                    key.getNumber(),
                    bookingService.getRowTierName(key.getRow(),
                            section == null ? 1 : section.getRows()),
                    currency(bookingService.getSeatPrice(event, key)),
                    booking == null ? "—" : booking.getReference(),
                    booking == null ? "—" : booking.getCustomerName(),
                    booking == null ? "—" : formatStamp(booking.getCreatedAt())
            });
        }

        double vacancy = bookingService.getVacancyPercentage(event);
        // Two decimals, so a nearly empty ground never reads as a flat "100.0%".
        String vacancyText = String.format(Locale.US, "%.2f", vacancy);
        if (vacancyText.endsWith("00")) {
            vacancyText = vacancyText.substring(0, vacancyText.length() - 3);
        }
        ledgerSummaryLabel.setText(booked.size() + (booked.size() == 1 ? " seat" : " seats")
                + " booked of " + formatCapacity(total) + "  •  "
                + vacancyText + "% vacant  •  "
                + (event == null ? "—" : event.getHeadline()));
        StringBuilder sections = new StringBuilder("By section:  ");
        if (stadium != null) {
            for (SeatSection section : stadium.getSections()) {
                if (sections.length() > "By section:  ".length()) {
                    sections.append("     ");
                }
                sections.append(section.getId()).append(" ")
                        .append(section.getLabel()).append("  ")
                        .append(perSection.getOrDefault(section.getId(), 0))
                        .append('/').append(formatCapacity(section.getSeatCount()));
            }
        }
        ledgerSectionLabel.setText(sections.toString());
    }

    private String formatStamp(java.time.Instant instant) {
        if (instant == null) {
            return "—";
        }
        return java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    // ---------------------------------------------------------------------
    // Shared UI helpers
    // ---------------------------------------------------------------------

    private JPanel buildSearchBar(JTextField field, String hint) {
        styleSearchField(field);
        describe(field, Messages.get("search.bookingTitleHint"), "Type to search. Use the arrow keys and Enter to choose a suggestion.");
        if (field instanceof SearchField) {
            ((SearchField) field).setHint(hint);
        }
        JPanel bar = new JPanel(new BorderLayout(7, 0));
        bar.setBackground(WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(4, 9, 4, 9)));
        JLabel icon = new JLabel("⌕");
        icon.setForeground(BLUE);
        icon.setFont(icon.getFont().deriveFont(Font.BOLD, 20f));
        icon.setPreferredSize(new Dimension(20, 28));
        field.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        bar.add(icon, BorderLayout.WEST);
        bar.add(field, BorderLayout.CENTER);
        return bar;
    }

    private void styleSearchField(JTextField field) {
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 12f));
        field.setForeground(TEXT);
        field.setBackground(WHITE);
        field.setCaretColor(BLUE);
        field.setPreferredSize(new Dimension(220, 30));
    }

    /**
     * The longer description for a contact field, given its label.
     *
     * <p>Matched on the field name rather than the visible label, because the
     * label changes with the language and a switch on it would stop matching —
     * and a switch on a translated string is not allowed anyway, since Java
     * needs a constant there.
     */
    private String describeField(String fieldName) {
        if ("nameField".equals(fieldName)) {
            return Messages.get("form.hintName");
        }
        if ("emailField".equals(fieldName)) {
            return Messages.get("form.hintEmail");
        }
        if ("phoneField".equals(fieldName)) {
            return Messages.get("form.hintPhone");
        }
        return null;
    }

    /** Gives a field a label a screen reader can announce. */
    private void describe(JComponent component, String name, String description) {
        component.getAccessibleContext().setAccessibleName(name);
        if (description != null) {
            component.getAccessibleContext().setAccessibleDescription(description);
        }
    }

    private void styleTextField(JTextField field) {
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 13f));
        field.setForeground(TEXT);
        field.setBackground(FIELD);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 10, 8, 10)));
        field.setPreferredSize(new Dimension(180, 37));
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        return card;
    }

    /**
     * Button delegate that paints a flat fill taken straight from
     * {@link AbstractButton#getBackground()}.
     *
     * <p>The stock look-and-feel delegate repaints the button with its own
     * "pressed" colour, which overrode the background the application set and made
     * the pressed state impossible to control. Painting the fill here guarantees
     * the hover and pressed colours are exactly the ones requested.
     */
    private static final class FlatButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics g, JComponent c) {
            if (!(c instanceof AbstractButton button)) {
                super.paint(g, c);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                boolean enabled = button.isEnabled();
                // Unfilled buttons (header navigation) keep the panel behind them.
                if (c.isOpaque()) {
                    g2.setColor(enabled ? button.getBackground()
                            : blend(button.getBackground(), PAGE, 0.62));
                    g2.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                String label = button.getText();
                if (label == null || label.isEmpty()) {
                    return;
                }
                Insets insets = c.getInsets();
                int availableWidth = c.getWidth() - insets.left - insets.right;
                int availableHeight = c.getHeight() - insets.top - insets.bottom;
                if (availableWidth <= 0 || availableHeight <= 0) {
                    return;
                }
                java.awt.FontMetrics metrics = g2.getFontMetrics(button.getFont());
                g2.setColor(enabled ? button.getForeground()
                        : blend(button.getForeground(), PAGE, 0.35));
                g2.drawString(label,
                        insets.left + (availableWidth - metrics.stringWidth(label)) / 2,
                        insets.top + (availableHeight + metrics.getAscent() - metrics.getDescent()) / 2);
            } finally {
                g2.dispose();
            }
        }
    }

    /** A {@link JButton} that keeps {@link FlatButtonUI} installed. */
    private static final class FeedbackButton extends JButton {
        FeedbackButton(String text) {
            super(text);
        }

        @Override
        public void updateUI() {
            if (!(getUI() instanceof FlatButtonUI)) {
                setUI(new FlatButtonUI());
            }
        }
    }

    /**
     * Blends {@code from} towards {@code to}. An {@code amount} of 0 keeps
     * {@code from} unchanged and 1 returns {@code to}.
     */
    private static Color blend(Color from, Color to, double amount) {
        double keep = 1.0 - amount;
        return new Color(
                (int) Math.round(from.getRed() * keep + to.getRed() * amount),
                (int) Math.round(from.getGreen() * keep + to.getGreen() * amount),
                (int) Math.round(from.getBlue() * keep + to.getBlue() * amount));
    }

    /**
     * Rebuilds a button border with a new outline colour while keeping the
     * original outline thickness and inner padding, so hovering never shifts
     * the layout.
     */
    private static Border recolourBorder(Border base, Color lineColor) {
        if (base instanceof CompoundBorder compound) {
            int thickness = compound.getOutsideBorder() instanceof LineBorder line
                    ? line.getThickness() : 1;
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(lineColor, thickness), compound.getInsideBorder());
        }
        if (base instanceof LineBorder line) {
            return BorderFactory.createLineBorder(lineColor, line.getThickness());
        }
        return base;
    }

    /**
     * Gives a button clear hover, pressed and released feedback so it is always
     * obvious which control the pointer is over and which one is being clicked.
     *
     * <p>Feedback is built from the button's own colours so it works for every
     * style. Unfilled buttons (such as the header navigation) cannot show a
     * background change, so their outline and text brighten instead. The current
     * button label is also echoed into the status bar and the tooltip.
     */
    private void addInteractionFeedback(JButton button) {
        Color baseBackground = button.getBackground();
        Color baseForeground = button.getForeground();
        Border baseBorder = button.getBorder();
        boolean filled = button.isContentAreaFilled();

        Color hoverBackground = filled ? blend(baseBackground, WHITE, 0.16) : baseBackground;
        Color hoverForeground = filled ? baseForeground : blend(baseForeground, WHITE, 0.55);
        Color hoverOutline = blend(baseForeground, WHITE, 0.25);
        Color pressBackground = filled ? blend(baseBackground, Color.BLACK, 0.22) : baseBackground;
        Color pressForeground = filled ? baseForeground : blend(baseForeground, WHITE, 0.85);
        Color pressOutline = filled ? blend(baseForeground, WHITE, 0.75) : WHITE;

        String label = button.getText();
        if (button.getToolTipText() == null) {
            button.setToolTipText(label == null || label.isBlank() ? null : label.trim());
        }

        Runnable rest = () -> {
            button.setBackground(baseBackground);
            button.setForeground(baseForeground);
            button.setBorder(baseBorder);
        };
        Runnable hover = () -> {
            button.setBackground(hoverBackground);
            button.setForeground(hoverForeground);
            button.setBorder(recolourBorder(baseBorder, hoverOutline));
        };
        Runnable press = () -> {
            button.setBackground(pressBackground);
            button.setForeground(pressForeground);
            button.setBorder(recolourBorder(baseBorder, pressOutline));
        };

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (!button.isEnabled()) {
                    return;
                }
                hover.run();
                if (label != null && !label.isBlank()) {
                    showStatus("Hover: " + label.trim());
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                rest.run();
            }

            @Override
            public void mousePressed(MouseEvent event) {
                if (!button.isEnabled()) {
                    return;
                }
                press.run();
                button.repaint();
                if (label != null && !label.isBlank()) {
                    showStatus("Clicking: " + label.trim());
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (!button.isEnabled()) {
                    return;
                }
                hover.run();
                button.repaint();
            }
        });
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new FeedbackButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11f));
        button.setBackground(BLUE);
        button.setForeground(Theme.current().onAccent());
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BLUE_DARK), new EmptyBorder(9, 14, 9, 14)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addInteractionFeedback(button);
        return button;
    }

    private JButton createSecondaryButton(String text) {
        return createSecondaryButton(text, TEXT);
    }

    private JButton createSecondaryButton(String text, Color foreground) {
        JButton button = new FeedbackButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11f));
        button.setForeground(foreground);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 202, 218)), new EmptyBorder(9, 13, 9, 13)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addInteractionFeedback(button);
        return button;
    }

    private JButton createOutlineButton(String text, Color accent) {
        JButton button = new FeedbackButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11f));
        button.setForeground(accent.darker());
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent), new EmptyBorder(8, 12, 8, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addInteractionFeedback(button);
        return button;
    }

    private JPanel buildEmptyState(String title, String message) {
        JPanel empty = createCard();
        empty.setLayout(new GridBagLayout());
        empty.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(28, 20, 28, 20)));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        JLabel heading = new JLabel(title);
        heading.setForeground(TEXT);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 15f));
        empty.add(heading, constraints);
        JLabel detail = new JLabel(message);
        detail.setForeground(MUTED);
        detail.setFont(detail.getFont().deriveFont(Font.PLAIN, 11f));
        constraints.gridy = 1;
        constraints.insets = new Insets(6, 0, 0, 0);
        empty.add(detail, constraints);
        return empty;
    }

    private void showStatus(String message) {
        statusValue.setText(message == null || message.trim().isEmpty() ? Messages.get("status.ready") : message);
    }

    private void showWarning(String message) {
        showDialogChoice(this, message, Messages.get("status.checkBooking"),
                JOptionPane.WARNING_MESSAGE, backLabel(), backLabel());
    }

    private String joinSeatNames(List<Seat> seats) {
        StringBuilder result = new StringBuilder();
        for (Seat seat : seats) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(seat.display());
        }
        return result.toString();
    }

    private String htmlText(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String currency(double amount) {
        return BookingService.formatMoney(amount);
    }

    private String formatCapacity(int capacity) {
        return NumberFormat.getIntegerInstance(Locale.US).format(capacity);
    }

    private Color colorFromHex(String value, Color fallback) {
        try {
            return Color.decode(value);
        } catch (Exception exception) {
            return fallback;
        }
    }

    private final class BookingTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            if (!isSelected) {
                String status = String.valueOf(table.getValueAt(row, 6));
                if ("CANCELLED".equals(status)) {
                    component.setForeground(CANCELLED);
                } else if (column == 6) {
                    component.setForeground(SUCCESS_DARK);
                } else if (column == 0) {
                    component.setForeground(BLUE_DARK);
                }
            }
            setBorder(new EmptyBorder(0, 8, 0, 8));
            return component;
        }
    }

    /** One row in a live suggestion list. */
    private static final class Suggestion {
        final String label;
        final String detail;
        final String value;

        Suggestion(String label, String detail, String value) {
            this.label = label;
            this.detail = detail == null ? "" : detail;
            this.value = value;
        }
    }

    /**
     * A live suggestion list attached to a search field. The list refreshes shortly
     * after every keystroke and can be driven with the arrow keys, Enter, Escape or
     * the mouse.
     *
     * <p>Choosing a row writes the suggestion back into the field, which fires the
     * field's document listener and so re-runs the existing search filter.
     */
    private final class SearchSuggestions {
        private static final int MAX_ROWS = 8;
        private final JTextField field;
        private final Function<String, List<Suggestion>> source;
        private final Timer debounce;
        private final JPopupMenu popup = new JPopupMenu();
        private final JList<Suggestion> list = new JList<>();

        SearchSuggestions(JTextField field, Function<String, List<Suggestion>> source) {
            this.field = field;
            this.source = source;
            this.debounce = new Timer(110, event -> refresh());
            this.debounce.setRepeats(false);

            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setCellRenderer(new SuggestionRenderer());
            list.setBackground(WHITE);
            list.setFixedCellHeight(44);
            list.setBorder(new EmptyBorder(4, 0, 4, 0));
            list.setVisibleRowCount(MAX_ROWS);

            // A non-focusable popup never steals focus, so the field keeps the caret
            // and the arrow keys keep working while the list is open.
            popup.setFocusable(false);
            popup.setBackground(WHITE);
            popup.setBorder(BorderFactory.createLineBorder(BORDER));
            popup.add(list);
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    int index = list.locationToIndex(event.getPoint());
                    if (index >= 0) {
                        list.setSelectedIndex(index);
                        accept(index);
                    }
                }
            });

            field.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent event) {
                    schedule();
                }

                @Override
                public void removeUpdate(DocumentEvent event) {
                    schedule();
                }

                @Override
                public void changedUpdate(DocumentEvent event) {
                    schedule();
                }
            });
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    handleKey(event);
                }
            });
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent event) {
                    schedule();
                }

                @Override
                public void focusLost(FocusEvent event) {
                    hideLater();
                }
            });
        }

        private void schedule() {
            debounce.restart();
        }

        private void hideLater() {
            // Let a click on the popup finish before dismissing it.
            Timer closer = new Timer(120, event -> popup.setVisible(false));
            closer.setRepeats(false);
            closer.start();
        }

        private void refresh() {
            List<Suggestion> suggestions = source.apply(field.getText());
            if (suggestions.isEmpty()) {
                suggestions = List.of(new Suggestion(Messages.get("search.noMatches"),
                        Messages.get("search.tryDifferentWord"), null));
            }
            if (suggestions.size() > MAX_ROWS) {
                suggestions = new ArrayList<>(suggestions.subList(0, MAX_ROWS));
            }
            DefaultListModel<Suggestion> model = new DefaultListModel<>();
            for (Suggestion suggestion : suggestions) {
                model.addElement(suggestion);
            }
            list.setModel(model);
            list.setSelectedIndex(0);
            if (!field.isFocusOwner()) {
                return;
            }
            popup.setPopupSize(Math.max(field.getWidth(), 340),
                    Math.min(MAX_ROWS, suggestions.size()) * 46 + 8);
            popup.show(field, 0, field.getHeight() + 2);
        }

        private void handleKey(KeyEvent event) {
            if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (popup.isVisible()) {
                    popup.setVisible(false);
                    event.consume();
                }
                return;
            }
            if (event.getKeyCode() == KeyEvent.VK_DOWN && popup.isVisible()) {
                if (list.getSelectedIndex() < list.getModel().getSize() - 1) {
                    list.setSelectedIndex(list.getSelectedIndex() + 1);
                }
                event.consume();
                return;
            }
            if (event.getKeyCode() == KeyEvent.VK_UP && popup.isVisible()) {
                if (list.getSelectedIndex() > 0) {
                    list.setSelectedIndex(list.getSelectedIndex() - 1);
                }
                event.consume();
                return;
            }
            if (event.getKeyCode() == KeyEvent.VK_ENTER && popup.isVisible()) {
                accept(list.getSelectedIndex());
                event.consume();
            }
        }

        private void accept(int index) {
            popup.setVisible(false);
            if (index < 0 || index >= list.getModel().getSize()) {
                return;
            }
            Suggestion suggestion = list.getModel().getElementAt(index);
            if (suggestion.value == null) {
                return;
            }
            field.setText(suggestion.value);
            field.setCaretPosition(field.getText().length());
            field.requestFocusInWindow();
        }
    }

    private final class SuggestionRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> source, Object value, int index,
                                                      boolean selected, boolean focused) {
            if (!(value instanceof Suggestion suggestion)) {
                return super.getListCellRendererComponent(source, value, index, selected, focused);
            }
            JPanel row = new JPanel(new BorderLayout(0, 1));
            row.setOpaque(true);
            row.setBackground(suggestion.value == null ? WHITE
                    : selected ? new Color(219, 234, 254) : WHITE);
            JLabel label = new JLabel(suggestion.label);
            label.setForeground(suggestion.value == null ? MUTED : TEXT);
            label.setFont(label.getFont().deriveFont(
                    suggestion.value == null ? Font.PLAIN : Font.BOLD, 11f));
            JLabel detail = new JLabel(suggestion.detail);
            detail.setForeground(suggestion.value == null ? new Color(160, 172, 188)
                    : selected ? BLUE_DARK : MUTED);
            detail.setFont(detail.getFont().deriveFont(Font.PLAIN, 10f));
            row.add(label, BorderLayout.NORTH);
            row.add(detail, BorderLayout.CENTER);
            row.setBorder(new EmptyBorder(6, 12, 6, 12));
            return row;
        }
    }

    private List<Suggestion> stadiumSuggestions(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ENGLISH);
        List<Suggestion> suggestions = new ArrayList<>();
        int venues = 0;
        for (Stadium stadium : StadiumData.getStadiums()) {
            List<StadiumEvent> events = StadiumData.getEvents(stadium.getId());

            // A venue matches if the venue itself matches, or if any of its
            // events match. Searching "Cranes" should find Namboole even though
            // "Cranes" is in the event names and not in the venue name.
            boolean eventMatch = false;
            for (StadiumEvent event : events) {
                if (event.searchableText().contains(q)) {
                    eventMatch = true;
                    break;
                }
            }

            if (!q.isEmpty() && !stadium.searchableText().contains(q) && !eventMatch) {
                continue;
            }
            venues++;
            StadiumEvent next = events.isEmpty() ? null : events.get(0);
            suggestions.add(new Suggestion(stadium.getName(),
                    stadium.getLocation() + "  •  " + formatCapacity(stadium.getCapacity()) + " seats"
                            + (next == null ? "" : "  •  next " + next.getDate().format(DATE_FORMATTER)),
                    stadium.getName()));
        }
        if (q.isEmpty()) {
            return suggestions;
        }
        // Teams and artists hosted at the venue are useful shortcuts too.
        for (String name : distinctTeamsAndArtists(q)) {
            suggestions.add(new Suggestion(name, Messages.get("schedules.teamOrArtistHint"), name));
        }
        if (suggestions.isEmpty()) {
            return suggestions;
        }
        suggestions.add(new Suggestion(venues + (venues == 1 ? " venue matches" : " venues match"),
                Messages.get("schedules.enterForList"), null));
        return suggestions;
    }

    private List<Suggestion> eventSuggestions(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ENGLISH);
        List<Suggestion> suggestions = new ArrayList<>();
        Stadium stadium = selectedStadium;
        List<StadiumEvent> events = stadium == null
                ? StadiumData.getEvents() : StadiumData.getEvents(stadium.getId());
        int matches = 0;
        for (StadiumEvent event : events) {
            if (!q.isEmpty() && !event.searchableText().contains(q)
                    && !venueMatches(event, q)) {
                continue;
            }
            matches++;
            Stadium home = StadiumData.getStadium(event.getStadiumId());
            suggestions.add(new Suggestion(event.getHeadline(),
                    (home == null ? "" : home.getName() + "  •  ")
                            + event.getDate().format(DATE_FORMATTER) + "  •  "
                            + event.getStartTime().format(TIME_FORMATTER),
                    event.getHeadline()));
        }
        if (!q.isEmpty()) {
            for (String name : distinctTeamsAndArtists(q)) {
                suggestions.add(new Suggestion(name, Messages.get("schedules.teamOrArtist"), name));
            }
        }
        if (suggestions.isEmpty()) {
            return suggestions;
        }
        suggestions.add(new Suggestion(matches + (matches == 1 ? " event matches" : " events match"),
                Messages.get("schedules.enterForList"), null));
        return suggestions;
    }

    private List<Suggestion> bookingSuggestions(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ENGLISH);
        List<Suggestion> suggestions = new ArrayList<>();
        int matches = 0;
        for (Booking booking : bookingService.getBookings()) {
            if (!q.isEmpty() && !bookingSearchText(booking).contains(q)) {
                continue;
            }
            matches++;
            Stadium stadium = StadiumData.getStadium(booking.getStadiumId());
            suggestions.add(new Suggestion(booking.getReference() + "  •  " + booking.getEvent(),
                    (stadium == null ? "" : stadium.getName() + "  •  ")
                            + booking.getSeatDisplay() + "  •  " + booking.getStatus().name(),
                    booking.getReference()));
        }
        if (suggestions.isEmpty()) {
            return suggestions;
        }
        suggestions.add(new Suggestion(matches + (matches == 1 ? " booking matches" : " bookings match"),
                Messages.get("schedules.enterForList"), null));
        return suggestions;
    }

    /** Distinct team and artist names in the dataset that contain the query. */
    private List<String> distinctTeamsAndArtists(String query) {
        List<String> names = new ArrayList<>();
        for (StadiumEvent event : StadiumData.getEvents()) {
            for (String name : List.of(
                    event.getTeamOne() == null ? "" : event.getTeamOne(),
                    event.getTeamTwo() == null ? "" : event.getTeamTwo(),
                    event.getArtist() == null ? "" : event.getArtist())) {
                if (name.isEmpty() || names.contains(name)) {
                    continue;
                }
                if (name.toLowerCase(Locale.ENGLISH).contains(query)) {
                    names.add(name);
                }
            }
        }
        return names;
    }

    private static final class SearchField extends JTextField {
        private String hint = "";

        SearchField(String hint) {
            this.hint = hint;
            setOpaque(true);
        }

        void setHint(String hint) {
            this.hint = hint == null ? "" : hint;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (!getText().isEmpty() || hint.isEmpty()) {
                return;
            }
            Graphics2D copy = (Graphics2D) graphics.create();
            copy.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            copy.setColor(new Color(148, 163, 184));
            Insets insets = getInsets();
            copy.drawString(hint, insets.left + 2, insets.top + getFontMetrics(getFont()).getAscent());
            copy.dispose();
        }
    }

    private void applySystemDefaults() {
        Theme.Palette palette = Theme.current();
        javax.swing.UIManager.put("Panel.background", palette.card());
        javax.swing.UIManager.put("OptionPane.background", palette.card());
        javax.swing.UIManager.put("OptionPane.messageForeground", palette.text());
        javax.swing.UIManager.put("Label.foreground", palette.text());
        javax.swing.UIManager.put("TextField.background", palette.field());
        javax.swing.UIManager.put("TextField.foreground", palette.text());
        javax.swing.UIManager.put("TextField.caretForeground", palette.text());
        javax.swing.UIManager.put("TextField.border",
                javax.swing.BorderFactory.createLineBorder(palette.fieldBorder()));
                javax.swing.UIManager.put("ComboBox.background", palette.field());
        javax.swing.UIManager.put("ComboBox.foreground", palette.text());
        javax.swing.UIManager.put("ComboBox.selectionBackground", palette.accent());
        javax.swing.UIManager.put("ComboBox.selectionForeground", palette.onAccent());
        javax.swing.UIManager.put("Table.background", palette.card());
        javax.swing.UIManager.put("Table.foreground", palette.text());
        javax.swing.UIManager.put("Table.gridColor", palette.tableGrid());
        javax.swing.UIManager.put("TableHeader.background", palette.tableHeader());
        javax.swing.UIManager.put("TableHeader.foreground", palette.accentDark());
        javax.swing.UIManager.put("Table.selectionBackground", palette.accent());
        javax.swing.UIManager.put("Table.selectionForeground", palette.onAccent());
        javax.swing.UIManager.put("ScrollBar.background", palette.page());
        javax.swing.UIManager.put("Viewport.background", palette.page());
        javax.swing.UIManager.put("TabbedPane.background", palette.page());
        javax.swing.UIManager.put("TabbedPane.foreground", palette.text());
        javax.swing.UIManager.put("TabbedPane.selected", palette.card());
        javax.swing.UIManager.put("ToolTip.background", palette.header());
        javax.swing.UIManager.put("ToolTip.foreground", Color.WHITE);
    }

    /**
     * Builds whichever screen is on show again, so the new palette takes effect
     * everywhere and not only on the screen built after the switch.
     */
    private void rebuildCurrentScreen() {
        if (seatMapPanel != null) {
            seatMapPanel.applyTheme();
        }
        StadiumPhotoPanel.forgetCachedPhotographs();
        contentHost.removeAll();
        switch (currentScreen == null ? "directory" : currentScreen) {
            case "booking":
                if (selectedEvent != null && selectedStadium != null) {
                    bookingService.selectEvent(selectedEvent);
                    setHeader(Messages.get("booking.title"),
                            selectedStadium.getName() + "  •  " + selectedEvent.getHeadline());
                    contentHost.add(buildBookingScreen(), BorderLayout.CENTER);
                }
                break;
            case "stadium":
                if (selectedStadium != null) {
                    setHeader(selectedStadium.getName(),
                            selectedStadium.getLocation() + "  •  " + selectedStadium.getVenueType());
                    contentHost.add(buildStadiumDashboard(selectedStadium), BorderLayout.CENTER);
                }
                break;
            case "stadium-details":
                if (selectedStadium != null) {
                    contentHost.add(buildStadiumDetailsContent(selectedStadium,
                                    StadiumDetails.of(selectedStadium, bookingService)),
                            BorderLayout.CENTER);
                }
                break;
            case "event-details":
                if (selectedEvent != null) {
                    // The seat picker is a single live component, so it has to
                    // be re-attached to whichever screen is being built. Without
                    // this it would still belong to the previous screen's tree
                    // and would silently fail to appear after a theme change.
                    if (seatMapPanel.getParent() != null) {
                        seatMapPanel.getParent().remove(seatMapPanel);
                    }
                    bookingService.selectEvent(selectedEvent);
                    seatMapPanel.refreshStatuses();
                    contentHost.add(buildEventDetailsContent(selectedEvent), BorderLayout.CENTER);
                }
                break;
            case "bookings":
                contentHost.add(buildBookingsContent(), BorderLayout.CENTER);
                break;
            case "schedules":
                contentHost.add(buildLiveSchedulesContent(), BorderLayout.CENTER);
                break;
            case "occupancy":
                contentHost.add(buildOccupancyContent(), BorderLayout.CENTER);
                break;
            case "seat-ledger":
                contentHost.add(buildSeatLedgerContent(), BorderLayout.CENTER);
                break;
            case "saved-seats":
                contentHost.add(buildSavedSeatsContent(), BorderLayout.CENTER);
                break;
            default:
                contentHost.add(buildStadiumDashboard(StadiumData.getStadiums().get(0)),
                        BorderLayout.CENTER);
                break;
        }
        contentHost.revalidate();
        contentHost.repaint();
        if (seatMapPanel != null) {
            seatMapPanel.refreshStatuses();
        }
    }

    /** The label on the dark mode button, which also reads as its current state. */
    private void updateDarkModeButton() {
        if (darkModeButton == null) {
            return;
        }
        boolean dark = Theme.isDark();
        darkModeButton.setText(dark ? "\u263D  Dark" : "\u263E  Light");
        darkModeButton.setToolTipText(dark
                ? "Switch to the light theme"
                : "Switch to the dark theme");
        darkModeButton.getAccessibleContext().setAccessibleDescription(
                dark ? "Currently the dark theme. Activate for the light theme."
                        : "Currently the light theme. Activate for the dark theme.");
    }

    /**
     * Switches the whole application between the light and dark themes.
     *
     * <p>Every screen is built from the palette, so the one on show is simply built
     * again in the new colours rather than each screen being taught to repaint
     * itself. The choice is remembered for next time.
     */
    private void toggleDarkMode() {
        boolean dark = !Theme.isDark();
        Theme.setDark(dark);
        applyThemeColours();
        ThemePreference.save();
        applySystemDefaults();
        updateDarkModeButton();
        rethemeChrome();
        rebuildCurrentScreen();
        showStatus(dark ? "Dark mode on" : "Light mode on");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The default Swing look and feel is a suitable fallback.
            }
            StadiumBookingApp app = new StadiumBookingApp();
            app.setVisible(true);
        });
    }
}
