import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * Helper object for generating build timestamps in the same format as Maven.
 * Used by lockss-java-conventions.gradle.kts to generate build.properties files.
 */
object BuildTimestamp {
    /**
     * Formats a timestamp in the same format as Maven's antrun buildInfo step:
     * dd-MMM-yy' 'HH:mm:ss' 'zzz (e.g., "04-Dec-24 10:30:45 PST")
     */
    fun format(millis: Long): String {
        val dateFormat = SimpleDateFormat("dd-MMM-yy HH:mm:ss zzz")
        return dateFormat.format(Date(millis))
    }

    /**
     * Returns a formatted timestamp for the current time.
     */
    fun now(): String = format(System.currentTimeMillis())

    /**
     * Returns the current time in milliseconds.
     */
    fun nowMillis(): Long = System.currentTimeMillis()
}
