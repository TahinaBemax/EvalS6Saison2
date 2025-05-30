package itu.mg.erprh.csv.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import itu.mg.erprh.csv.customValidator.LocalDateConverter;
import itu.mg.erprh.csv.customValidator.Required;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestForQuotation {
    @CsvCustomBindByName(column = "date", converter = LocalDateConverter.class)
    @CsvDate
    LocalDate date;

    @CsvBindByName(column = "item_name", required = true)
    String itemName;

    @CsvBindByName(column = "item_groupe", required = true)
    String itemGroup;

    @CsvCustomBindByName(column = "required_by", required = true, converter = LocalDateConverter.class)
    @CsvDate
    LocalDate requiredBy;

    @CsvBindByName(column = "quantity")
    double qty;

    @CsvCustomBindByName(column = "purpose", converter = Required.class, required = true)
    String purpose;

    @CsvBindByName(column = "target_warehouse", required = true)
    String targetWarehouse;

    @CsvBindByName(column = "ref", required = true)
    String ref;
}
