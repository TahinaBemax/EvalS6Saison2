package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import itu.mg.rh.csv.exception.DuplicateValueException;

import java.util.HashSet;
import java.util.Set;

public class UniqueEmployeeRef extends AbstractBeanField<String, String> {
    Set<String> existingRef;

    public UniqueEmployeeRef() {
        existingRef = new HashSet<>();
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException("Ref is required and can not be blank");

        if (existingRef.contains(s))
            throw new DuplicateValueException(field.getName(), s);

        existingRef.add(s);
        return s;
    }
}
