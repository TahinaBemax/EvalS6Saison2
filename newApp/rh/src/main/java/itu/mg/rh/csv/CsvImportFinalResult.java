package itu.mg.rh.csv;


import itu.mg.rh.csv.dto.SalaryComponentDTO;
import itu.mg.rh.csv.dto.EmployeeDTO;
import itu.mg.rh.csv.dto.SalarySlipDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CsvImportFinalResult {
    CsvParseResult<EmployeeDTO> employees;
    CsvParseResult<SalaryComponentDTO> salaryComponents;
    CsvParseResult<SalarySlipDTO> salarySlips;
    String errorLevel;
    List<String> errorGlobal;

    public CsvImportFinalResult(CsvParseResult<EmployeeDTO> employees, CsvParseResult<SalaryComponentDTO> salaryComponents, CsvParseResult<SalarySlipDTO> salarySlips) {
        this.employees = employees;
        this.salaryComponents = salaryComponents;
        this.salarySlips = salarySlips;
        this.errorLevel = "";
        this.errorGlobal = new ArrayList<>();
    }

    public boolean isValid() {
        return (employees.isValid() && salaryComponents.isValid() && salarySlips.isValid() && errorGlobal.isEmpty());
    }
    public List<EmployeeDTO> getValidEmployees(){
        return employees.getValidRows();
    }
    public List<SalaryComponentDTO> getValidSalaryComponents(){
        return salaryComponents.getValidRows();
    }
    public List<SalarySlipDTO> getValidSalarySlips(){
        return salarySlips.getValidRows();
    }


    public List<CsvErrorMessage> getEmployeeDTOErrors(){
        return this.employees.getErrors();
    }
    public List<CsvErrorMessage> getSalaryComponentDTOErrors(){
        return this.salaryComponents.getErrors();
    }
    public List<CsvErrorMessage> getSalarySlipDTOErrors(){
        return this.salarySlips.getErrors();
    }
}
