package cn.zswltech.mithras.fund.application.lib.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfoLib;

public interface FundReceiptRepayBaseInfoAssembler {

    FundReceiptRepayBaseInfoDetailRSP lib2Rsp(FundReceiptRepayBaseInfo baseInfo,
                                              FundReceiptRepayBaseInfoLib lib);
}
