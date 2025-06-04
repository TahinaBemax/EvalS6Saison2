package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import itu.mg.rh.csv.exception.DuplicateValueException;

import java.util.HashSet;
import java.util.Set;

public class UniqueSalaryComponentName extends AbstractBeanField<String, String> {
    Set<String> existing;

    public UniqueSalaryComponentName() {
        existing = new HashSet<>();
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException("Salary Component Name is required and can not be blank");

        if (existing.contains(s))
            throw new DuplicateValueException(field.getName(), s);

        existing.add(s);
        return s;
    }
}
