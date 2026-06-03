package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.receiptrepay;

import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 * @deprecated 本表会继续保留使用，优化后数据会同步至FundReceiptFlowPlan，后续尽可能使用新表
 */
@Deprecated
public interface FundReceiptRepayCashFlowMapper extends CustomBaseMapper<FundReceiptRepayCashFlow> {

}