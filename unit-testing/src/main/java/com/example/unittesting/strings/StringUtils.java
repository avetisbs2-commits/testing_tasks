package com.example.unittesting.strings;

/**
 * LEVEL 2 - text methods. Here the tricky part is the EDGE CASES: null and empty text.
 *
 * This code is FINISHED and correct. Do not change it.
 * Your job is to write the tests: see StringUtilsTest.
 */
public class StringUtils {

    private StringUtils() {
    }

    /** "abc" -> "cba". Returns null if the text is null. */
    public static String reverse(String text) {
        if (text == null) {
            return null;
        }
        return new StringBuilder(text).reverse().toString();
    }

    /**
     * True if the text reads the same forwards and backwards.
     * Spaces and upper/lower case are ignored: "Never odd or even" is a palindrome.
     * null is NOT a palindrome. The empty text IS a palindrome.
     */
    public static boolean isPalindrome(String text) {
        if (text == null) {
            return false;
        }
        String cleaned = text.replace(" ", "").toLowerCase();
        return cleaned.equals(reverse(cleaned));
    }

    /** Counts the vowels a, e, i, o, u (upper or lower case). null gives 0. */
    public static int countVowels(String text) {
        if (text == null) {
            return 0;
        }
        int count = 0;
        for (char letter : text.toCharArray()) {
            if ("aeiouAEIOU".indexOf(letter) >= 0) {
                count++;
            }
        }
        return count;
    }

    /** Makes the first letter big: "hello" -> "Hello". null gives null, empty gives empty. */
    public static String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
