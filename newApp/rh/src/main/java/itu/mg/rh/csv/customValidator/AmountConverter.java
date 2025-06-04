package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class AmountConverter extends AbstractBeanField<String, String> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if(s == null || s.trim().isBlank()) {
            String fieldName = getField().getName();
            throw new CsvDataTypeMismatchException(String.format("%s  is required and can not be blank", fieldName));
        }

        double amount = Double.parseDouble(s.trim());
        if (amount < 0) {
            throw new CsvDataTypeMismatchException("Amount must be greatest than Zero!");
        }

        return amount;
    }
}
