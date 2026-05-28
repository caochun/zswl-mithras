package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class FinanceProjectDistributionFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.ProjectProfitSharingFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        R<String> res = financeProjectDistributionService.submit(Long.parseLong(prepare.getBusinessId()),false);
        return res.getData();
    }

}
