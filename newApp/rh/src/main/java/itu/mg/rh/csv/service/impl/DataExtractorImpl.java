package itu.mg.rh.csv.service.impl;

import itu.mg.rh.csv.CsvImportFinalResult;
import itu.mg.rh.csv.dto.EmployeeDTO;
import itu.mg.rh.csv.dto.SalaryComponentDTO;
import itu.mg.rh.csv.dto.SalarySlipDTO;
import itu.mg.rh.csv.dto.export.*;
import itu.mg.rh.csv.service.DataExtractor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class DataExtractorImpl implements DataExtractor {
    @Override
    public List<EmployeeExportDTO> getEmployees(CsvImportFinalResult result) {
        return result.getValidEmployees().stream().map(this::mapToEmployee).toList();
    }

    @Override
    public List<SalaryComponentExportDTO> getSalaryComponent(CsvImportFinalResult result) {
        return result.getValidSalaryComponents().stream().map(this::mapToSalaryComponentExport).toList();
    }

    @Override
    public List<SalaryStructureExportDTO> getSalaryStructures(CsvImportFinalResult result) {
        Set<String> existingSalaryNames = new HashSet<>();

        return result.getValidSalaryComponents()
                .stream()
                .map(salComp -> {
                    if (!existingSalaryNames.contains(salComp.getSalaryStructure())){
                        existingSalaryNames.add(salComp.getSalaryStructure());
                        return mapToSalaryStructureExport(salComp, result.getValidSalaryComponents());
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<SalaryStructureAssignmentExportDTO> getSalaryAssigments(CsvImportFinalResult result) {
        return result.getValidSalarySlips()
                .stream()
                .map(salarySlip -> {
                    List<EmployeeDTO> validEmployees = result.getValidEmployees();
                    return mapToSalaryStructureAssignmentExport(validEmployees, salarySlip);
                }).toList();
    }

    @Override
    public List<SalarySlipExportDTO> getSalarySlips(CsvImportFinalResult result) {
        return result.getValidSalarySlips()
                .stream()
                .map(salarySlip -> {
                    List<EmployeeDTO> validEmployees = result.getValidEmployees();
                    return mapToSalarySlipExport(validEmployees, result.getValidSalaryComponents() ,salarySlip);
                }).toList();
    }

    @Override
    public List<CompanyExportDTO> getCompany(CsvImportFinalResult result) {
        Set<String> existingCompanies = new HashSet<>();

        Stream<String> employeeCompanies = result.getValidEmployees().stream()
                .map(e -> e.getCompany() != null ? e.getCompany().trim() : null);

        Stream<String> salaryCompanies = result.getValidSalaryComponents().stream()
                .map(e -> e.getCompany() != null ? e.getCompany().trim() : null);

        return Stream.concat(employeeCompanies, salaryCompanies)
                .filter(existingCompanies::add) // keep only unique companies
                .map(CompanyExportDTO::new)
                .toList();
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
        employee.setHolidayList("Jour Ferié");
        employee.setSalaryCurrency("EUR");

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

    private SalaryComponentExportDTO mapToSalaryComponentExport(SalaryComponentDTO dto){
        SalaryComponentExportDTO salaryComp = new SalaryComponentExportDTO();
        salaryComp.setName("");
        salaryComp.setAbbr(dto.getAbbr());
        salaryComp.setType(dto.getType());
        salaryComp.setAmountBasedOnFormula(1);
        salaryComp.setFormula(dto.getValeur());
        salaryComp.setDependsOnPaymentDays(0);
        salaryComp.setRemoveIfZeroValued(1);
        salaryComp.setIdAccounts("");
        salaryComp.setCompanyAccounts(dto.getCompany());
        salaryComp.setAccountAccounts("Cash-" + dto.getCompany());

        return salaryComp;
    }

    private SalaryStructureExportDTO mapToSalaryStructureExport(SalaryComponentDTO dto, List<SalaryComponentDTO> results){
        SalaryStructureExportDTO salaryStructureExp = new SalaryStructureExportDTO();

        salaryStructureExp.setId(dto.getSalaryStructure());
        salaryStructureExp.setCompany(dto.getCompany());
        salaryStructureExp.setIsActive("Yes");
        salaryStructureExp.setCurrency("EUR");
        salaryStructureExp.setPayrollFrequency("Monthly");
        salaryStructureExp.setSalaryComponent(dto.getName());
        salaryStructureExp.setModeOfPayment("Cash");
        salaryStructureExp.setPaymentAccount("Cash-" + dto.getCompany());

        if (dto.getType() != null && !dto.getType().trim().isBlank()){
            switch (dto.getType().toLowerCase()){
                case ("earning") -> {
                    List<SalaryComponentDTO> earningsType = findBySalaryStructureNameAndSalCompType(results, dto.getSalaryStructure(), "earning");
                    for (SalaryComponentDTO salComp : earningsType) {
                        salaryStructureExp.setIdEarnings("");
                        salaryStructureExp.setAbbrEarnings(salComp.getAbbr());
                        salaryStructureExp.setComponentEarnings(salComp.getName());
                        salaryStructureExp.setFormulaEarnings(salComp.getValeur());
                        salaryStructureExp.setAmountBasedOnFormulaEarnings(1);
                        salaryStructureExp.setDependsOnPaymentDaysEarnings(0);
                    }
                }
                case ("deductions") -> {
                    List<SalaryComponentDTO> deductionsType = findBySalaryStructureNameAndSalCompType(results, dto.getSalaryStructure(), "deduction");
                    for (SalaryComponentDTO salComp : deductionsType) {
                        salaryStructureExp.setIdDeductions("");
                        salaryStructureExp.setComponentDeductions(salComp.getName());
                        salaryStructureExp.setFormulaDeductions(salComp.getValeur());
                        salaryStructureExp.setAmountBasedOnFormulaDeductions(1);
                        salaryStructureExp.setAbbrDeductions(salComp.getAbbr());
                        salaryStructureExp.setDependsOnPaymentDaysDeductions(0);
                    }
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
        salaryStructureExp.setBase(slipDTO.getBaseSalary());


        return salaryStructureExp;
    }

    private SalarySlipExportDTO mapToSalarySlipExport(List<EmployeeDTO> employeeDTOS, List<SalaryComponentDTO> salaryComponentDTOS, SalarySlipDTO slipDTO){
        SalarySlipExportDTO salarySlipExp = new SalarySlipExportDTO();
        EmployeeDTO matchedEmployee = findEmployeByRef(employeeDTOS, slipDTO.getRefEmployee());
        String fullName = matchedEmployee.getLastName() != null ? matchedEmployee.getFirstName() + " " + matchedEmployee.getFirstName() : matchedEmployee.getFirstName();

        salarySlipExp.setId("");
        salarySlipExp.setEmployee("");
        salarySlipExp.setEmployeeName(fullName);
        salarySlipExp.setCompany(matchedEmployee.getCompany());
        salarySlipExp.setPostingDate(slipDTO.getMois());
/*        salarySlipExp.setStartDate(slipDTO.getMois());
        salarySlipExp.setEndDate(slipDTO.getMois().plusMonths(1));*/
        salarySlipExp.setModeOfPayment("Cash");
        salarySlipExp.setCurrency("EUR");
        salarySlipExp.setExchangeRate(0.0);
        salarySlipExp.setSalaryStructure(slipDTO.getSalary());
        salarySlipExp.setWorkingDays();
        salarySlipExp.setPaymentDays();
        salarySlipExp.setPayrollFrequency("Monthly");
        salarySlipExp.setStatus("Submitted");

        List<SalaryComponentDTO> earningsType = findBySalaryStructureNameAndSalCompType(salaryComponentDTOS, slipDTO.getSalary(), "earning");
        if (earningsType != null && !earningsType.isEmpty()) {
            for (SalaryComponentDTO salComp : earningsType) {
                salarySlipExp.setIdEarnings("");
                salarySlipExp.setComponentEarnings(salComp.getName());
            }
        }

        List<SalaryComponentDTO> deductionsType = findBySalaryStructureNameAndSalCompType(salaryComponentDTOS, slipDTO.getSalary(), "deduction");
        if (deductionsType != null && !deductionsType.isEmpty()) {
            for (SalaryComponentDTO salComp : deductionsType) {
                salarySlipExp.setIdDeductions("");
                salarySlipExp.setComponentDeductions(salComp.getName());
            }
        }

        return salarySlipExp;
    }



    //======= helper ===========================
    private EmployeeDTO findEmployeByRef(List<EmployeeDTO> employeeDTOS, String ref) {
        return employeeDTOS
                .stream()
                .filter(employee -> employee.getRef().equals(ref))
                .findFirst().orElseThrow();
    }
    private static List<SalaryComponentDTO> findBySalaryStructureNameAndSalCompType(List<SalaryComponentDTO> salaryComponentDTOList, String name, String type){
        return salaryComponentDTOList.stream()
                .filter(
                        salary -> salary.getSalaryStructure().equals(name) && salary.getType().equalsIgnoreCase(type)
                ).toList();
    }
}
