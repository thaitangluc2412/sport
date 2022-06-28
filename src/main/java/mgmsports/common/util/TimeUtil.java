package mgmsports.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class TimeUtil {

    /**
     * Get client current time
     *
     * @param dateTimeOffset date time offset
     * @return Client current time
     */
    public static Date getClientCurrentLocalTime(int dateTimeOffset) {
        int offset = - dateTimeOffset - getServerTimeZoneOffset();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, offset);

        return calendar.getTime();
    }

    /**
     * Convert Server time to Client Time
     *
     * @param serverTime server time
     * @param clientDateTimeOffset client time zone offset
     * @return time of client
     */
    public static Date serverLocalTimeToClientLocalTime(Date serverTime, int clientDateTimeOffset) {
        int offset = - clientDateTimeOffset - getServerTimeZoneOffset();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(serverTime);

        calendar.add(Calendar.MINUTE, offset);

        return calendar.getTime();
    }

    /**
     * Get server time zone offset
     *
     * @return server time zone offset in minute
     */
    private static int getServerTimeZoneOffset(){
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = GregorianCalendar.getInstance(timeZone);

        return timeZone.getOffset(calendar.getTimeInMillis()) / (1000 * 60);
    }

    /**
     * Turn date into String in format("dd/MM/yyy")
     *
     * @param date date
     * @return date in String
     */
    public static String dateToString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * Turn string in format dd/MM/yyyy HH:mm:ss
     *
     * @param strDate string in format dd/MM/yyyy HH:mm:ss
     * @return date
     * @throws ParseException if string is in incorrect format
     */
    public static Date stringToDate(String strDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(strDate);
    }
}
