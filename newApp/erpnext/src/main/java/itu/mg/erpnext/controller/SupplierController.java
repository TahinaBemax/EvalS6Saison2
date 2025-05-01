package itu.mg.erpnext.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/suppliers")
public class SupplierController extends MainController{

    @GetMapping
    public String chooseSupplierPage(){
        return "fournisseur/choose-fournisseur";
    }
}
