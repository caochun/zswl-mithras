package cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.payment;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.domain.resp.TaskResp;
import cn.zswltech.flow.core.model.ext.UserTaskExt;
import cn.zswltech.mithras.dto.flow.form.PaymentUpdateIrrRSP;
import cn.zswltech.mithras.dto.flow.search.TaskDetailRSP;
import cn.zswltech.mithras.workflow.enums.FlowDynamicFormEnum;
import cn.zswltech.mithras.workflow.flow.dynamicform.DynamicFormHandler;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author yupengfei
 * @date 2024/5/18 17:56
 */
@Component
public class PaymentUpdateIrrHandler implements DynamicFormHandler {
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public void handle(Map<String, Object> formMap, TaskResp taskResp, UserTaskExt userTaskExt) {
        Long paymentId = Long.valueOf(taskResp.getBusinessKey());
        String jsonStr = JSONUtil.toJsonStr(formMap.get(this.getType().name()));
        if (StrUtil.isNotBlank(jsonStr)) {
            PaymentUpdateIrrRSP updateIrrRSP = JSONUtil.toBean(jsonStr, PaymentUpdateIrrRSP.class);
            paymentBaseInfoMapper.update(null, Wrappers.<PaymentBaseInfo>lambdaUpdate()
                    .eq(PaymentBaseInfo::getId, paymentId)
                    .set(PaymentBaseInfo::getLowestIrr, updateIrrRSP.getLowestIrr()));
        }
    }

    @Override
    public void collect(TaskDetailRSP rsp) {
        PaymentUpdateIrrRSP paymentUpdateIrrRSP = new PaymentUpdateIrrRSP();

        Long paymentId = Long.valueOf(rsp.getBusinessKey());
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
        if (Objects.nonNull(paymentBaseInfo)) {
            paymentUpdateIrrRSP.setLowestIrr(paymentBaseInfo.getLowestIrr());
        }

        rsp.getDynamicFormData().put(this.getType().name(), paymentUpdateIrrRSP);
    }

    @Override
    public FlowDynamicFormEnum getType() {
        return FlowDynamicFormEnum.payment_updateIRR;
    }
}
