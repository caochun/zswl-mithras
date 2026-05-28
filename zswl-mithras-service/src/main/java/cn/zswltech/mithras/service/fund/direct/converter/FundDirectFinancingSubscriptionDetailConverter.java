package cn.zswltech.mithras.service.fund.direct.converter;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingSubscriptionDetailAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingSubscriptionDetailModifyREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingSubscriptionDetailRSP;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingSubscriptionDetail;
import cn.zswltech.mithras.service.fund.direct.excel.FundDirectFinancingSubscriptionDetailExcelModel;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-认购明细
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingSubscriptionDetailConverter {

    FundDirectFinancingSubscriptionDetail adReq2Entity(FundDirectFinancingSubscriptionDetailAddREQ req);

    FundDirectFinancingSubscriptionDetail modifyReq2Entity(FundDirectFinancingSubscriptionDetailModifyREQ req);

    List<FundDirectFinancingSubscriptionDetailRSP> entity2Rsp(List<FundDirectFinancingSubscriptionDetail> records);

    FundDirectFinancingSubscriptionDetailExcelModel entity2ExcelModel(FundDirectFinancingSubscriptionDetail fundDirectFinancingSubscriptionDetail);

    List<FundDirectFinancingSubscriptionDetailExcelModel> entity2ExcelModel(List<FundDirectFinancingSubscriptionDetail> records);
}
