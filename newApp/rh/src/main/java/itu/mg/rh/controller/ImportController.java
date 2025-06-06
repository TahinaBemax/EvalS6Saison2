package itu.mg.rh.controller;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.rh.csv.CsvImportFinalResult;
import itu.mg.rh.csv.service.ImportCsv;
import itu.mg.rh.dto.ApiResponse;
import itu.mg.rh.dto.ImportDto;
import itu.mg.rh.exception.FrappeApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("")
public class ImportController {
    private final ImportCsv importCsv;

    @Autowired
    public ImportController(ImportCsv importCsv) {
        this.importCsv = importCsv;
    }

    @GetMapping("/import")
    public String importPage(Model model){
        model.addAttribute("importDto", new ImportDto());
        return "import/index";
    }

    @PostMapping("/delete-data")
    @ResponseBody
    public ResponseEntity<?> deleteData(){
        try {
            ApiResponse<?> apiResponse = importCsv.deleteData();

            if (apiResponse.getStatus().equals("success")) {
                return ResponseEntity.ok(apiResponse);
            }

            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            ApiResponse response = new ApiResponse<>(null, e.getMessage(), "error", null);

            if (cause instanceof RestClientException){
                RestClientException ex = (RestClientException) cause;
                response = ApiResponse.parseJsonErrorToApiResponse(ex);
            }

            return ResponseEntity
                    .status(417)
                    .body(response);
        }
    }

    @PostMapping
    public String save(@Validated ImportDto importDto, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        if (bindingResult.hasErrors()) {
            return importPage(model);
        }

        CsvImportFinalResult result = importCsv.dataImport(importDto);

        if (result.isValid()){
            redirectAttributes.addFlashAttribute("success", "Imported successfuly");
        } else {
            redirectAttributes.addFlashAttribute("hasErrors", true);
        }

        redirectAttributes.addFlashAttribute("results", result);
        return "redirect:/import";
    }
}
