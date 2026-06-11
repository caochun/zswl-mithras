package cn.zswltech.mithras.fund.application.process.prepare.handle;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.fund.application.process.prepare.FundProcessPrepareMaterialPort;
import cn.zswltech.mithras.fund.enums.financing.FundDirectFinancingMaterialsEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
import static java.lang.String.valueOf;


@Slf4j
@Component
public class FundDirectFinancingRecordFlowHandle extends AbstractFlowCommitHandle {

    private static final String FUND_DIRECT_FINANCING = "FUND_DIRECT_FINANCING";

    @Resource
    private FundDirectFinancingBaseInfoMapper directFinancingBaseInfoMapper;
    @Resource
    private FundProcessPrepareMaterialPort materialPort;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.DirectFinancingRecordFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingBaseInfoMapper.selectById(prepare.getBusinessId());
        Assert.notNull(directFinancingBaseInfo, () -> MithrasException.newException("直融数据不存在"));

        if (!materialPort.hasMaterials(FUND_DIRECT_FINANCING, FundDirectFinancingMaterialsEnum.listAll(), directFinancingBaseInfo.getId())) {
            throw new MithrasException("【资料清单】至少上传一个附件");
        }

        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setBusinessKey(valueOf(prepare.getBusinessId()));
        req.setStartUserDeptId(valueOf(directFinancingBaseInfo.getDeptId()));
        return getBean(FlowProcessApiService.class).start(req);

    }
}
