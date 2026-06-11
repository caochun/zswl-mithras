package cn.zswltech.mithras.fund.application.convert.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundRepayAccountListRSP;
import cn.zswltech.mithras.fund.model.receiptrepay.FundRepayAccount;
import cn.zswltech.mithras.fund.model.receiptrepay.FundRepayAccountLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/22 14:59
 */
@Mapper(componentModel = "spring")
public interface FundRepayAccountConverter {
    List<FundRepayAccountListRSP> entity2ListRsp(List<FundRepayAccount> records);

    List<FundRepayAccountListRSP> lib2ListRsp(List<FundRepayAccountLib> records);

    @Mapping(target = "id", source = "originId")
    FundRepayAccountListRSP lib2ListRsp(FundRepayAccountLib record);
}
