package cn.zswltech.mithras.service.mapper.lib.fund.receiptrepay;

import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
public interface FundReceiptRepayBaseInfoLibMapper extends CustomBaseMapper<FundReceiptRepayBaseInfoLib> {

    /**
     * 获取最新的收付款基本信息
     *
     * @param receiptRepayId 收付款id
     * @return 收付款基本信息
     */
    FundReceiptRepayBaseInfoLib getLastestDirectLib(@Param("receiptRepayId") Long receiptRepayId);
}