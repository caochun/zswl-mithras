package cn.zswltech.mithras.fund.application.convert.receiptrepay;

import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBorrowingModifyREQ;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBorrowing;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBorrowingLib;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/22 11:33
 */
@Mapper(componentModel = "spring")
public interface FundReceiptRepayBorrowingConverter {
    List<FundReceiptRepayBorrowing> modifyReq2Entity(List<FundReceiptRepayBorrowingModifyREQ> req);

    List<FundReceiptRepayBorrowingListRSP> lib2ListRsp(List<FundReceiptRepayBorrowingLib> records);

    @Mapping(target = "id", source = "originId")
    FundReceiptRepayBorrowingListRSP lib2ListRsp(FundReceiptRepayBorrowingLib record);

    List<FundReceiptRepayBorrowingListRSP> entity2ListRsp(List<FundReceiptRepayBorrowing> records);
}
