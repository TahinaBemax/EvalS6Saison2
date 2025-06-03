package itu.mg.rh.csv.service;

import itu.mg.rh.csv.CsvParseFinalResult;
import itu.mg.rh.csv.dto.export.EmployeeExportDTO;
import itu.mg.rh.csv.dto.export.SalaryComponentExportDTO;
import itu.mg.rh.csv.dto.export.SalarySlipExportDTO;
import itu.mg.rh.csv.dto.export.SalaryStructureExportDTO;
import itu.mg.rh.models.Employee;
import itu.mg.rh.models.SalarySlip;
import itu.mg.rh.models.SalaryStructure;

import java.util.List;

public interface DataExtractor {
    List<EmployeeExportDTO> getEmployees(CsvParseFinalResult result);
    List<SalaryComponentExportDTO> getSalaryComponent(CsvParseFinalResult result);
    List<SalaryStructureExportDTO> getSalaryStructures(CsvParseFinalResult result);
    List<?> getSalaryAssigments(CsvParseFinalResult result);
    List<SalarySlipExportDTO> getSalarySlips(CsvParseFinalResult result);
    List<?> getCompany(CsvParseFinalResult result);
}
