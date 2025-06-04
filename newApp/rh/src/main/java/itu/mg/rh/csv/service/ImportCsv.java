package itu.mg.rh.csv.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.rh.components.SessionManager;
import itu.mg.rh.csv.CsvErrorMessage;
import itu.mg.rh.csv.CsvImportFinalResult;
import itu.mg.rh.csv.CsvParseResult;
import itu.mg.rh.csv.dto.CsvValidationResultDTO;
import itu.mg.rh.csv.dto.EmployeeDTO;
import itu.mg.rh.csv.dto.SalaryComponentDTO;
import itu.mg.rh.csv.dto.SalarySlipDTO;
import itu.mg.rh.csv.dto.export.*;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.dto.ImportDto;
import itu.mg.rh.services.MainService;
import itu.mg.rh.services.SalaryStructureAssignmentService;
import itu.mg.rh.services.SalaryStructureService;
import itu.mg.rh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImportCsv {
    private final String companyFileName = "company.csv";
    private final String employeeFileName = "employee.csv";
    private final String salaryComponentFileName = "salary_component.csv";
    private final String salaryStructureFileName = "salary_structure.csv";
    private final String salaryStructureAssignmentFileName = "salary_structure_assignment.csv";
    private final String salarySlipFileName = "salary_slip.csv";
    private final ExportCsvService exportCsvService;
    private final DataExtractor dataExtractor;
    private final MainService mainService;
    private final SalaryStructureService salaryStructureService;
    private final SalaryStructureAssignmentService salaryStructureAssignmentService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ImportCsv(ExportCsvService exportCsvService
            , DataExtractor dataExtractor, MainService mainService,
                     SalaryStructureService salaryStructureService,
                     SalaryStructureAssignmentService salaryStructureAssignmentService) {
        this.exportCsvService = exportCsvService;
        this.dataExtractor = dataExtractor;
        this.mainService = mainService;
        this.salaryStructureService = salaryStructureService;
        this.salaryStructureAssignmentService = salaryStructureAssignmentService;
    }

    public ApiResponse deleteData() {
        String url = String.format("%s/api/method/importapp.api.import.delete_all_data", this.mainService.getErpNextUrl());

        HttpHeaders headers = this.mainService.getHeaders();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse> response = null;
        try {
            response = this.mainService.getRestTemplate()
                    .exchange(url, HttpMethod.GET, entity, ApiResponse.class);
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        return response.getBody();
    }

    public CsvImportFinalResult dataImport(ImportDto importDto) {
        CsvImportFinalResult csvImportFinalResult = csvFileReader(importDto);

        if (csvImportFinalResult.isValid()) {
            List<CompanyExportDTO> company = dataExtractor.getCompany(csvImportFinalResult);
            List<EmployeeExportDTO> employees = dataExtractor.getEmployees(csvImportFinalResult);
            List<SalaryComponentExportDTO> salaryComponent = dataExtractor.getSalaryComponent(csvImportFinalResult);
            List<SalaryStructureExportDTO> salaryStructures = dataExtractor.getSalaryStructures(csvImportFinalResult);

            List<SalaryStructureAssignmentExportDTO> salaryAssigments = dataExtractor.getSalaryAssigments(csvImportFinalResult);
            setSalaryStructureAssignmentEmployeeID(employees, salaryAssigments);

            List<SalarySlipExportDTO> salarySlips = dataExtractor.getSalarySlips(csvImportFinalResult);
            setSalarySlipEmployeeID(employees, salarySlips);
            try {
                convertToFrappeImportCsvModel(company, companyFileName);
                convertToFrappeImportCsvModel(employees, employeeFileName);
                convertToFrappeImportCsvModel(salaryComponent, salaryComponentFileName);
                convertToFrappeImportCsvModel(salaryStructures, salaryStructureFileName);
                convertToFrappeImportCsvModel(salaryAssigments, salaryStructureAssignmentFileName);
                convertToFrappeImportCsvModel(salarySlips, salarySlipFileName);
            } catch (Exception e) {
                csvImportFinalResult.setErrorLevel("Converting to frappe data import csv model!");
                csvImportFinalResult.setErrorGlobal(List.of(e.getMessage()));
                logger.error(e.getLocalizedMessage());
            }

            try {
                Map firstImport = prepareFirstRoundFormData();
                if (submit(firstImport)){
                    salaryStructureService.insertSalaryStructures(salaryStructures);
                    //this.salaryStructureAssignmentService.saveSalaryStructureAssignment(salaryAssigments);
                    Map second = prepareSecondRoundFormData();
                    submit(second);
                }
            } catch (RuntimeException e) {
                csvImportFinalResult.setErrorLevel("Importing from frappe data import!");
                logger.error(e.getLocalizedMessage());
                Throwable cause = e.getCause();

                if (cause instanceof RestClientException){
                    RestClientException ex = (RestClientException) cause;
                    ApiResponse response = ApiResponse.parseJsonErrorToApiResponse(ex);
                    csvImportFinalResult.setErrorGlobal(List.of(response.getMessage()));
                } else {
                    csvImportFinalResult.setErrorGlobal(List.of(e.getMessage()));
                }
            }
        }

        return csvImportFinalResult;
    }

    private void setSalaryStructureAssignmentEmployeeID(List<EmployeeExportDTO> employees, List<SalaryStructureAssignmentExportDTO> dtos){
        for (SalaryStructureAssignmentExportDTO dto : dtos) {
            for (EmployeeExportDTO employee : employees) {
                if (dto.getEmployeeName().equals(employee.getFullName())){
                    dto.setEmployee(employee.getId());
                    break;
                }
            }
        }
    }

    private void setSalarySlipEmployeeID(List<EmployeeExportDTO> employees, List<SalarySlipExportDTO> dtos){
        for (SalarySlipExportDTO dto : dtos) {
            for (EmployeeExportDTO employee : employees) {
                if (dto.getEmployeeName().equals(employee.getFullName())){
                    dto.setEmployee(employee.getId());
                    break;
                }
            }
        }
    }
    private <T> Object convertToFrappeImportCsvModel(List<T> data, String fileName) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        return exportCsvService.beanToCsv(data, fileName);
    }

    private CsvImportFinalResult csvFileReader(ImportDto importDto) {
        CsvParseResult<EmployeeDTO> employees = null;
        CsvParseResult<SalaryComponentDTO> salaryComponents = null;
        CsvParseResult<SalarySlipDTO> salarySlips = null;

        employees = csvParser(importDto.getEmployeeFile(), EmployeeDTO.class);
        salaryComponents = csvParser(importDto.getSalaryComponentFile(), SalaryComponentDTO.class);
        salarySlips = csvParser(importDto.getSalarySlipFile(), SalarySlipDTO.class);

        return new CsvImportFinalResult(employees, salaryComponents, salarySlips);
    }

    private <T> CsvParseResult<T> csvParser(MultipartFile file, Class<T> model) {
        List<T> validRows = new ArrayList<>();
        List<CsvErrorMessage> errors = new ArrayList<>();
        String fileName = file.getResource().getFilename();
        List<String> originalLines = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            // = this.getOriginalLines(reader);

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(model)
                    .withThrowExceptions(false)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            validRows = csvToBean.parse();

            List<CsvException> capturedExceptions = csvToBean.getCapturedExceptions();

            for (CsvException capturedException : capturedExceptions) {
                String field = "unknown";

                Throwable cause = capturedException.getCause();
                String message = cause != null ? cause.getMessage() : capturedException.getMessage();

                CsvErrorMessage error = new CsvErrorMessage(
                        fileName,
                        field,
                        message,
                        capturedException.getLineNumber()
                );

                errors.add(error);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            String message = cause != null ? cause.getMessage() : e.getMessage();

            CsvErrorMessage error = new CsvErrorMessage(
                    fileName,
                    "unknown",
                    message,
                    null
            );
            errors.add(error);
        }

        //List<CsvValidationResultDTO> csvValidationResultDTOList = getValidationResults(originalLines, errors);
        return new CsvParseResult<>(validRows, errors);
    }

    public boolean submit(Map data) {
        String url = String.format("%s/api/method/importapp.api.import.import_multiple_csv_files", this.mainService.getErpNextUrl());

        HttpHeaders headers = this.mainService.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map> entity = new HttpEntity<>(data, headers);

        ResponseEntity<Map> response = null;
        try {
            response = this.mainService.getRestTemplate()
                    .exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                return true;
            }
        } catch (RestClientException e) {
            throw e;
        }
        return false;
    }

    private Map<String, Object> prepareFirstRoundFormData(){
        Map<String, Object> data = new HashMap<>();
        List<Map<String, String>> files = new ArrayList<>();

        Map<String, String> Company = new HashMap<>();
        Company.put("file", companyFileName);
        Company.put("doctype", "Company");

        Map<String, String> employee = new HashMap<>();
        employee.put("file", employeeFileName);
        employee.put("doctype", "Employee");

        Map<String, String> salaryComponent = new HashMap<>();
        salaryComponent.put("file", salaryComponentFileName);
        salaryComponent.put("doctype", "Salary Component");

/*
        Map<String, String> salaryStructure = new HashMap<>();
        salaryStructure.put("file", salaryStructureFileName);
        salaryStructure.put("doctype", "Salary Structure");
*/

        files.add(Company);
        files.add(employee);
        files.add(salaryComponent);
        //files.add(salaryStructure);
        data.put("files", files);
        
        return data;
    }

    private Map<String, Object> prepareSecondRoundFormData(){
        Map<String, Object> data = new HashMap<>();
        List<Map<String, String>> files = new ArrayList<>();

        Map<String, String> salaryStructureAssigment = new HashMap<>();
        salaryStructureAssigment.put("file", salaryStructureAssignmentFileName);
        salaryStructureAssigment.put("doctype", "Salary Structure Assignment");

        Map<String, String> salarySlip = new HashMap<>();
        salarySlip.put("file", salarySlipFileName);
        salarySlip.put("doctype", "Salary Slip");

        files.add(salaryStructureAssigment);
        files.add(salarySlip);

        data.put("files", files);

        return data;
    }

    private List<String> getOriginalLines(Reader reader) throws IOException {
        List<String> originalLines = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {

            // Read header
            String header = bufferedReader.readLine();
            originalLines.add(header);

            // Read and store all lines
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                originalLines.add(line);
            }
        }

        reader.reset();
        return originalLines;
    }

    private List<CsvValidationResultDTO> getValidationResults(List<String> originalLines, List<CsvErrorMessage> errors) {
        List<CsvValidationResultDTO> results = new ArrayList<>();

        // Add header
        results.add(new CsvValidationResultDTO(originalLines.get(0), true, null, 1));

        // Process data lines
        for (int i = 1; i < originalLines.size(); i++) {
            String line = originalLines.get(i);
            boolean isValid = true;
            String errorMessage = null;

            // Check if this line has an error
            for (CsvErrorMessage error : errors) {
                if (error.getLine() != null && (Integer) error.getLine() == i + 1) {
                    isValid = false;
                    errorMessage = error.getMessage();
                    break;
                }
            }

            results.add(new CsvValidationResultDTO(line, isValid, errorMessage, i + 1));
        }

        return results;
    }
}
