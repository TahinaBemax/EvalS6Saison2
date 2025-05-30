package itu.mg.erprh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class Required extends AbstractBeanField<String, String> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if(s == null || s.trim().isBlank()) {
            String fieldName = getField().getName();
            throw new CsvDataTypeMismatchException(String.format("%s  is required and can not be blank", fieldName));
        }

        return s.trim() ;
    }
}
