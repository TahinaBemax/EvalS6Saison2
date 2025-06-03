package itu.mg.rh.csv.service.impl;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.rh.csv.helper.CustomHeaderColumnNameMappingStrategy;
import itu.mg.rh.csv.service.ExportCsvService;
import itu.mg.rh.csv.service.ImportCsv;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@Data
public class ExportCsvServiceImpl implements ExportCsvService {
    private final String output_dir = "C:\\Users\\TahinaBemax\\Desktop\\EvaluationS6\\Saison_2\\bench2.0\\sites\\itu.erpnext\\private\\files";
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);

    @Override
    public <T>Object beanToCsv(List<T> beans, String outputFileName) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        String file_output =  String.format("%s\\%s", output_dir, outputFileName);

        if (beans == null || beans.isEmpty())
            return null;

        try(CSVWriter writer = new CSVWriter(new FileWriter(file_output))){
            CustomHeaderColumnNameMappingStrategy<T> strategy = new CustomHeaderColumnNameMappingStrategy<>();
            strategy.setType((Class<T>) beans.get(0).getClass());

            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer)
                    //.withMappingStrategy(strategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
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
}
