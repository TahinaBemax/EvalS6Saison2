package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Item {
    private String name;
    @NotNull
    private String item_name;
    @NotNull
    private String item_code;
    @NotNull
    private String item_group;
    @NotBlank
    private String stock_uom;
    private String warehouse;
    private double qty;
    private BigDecimal rate;
    private BigDecimal amount;
}
