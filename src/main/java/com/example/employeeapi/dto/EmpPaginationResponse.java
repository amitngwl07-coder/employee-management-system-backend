package com.example.employeeapi.dto;

import java.util.List;


public class EmpPaginationResponse {
    private List<EmployeeResponse> listResult;
    private long totalResult;

    public EmpPaginationResponse(List<EmployeeResponse> listResult, long totalResult) {
        this.listResult = listResult;
        this.totalResult = totalResult;
    }

    public List<EmployeeResponse> getListResult() {
        return listResult;
    }

    public void setListResult(List<EmployeeResponse> listResult) {
        this.listResult = listResult;
    }

    public long getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(long totalResult) {
        this.totalResult = totalResult;
    }
}
