package mgmsports.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeIntervals {

    public static Date get7DaysFromToday() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyy");
        Date date, today = null;
        try {
            today = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7);
            date = simpleDateFormat.parse(simpleDateFormat.format(cal.getTime()));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
