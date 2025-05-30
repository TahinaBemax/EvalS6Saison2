package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SupplierQuotationItem {
    @NotBlank
    private String name;
    @NotBlank
    private String item_code;
    @NotBlank
    private String item_name;
    @NotNull
    private double qty;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private BigDecimal amount;
}
