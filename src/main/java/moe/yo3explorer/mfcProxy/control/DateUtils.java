package moe.yo3explorer.mfcProxy.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private DateUtils() {}

    private static SimpleDateFormat mmddyyyyhhmmss;
    public static long mmddyyyyhhmmssToUnixtime(String theString)
    {
        if (mmddyyyyhhmmss == null)
        {
            //  09/06/2020, 01:52:26
            mmddyyyyhhmmss = new SimpleDateFormat("MM/dd/yyyy, HH:mm:ss");
        }
        try {
            Date parse = mmddyyyyhhmmss.parse(theString);
            return parse.getTime() / 1000;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static SimpleDateFormat mmddyyyy;
    public static long mmddyyyyToUnixtime(String theString)
    {
        if (mmddyyyy == null)
        {
            //  09/06/2020, 01:52:26
            mmddyyyy = new SimpleDateFormat("MM/dd/yyyy");
        }
        try {
            Date parse = mmddyyyy.parse(theString);
            return parse.getTime() / 1000;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
