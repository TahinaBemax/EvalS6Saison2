package itu.mg.rh.models;

import java.time.LocalTime;

public class ShiftType {
    String shiftTypeName;
    LocalTime startTime;
    LocalTime endTime;
    LocalTime lateEntryGraceTime;
    LocalTime earlyExitGraceTime;
    int workingHoursThreshold;
}
