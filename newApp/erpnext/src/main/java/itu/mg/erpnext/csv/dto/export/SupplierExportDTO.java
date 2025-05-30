package itu.mg.erpnext.csv.dto.export;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import itu.mg.erpnext.csv.customValidator.Required;
import lombok.Data;

@Data
public class SupplierExportDTO {
    @CsvBindByName(column = "id", required = true)
    String id;

    @CsvBindByName(column = "supplier_name", required = true)
    String supplierName;

    @CsvCustomBindByName(column = "country", converter = Required.class, required = true)
    String country;

    @CsvBindByName(column = "supplier_type", required = true)
    String type;

    public SupplierExportDTO() {
        this.id = "";
    }

    public SupplierExportDTO(String supplierName, String country, String type) {
        this.id = "";
        this.supplierName = supplierName;
        this.country = country;
        this.type = type;
    }
}
