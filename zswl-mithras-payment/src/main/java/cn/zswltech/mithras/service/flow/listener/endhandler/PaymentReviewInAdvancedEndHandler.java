package cn.zswltech.mithras.service.flow.listener.endhandler;

import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.workflow.application.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Objects;

import static cn.hutool.core.text.CharSequenceUtil.equalsAny;

/**
 * @author dingqi
 * @date 2025/2/7
 * @description
 */
@Slf4j
@Component
public class PaymentReviewInAdvancedEndHandler extends AbstractProcessEndHandler {
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public boolean needHandle(ProcessEndContext endContext) {
        return equalsAny(endContext.getModelKey(), ProcessModelTypeEnum.PaymentReviewInAdvancedFlow.name());
    }

    @Override
    public void handle(ProcessEndContext endContext) {
        ProcessBusinessStatusEnum processBusinessStatusEnum = ProcessBusinessStatusEnum.getByType(endContext.getEndType());
        if (Objects.isNull(processBusinessStatusEnum)) {
            throw new MithrasException("未定义的审批状态");
        }
        switch (processBusinessStatusEnum) {
            case PASS:
            case PASS_ALL: {
                // 更新运营审核日期、状态和超期天数
                Long paymentId = Long.valueOf(endContext.getBusinessKey());
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectById(paymentId);
                paymentBaseInfo.setBeyondDays(PaymentBaseInfo.DEFAULT_BEYOND_DAYS);
                paymentBaseInfo.setYunyingReviewState(YesOrNoNumberEnum.YES.getCode());
                paymentBaseInfo.setYunyingReviewDate(LocalDate.now());
                paymentBaseInfoMapper.updateById(paymentBaseInfo);
                break;
            }
            default: {
                // 关闭流程暂不处理
            }
        }
    }
}
