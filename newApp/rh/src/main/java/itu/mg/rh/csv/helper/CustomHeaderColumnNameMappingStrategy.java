package itu.mg.rh.csv.helper;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.lang.reflect.Field;
import java.util.*;

public class CustomHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    private Map<String, Field> fieldMap = new HashMap<>();

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        List<String> headers = new ArrayList<>();
        Field[] fields = getType().getDeclaredFields();
        fieldMap.clear();

        for (Field field : fields) {
            if (field.isAnnotationPresent(CsvBindByName.class)) {
                CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
                String columnName = annotation.column();
                headers.add(columnName);
                field.setAccessible(true);
                fieldMap.put(columnName, field);
            } else if (field.isAnnotationPresent(CsvCustomBindByName.class)) {
                CsvCustomBindByName annotation = field.getAnnotation(CsvCustomBindByName.class);
                String columnName = annotation.column();
                headers.add(columnName);
                field.setAccessible(true);
                fieldMap.put(columnName, field);
            }
        }

        return headers.toArray(new String[0]);
    }

    @Override
    public String getColumnName(int col) {
        return fieldMap.keySet().toArray(new String[0])[col];
    }

    public Object getFieldValue(T bean, String columnName) {
        try {
            Field field = fieldMap.get(columnName);
            if (field != null) {
                return field.get(bean);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing field: " + columnName, e);
        }
        return null;
    }
}