package cn.zswltech.mithras.fund.directfinancing.application.directfinancing.convert;

import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingProductDetailAddREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingProductDetailModifyREQ;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingProductDetailRSP;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.fund.directfinancing.excel.directfinancing.FundDirectFinancingProductDetailExcelModel;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-产品明细
 * @date 2023-06-17
 */
@Mapper(componentModel = "spring")
public interface FundDirectFinancingProductDetailConverter {

    FundDirectFinancingProductDetail addReq2Entity(FundDirectFinancingProductDetailAddREQ req);

    FundDirectFinancingProductDetail modifyReq2Entity(FundDirectFinancingProductDetailModifyREQ req);

    List<FundDirectFinancingProductDetailRSP> entity2Rsp(List<FundDirectFinancingProductDetail> records);

    FundDirectFinancingProductDetailRSP entity2Rsp(FundDirectFinancingProductDetail record);

    List<FundDirectFinancingProductDetailExcelModel> entity2ExcelModel(List<FundDirectFinancingProductDetail> list);
}
