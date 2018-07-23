package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2018/7/17.
 */

public class TimeUtils {

    public static String geDateTime(long time) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(time);
    }


    public static Date getCurrentDateTime() throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(geDateTime(System.currentTimeMillis()));
    }

}
