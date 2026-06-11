package cn.zswltech.mithras.application.orchestration.adapter.fund;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationCreditLimitPort;
import cn.zswltech.mithras.application.orchestration.fund.FundCreditService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class FundOrganizationCreditLimitPortAdapter implements FundOrganizationCreditLimitPort {

    @Resource
    private FundCreditService fundCreditService;

    @Override
    public Map<Long, Pair<Long, Long>> queryCreditLimitBatch(List<Long> orgIdList) {
        return fundCreditService.queryCreditLimitBatch(orgIdList);
    }
}
