package cn.zswltech.mithras.dashboard.model;

import lombok.Data;

import java.time.LocalDate;


@Data
public class DashboardRepayDateQuery {
    private LocalDate dateForm;
    private LocalDate dateTo;
    private String queryType;
}
