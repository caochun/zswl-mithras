package cn.zswltech.mithras.fund.mapper.receiptrepay;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayExpense;

/**
* @description 费用一览表
* @author zhaozhengkang
* @date 2023-02-20
 * @deprecated 本表会继续保留使用，优化后数据会同步至FundReceiptFlowPlan，后续尽可能使用新表
*/
@Deprecated
public interface FundReceiptRepayExpenseMapper extends CustomBaseMapper<FundReceiptRepayExpense> {

}