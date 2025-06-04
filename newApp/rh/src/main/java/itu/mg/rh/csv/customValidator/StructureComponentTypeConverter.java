package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

public class StructureComponentTypeConverter extends AbstractBeanField<String, String> {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException("Salary Component Type is required and can not be blank");

        if (s.trim().equalsIgnoreCase("earning")){
            return "Earning";
        } else if (s.trim().equalsIgnoreCase("deduction")){
            return "Deduction";
        }
        throw new CsvDataTypeMismatchException("Salary Component Type must be Earning or Deduction!");
    }
}
