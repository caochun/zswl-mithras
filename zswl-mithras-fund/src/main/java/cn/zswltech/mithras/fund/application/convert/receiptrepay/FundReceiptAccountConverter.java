package cn.zswltech.mithras.fund.application.convert.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptAccountListRSP;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptAccount;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptAccountLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/22 14:59
 */
@Mapper(componentModel = "spring")
public interface FundReceiptAccountConverter {

    List<FundReceiptAccountListRSP> lib2ListRsp(List<FundReceiptAccountLib> records);

    @Mapping(target = "id", source = "originId")
    FundReceiptAccountListRSP lib2ListRsp(FundReceiptAccountLib record);

    List<FundReceiptAccountListRSP> entity2ListRsp(List<FundReceiptAccount> records);
}
