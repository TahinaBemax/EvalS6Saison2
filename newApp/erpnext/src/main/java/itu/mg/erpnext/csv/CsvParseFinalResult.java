package itu.mg.erpnext.csv;

import itu.mg.erpnext.csv.dto.RequestForQuotation;
import itu.mg.erpnext.csv.dto.Supplier;
import itu.mg.erpnext.csv.dto.SupplierQuotation;
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
