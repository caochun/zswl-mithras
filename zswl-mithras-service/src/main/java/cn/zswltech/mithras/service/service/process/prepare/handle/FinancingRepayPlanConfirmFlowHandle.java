package cn.zswltech.mithras.service.service.process.prepare.handle;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.workflow.infrastructure.persistence.mapper.model.process.prepare.RentCollectionMonthDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.workflow.application.process.prepare.FinancingRepayActualProcessDetailService;
import cn.zswltech.mithras.workflow.application.process.prepare.RentCollectionMonthDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.collection.ListUtil.toList;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class FinancingRepayPlanConfirmFlowHandle extends AbstractFlowCommitHandle {

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.FinancingRepayPlanConfirmFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        List<FinancingRepayActualProcessDetail> detailList = getBean(FinancingRepayActualProcessDetailService.class).byPrepareId(prepare.getId());
        if (detailList.isEmpty()) {
            throw new MithrasException("没有提前融资还款数据");
        }
        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setBusinessKey(valueOf(prepare.getId()));
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        return getBean(FlowProcessApiService.class).start(req);
    }


}
