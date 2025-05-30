package itu.mg.erprh.csv.service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.erprh.csv.dto.export.SupplierExportDTO;
import itu.mg.erprh.csv.helper.CustomHeaderColumnNameMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class ExportCsvService {
    private final String output_dir = "C:\\Users\\TahinaBemax\\Desktop\\EvaluationS6\\Saison_2\\bench2.0\\sites\\itu.erpnext\\private\\files";
    private final String supplier_filename = "supplier.csv";

    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);

    public String getFileName() {
        return supplier_filename;
    }
    public boolean exportSupplierToCsv(List<SupplierExportDTO> supplier) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        String file_output =  String.format("%s\\%s", output_dir, supplier_filename);

        try(CSVWriter writer = new CSVWriter(new FileWriter(file_output))){
            CustomHeaderColumnNameMappingStrategy<SupplierExportDTO> strategy = new CustomHeaderColumnNameMappingStrategy<>();
            strategy.setType(SupplierExportDTO.class);

/*            // Set columns in order and with the names you want in the header
            String[] columns = new String[]{"id", "supplier_name", "country", "supplier_type"};
            writer.writeNext(columns);*/

            StatefulBeanToCsv<SupplierExportDTO> beanToCsv = new StatefulBeanToCsvBuilder<SupplierExportDTO>(writer)
                    .withMappingStrategy(strategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(true)
                    .withApplyQuotesToAll(false)
                    .build();

            beanToCsv.write(supplier);

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
