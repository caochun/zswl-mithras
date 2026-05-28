package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardFundRepayDateResult {

    private Long financingId;
    private LocalDate repayDate;
    private Long loanAmount;
    private Long comprehensiveInterestRate;


}
