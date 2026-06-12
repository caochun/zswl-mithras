package cn.zswltech.mithras.application.orchestration.workflow.process.prepare.handle.fund;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.persistence.model.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
import static java.lang.String.valueOf;


@Slf4j
@Component
public class FundFinancingFloatRateFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FundFinancingBaseInfoMapper financingBaseInfoMapper;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.FinancingFloatRateAdjustFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoMapper.selectById(prepare.getBusinessId());
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));
        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setBusinessKey(valueOf(prepare.getBusinessId()));
        req.setStartUserDeptId(valueOf(financingBaseInfo.getDeptId()));
        return getBean(FlowProcessApiService.class).start(req);

    }
}
