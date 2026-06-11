package cn.zswltech.mithras.application.orchestration.adapter.payment;

import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentActualDetailData;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentBaseInfoData;
import cn.zswltech.mithras.projectprocess.excel.payment.PaymentCashFlowQueryPort;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentCashFlowQueryPortAdapter implements PaymentCashFlowQueryPort {
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;

    @Override
    public List<PaymentBaseInfoData> listPaymentBaseInfoByReceiptId(Long receiptId) {
        return paymentBaseInfoService.listByReceiptId(receiptId).stream()
                .map(this::toPaymentBaseInfoData)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentActualDetailData> listActualDetailByPaymentId(Long paymentId) {
        return paymentActualDetailService.listByPaymentId(paymentId).stream()
                .map(this::toPaymentActualDetailData)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, List<PaymentActualDetailData>> mapActualDetailByPaymentIds(Collection<Long> paymentIds) {
        if (paymentIds == null || paymentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return paymentActualDetailService.getMapByPaymentIds(paymentIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().map(this::toPaymentActualDetailData).collect(Collectors.toList())));
    }

    private PaymentBaseInfoData toPaymentBaseInfoData(PaymentBaseInfo paymentBaseInfo) {
        PaymentBaseInfoData data = new PaymentBaseInfoData();
        data.setId(paymentBaseInfo.getId());
        data.setApplyPaymentAmount(paymentBaseInfo.getApplyPaymentAmount());
        data.setEarnestMoney(paymentBaseInfo.getEarnestMoney());
        data.setDownPayment(paymentBaseInfo.getDownPayment());
        data.setConsultingFee(paymentBaseInfo.getConsultingFee());
        data.setNominalPrice(paymentBaseInfo.getNominalPrice());
        return data;
    }

    private PaymentActualDetailData toPaymentActualDetailData(PaymentActualDetail paymentActualDetail) {
        PaymentActualDetailData data = new PaymentActualDetailData();
        data.setPaymentId(paymentActualDetail.getPaymentId());
        data.setPaidInDate(paymentActualDetail.getPaidInDate());
        data.setPaidInAmount(paymentActualDetail.getPaidInAmount());
        return data;
    }
}
