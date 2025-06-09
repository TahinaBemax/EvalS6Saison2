package itu.mg.rh.csv.service.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.rh.csv.dto.ExtractedData;
import itu.mg.rh.csv.helper.FileCsvName;
import itu.mg.rh.csv.service.ExportCsvService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class ExportCsvServiceImpl implements ExportCsvService {
    private final String output_dir = "C:\\Users\\TahinaBemax\\Desktop\\EvaluationS6\\Saison_2\\bench2.0\\sites\\itu.erpnext\\private\\files";
    public static final Logger logger = LoggerFactory.getLogger(ImportCsvImpl.class);

    @Override
    public <T> Object beanToCsv(List<T> beans, String outputFileName) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        String file_output = String.format("%s\\%s", output_dir, outputFileName);

        if (beans == null || beans.isEmpty())
            return null;

        Class<T> clazz = (Class<T>) beans.get(0).getClass();

        List<String> headers = new ArrayList<>();
        List<String> fieldOrder = new ArrayList<>();

        // Use reflection to extract @CsvBindByName values
        for (Field field : clazz.getDeclaredFields()) {
            CsvBindByName bind = field.getAnnotation(CsvBindByName.class);
            CsvCustomBindByName customBind = field.getAnnotation(CsvCustomBindByName.class);
            if (bind != null) {
                headers.add(bind.column());
                fieldOrder.add(field.getName());
            } else if (customBind != null) {
                headers.add(customBind.column());
                fieldOrder.add(field.getName());
            }
        }


            try (CSVWriter writer = new CSVWriter(new FileWriter(file_output))) {
                // Write header
                writer.writeNext(headers.toArray(new String[0]));

/*                HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
                strategy.setType((Class<T>) beans.get(0).getClass());*/
                // Use ColumnPositionMappingStrategy with dynamically extracted field names
                ColumnPositionMappingStrategy<T> strategy = new ColumnPositionMappingStrategy<>();
                strategy.setType(clazz);
                strategy.setColumnMapping(fieldOrder.toArray(new String[0]));

                StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                        .withMappingStrategy(strategy)
/*                        .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)*/
                        .withOrderedResults(true)
                        .withApplyQuotesToAll(false)
                        .build();

                beanToCsv.write(beans);
                writer.flush();

                return true;
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
                logger.error(e.getMessage());
                throw e;
            }
        }

    @Override
    public boolean exportToFrappeTemplateCsv(ExtractedData extractedData) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        beanToCsv(extractedData.getCompany(), FileCsvName.companyFileName);
        beanToCsv(extractedData.getEmployees(), FileCsvName.employeeFileName);
        beanToCsv(extractedData.getSalaryComponent(), FileCsvName.salaryComponentFileName);
        beanToCsv(extractedData.getSalaryStructures(), FileCsvName.salaryStructureFileName);
        beanToCsv(extractedData.getSalaryAssigments(), FileCsvName.salaryStructureAssignmentFileName);
        beanToCsv(extractedData.getSalarySlips(), FileCsvName.salarySlipFileName);

        return true;
    }
}
