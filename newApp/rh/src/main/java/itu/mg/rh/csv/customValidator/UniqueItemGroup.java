package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import itu.mg.rh.csv.exception.DuplicateValueException;

import java.util.HashSet;
import java.util.Set;

public class UniqueItemGroup extends AbstractBeanField<String, String> {
    Set<String> existingItemGroup;

    public UniqueItemGroup() {
        existingItemGroup = new HashSet<>();
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException("Item Group is required and can not be blank");

        if (existingItemGroup.contains(s))
            throw new DuplicateValueException(field.getName(), s);

        existingItemGroup.add(s);
        return s;
    }
}
