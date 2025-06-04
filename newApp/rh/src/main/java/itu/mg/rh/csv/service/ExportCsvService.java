package itu.mg.rh.csv.service;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.List;

public interface ExportCsvService {
    <T>Object beanToCsv(List<T> beans, String outputFile) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException;
}
