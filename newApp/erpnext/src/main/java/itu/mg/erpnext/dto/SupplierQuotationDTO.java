package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class SupplierQuotationDTO {
    @NotBlank
    private String supplier;
    @NotNull
    private String series;
    @NotNull
    private LocalDate transaction_date;

    private LocalDate valid_till;
    @NotNull
    private List<Item> items;

    public SupplierQuotationDTO() {
        this.series = series = "PUR-SQTN-.YYYY";
        this.transaction_date = LocalDate.now();
    }
}
