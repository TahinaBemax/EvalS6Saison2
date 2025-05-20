package itu.mg.erpnext.dto;

import itu.mg.erpnext.models.ItemGroup;
import lombok.Data;

import java.util.List;

@Data
public class ItemGroupResponse {
    List<ItemGroup> data;
}
