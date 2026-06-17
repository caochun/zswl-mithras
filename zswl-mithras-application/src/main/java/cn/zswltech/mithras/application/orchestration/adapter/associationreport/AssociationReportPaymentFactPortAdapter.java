package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.associationreport.application.AssociationReportPaymentFactPort;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AssociationReportPaymentFactPortAdapter implements AssociationReportPaymentFactPort {

    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public int countPaidClient(LocalDate fromDate, LocalDate toDate) {
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMapper.selectList(
                Wrappers.<PaymentActualDetail>lambdaQuery()
                        .ge(PaymentActualDetail::getPaidInDate, fromDate)
                        .le(PaymentActualDetail::getPaidInDate, toDate)
        );
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return 0;
        }
        Set<Long> paymentIds = paymentActualDetailList.stream()
                .map(PaymentActualDetail::getPaymentId)
                .collect(Collectors.toSet());
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectBatchIds(paymentIds);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return 0;
        }
        return (int) paymentBaseInfoList.stream()
                .map(PaymentBaseInfo::getClientId)
                .distinct()
                .count();
    }
}
