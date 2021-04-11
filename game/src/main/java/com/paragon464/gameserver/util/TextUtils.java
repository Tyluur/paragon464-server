package com.paragon464.gameserver.util;

import com.paragon464.gameserver.model.item.Item;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;

/**
 * Text utility class.
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 */
public class TextUtils {

    /**
     * An array of valid characters in a long username.
     */
    public static final char[] VALID_CHARS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',',
        '"', '[', ']', '|', '?', '/', '`'};
    /**
     * Packed text translate table.
     */
    public static final char[] XLATE_TABLE = {' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm',
        'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=',
        '\243', '$', '%', '"', '[', ']'};
    private static Random random = new Random();

    public static String implode(String glue, String[] array) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(glue);
            }
        }
        return builder.toString();
    }

    public static String implode(String glue, boolean[] array) {
        StringBuilder builder = new StringBuilder();
        if (array == null) {
            return "";
        }
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(glue);
            }
        }
        return builder.toString();
    }

    public static String implode(String glue, int[] array) {
        StringBuilder builder = new StringBuilder();
        if (array == null) {
            return "";
        }
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i < array.length - 1) {
                builder.append(glue);
            }
        }
        return builder.toString();
    }

    public static String implode(List<?> array, String glue) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            builder.append(array.get(i));
            if (i < array.size() - 1) {
                builder.append(glue);
            }
        }
        return builder.toString();
    }

    public static String getItemLogDisplay(Item[] items) {
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < 28; i++) {
            if (items[i] != null && items[i].getId() != -1) {
                list.append(items[i].getDefinition().getName());
                if (items[i].getAmount() > 1) {
                    list.append(" x ").append(items[i].getAmount()).append(", ");
                } else {
                    list.append(", ");
                }
            }
        }
        if (list.length() < 1) {
            list = new StringBuilder("nothing");
        }
        return list.toString();
    }

    /**
     * Checks if a name is valid.
     *
     * @param s The name.
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public static boolean isValidName(String s) {
        return formatNameForProtocol(s).matches("[a-z0-9_]+");
    }

    /**
     * Formats a name for use in the protocol.
     *
     * @param s The name.
     * @return The formatted name.
     */
    public static String formatNameForProtocol(String s) {
        return s.toLowerCase().replace(" ", "_");
    }

    /**
     * Converts a name to a long.
     *
     * @param s The name.
     * @return The long.
     */
    public static long stringToLong(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        while (l % 37L == 0L && l != 0L)
            l /= 37L;
        return l;
    }

    /**
     * Converts a long to a name.
     *
     * @param l The long.
     * @return The name.
     */
    public static String longToName(long l) {
        int i = 0;
        char[] ac = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    /**
     * Formats a name for display.
     *
     * @param s The name.
     * @return The formatted name.
     */
    public static String formatName(String s) {
        return fixName(s.replace(" ", "_"));
    }

    /**
     * Method that fixes capitalization in a name.
     *
     * @param s The name.
     * @return The formatted name.
     */
    public static String fixName(final String s) {
        if (s.length() > 0) {
            final char[] ac = s.toCharArray();
            for (int j = 0; j < ac.length; j++)
                if (ac[j] == '_') {
                    ac[j] = ' ';
                    if ((j + 1 < ac.length) && (ac[j + 1] >= 'a') && (ac[j + 1] <= 'z')) {
                        ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
                    }
                }

            if ((ac[0] >= 'a') && (ac[0] <= 'z')) {
                ac[0] = (char) ((ac[0] + 65) - 97);
            }
            return new String(ac);
        } else {
            return s;
        }
    }

    public static String fixChatMessage(String message) {
        StringBuilder newText = new StringBuilder();
        boolean wasSpace = true;
        boolean exception = false;
        for (int i = 0; i < message.length(); i++) {
            if (!exception) {
                if (wasSpace) {
                    newText.append(("" + message.charAt(i)).toUpperCase());
                    if (!String.valueOf(message.charAt(i)).equals(" "))
                        wasSpace = false;
                } else {
                    newText.append(("" + message.charAt(i)).toLowerCase());
                }
            } else {
                newText.append(message.charAt(i));
            }
            if (String.valueOf(message.charAt(i)).contains(":"))
                exception = true;
            else if (String.valueOf(message.charAt(i)).contains(".") || String.valueOf(message.charAt(i)).contains("!")
                || String.valueOf(message.charAt(i)).contains("?"))
                wasSpace = true;
        }
        return newText.toString();
    }

    /**
     * Escapes a string. Useful for user supplied text that gets pushed to others.
     * <p>
     * <p>Example: {@code escapeText("<img=1>")} would output {@code "<lt>img=1<gt>"}.</p>
     *
     * @param text The text to escape.
     * @return The escaped text.
     */
    public static String escapeText(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);

            if (character == '>') {
                builder.append("<gt>");
            } else if (character == '<') {
                builder.append("<lt>");
            } else {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    public static String getRandomString(int chars) {
        return new BigInteger(130, random).toString(chars);
    }
}
