package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {
    private String name;
    private String item_name;
    @NotNull
    private String item_code;
    @NotBlank
    private String uom;
    @NotBlank
    private String warehouse;
    @NotNull
    private double qty;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private BigDecimal amount;
}
