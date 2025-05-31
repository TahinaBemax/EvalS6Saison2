package itu.mg.rh.csv.helper;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        List<String> headers = new ArrayList<>();
        Field[] fields = getType().getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(CsvBindByName.class)) {
                CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
                headers.add(annotation.column());
            } else if (field.isAnnotationPresent(CsvCustomBindByName.class)) {
                CsvCustomBindByName annotation = field.getAnnotation(CsvCustomBindByName.class);
                headers.add(annotation.column());
            }
        }

        return headers.toArray(new String[0]);
    }
}