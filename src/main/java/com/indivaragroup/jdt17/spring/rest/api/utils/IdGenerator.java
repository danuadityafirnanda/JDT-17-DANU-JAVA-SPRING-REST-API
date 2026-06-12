package com.indivaragroup.jdt17.spring.rest.api.utils;

import java.util.concurrent.ThreadLocalRandom;

public class IdGenerator {

    public static String generateAccountNumber() {
        int number = ThreadLocalRandom.current().nextInt(100000000);
        return String.format("%08d", number);
    }
}
