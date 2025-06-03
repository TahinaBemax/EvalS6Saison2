package itu.mg.rh.csv.service.impl;

import itu.mg.rh.csv.CsvParseFinalResult;
import itu.mg.rh.csv.dto.EmployeeDTO;
import itu.mg.rh.csv.dto.SalaryComponentDTO;
import itu.mg.rh.csv.dto.SalarySlipDTO;
import itu.mg.rh.csv.dto.export.*;
import itu.mg.rh.csv.service.DataExtractor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataExtractorImpl implements DataExtractor {
    @Override
    public List<EmployeeExportDTO> getEmployees(CsvParseFinalResult result) {
        return result.getValidEmployees().stream().map(this::mapToEmployee).toList();
    }

    @Override
    public List<SalaryComponentExportDTO> getSalaryComponent(CsvParseFinalResult result) {
        return result.getValidSalaryComponents().stream().map(this::mapToSalaryComponentExport).toList();
    }

    @Override
    public List<SalaryStructureExportDTO> getSalaryStructures(CsvParseFinalResult result) {
        return result.getValidSalaryComponents().stream().map(this::mapToSalaryStructureExport).toList();
    }

    @Override
    public List<SalaryStructureAssignmentExportDTO> getSalaryAssigments(CsvParseFinalResult result) {
        return result.getValidSalarySlips()
                .stream()
                .map(salarySlip -> {
                    List<EmployeeDTO> validEmployees = result.getValidEmployees();
                    return mapToSalaryStructureAssignmentExport(validEmployees, salarySlip);
                }).toList();
    }

    @Override
    public List<SalarySlipExportDTO> getSalarySlips(CsvParseFinalResult result) {
        return result.getValidSalarySlips()
                .stream()
                .map(salarySlip -> {
                    List<EmployeeDTO> validEmployees = result.getValidEmployees();
                    return mapToSalarySlipExport(validEmployees, salarySlip);
                }).toList();
    }

    @Override
    public List<?> getCompany(CsvParseFinalResult result) {
        return null;
    }


    private EmployeeExportDTO mapToEmployee(EmployeeDTO dto) {
        EmployeeExportDTO employee = new EmployeeExportDTO();
        employee.setId("");
        employee.setSeries("HR-EMP-");
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setFullName();
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setHireDate(dto.getHireDate());
        employee.setGender(employeeGenderHandler(dto.getGender()));
        employee.setStatus("Active");
        employee.setCompany(dto.getCompany());

        return employee;
    }
    private String employeeGenderHandler(String gender) {
        if (gender == null || gender.trim().isBlank())
            throw new RuntimeException("Gender is Null!");

        String genderLowercase = gender.toLowerCase();

        if (genderLowercase.equals("masculin") || genderLowercase.equals("male")) {
            return "Male";
        } else if (genderLowercase.equals("feminin") || genderLowercase.equals("féminin") || genderLowercase.equals("female")) {
            return "Female";
        }

        return gender;
    }
    private EmployeeDTO findEmployeByRef(List<EmployeeDTO> employeeDTOS, String ref) {
        return employeeDTOS
                .stream()
                .filter(employee -> employee.getRef().equals(ref))
                .findFirst().orElseThrow();
    }

    private SalaryComponentExportDTO mapToSalaryComponentExport(SalaryComponentDTO dto){
        SalaryComponentExportDTO salaryExp = new SalaryComponentExportDTO();
        salaryExp.setName("");
        salaryExp.setAbbr(dto.getAbbr());
        salaryExp.setType(dto.getType());
        salaryExp.setFormula(dto.getValeur());
        salaryExp.setAmountBasedOnFormula("1");
        salaryExp.setIdAccounts("");
        salaryExp.setCompanyAccounts(dto.getCompany());
        salaryExp.setAccountAccounts("Cash-" + dto.getCompany());

        return salaryExp;
    }

    private SalaryStructureExportDTO mapToSalaryStructureExport(SalaryComponentDTO dto){
        SalaryStructureExportDTO salaryStructureExp = new SalaryStructureExportDTO();
        salaryStructureExp.setId(dto.getSalaryStructure());
        salaryStructureExp.setCompany(dto.getCompany());
        salaryStructureExp.setIsActive("Yes");
        salaryStructureExp.setCurrency("EUR");
        salaryStructureExp.setPayrollFrequency("Monthly");
        salaryStructureExp.setModeOfPayment("Cash");
        salaryStructureExp.setPaymentAccount("Cash-" + dto.getCompany());

        if (dto.getType() != null && !dto.getType().trim().isBlank()){
            switch (dto.getType().toLowerCase()){
                case ("earning") -> {
                    salaryStructureExp.setIDEarnings("");
                    salaryStructureExp.setComponentEarnings("");
                    salaryStructureExp.setFormulaEarnings(dto.getValeur());
                    salaryStructureExp.setAmountBasedOnFormulaEarnings("");
                }
                case ("deductions") -> {
                    salaryStructureExp.setIdDeductions("");
                    salaryStructureExp.setFormulaDeductions("");
                    salaryStructureExp.setComponentDeductions(dto.getValeur());
                    salaryStructureExp.setAmountBasedOnFormulaDeductions("");
                }
            }

        }

        return salaryStructureExp;
    }

    private SalaryStructureAssignmentExportDTO mapToSalaryStructureAssignmentExport(List<EmployeeDTO> employeeDTOS, SalarySlipDTO slipDTO){
        SalaryStructureAssignmentExportDTO salaryStructureExp = new SalaryStructureAssignmentExportDTO();
        EmployeeDTO matchedEmployee = findEmployeByRef(employeeDTOS, slipDTO.getRefEmployee());
        String fullName = matchedEmployee.getLastName() != null ? matchedEmployee.getFirstName() + " " + matchedEmployee.getFirstName() : matchedEmployee.getFirstName();

        salaryStructureExp.setId("");
        salaryStructureExp.setEmployee("");
        salaryStructureExp.setEmployeeName(fullName);
        salaryStructureExp.setCompany(matchedEmployee.getCompany());
        salaryStructureExp.setCurrency("EUR");
        salaryStructureExp.setSalaryStructure(slipDTO.getSalary());
        salaryStructureExp.setFromDate(slipDTO.getMois());
        salaryStructureExp.setPayrollPayableAccount("Payroll Payable - " + matchedEmployee.getCompany());


        return salaryStructureExp;
    }

    private SalarySlipExportDTO mapToSalarySlipExport(List<EmployeeDTO> employeeDTOS, SalarySlipDTO slipDTO){
        SalarySlipExportDTO salaryStructureExp = new SalarySlipExportDTO();
        EmployeeDTO matchedEmployee = findEmployeByRef(employeeDTOS, slipDTO.getRefEmployee());
        String fullName = matchedEmployee.getLastName() != null ? matchedEmployee.getFirstName() + " " + matchedEmployee.getFirstName() : matchedEmployee.getFirstName();

        salaryStructureExp.setId("");
        salaryStructureExp.setEmployee("");
        salaryStructureExp.setEmployeeName(fullName);
        salaryStructureExp.setCompany(matchedEmployee.getCompany());
        salaryStructureExp.setPostingDate(slipDTO.getMois());
        salaryStructureExp.setStartDate(slipDTO.getMois());
        salaryStructureExp.setEndDate(slipDTO.getMois().plusMonths(1));
        salaryStructureExp.setModeOfPayment("Cash");
        salaryStructureExp.setCurrency("EUR");
        salaryStructureExp.setExchangeRate(0.0);
        salaryStructureExp.setSalaryStructure(slipDTO.getSalary());
        salaryStructureExp.setWorkingDays(28);
        salaryStructureExp.setPaymentDays(28);
        salaryStructureExp.setPayrollFrequency("Monthly");

        return salaryStructureExp;
    }
}
