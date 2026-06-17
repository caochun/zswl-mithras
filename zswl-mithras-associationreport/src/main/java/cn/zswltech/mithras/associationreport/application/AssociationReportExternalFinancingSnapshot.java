package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class AssociationReportExternalFinancingSnapshot {

    Long loanBalance;

    String financingBusinessTypeName;

    String capitalProvider;

    BigDecimal financingInterestRate;

    LocalDate financingLoanDate;

    LocalDate financingMaturityDate;
}
