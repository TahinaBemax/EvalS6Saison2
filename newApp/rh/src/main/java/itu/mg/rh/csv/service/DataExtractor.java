package itu.mg.rh.csv.service;

import itu.mg.rh.csv.CsvImportFinalResult;
import itu.mg.rh.csv.dto.export.*;

import java.util.List;

public interface DataExtractor {
    List<EmployeeExportDTO> getEmployees(CsvImportFinalResult result);
    List<SalaryComponentExportDTO> getSalaryComponent(CsvImportFinalResult result);
    List<SalaryStructureExportDTO> getSalaryStructures(CsvImportFinalResult result);
    List<SalaryStructureAssignmentExportDTO> getSalaryAssigments(CsvImportFinalResult result);
    List<SalarySlipExportDTO> getSalarySlips(CsvImportFinalResult result);
    List<CompanyExportDTO> getCompany(CsvImportFinalResult result);
}
