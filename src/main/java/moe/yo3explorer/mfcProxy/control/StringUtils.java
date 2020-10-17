package moe.yo3explorer.mfcProxy.control;

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
}
