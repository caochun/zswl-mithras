package cn.zswltech.mithras.application.orchestration.workflow.process.prepare.handle.finance;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.finance.projectdistribution.service.impl.FinanceProjectDistributionService;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.persistence.model.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FinanceProjectDistributionFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.ProjectProfitSharingFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        R<String> res = financeProjectDistributionService.submit(Long.parseLong(prepare.getBusinessId()), false);
        return res.getData();
    }
}
