package cn.zswltech.mithras.fund.application.convert.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashDepositListRSP;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashDeposit;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayCashDepositLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/22 10:31
 */
@Mapper(componentModel = "spring")
public interface FundReceiptRepayCashDepositConverter {

    List<FundReceiptRepayCashDepositListRSP> lib2ListRsp(List<FundReceiptRepayCashDepositLib> depositLibs);

    @Mapping(target = "id", source = "originId")
    FundReceiptRepayCashDepositListRSP lib2ListRsp(FundReceiptRepayCashDepositLib depositLib);

    List<FundReceiptRepayCashDepositListRSP> entity2ListRsp(List<FundReceiptRepayCashDeposit> deposits);
}
