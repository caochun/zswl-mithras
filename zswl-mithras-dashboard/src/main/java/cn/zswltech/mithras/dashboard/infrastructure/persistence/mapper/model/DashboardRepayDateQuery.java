package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;

import java.time.LocalDate;


@Data
public class DashboardRepayDateQuery {
    private LocalDate dateForm;
    private LocalDate dateTo;
    private String queryType;
}
