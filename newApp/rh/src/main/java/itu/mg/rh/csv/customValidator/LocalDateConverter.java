package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends AbstractBeanField<String, String> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s == null || s.trim().isBlank())
            throw  new CsvDataTypeMismatchException(String.format("The Field %s is required and can't be blank", getField().getName()));

        return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
