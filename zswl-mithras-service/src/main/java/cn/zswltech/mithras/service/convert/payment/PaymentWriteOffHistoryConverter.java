package cn.zswltech.mithras.service.convert.payment;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/19 14:32
 */

import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListRsp;
import cn.zswltech.mithras.service.mapper.payment.PaymentWriteOffHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentWriteOffHistoryConverter {
    PaymentWriteOffHistoryListRsp entityToRsp(PaymentWriteOffHistory entity);
    List<PaymentWriteOffHistoryListRsp> entitiesToRsps(List<PaymentWriteOffHistory> records);
}
