package app.controller;

import app.domain.model.Company;
import app.domain.stores.ClientStore;
import app.domain.stores.TestStore;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CheckPerformanceController {
    private final Company company;
    private final TestStore tStore;
    private final ClientStore cStore;

    public CheckPerformanceController() {
        this(App.getInstance().getCompany());
    }

    public CheckPerformanceController(Company company) {
        this.company = company;
        this.tStore = company.getTestList();
        this.cStore = company.getClientList();
    }

    public int[] getSubArray(LocalDate beg, LocalDate end) {

        return null;
    }


}
