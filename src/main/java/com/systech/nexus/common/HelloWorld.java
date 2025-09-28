package com.systech.nexus.common;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Simple HelloWorld application with age calculator functionality.
 *
 * MAJOR CHANGES:
 * v1.0 (2025-09-19) - Initial implementation with basic console output
 * v1.1 (2025-09-19) - Added age calculator functionality with DOB input
 *
 * Features:
 * - Simple console output demonstration
 * - Age calculator based on date of birth
 * - Interactive user input for DOB
 * - Maven-compatible main method
 * - Package structure following project conventions
 *
 * @author Claude
 * @version 1.1
 * @since 1.0
 */
public class HelloWorld {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Main method to run the HelloWorld application with age calculator.
     *
     * @param args command line arguments - can provide DOB as first argument (yyyy-MM-dd format)
     * @since 1.0
     */
    public static void main(String[] args) {
        System.out.println("Hello World from Nexus SpringBoot Application!");
        System.out.println("Maven execution successful!");
        System.out.println("Current package: " + HelloWorld.class.getPackage().getName());
        System.out.println("=".repeat(50));

        if (args.length > 0) {
            // Use command line argument if provided
            calculateAgeFromString(args[0]);
        } else {
            // Interactive mode
            runInteractiveAgeCalculator();
        }
    }

    /**
     * Runs interactive age calculator that prompts user for date of birth.
     *
     * @since 1.1
     */
    private static void runInteractiveAgeCalculator() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Age Calculator");
        System.out.println("Enter your date of birth (yyyy-MM-dd format, e.g., 1990-05-15):");
        System.out.print("DOB: ");

        String dobInput = scanner.nextLine().trim();
        calculateAgeFromString(dobInput);

        scanner.close();
    }

    /**
     * Calculates and displays age from a date string.
     *
     * @param dobString date of birth as string in yyyy-MM-dd format
     * @since 1.1
     */
    private static void calculateAgeFromString(String dobString) {
        try {
            LocalDate dob = LocalDate.parse(dobString, DATE_FORMATTER);
            AgeResult ageResult = calculateAge(dob);

            System.out.println("\nAge Calculation Results:");
            System.out.println("Date of Birth: " + dob.format(DATE_FORMATTER));
            System.out.println("Today's Date: " + LocalDate.now().format(DATE_FORMATTER));
            System.out.println("Age: " + ageResult.years + " years, " +
                             ageResult.months + " months, " +
                             ageResult.days + " days");
            System.out.println("Total Days Lived: " + ageResult.totalDays);

        } catch (DateTimeParseException e) {
            System.err.println("Error: Invalid date format. Please use yyyy-MM-dd format (e.g., 1990-05-15)");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Calculates age from date of birth to current date.
     *
     * @param dob date of birth
     * @return AgeResult containing years, months, days, and total days
     * @throws IllegalArgumentException if date of birth is in the future
     * @since 1.1
     */
    public static AgeResult calculateAge(LocalDate dob) {
        LocalDate today = LocalDate.now();

        if (dob.isAfter(today)) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }

        Period period = Period.between(dob, today);
        long totalDays = dob.until(today).getDays();

        return new AgeResult(period.getYears(), period.getMonths(), period.getDays(), totalDays);
    }

    /**
     * Data class to hold age calculation results.
     *
     * @since 1.1
     */
    public static class AgeResult {
        public final int years;
        public final int months;
        public final int days;
        public final long totalDays;

        /**
         * Constructor for AgeResult.
         *
         * @param years number of complete years
         * @param months number of complete months (after years)
         * @param days number of complete days (after months)
         * @param totalDays total number of days lived
         * @since 1.1
         */
        public AgeResult(int years, int months, int days, long totalDays) {
            this.years = years;
            this.months = months;
            this.days = days;
            this.totalDays = totalDays;
        }
    }
}