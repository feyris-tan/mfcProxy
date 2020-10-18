package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.model.subtypes.Price;
import org.jboss.logging.Logger;

import java.util.Currency;

public class StringUtils
{
    private StringUtils() {}

    public static Integer findFirstInteger(String...args)
    {
        for (int i = 0; i < args.length; i++)
        {
            if (isInteger(args[i]))
            {
                return Integer.parseInt(args[i]);
            }
        }
        return null;
    }

    public static boolean isInteger(String str)
    {
        if (str.equals(""))
            return false;
        if (str == null)
            return false;

        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            if (!Character.isDigit(chars[i]))
                return false;
        }
        return true;
    }

    public static String cutoffFileExtension(String str)
    {
        if (!str.contains("."))
            return str;

        if (str.charAt(0) == '.')
            return "";

        while (str.contains("."))
        {
            str = str.substring(0,str.length() - 1);
        }
        return str;
    }

    private static Logger logger;
    private static void prepareLogger()
    {
        if (logger == null) {
            logger = Logger.getLogger(StringUtils.class);
        }
    }

    public static Price parsePrice(String str)
    {
        prepareLogger();

        if (str.contains(","))
            str = str.replace(",","");

        Price result = new Price();

        if (str.startsWith("¥")) {
            result.currency = "JPY";
            str = str.substring(1);
        }
        else
        {
            logger.warnf("Failed to determine currency from: " + str);
            return null;
        }

        result.units = Double.parseDouble(str);
        return result;
    }

    public static int findFirstIntegerOrZero(String...args)
    {
        Integer firstInteger = findFirstInteger(args);
        return firstInteger != null ? firstInteger : 0;
    }
}
