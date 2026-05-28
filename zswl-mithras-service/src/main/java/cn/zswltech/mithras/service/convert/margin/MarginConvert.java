package cn.zswltech.mithras.service.convert.margin;

import cn.zswltech.mithras.dto.margin.MarginBaseInfoListRSP;
import cn.zswltech.mithras.service.excel.model.MarginBaseInfoListExcelModel;
import cn.zswltech.mithras.service.others.Util;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @create: 2022-08-22
 **/

public class MarginConvert {

    public static MarginBaseInfoListExcelModel MarginBaseInfoListRSP2ExcelModel(MarginBaseInfoListRSP rsp){
        MarginBaseInfoListExcelModel marginBaseInfoListExcelModel = new MarginBaseInfoListExcelModel();
        marginBaseInfoListExcelModel.setCode(rsp.getCode());
        marginBaseInfoListExcelModel.setContractCode(rsp.getContractCode());
        marginBaseInfoListExcelModel.setClientName(rsp.getClientName());
        marginBaseInfoListExcelModel.setCollectionDate(rsp.getCollectionDate());
        marginBaseInfoListExcelModel.setMarginAmount(Optional.ofNullable(rsp.getMarginAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        marginBaseInfoListExcelModel.setBackAmount(Optional.ofNullable(rsp.getBackAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        marginBaseInfoListExcelModel.setDeductAmount(Optional.ofNullable(rsp.getDeductAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        marginBaseInfoListExcelModel.setCanBackAmount(Optional.ofNullable(rsp.getCanBackAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        return marginBaseInfoListExcelModel;
    }
}
