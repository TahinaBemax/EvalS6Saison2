package itu.mg.erprh.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import itu.mg.erprh.csv.customValidator.Required;
import itu.mg.erprh.csv.customValidator.UniqueSupplierName;
import lombok.Data;

@Data
public class Supplier {
    @CsvCustomBindByName(column = "supplier_name", converter = UniqueSupplierName.class, required = true)
    String supplierName;

    @CsvCustomBindByName(column = "country", converter = Required.class, required = true)
    String country;

    @CsvBindByName(column = "type", required = true)
    String type;
}
