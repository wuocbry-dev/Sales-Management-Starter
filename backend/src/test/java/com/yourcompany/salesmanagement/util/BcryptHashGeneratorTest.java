package com.yourcompany.salesmanagement.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility test to generate BCrypt hashes for seed data.
 */
public class BcryptHashGeneratorTest {
    @Test
    void printHashes() {
        var encoder = new BCryptPasswordEncoder();
        System.out.println("admin123=" + encoder.encode("admin123"));
        System.out.println("manager123=" + encoder.encode("manager123"));
        System.out.println("cashier123=" + encoder.encode("cashier123"));
    }
}

