package itu.mg.erprh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import itu.mg.erprh.csv.exception.DuplicateValueException;

import java.util.HashSet;
import java.util.Set;

public class UniqueItemName extends AbstractBeanField<String, String> {
    Set<String> existingItemName;

    public UniqueItemName() {
        existingItemName = new HashSet<>();
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException("Item Name is required and can not be blank");

        if (existingItemName.contains(s))
            throw new DuplicateValueException(field.getName(), s);

        existingItemName.add(s);
        return s;
    }
}
