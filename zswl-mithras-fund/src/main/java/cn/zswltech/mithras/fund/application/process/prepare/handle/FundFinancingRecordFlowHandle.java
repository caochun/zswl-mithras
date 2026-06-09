package cn.zswltech.mithras.fund.application.process.prepare.handle;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.fund.application.process.prepare.FundProcessPrepareMaterialPort;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.workflow.application.process.prepare.handle.AbstractFlowCommitHandle;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static java.lang.String.valueOf;


@Slf4j
@Component
public class FundFinancingRecordFlowHandle extends AbstractFlowCommitHandle {

    private static final String FUND_FINANCING = "FUND_FINANCING";

    @Resource
    private FundFinancingBaseInfoMapper financingBaseInfoMapper;
    @Resource
    private FundProcessPrepareMaterialPort materialPort;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.FinancingRecordFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoMapper.selectById(prepare.getBusinessId());
        Assert.notNull(financingBaseInfo, () -> MithrasException.newException("融资数据不存在"));

        List<FundFinancingMaterialsEnum> materialsEnumList = FundFinancingMaterialsEnum.getMaterialTypeByFinancingType(financingBaseInfo.getBusinessType());
        List<String> materialTypes = materialsEnumList.stream().map(FundFinancingMaterialsEnum::name).collect(Collectors.toList());
        if (!materialPort.hasMaterials(FUND_FINANCING, materialTypes, financingBaseInfo.getId())) {
            throw new MithrasException("【资料清单】至少上传一个附件");
        }

        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setStartUserDeptId(valueOf(financingBaseInfo.getDeptId()));
        req.setBusinessKey(valueOf(prepare.getBusinessId()));
        return getBean(FlowProcessApiService.class).start(req);

    }
}
