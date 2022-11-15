package de.loci.lobbynpcs.util;

import java.util.Random;

public class RandomString {
    public static String randomString(int length, boolean numeric, boolean alphabetical, boolean symbolic) {
        if (!numeric && !alphabetical && !symbolic) {
            alphabetical = true;
            numeric = true;
        }
        String characters = (alphabetical ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ" : "")
                + (numeric ? "0123456789" : "")
                + (symbolic ? "~,.:?;[]{}´`^!@#$%¨&*()-_+=></ " : "");
        Random random = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = characters.charAt(random.nextInt(characters.length()));
            if (random.nextInt(2) == 0 && Character.isUpperCase(c)) {
                c = Character.toLowerCase(c);
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
