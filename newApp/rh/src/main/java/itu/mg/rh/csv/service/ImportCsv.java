package itu.mg.rh.csv.service;

import itu.mg.rh.csv.CsvImportFinalResult;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.dto.ImportDto;

import java.util.Map;

public interface ImportCsv {
     ApiResponse deleteData();
     CsvImportFinalResult dataImport(ImportDto importDto);
     boolean submit(Map data);
}
