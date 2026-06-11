package cn.zswltech.mithras.fund.mapper.lib.receiptrepay;

import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 */
public interface FundReceiptRepayCashFlowLibMapper extends CustomBaseMapper<FundReceiptRepayCashFlowLib> {

    List<FundReceiptRepayCashFlowLib> newestCashFlowLib(@Param("repayDateFrom") LocalDate repayDateFrom,
                                                        @Param("repayDateTo") LocalDate repayDateTo);

}