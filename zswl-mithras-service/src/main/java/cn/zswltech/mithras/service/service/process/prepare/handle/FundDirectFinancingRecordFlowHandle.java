package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundDirectFinancingMaterialsEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingMaterialsEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
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
public class FundDirectFinancingRecordFlowHandle extends AbstractFlowCommitHandle {

    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.DirectFinancingRecordFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        FundDirectFinancingBaseInfo directFinancingBaseInfo = directFinancingBaseInfoService.getById(prepare.getBusinessId());
        Assert.notNull(directFinancingBaseInfo, () -> MithrasException.newException("直融数据不存在"));

        List<MaterialsList> materialsList = materialsListService.list(BusinessModuleEnum.FUND_DIRECT_FINANCING.name(),
                FundDirectFinancingMaterialsEnum.listAll(), Collections.singletonList(directFinancingBaseInfo.getId()));
        if(CollectionUtil.isEmpty(materialsList)){
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
