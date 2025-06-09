package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class AmountConverter extends AbstractBeanField<String, String> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s == null || s.trim().isEmpty()) {
            String fieldName = getField().getName();
            throw new CsvDataTypeMismatchException(String.format("%s is required and cannot be blank", fieldName));
        }

        s = s.trim();
        if (s.indexOf("-") == 0)
            throw new CsvDataTypeMismatchException(String.format("%s must be a positive number", getField().getName()));

        // Remove currency symbols and non-numeric signs except decimal/thousand separators
        s = s.replaceAll("[^\\d.,\s]", "");

        // Remove spaces (they are usually thousand separators)
        s = s.replace(" ", "");

        // If both ',' and '.' are present, determine which is decimal
        int lastComma = s.lastIndexOf(',');
        int lastDot = s.lastIndexOf('.');
        String normalized = s;
        if (lastComma != -1 && lastDot != -1) {
            if (lastComma > lastDot) {
                // ',' is decimal, '.' is thousand
                normalized = s.replace(".", "").replace(',', '.');
            } else {
                // '.' is decimal, ',' is thousand
                normalized = s.replace(",", "");
            }
        } else if (lastComma != -1) {
            // Only comma present
            int decimals = s.length() - lastComma - 1;
            if (decimals == 3) {
                // Likely thousand separator
                normalized = s.replace(",", "");
            } else {
                // Likely decimal separator
                normalized = s.replace(',', '.');
            }
        } else if (lastDot != -1) {
            // Only dot present
            int decimals = s.length() - lastDot - 1;
            if (decimals == 3) {
                // Likely thousand separator
                normalized = s.replace(".", "");
            } // else, dot is decimal separator, no change needed
        }
        // else: only digits, no separators

        try {
            double amount = Double.parseDouble(normalized);
            if (amount < 0) {
                throw new CsvDataTypeMismatchException("Amount must be greater than or equal to zero!");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException("Invalid amount format: " + s);
        }
    }

    private boolean isLikelyFrenchFormat(String value) {
        // Example: "1 234,56" or "1234,56"
        int commaIndex = value.lastIndexOf(',');
        int dotIndex = value.lastIndexOf('.');
        int spaceIndex = value.indexOf(' ');

        if (commaIndex > dotIndex) return true;
        if (spaceIndex != -1 && commaIndex != -1) return true;
        if (spaceIndex != -1) return true;

        return false;
    }
}
