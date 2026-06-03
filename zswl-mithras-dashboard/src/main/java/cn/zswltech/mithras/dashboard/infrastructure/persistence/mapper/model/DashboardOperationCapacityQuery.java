package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Data
public class DashboardOperationCapacityQuery {
    private String projStage;
    private LocalDate queryDateFrom;
    private LocalDate queryDateTo;
    private LocalDate queryDateLastFrom;
    private LocalDate queryDateLastTo;
    private List<Long> bizDeptIdList;
    private List<String> riskControlList;
    private String type;



}
