package cn.zswltech.mithras.fund.application.convert.financing;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailAddREQ;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailModifyREQ;
import cn.zswltech.mithras.dto.fund.financing.fee.FundFinancingFeeDetailRSP;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingFeeDetail;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundFinancingFeeDetailConverter {

    List<FundFinancingFeeDetailRSP> entity2Rsp(List<FundFinancingFeeDetail> records);

    FundFinancingFeeDetailRSP entity2Rsp(FundFinancingFeeDetail record);

    FundFinancingFeeDetail addReq2Entity(FundFinancingFeeDetailAddREQ req);

    FundFinancingFeeDetail modifyReq2Entity(FundFinancingFeeDetailModifyREQ req);
}
