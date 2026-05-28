package cn.zswltech.mithras.service.convert.fund.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayCashFlowModifyREQ;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlowLib;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.service.service.bo.CashFlowBO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/22 10:48
 */
@Mapper(componentModel = "spring")
public interface FundReceiptRepayCashFlowConverter {
    List<FundReceiptRepayCashFlow> modifyReq2Entity(List<FundReceiptRepayCashFlowModifyREQ> req);

    FundReceiptRepayCashFlowListRSP entity2ListRsp(FundReceiptRepayCashFlow record);

    List<FundReceiptRepayCashFlowListRSP> entity2ListRsp(List<FundReceiptRepayCashFlow> records);

    @Mapping(target = "id", source = "originId")
    FundReceiptRepayCashFlowListRSP lib2ListRsp(FundReceiptRepayCashFlowLib record);

    @Mapping(target = "id", source = "originId")
    List<FundReceiptRepayCashFlowListRSP> lib2ListRsp(List<FundReceiptRepayCashFlowLib> records);

    @Mapping(target = "id", source = "originId")
    List<FundReceiptRepayCashFlow> lib2Entity(List<FundReceiptRepayCashFlowLib> cashFlowLibs);
}
