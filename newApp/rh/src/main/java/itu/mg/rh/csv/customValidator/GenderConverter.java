package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import itu.mg.rh.csv.exception.DuplicateValueException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GenderConverter extends AbstractBeanField<String, String> {
    String[] validGenders;

    public GenderConverter() {
        validGenders = new String[] {"male", "homme", "masculin", "femme", "féminin", "feminin", "female"};
    }

    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException("Ref is required and can not be blank");

        if (!List.of(validGenders).contains(s.trim().toLowerCase()))
            throw new CsvDataTypeMismatchException(String.format("Field %s, value must one of %s", getField().getName(), Arrays.toString(validGenders)));

        return s;
    }
}
