package cn.zswltech.mithras.fund.versioning.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoDetailRSP;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfoLib;

public interface FundReceiptRepayBaseInfoAssembler {

    FundReceiptRepayBaseInfoDetailRSP lib2Rsp(FundReceiptRepayBaseInfo baseInfo,
                                              FundReceiptRepayBaseInfoLib lib);
}
