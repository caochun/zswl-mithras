package cn.zswltech.mithras.fund.application.organization;

import cn.hutool.core.lang.Pair;

import java.util.List;
import java.util.Map;

public interface FundOrganizationCreditLimitPort {

    Map<Long, Pair<Long, Long>> queryCreditLimitBatch(List<Long> orgIdList);
}
