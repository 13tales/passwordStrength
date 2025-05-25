package com.oc.codingtest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    return 0;
  }
}
