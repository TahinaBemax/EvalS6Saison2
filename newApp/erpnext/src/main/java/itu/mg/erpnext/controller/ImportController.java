package itu.mg.erpnext.controller;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import itu.mg.erpnext.csv.CsvParseFinalResult;
import itu.mg.erpnext.dto.ImportDto;
import itu.mg.erpnext.csv.service.ImportCsv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
