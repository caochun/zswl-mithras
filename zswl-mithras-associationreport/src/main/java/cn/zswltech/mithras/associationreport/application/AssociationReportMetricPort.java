package cn.zswltech.mithras.associationreport.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AssociationReportMetricPort {

    Map<String, Long> capitalBalance(int year, int month);

    Map<String, Long> profit(int year, int month);

    Map<String, Long> guoZiKuaiBao(int year, int month);

    Map<String, Long> subjectBalance(int year, int month);

    BigDecimal subjectBalanceSum(int year, int month, List<String> factorNames);
}
