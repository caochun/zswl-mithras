package cn.zswltech.mithras.workflow.process.prepare.handle;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.workflow.process.prepare.FinancingRepayActualProcessDetailService;
import cn.zswltech.mithras.workflow.mapper.model.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.mapper.model.FinancingRepayActualProcessDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;
import static java.lang.String.valueOf;

/**
 * @ClassName RentPaymentNotifyFlowHandle
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/4/7 5:18 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class FinancingRepayWriteOffConfirmFlowHandle extends AbstractFlowCommitHandle {

    @Override
    public boolean needHandle(String processType) {
        return StrUtil.equals(processType, ProcessModelTypeEnum.FinancingRepayWriteOffConfirmFlow.name());
    }

    @Override
    public String commit(CommonProcessPrepare prepare) {
        List<FinancingRepayActualProcessDetail> detailList = getBean(FinancingRepayActualProcessDetailService.class).byPrepareId(prepare.getId());
        if (detailList.isEmpty()) {
            throw new MithrasException("没有融资还款核销数据");
        }
        StartProcessReq req = new StartProcessReq();
        req.setModelKey(prepare.getProcessType());
        req.setProcessInstanceName(prepare.getFormName());
        req.setStartUserId(valueOf(AccountUtil.getLoginInfo().getId()));
        req.setBusinessKey(valueOf(prepare.getId()));
        return getBean(FlowProcessApiService.class).start(req);
    }
}
