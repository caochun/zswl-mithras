package cn.zswltech.mithras.service.service.payment;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListReq;
import cn.zswltech.mithras.api.payment.writeoff.PaymentWriteOffHistoryListRsp;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.zswltech.mithras.service.mapper.payment.PaymentWriteOffHistoryMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentWriteOffHistory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description payment_write_off_history
 * @date 2022-08-19
 */
@Service
public class PaymentWriteOffHistoryService extends ServiceImpl<PaymentWriteOffHistoryMapper, PaymentWriteOffHistory> {

    @Transactional(rollbackFor = Throwable.class)
    public void add(PaymentWriteOffHistory entity) {
        baseMapper.insert(entity);
    }

    public PageR<PaymentWriteOffHistoryListRsp> list(PaymentWriteOffHistoryListReq req) {
        Page<PaymentWriteOffHistory> page = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<PaymentWriteOffHistory>lambdaQuery()
                        .eq(PaymentWriteOffHistory::getPaymentid, req.getPaymentId())
                        .orderByDesc(PaymentWriteOffHistory::getOperateTime));
        return PageR.of(page, page.getRecords().stream().map(this::toRsp).collect(Collectors.toList()));
    }

    private PaymentWriteOffHistoryListRsp toRsp(PaymentWriteOffHistory entity) {
        PaymentWriteOffHistoryListRsp rsp = new PaymentWriteOffHistoryListRsp();
        rsp.setPaymentid(entity.getPaymentid());
        rsp.setOperateTime(entity.getOperateTime());
        rsp.setOperatePersonId(entity.getOperatePersonId());
        rsp.setOperatePersonName(entity.getOperatePersonName());
        rsp.setOperateDataType(entity.getOperateDataType());
        rsp.setOperateDataCode(entity.getOperateDataCode());
        rsp.setOperation(entity.getOperation());
        rsp.setDataStatus(entity.getDataStatus());
        return rsp;
    }
}
