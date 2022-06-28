package mgmsports.common.util;

import mgmsports.model.TimeInterval;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static mgmsports.common.util.TimeUtil.getClientCurrentLocalTime;

public class TimeIntervalUtil {

    /**
     * Get date by Timeinterval
     *
     * @param timeInterval time interval
     * @return date
     */
    public static Date getClientCurrentLocalDateFromDate(TimeInterval timeInterval, int timeZoneOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getClientCurrentLocalTime(timeZoneOffset));
        switch (timeInterval) {
            case Last7Days:
                calendar.add(Calendar.DATE, -7);
                break;
            case Last30Days:
                calendar.add(Calendar.DATE, -30);
                break;
            case CurrentYear:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                calendar.set(Calendar.MONTH, 0);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                break;
            case ThisWeek:
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                break;
            default:
                calendar.set(Calendar.YEAR, 2000);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                break;
        }

        return calendar.getTime();
    }

}
