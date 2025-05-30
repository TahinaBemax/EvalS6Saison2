package itu.mg.erprh.controller;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.erprh.csv.CsvParseFinalResult;
import itu.mg.erprh.csv.service.ImportCsv;
import itu.mg.erprh.dto.ApiResponse;
import itu.mg.erprh.dto.ImportDto;
import itu.mg.erprh.exception.FrappeApiException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class ImportController {
    private final ImportCsv importCsv;

    @Autowired
    public ImportController(ImportCsv importCsv) {
        this.importCsv = importCsv;
    }

    @GetMapping
    public String importPage(Model model){
        model.addAttribute("importDto", new ImportDto());
        return "import/index";
    }

    @PostMapping("delete-data")
    @ResponseBody
    public ResponseEntity<?> deleteData(){
        try {
            ApiResponse<?> apiResponse = importCsv.deleteData();

            if (apiResponse.getStatus().equals("success")) {
                return ResponseEntity.ok(apiResponse);
            }

            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        } catch (FrappeApiException e) {
            return ResponseEntity
                    .status(417)
                    .body(e.getErrorResponse().getServerMessages());
        }
    }

    @PostMapping
    public String save(@Validated ImportDto importDto, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Model model) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        if (bindingResult.hasErrors()) {
            return importPage(model);
        }

        CsvParseFinalResult result = importCsv.importation(importDto);

        if (result.isValid()){
            redirectAttributes.addFlashAttribute("success", "Imported successfuly");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("results", result);
        return "redirect:/";

    }
}
