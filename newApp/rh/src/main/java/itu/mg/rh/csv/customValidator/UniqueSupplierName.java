package itu.mg.rh.csv.customValidator;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.util.HashSet;
import java.util.Set;

public class UniqueSupplierName extends AbstractBeanField<String, String> {
    Set<String> existingSupplierName;

    public UniqueSupplierName() {
        existingSupplierName = new HashSet<>();
    }
    
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException{
        if (s.trim().isEmpty())
            throw new CsvDataTypeMismatchException(String.format("%s is required and can not be blank!", getField().getName()));

        if (existingSupplierName.contains(s))
            throw new CsvDataTypeMismatchException(String.format("Field '%s' has duplicate value '%s'!", getField().getName() ,s));

        existingSupplierName.add(s);
        return s;
    }
}
