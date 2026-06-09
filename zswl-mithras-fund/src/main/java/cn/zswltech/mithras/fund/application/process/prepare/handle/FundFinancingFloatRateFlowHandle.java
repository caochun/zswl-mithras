package cn.zswltech.mithras.fund.application.process.prepare.handle;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.workflow.application.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
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
