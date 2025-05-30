package itu.mg.erprh.csv.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.erprh.components.SessionManager;
import itu.mg.erprh.csv.CsvErrorMessage;
import itu.mg.erprh.csv.CsvParseFinalResult;
import itu.mg.erprh.csv.CsvParseResult;
import itu.mg.erprh.csv.dto.RequestForQuotation;
import itu.mg.erprh.csv.dto.Supplier;
import itu.mg.erprh.csv.dto.SupplierQuotation;
import itu.mg.erprh.csv.dto.export.SupplierExportDTO;
import itu.mg.erprh.dto.ApiResponse;
import itu.mg.erprh.dto.ImportDto;
import itu.mg.erprh.exception.FrappeApiException;
import itu.mg.erprh.models.error.FrappeApiErrorResponse;
import itu.mg.erprh.services.MainService;
import itu.mg.erprh.utils.RestClientExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImportCsv extends MainService {
    private final ExportCsvService exportCsvService;
    public static final Logger logger = LoggerFactory.getLogger(ImportCsv.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ImportCsv(RestTemplateBuilder restTemplate, SessionManager sessionManager, ExportCsvService exportCsvService) {
        super(restTemplate, sessionManager);
        this.exportCsvService = exportCsvService;
    }

    public ApiResponse deleteData(){
        String url = String.format("%s/api/method/importapp.api.import.delete_all_data", this.getErpNextUrl());

        HttpHeaders headers = this.getHeaders();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse> response = null;
        try {
            response = this.getRestTemplate()
                    .exchange(url, HttpMethod.GET, entity, ApiResponse.class);
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
            RestClientExceptionHandler.handleError(e);
        }

        return response.getBody();
    }
    public CsvParseFinalResult importation(ImportDto importDto)  {
        CsvParseResult<Supplier> suppliers = null;
        try {
            suppliers = csvParser(importDto.getSupplierFile(), Supplier.class);
            exportCsvService.exportSupplierToCsv(prepareData(suppliers.getValidRows()));
            submit();
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        }

        CsvParseResult<RequestForQuotation> requestForQuotations = csvParser(importDto.getRequestForQuotation(), RequestForQuotation.class);
        CsvParseResult<SupplierQuotation> supplierQuotations = csvParser(importDto.getSupplierQuotation(), SupplierQuotation.class);

        return new CsvParseFinalResult(suppliers, requestForQuotations, supplierQuotations);
    }
    private <T> CsvParseResult<T> csvParser(MultipartFile file, Class<T> model) {
        List<T> validRows = new ArrayList<>();
        List<CsvErrorMessage> errors = new ArrayList<>();
        String fileName = file.getResource().getFilename();

        try (InputStream inputStream = file.getInputStream();
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(model)
                    .withThrowExceptions(false)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            validRows = csvToBean.parse();

            List<CsvException> capturedExceptions = csvToBean.getCapturedExceptions();

            for (CsvException capturedException : capturedExceptions) {
                String field = "unknown";

                Throwable cause  = capturedException.getCause();
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

        return new CsvParseResult<>(validRows, errors);
    }
    private List<SupplierExportDTO> prepareData(List<Supplier> suppliers){
        List<SupplierExportDTO> results = new ArrayList<>();

        for (Supplier supplier : suppliers) {
            SupplierExportDTO temporary = new SupplierExportDTO(
                    supplier.getSupplierName(),
                    supplier.getCountry(),
                    supplier.getType()
            );

            results.add(temporary);
        }

        return results;
    }

    public boolean submit() {
        String url = String.format("%s/api/method/importapp.api.import.import_csv_file", this.getErpNextUrl());

        HttpHeaders headers = this.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> data = new HashMap<>();

        data.put("file", exportCsvService.getFileName());
        data.put("doctype", "supplier");

        HttpEntity<Map> entity = new HttpEntity<>(data, headers);

        ResponseEntity<Map> response = null;
        try {
            response = this.getRestTemplate()
                    .exchange(url, HttpMethod.GET, entity, Map.class);
             if(response.getStatusCode().is2xxSuccessful()) {
                 Map<String, Object> responseBody = response.getBody();
                 return true;
             }
        } catch (RestClientException e) {
            RestClientExceptionHandler.handleError(e);
        }
        return false;
    }
}
