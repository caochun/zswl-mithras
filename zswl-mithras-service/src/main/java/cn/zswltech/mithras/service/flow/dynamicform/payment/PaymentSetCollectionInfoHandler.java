package cn.zswltech.mithras.service.flow.dynamicform.payment;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionHandleStatus;
import cn.zswltech.mithras.service.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.ProjClientRole;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 付款核销流程-财务经理节点补充收款信息
 */
@Component
public class PaymentSetCollectionInfoHandler implements DynamicFormHandler {
    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void check(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {

    }

    @Override
    public void collect(TaskDetailRSP rsp) {

    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.payment_setCollectionInfo;
    }
}
