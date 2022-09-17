package de.carstendroesser.obdtripmate.utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by carstendrosser on 05.06.17.
 */

public class FormatUtils {

    /**
     * Converts a given hex-string to a decimal.
     *
     * @param pHex the hex to convert to decimal
     * @return a decimal number as string
     */
    public static String hexToDecimal(String pHex) {
        return "" + (Integer.parseInt(pHex.trim(), 16));
    }

    /**
     * Converts a given hex-string to a binary-string.
     *
     * @param pHex the hex number as string
     * @return the binary number as string
     */
    public static String hexToBinary(String pHex) {
        int len = pHex.length() * 4;
        String bin = new BigInteger(pHex, 16).toString(2);

        // BigInteger removes all 0s in the beginning
        // so readd them...
        if (bin.length() < len) {
            int diff = len - bin.length();
            String pad = "";
            for (int i = 0; i < diff; ++i) {
                pad = pad.concat("0");
            }
            bin = pad.concat(bin);
        }
        return bin;
    }

    /**
     * Removes all whitespace from a string.
     *
     * @param pText the string to remove all whitespace from
     * @return the same string without whitespace
     */
    public static String removeAllSpace(String pText) {
        return pText.replaceAll("\\s+", "");
    }

    /**
     * Adds to hex numbers given as strings.
     *
     * @param pA hex number
     * @param pB hex number
     * @return the sum of the hex numbers as string
     */
    public static String sumOfHex(String pA, String pB) {
        return Integer.toHexString(Integer.parseInt(pA, 16) + Integer.parseInt(pB, 16));
    }

    /**
     * Interpretes the hexstring as chars and computes a ASCII String out of it.
     *
     * @param pHexString the string containing chars coded in hex
     * @return a string containing readable chars
     */
    public static String hexToString(String pHexString) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pHexString.length() - 1; i += 2) {
            String output = pHexString.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    /**
     * Converts miliseconds into a human readable date
     * of the format dd.MM.yyyy, HH:mm.
     *
     * @param pMilliseconds the ms to convert
     * @return a String containing the date
     */
    public static String toReadableDate(long pMilliseconds) {
        Date date = new Date(pMilliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        return formatter.format(date);
    }

    /**
     * Converts miliseconds into a human readable duration of xx:xx:xx.
     *
     * @param pMilliseconds the ms to convert
     * @return a String containting the duration
     */
    public static String toReadableDuration(long pMilliseconds) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(pMilliseconds),
                TimeUnit.MILLISECONDS.toMinutes(pMilliseconds) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(pMilliseconds) % TimeUnit.MINUTES.toSeconds(1));
    }

    /**
     * Converts miliseconds into a readable time
     *
     * @param pMilliseconds the ms to convert
     * @return a string containing the time
     */
    public static String toReadableTime(long pMilliseconds) {
        Date date = new Date(pMilliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * Converts meters to km.
     *
     * @param pMeters the meters to convert
     * @return a string containing the km
     */
    public static String metersToKm(long pMeters) {
        DecimalFormat df = new DecimalFormat("0.00");
        float km = (float) pMeters / 1000f;
        return "" + df.format(km);
    }

}
