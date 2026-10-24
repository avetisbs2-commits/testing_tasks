package com.example.unittesting.strings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LEVEL 2 - EDGE CASES and PARAMETERIZED TESTS.
 *
 * Most bugs hide in unusual input: null, empty text, one character, upper/lower case.
 * A good tester always asks: "what is the strangest thing somebody can give to this method?"
 *
 * New tools:
 *   assertNull(value)
 *   @ParameterizedTest + @CsvSource({"input,expected", ...})
 *       one test method runs many times, once for each line. Less copy and paste.
 *       In @CsvSource an empty text is written as '' (two single quotes).
 */
class StringUtilsTest {

    // ---------------------------------------------------------------
    // WORKED EXAMPLES
    // ---------------------------------------------------------------
    @Test
    void reverse_normalText_returnsReversedText() {
        assertEquals("cba", StringUtils.reverse("abc"));
    }

    @ParameterizedTest
    @CsvSource({
            "hello, 2",
            "AEIOU, 5",
            "xyz, 0"
    })
    void countVowels_countsAEIOU_upperOrLowerCase(String text, int expectedCount) {
        assertEquals(expectedCount, StringUtils.countVowels(text));
    }


    // reverse
    // TODO 1: reverse(null)                      returns null          (assertNull)
    @Test
    void reverse_null_returnsNull() {
        assertNull(StringUtils.reverse(null));
    }


    // TODO 2: reverse("")                        returns ""
    @Test
    void reverse_emptyText_returnsEmptyText() {
        assertEquals("", StringUtils.reverse(""));
    }
    // TODO 3: reverse("a")                       returns "a"           (one character)
    @Test
    void reverse_oneCharacter_returnsSame() {
        assertEquals("a", StringUtils.reverse("a"));
    }

    // isPalindrome  (tip: use a @ParameterizedTest for the "true" cases and one for the "false" cases)
    // TODO 4: true for:  "level", "Level" (upper case is ignored), "Never odd or even" (spaces are ignored), "a"
    @ParameterizedTest
    @CsvSource({
            "Level",
            "Never odd or even",
            "a"
    })
    void isPalindrome_returnTrue(String text) {
        assertTrue(StringUtils.isPalindrome(text));
    }

    // TODO 5: false for: "hello", "ab"
    @ParameterizedTest
    @CsvSource({
            "hello",
            "ab"
    })
    void isPalindrome_returnFalse(String text) {
        assertFalse(StringUtils.isPalindrome(text));
    }
    // TODO 6: isPalindrome(null)                 is false
    @Test
    void isNullPalindrome_returnFalse() {
        assertFalse(StringUtils.isPalindrome(null));
    }
    // TODO 7: isPalindrome("")                   is true               (read the comment in StringUtils)
    @Test
    void isEmptyPalindrome_returnTrue() {
        assertTrue(StringUtils.isPalindrome(""));
    }

    // countVowels
    // TODO 8: countVowels(null)                  returns 0
    @Test
    void countVowels_countsNULL() {
        assertEquals(0 ,StringUtils.countVowels(null));
    }
    // TODO 9: countVowels("")                    returns 0
    @Test
    void countVowels_countsEmpty() {
        assertEquals(0 ,StringUtils.countVowels(""));
    }
    // TODO 9b: countVowels("aeiou")              returns 5     (every vowel on its own counts.
    //          The first vowel in the code's list is special. Read countVowels() and find out why a test with "a" matters.)
    @Test
    void countVowels_counts_aeiou() {
        assertEquals(5 ,StringUtils.countVowels("aeiou"));
    }
    // capitalize
    // TODO 10: capitalize("hello")               returns "Hello"
    // TODO 11: capitalize("Hello")               returns "Hello"       (already big)
    // TODO 12: capitalize("a")                   returns "A"
    @Test
    void capitalize_returnCapitalized() {
        assertEquals("Hello" ,StringUtils.capitalize("hello"));
    }
    @Test
    void capitalizeAlreadyCapitalized_returnSame() {
        assertEquals("Hello" ,StringUtils.capitalize("Hello"));
    }
    @Test
    void capitalizeChar_returnCapitalized() {
        assertEquals("A" ,StringUtils.capitalize("a"));
    }
    // TODO 13: capitalize(null)                  returns null
    @Test
    void capitalizeNULL_returnNULL() {
        assertEquals(null ,StringUtils.capitalize(null));
    }
    // TODO 14: capitalize("")                    returns ""
    @Test
    void capitalizeEmpty_returnEmpty() {
        assertEquals("" ,StringUtils.capitalize(""));
    }
    // TODO 15: capitalize("hELLO")               returns "HELLO"       (only the FIRST letter changes. Is that what you expect? Check the code!)
    @Test
    void capitalize_hELLO_returnHELLO() {
        assertEquals("HELLO" ,StringUtils.capitalize("hELLO"));
    }
}
