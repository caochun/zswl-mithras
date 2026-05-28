package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static java.lang.String.valueOf;


@Slf4j
@Component
public class FundFinancingFloatRateFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.FinancingFloatRateAdjustFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        FundFinancingBaseInfo financingBaseInfo = financingBaseInfoService.getById(prepare.getBusinessId());
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
