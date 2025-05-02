package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Quotation {
    @NotBlank
    private String name;
    @NotNull
    private LocalDateTime transaction_date;
    @NotBlank
    private String company;
    @NotBlank
    private String customer_name;
    @NotBlank
    private String order_type;
    @NotNull
    private BigDecimal total_quantity;
    @NotNull
    private BigDecimal base_total;
}
