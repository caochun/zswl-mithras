package cn.zswltech.mithras.service.service.lib.fund.receiptrepay.service;

import cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay.FundReceiptRepayCashFlowLibMapper;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlowLib;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 本金与利息一览表
 *
 * @author wangchuanhao
 * @date 2023/2/20 4:16 PM
 */
@Service
public class FundReceiptRepayCashFlowLibService extends ServiceImpl<FundReceiptRepayCashFlowLibMapper, FundReceiptRepayCashFlowLib> {

    public List<FundReceiptRepayCashFlowLib> newestCashFlowLib(LocalDate from, LocalDate to) {
        return baseMapper.newestCashFlowLib(from, to);
    }
}
