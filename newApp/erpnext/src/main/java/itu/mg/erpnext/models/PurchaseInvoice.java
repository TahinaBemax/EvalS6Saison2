package itu.mg.erpnext.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseInvoice {
    @NotNull
    private String name;
    @NotNull
    private String supplier;
    @NotNull
    private LocalDate bill_date;
    @NotBlank
    private String company;
    @NotBlank
    private String status;
    @NotNull
    private BigDecimal grand_total;

    private String credit_to;
    private String outstanding_amount;

    public String getColor(){
        if (this.status != null){
            switch (this.status) {
                case "Overdue" -> {
                    return "#bd3e0c";
                }
                case "Paid" -> {
                    return "#167d4f";
                }
            }
        }
        return "";
    }

}
