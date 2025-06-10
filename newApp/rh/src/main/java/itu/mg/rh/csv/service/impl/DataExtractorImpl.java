package itu.mg.rh.csv.service.impl;

import itu.mg.rh.csv.CsvImportFinalResult;
import itu.mg.rh.csv.dto.EmployeeDTO;
import itu.mg.rh.csv.dto.ExtractedData;
import itu.mg.rh.csv.dto.SalaryComponentDTO;
import itu.mg.rh.csv.dto.SalarySlipDTO;
import itu.mg.rh.csv.dto.export.*;
import itu.mg.rh.csv.service.DataExtractor;
import itu.mg.rh.services.helper.EmployeeIDGenerator;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class DataExtractorImpl implements DataExtractor {
    EmployeeIDGenerator idGenerator = new EmployeeIDGenerator();

    @Override
    public List<EmployeeExportDTO> getEmployees(CsvImportFinalResult result) {
        return result.getValidEmployees().stream().map(this::mapToEmployee).toList();
    }
    @Override
    public List<SalaryComponentExportDTO> getSalaryComponent(CsvImportFinalResult result) {
        Set<String> existingComponentName = new HashSet<>();
        List<SalaryComponentExportDTO> list = result.getValidSalaryComponents()
                .stream()
                .map(c -> {
                    if (!existingComponentName.contains(c.getName())) {
                        existingComponentName.add(c.getName());
                        return mapToSalaryComponentExport(c);
                    }
                    return null;
                })
                .toList();

        return list.stream().filter(Objects::nonNull).toList();
    }
    @Override
    public List<SalaryStructureExportDTO> getSalaryStructures(CsvImportFinalResult result) {
        List<SalaryStructureExportDTO> structures = new ArrayList<>();
        Set<String> existingSalaryNames = new HashSet<>();

        for (SalaryComponentDTO salComp : result.getValidSalaryComponents()) {
            SalaryStructureExportDTO baseStructure = mapToSalaryStructureExportDTO(salComp);
            structures.add(baseStructure);
        }

        return structures;
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
                    return mapToSalarySlipExport(validEmployees, result.getValidSalaryComponents(), salarySlip);
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

    @Override
    public ExtractedData getExtractedData(CsvImportFinalResult csvImportFinalResult) {
        List<CompanyExportDTO> company = this.getCompany(csvImportFinalResult);
        List<EmployeeExportDTO> employees = this.getEmployees(csvImportFinalResult);
        List<SalaryComponentExportDTO> salaryComponent = this.getSalaryComponent(csvImportFinalResult);
        List<SalaryStructureExportDTO> salaryStructures = this.getSalaryStructures(csvImportFinalResult);
        List<SalaryStructureAssignmentExportDTO> salaryAssigments = this.getSalaryAssigments(csvImportFinalResult);
        List<SalarySlipExportDTO> salarySlips = this.getSalarySlips(csvImportFinalResult);

        return new ExtractedData(company, employees, salaryComponent, salaryStructures, salaryAssigments, salarySlips);
    }


    private EmployeeExportDTO mapToEmployee(EmployeeDTO dto) {
        EmployeeExportDTO employee = new EmployeeExportDTO();
        employee.setId(idGenerator.generateEmployeeId());
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
    private SalaryComponentExportDTO mapToSalaryComponentExport(SalaryComponentDTO dto) {
        SalaryComponentExportDTO salaryComp = new SalaryComponentExportDTO();
        salaryComp.setName(dto.getName());
        salaryComp.setAbbr(dto.getAbbr());
        salaryComp.setType(dto.getType());
        salaryComp.setAmountBasedOnFormula(1);
        salaryComp.setFormula(dto.getValeur());
        salaryComp.setDependsOnPaymentDays(0);
        salaryComp.setRemoveIfZeroValued(1);

        salaryComp.setIdAccounts("");
        salaryComp.setCompanyAccounts(dto.getCompany());
        salaryComp.setAccountAccounts("Cash - " + CompanyExportDTO.getAbbr(dto.getCompany()));

        return salaryComp;
    }
    private SalaryStructureExportDTO mapToSalaryStructureExportDTO(SalaryComponentDTO salComp) {
        SalaryStructureExportDTO salaryStructure = new SalaryStructureExportDTO();

        salaryStructure.setId(salComp.getSalaryStructure());
        salaryStructure.setCompany(salComp.getCompany());
        salaryStructure.setIsActive("Yes");
        salaryStructure.setCurrency("EUR");
        salaryStructure.setPayrollFrequency("Monthly");
        salaryStructure.setSalaryComponent(salComp.getName());
        salaryStructure.setModeOfPayment("Cash");
        salaryStructure.setPaymentAccount("Cash - " + CompanyExportDTO.getAbbr(salComp.getCompany()));

        if (salComp.getType().equalsIgnoreCase("earning")){
            salaryStructure.setIdEarnings("");
            salaryStructure.setComponentEarnings(salComp.getName());
            salaryStructure.setFormulaEarnings(salComp.getValeur());
            salaryStructure.setAmountBasedOnFormulaEarnings(1);
            salaryStructure.setAbbrEarnings(salComp.getAbbr());
            salaryStructure.setDependsOnPaymentDaysEarnings(0);
            salaryStructure.setIsTaxApplicableEarnings(1);
        } else if (salComp.getType().equalsIgnoreCase("deduction")) {
            salaryStructure.setIdDeductions("");
            salaryStructure.setComponentDeductions(salComp.getName());
            salaryStructure.setFormulaDeductions(salComp.getValeur());
            salaryStructure.setAmountBasedOnFormulaDeductions(1);
            salaryStructure.setAbbrDeductions(salComp.getAbbr());
            salaryStructure.setDependsOnPaymentDaysDeductions(0);
            salaryStructure.setIsTaxApplicableDeductions(0);
        }

        return salaryStructure;
    }
    private SalaryStructureAssignmentExportDTO mapToSalaryStructureAssignmentExport(List<EmployeeDTO> employeeDTOS, SalarySlipDTO slipDTO) {
        SalaryStructureAssignmentExportDTO salaryStructureExp = new SalaryStructureAssignmentExportDTO();
        EmployeeDTO matchedEmployee = findEmployeByRef(employeeDTOS, slipDTO.getRefEmployee());

        if (matchedEmployee.getHireDate().isAfter(slipDTO.getMois())){
            throw new RuntimeException(String.format("From Date %s cannot be before employee's joining Date %s", slipDTO.getMois(), matchedEmployee.getHireDate()));
        }

        String fullName = matchedEmployee.getLastName() != null ? matchedEmployee.getFirstName() + " " + matchedEmployee.getLastName() : matchedEmployee.getFirstName();

        String abbr = CompanyExportDTO.getAbbr(matchedEmployee.getCompany());

        salaryStructureExp.setId("");
        salaryStructureExp.setEmployee("");
        salaryStructureExp.setEmployeeName(fullName);
        salaryStructureExp.setCompany(matchedEmployee.getCompany());
        salaryStructureExp.setCurrency("EUR");
        salaryStructureExp.setSalaryStructure(slipDTO.getSalary());
        salaryStructureExp.setFromDate(slipDTO.getMois());
        salaryStructureExp.setPayrollPayableAccount("Payroll Payable - " + abbr);
        salaryStructureExp.setBase(slipDTO.getBaseSalary());


        return salaryStructureExp;
    }
    private SalarySlipExportDTO mapToSalarySlipExport(List<EmployeeDTO> employeeDTOS, List<SalaryComponentDTO> salaryComponentDTOS, SalarySlipDTO slipDTO) {
        SalarySlipExportDTO salarySlipExp = new SalarySlipExportDTO();
        EmployeeDTO matchedEmployee = findEmployeByRef(employeeDTOS, slipDTO.getRefEmployee());
        String fullName = matchedEmployee.getLastName() != null ? matchedEmployee.getFirstName() + " " + matchedEmployee.getLastName() : matchedEmployee.getFirstName();

        salarySlipExp.setId("");
        salarySlipExp.setEmployee("");
        salarySlipExp.setEmployeeName(fullName);
        salarySlipExp.setCompany(matchedEmployee.getCompany());
        salarySlipExp.setPostingDate(slipDTO.getMois());
        salarySlipExp.setStartDate(slipDTO.getMois());
        salarySlipExp.setEndDate(slipDTO.getMois().plusMonths(1));
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

    private String employeeGenderHandler(String gender) {
        if (gender == null || gender.trim().isBlank())
            throw new RuntimeException("Gender is Null!");

        String genderLowercase = gender.toLowerCase();

        if (gender.equalsIgnoreCase("masculin")
                || gender.equalsIgnoreCase("male")
                || gender.equalsIgnoreCase("Homme"))
        {
            return "Male";
        } else if (gender.equalsIgnoreCase("feminin")
                || gender.equalsIgnoreCase("féminin")
                || gender.equalsIgnoreCase("female")
                || gender.equalsIgnoreCase("Femme"))
        {
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
    private static List<SalaryComponentDTO> findBySalaryStructureNameAndSalCompType(List<SalaryComponentDTO> salaryComponentDTOList, String name, String type) {
        return salaryComponentDTOList.stream()
                .filter(
                        salary -> salary.getSalaryStructure().equals(name) && salary.getType().equalsIgnoreCase(type)
                ).toList();
    }
}
