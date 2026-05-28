package cn.zswltech.mithras.service.convert.fund.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayExpenseModifyREQ;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingFeeDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayExpense;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayExpenseLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/22 13:48
 */
@Mapper(componentModel = "spring")
public interface FundReceiptRepayExpenseConverter {
    List<FundReceiptRepayExpense> modifyReq2Entity(List<FundReceiptRepayExpenseModifyREQ> req);

    List<FundReceiptRepayExpenseListRSP> lib2ListRsp(List<FundReceiptRepayExpenseLib> records);

    @Mapping(target = "id", source = "originId")
    FundReceiptRepayExpenseListRSP lib2ListRsp(FundReceiptRepayExpenseLib record);

    List<FundReceiptRepayExpenseListRSP> entity2ListRsp(List<FundReceiptRepayExpense> records);

    FundReceiptRepayExpense fee2Expense(FundDirectFinancingFeeDetail value);
}
