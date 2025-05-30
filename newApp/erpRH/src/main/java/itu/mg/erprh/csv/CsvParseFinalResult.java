package itu.mg.erprh.csv;


import itu.mg.erprh.csv.dto.RequestForQuotation;
import itu.mg.erprh.csv.dto.Supplier;
import itu.mg.erprh.csv.dto.SupplierQuotation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CsvParseFinalResult {
    CsvParseResult<Supplier> suppliers;
    CsvParseResult<RequestForQuotation> requestForQuotations;
    CsvParseResult<SupplierQuotation> supplierQuotations;

    public boolean isValid() {
        return (suppliers.isValid() && requestForQuotations.isValid() && supplierQuotations.isValid());
    }

    public List<Supplier> getValidSuppliers(){
        return suppliers.getValidRows();
    }

    public List<CsvErrorMessage> getSupplierErrors(){
        return this.suppliers.getErrors();
    }

    public List<CsvErrorMessage> getRequestForQuotationErrors(){
        return this.requestForQuotations.getErrors();
    }

    public List<CsvErrorMessage> getSupplierQuotationErrors(){
        return this.requestForQuotations.getErrors();
    }
}
