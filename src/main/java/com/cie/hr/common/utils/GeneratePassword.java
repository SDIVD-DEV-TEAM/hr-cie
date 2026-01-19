package com.cie.hr.common.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
public class GeneratePassword {

    public static String get(int length, boolean letter, boolean number) {
        return RandomStringUtils.secureStrong().next(length, letter, number);
    }

    public static String generateCommonLangPassword() {
        String upperCaseLetters = RandomStringUtils.secureStrong().next(3, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.secureStrong().next(3, 97, 122, true, true);
        String numbers = RandomStringUtils.secureStrong().nextNumeric(3);
        String characters = "!@#$%^&*()-_=+[{]}|;:,<.>/?";
        String specialChar = RandomStringUtils.secureStrong().next(2, characters);
        String totalChars = RandomStringUtils.secureStrong().nextNumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        return pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }


}
