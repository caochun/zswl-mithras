package cn.zswltech.mithras.application.orchestration.adapter.margin;

import cn.zswltech.mithras.margin.application.port.MarginPaymentReceiptPort;
import cn.zswltech.mithras.margin.application.port.model.MarginPaymentReceiptInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MarginPaymentReceiptPortAdapter implements MarginPaymentReceiptPort {
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;

    @Override
    public List<MarginPaymentReceiptInfo> listByReceiptIds(Collection<Long> receiptIds) {
        return paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .in(PaymentBaseInfo::getReceiptIdFinal, receiptIds))
                .stream()
                .map(this::toInfo)
                .collect(Collectors.toList());
    }

    private MarginPaymentReceiptInfo toInfo(PaymentBaseInfo payment) {
        MarginPaymentReceiptInfo info = new MarginPaymentReceiptInfo();
        info.setId(payment.getId());
        info.setContractId(payment.getContractId());
        info.setReceiptId(payment.getReceiptIdFinal());
        return info;
    }
}
