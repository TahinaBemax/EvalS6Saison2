package itu.mg.rh.services.helper;

import java.util.concurrent.atomic.AtomicInteger;

public class EmployeeIDGenerator {
    private final AtomicInteger counter;

    public EmployeeIDGenerator() {
        this.counter = new AtomicInteger();
    }

    public String generateEmployeeId(){
        int currentNumber = counter.incrementAndGet();
        return String.format("HR-EMP-%05d", currentNumber);
    }
}
