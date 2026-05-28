//package cn.zswltech.mithras.service.flow.listener.starthandler;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.json.JSONUtil;
//import cn.zswltech.flow.core.extension.event.ProcessStartEvent;
//import cn.zswltech.flow.core.extension.event.context.ProcessStartContext;
//import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
//import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
//import cn.zswltech.mithras.service.flow.listener.ProcessStartEventListener;
//import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetailUnconfirmed;
//import cn.zswltech.mithras.service.service.payment.PaymentActualDetailUnconfirmedService;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.Wrappers;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * @author bigbear
// * @date 2024/9/30 10:37
// * @description 付款实际核销确认流程开始监听器
// */
//@Slf4j
//@Component
//public class PaymentActualDetailStartHandler extends ProcessStartEventListener {
//
//    @Resource
//    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
//
//    @Override
//    public void onApplicationEvent(ProcessStartEvent processStartEvent) {
//        super.onApplicationEvent(processStartEvent);
//        ProcessStartContext processStartContext = processStartEvent.getProcessStartContext();
//        Long paymentId = Long.valueOf(processStartContext.getBusinessKey());
//        if (ProcessModelTypeEnum.PaymentActualDetailFlow.name().equals(processStartContext.getModelKey())) {
//            log.info("监听到付款实际核销确认流程开始, 数据: {}", JSONUtil.toJsonStr(processStartContext));
//            // 将流程中 「待确认」状态的数据设置为「已确认」,并将流程实例ID设置到表中，为后续流程不通过的一致性做兜底
//            LambdaQueryWrapper<PaymentActualDetailUnconfirmed> query = Wrappers.lambdaQuery();
//            query.eq(PaymentActualDetailUnconfirmed::getPaymentId, paymentId);
//            query.eq(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.COMMIT.name());
//            List<PaymentActualDetailUnconfirmed> paymentActualDetailUnConfirmed = paymentActualDetailUnconfirmedService.list(query);
//            if (CollUtil.isEmpty(paymentActualDetailUnConfirmed)) {
//                return;
//            }
//
//            String processInstanceId = processStartContext.getProcessInstanceId();
//            // 设置状态和流程实例ID
//            paymentActualDetailUnConfirmed.forEach(item -> {
//                item.setWriteOffStatus(WriteOffStatus.CONFIRM.name());
//                item.setProcessInstanceId(processInstanceId);
//            });
//
//            paymentActualDetailUnconfirmedService.updateBatchById(paymentActualDetailUnConfirmed);
//            log.info("付款实际核销确认流程开始, 可核销数据: {}", JSONUtil.toJsonStr(paymentActualDetailUnConfirmed));
//        }
//    }
//}
