package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseOrder {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate transaction_date;

    @NotBlank
    private String company;
    @NotBlank
    private String status;

    @NotBlank
    private String item_name;

    @NotNull
    private double qty;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private BigDecimal amount;

    public String getColor(){
        if (this.status != null){
            switch (this.status) {
                case "Received" -> {
                    return "#bd3e0c";
                }
                case "Completed" -> {
                    return "#167d4f";
                }
            }
        }
        return "";
    }

}
