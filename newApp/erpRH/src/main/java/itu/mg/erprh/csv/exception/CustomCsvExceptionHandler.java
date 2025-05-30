package itu.mg.erprh.csv.exception;

import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.exceptions.CsvException;

public class CustomCsvExceptionHandler implements CsvExceptionHandler {
    @Override
    public CsvException handleException(CsvException e) throws CsvException {
        return null;
    }
}
