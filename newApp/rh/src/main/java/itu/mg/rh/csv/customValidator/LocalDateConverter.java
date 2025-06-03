package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocalDateConverter extends AbstractBeanField<String, String> {
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH),
            DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)
    );

    private static final List<DateTimeFormatter> MONTH_YEAR_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("MM/yyyy", Locale.FRENCH),
            DateTimeFormatter.ofPattern("MM/yyyy", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyy-MM", Locale.ENGLISH)
    );

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s == null || s.trim().isBlank()) {
            throw new CsvDataTypeMismatchException(String.format("The Field %s is required and can't be blank", getField().getName()));
        }

        String trimmedDate = s.trim();

        // Try parsing as full date first
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(trimmedDate, formatter);
                return date;
            } catch (DateTimeParseException ignored) {}
        }

        // Try parsing as month/year
        for (DateTimeFormatter formatter : MONTH_YEAR_FORMATTERS) {
            try {
                YearMonth yearMonth = YearMonth.parse(trimmedDate, formatter);
                // Convert to first day of the month
                return yearMonth.atDay(1);
            } catch (DateTimeParseException ignored) {}
        }

        // If we get here, none of the formats matched
        throw new CsvDataTypeMismatchException(
                String.format("Invalid date format for field %s. Value '%s' must be in one of these formats: " +
                                "dd/MM/yyyy, MM/dd/yyyy, yyyy-MM-dd, dd-MM-yyyy, MM/yyyy, or yyyy-MM",
                        getField().getName(), trimmedDate)
        );
    }
}
