package com.oc.codingtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.lang.Math.abs;

public class PasswordStrength {

  private static final Logger log = LoggerFactory.getLogger(PasswordStrength.class);

  public boolean isPasswordPermissible(String password, int maxAllowedRepetitionCount, int maxAllowedSequenceLength) {
    // This method accepts a password (String) and calculates two password strength parameters:
    // Repetition count and Max Sequence length
    return getMaxRepetitionCount(password) <= maxAllowedRepetitionCount
        && getMaxSequenceLen(password) <= maxAllowedSequenceLength;
  }

  /**
   * Repetition count - the number of occurrences of the *most repeated* character within the password
   * eg1: "Melbourne" has a repetition count of 2 - for the 2 non-consecutive "e" characters.
   * eg2: "passwords" has a repetition count of 3 - for the 3 "s" characters
   * eg3: "lucky" has a repetition count of 1 - each character appears only once.
   * eg4: "Elephant" has a repetition count of 1 - as the two "e" characters have different cases (ie one "E", one "e")
   * The repetition count should be case-sensitive.
   *
   * @param password
   * @return
   */
  public int getMaxRepetitionCount(String password) {
    // Generate a frequency map of the characters/codepoints in the password string using Java streams.
    HashMap<Integer, Integer> frequencyMap = password
        .chars()
        .collect(
            HashMap::new,
            (HashMap<Integer, Integer> acc, int c) -> {
              acc.merge(c, 1, Integer::sum);
            },
            (HashMap<Integer, Integer> a, HashMap<Integer, Integer> b) ->
                b.forEach((k, v) -> a.merge(k, v, Integer::sum))
        );

    /**
     * Get a stream of the frequency counts and use .max to find the highest count.
     * `.orElse` is necessary because `.max` returns an optional value.
     */
    return frequencyMap.values().stream().max(Integer::compareTo).orElse(0);
  }

  /**
   * Max Sequence length - The length of the longest ascending/descending sequence of alphabetical or numeric characters
   * eg: "4678" and "4321" would both have sequence length of 4
   * eg2: "cdefgh" would have a sequence length of 6
   * eg3: "password123" would have a max. sequence length of 3 - for the sequence of "123".
   * eg3a: "1pass2word3" would have a max. sequence length of 0 - as there is no sequence.
   * eg3b: "passwordABC" would have a max. sequence length of 3 - for the sequence of "ABC".
   * eg4: "AbCdEf" would have a sequence length of 6, even though it is mixed case.
   * eg5: "ABC_DEF" would have a sequence length of 3, because the special character breaks the progression
   * Check the supplied password.  Return true if the repetition count and sequence length are below or equal to the
   * specified maximum.  Otherwise, return false.
   *
   * @param password
   * @return
   */
  public int getMaxSequenceLen(String password) {
    String normalisedPassword = password.toLowerCase();

    return normalisedPassword
        .codePoints()
        .collect(
            HashMap::new,
            (HashMap<String, Integer> acc, int c) -> {
              // Non-numeric or alphabetic characters break a sequence
              boolean isCountable = (Character.isAlphabetic(c) || Character.isDigit(c));

              /* If the absolute distance from the last is 1,
               * then we're either starting or continuing a sequence.
               * */
              if (isCountable) {
                // Get the last character or default to the current one
                int lastChar = acc.getOrDefault("lastChar", c);
                // Get the difference between the two
                int distance = lastChar - c;

                if (abs(distance) == 1) {
                  int current;
                /*
                 If the sequence has maintained its current direction, it's a continuation.
                 If not, it's a new sequence.
                */
                  if (distance == acc.get("direction")) {
                    // Increment the current sequence length.
                    current = acc.merge("current", 1, Integer::sum);
                  } else {
                    acc.put("current", 2);
                    current = 2;
                  }
                  // Set the current sequence direction (either -1 or 1)
                  acc.put("direction", distance);
                  // Update the maximum sequence length
                  acc.merge("max", current, Integer::max);
                } else {
                  // We've broken the sequence; reset the current length and direction
                  acc.put("current", 1);
                  acc.put("direction", 0);
                }
                // Store the current character for the next comparison
                acc.put("lastChar", c);
              } else {
                // If the current character is not countable, reset comparisons
                acc.remove("lastChar");
                acc.put("current", 1);
                acc.put("direction", 0);
              }
            },
            (a, b) -> {
              // Merge two accumulators by choosing the maximum sequence length
              int bMax = b.getOrDefault("max", 0);
              a.merge("max", bMax, Integer::max);
            }
        )
        .getOrDefault("max", 0);
  }
}
