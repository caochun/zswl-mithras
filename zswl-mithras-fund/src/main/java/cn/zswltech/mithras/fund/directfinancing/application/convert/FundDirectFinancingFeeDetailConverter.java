package cn.zswltech.mithras.fund.directfinancing.application.convert;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingFeeDetailAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingFeeDetailModifyREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingFeeDetailRSP;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingFeeDetail;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-费用明细
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingFeeDetailConverter {

    List<FundDirectFinancingFeeDetailRSP> entity2Rsp(List<FundDirectFinancingFeeDetail> records);

    FundDirectFinancingFeeDetailRSP entity2Rsp(FundDirectFinancingFeeDetail record);

    FundDirectFinancingFeeDetail addReq2Entity(FundDirectFinancingFeeDetailAddREQ req);

    FundDirectFinancingFeeDetail modifyReq2Entity(FundDirectFinancingFeeDetailModifyREQ req);
}
