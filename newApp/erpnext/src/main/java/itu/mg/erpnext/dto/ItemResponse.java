package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.Item;
import lombok.Data;

import java.util.List;

@Data
public class ItemResponse {
    List<Item> data;
}
