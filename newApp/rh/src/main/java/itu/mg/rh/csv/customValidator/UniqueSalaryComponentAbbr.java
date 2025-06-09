package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import itu.mg.rh.csv.exception.DuplicateValueException;

import java.util.HashSet;
import java.util.Set;

public class UniqueSalaryComponentAbbr extends AbstractBeanField<String, String> {
    Set<String> existing;

    public UniqueSalaryComponentAbbr() {
        existing = new HashSet<>();
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException(String.format("The Field %s is required and can not be blank", getField().getName()));

        if (existing.contains(s))
            throw new CsvDataTypeMismatchException(String.format("Field: %s, The value %s is already exist!", field.getName(), s));

        existing.add(s);
        return s;
    }
}
