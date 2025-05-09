package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SupplierQuotationDTO {
    @NotBlank
    private String supplier_name;


    private List<Item> items;
}
