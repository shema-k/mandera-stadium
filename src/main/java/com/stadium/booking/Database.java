package com.stadium.booking;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Shared access to the embedded H2 file that holds the bookings.
 *
 * <p>There is no password and no account. The system is open to the people who
 * book, so the file is opened straight away and there is nothing to unlock. The
 * connection plumbing lives here so {@link BookingStore} does not have to know
 * how the file is addressed.
 */
public final class Database implements AutoCloseable {
    /**
     * Suffix the old build appended to record that a password was required.
     * Nothing writes it now; see {@link #legacyMarkerFor(Path)}.
     */
    public static final String LEGACY_ENCRYPTION_MARKER_SUFFIX = ".encrypted";

    private final Path file;
    private final String jdbcUrl;

    public Database(Path file) {
        this.file = file;
        this.jdbcUrl = buildUrl(file);
    }

    private static String buildUrl(Path databaseFile) {
        if (databaseFile == null) {
            return null;
        }
        String path = databaseFile.toAbsolutePath().toString();
        // H2 derives its own storage names from the URL, so the legacy .dat
        // extension is dropped rather than ending up in the file name.
        if (path.endsWith(".dat")) {
            path = path.substring(0, path.length() - 4);
        }
        return "jdbc:h2:file:" + path.replace('\\', '/') + ";DB_CLOSE_ON_EXIT=FALSE";
    }

    public Path getFile() {
        return file;
    }

    /**
     * Always false: this build has no password to protect the file with.
     *
     * <p>Kept so the "is anything locked?" question has an honest answer. It
     * matters because the database password was the one thing that made this
     * file feel guarded, and removing staff accounts also removed it.
     */
    public boolean isProtected() {
        return false;
    }

    /**
     * Whether this file would need a password before it could be opened.
     *
     * <p>Always false. Kept at the call site rather than hard-coded, so there is
     * one place to change if a later version does protect the file.
     */
    public static boolean requiresPassword(Path databaseFile) {
        return false;
    }

    /**
     * Where the old password marker for a file would sit.
     *
     * <p>A build with staff accounts could protect the booking file with a
     * password and recorded that in a sidecar file. Nothing reads or writes that
     * marker now, but a file from such a build may still carry one beside it, and
     * this names the path so it can be recognised and removed.
     */
    public static Path legacyMarkerFor(Path databaseFile) {
        return databaseFile.resolveSibling(
                databaseFile.getFileName() + LEGACY_ENCRYPTION_MARKER_SUFFIX);
    }

    public Connection open() throws IOException, SQLException {
        if (jdbcUrl == null) {
            throw new IOException("No booking database path configured");
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        return DriverManager.getConnection(jdbcUrl, "sa", "");
    }

    /** Nothing is held open beyond the pool, so closing does nothing. */
    @Override
    public void close() {
        // The driver closes its connections when the JVM exits.
    }
}
