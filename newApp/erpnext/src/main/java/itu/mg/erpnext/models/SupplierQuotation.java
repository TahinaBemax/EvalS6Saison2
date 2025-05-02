package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SupplierQuotation {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate transaction_date;

    @NotBlank
    private String company;

    @NotBlank
    private String supplier;

    @NotBlank
    private String supplier_name;

    @NotBlank
    private double total_qty;

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
