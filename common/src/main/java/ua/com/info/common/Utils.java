package ua.com.info.common;

import android.util.Log;

import java.util.Date;
import java.text.DecimalFormat;

/**
 * Створено v.m 02.08.2017.
 */

public class Utils {
    public static int VersionCode;
    public static Class menuActivityClass;
    public final static String RowSeparator = "¤";
    public final static String FieldsSeparator = "#";
    public final static String ColumnsSeparator = ";";

    private static final int[] operatorCodes = {50, 63, 66, 67, 68, 73, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99};

    public static String rSum(double sum) {
        if (sum == 0) return "";
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(sum);
    }

    public static String Quantity(String pattern, double quantity) {
        if (quantity == 0) return "";
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(quantity);
    }

    public static double toDouble(Object value) {
        if (value == null)
            return 0;
        else if (value instanceof Double)
            return (Double) value;
        else if ((value instanceof String) && (String.valueOf(value).equals("")))
            return 0;
        else
            return Double.valueOf(String.valueOf(value).replace(",", "."));
    }

    public static String toString(Object o) {
        if (o == null)
            return "";
        else if (o instanceof String)
            return (String) o;
        else
            return String.valueOf(o);
    }

    public static CharSequence formatDate(Date d) {
        return android.text.format.DateFormat.format("dd.MM.yy", d);
    }

    public static String numEnding(int number, String _one, String _four, String _nineteen) {
        int m = number % 100;
        int v = 0;
        if (m > 20) {
            v = m % 10;
        }
        String s;
        if (m == 0) s = _nineteen;
        else if (m == 1) s = _one;
        else if (m < 5) s = _four;
        else if (m < 20) s = _nineteen;
        else if (v == 1) s = _one;
        else if (v < 5) s = _four;
        else s = _nineteen;

        return s;
    }

    private static String telToString(long number) {
        String s = "";
        DecimalFormat format = new DecimalFormat("(000) 000-0000");
        if (number > 0) {
            s = format.format(number);
        }
        return s;
    }

    public static Integer telToNumber(String tel) {
        if (tel == null) return null;
        String s = tel.replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
        Integer number;
        try {
            number = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        if (s.length() == 10 && telNumberAllowed(s)) {
            return number;
        }
        return null;
    }

    private static boolean telNumberAllowed(String tel) {
        int code = Integer.parseInt(tel.substring(1, 2));
        for (int c : operatorCodes) {
            if (code == c) {
                return true;
            }
        }
        return false;
    }
}
