package itu.mg.erprh.models;

import lombok.Data;

@Data
public class LeaveType {
    String leaveTypeName;
    int maxLeavesAllowed;
    boolean includeHolidaysInLeave;
}
