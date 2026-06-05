package cn.zswltech.mithras.margin.convert;

import cn.zswltech.mithras.dto.margin.MarginBaseInfoListRSP;
import cn.zswltech.mithras.service.excel.model.MarginBaseInfoListExcelModel;

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
        marginBaseInfoListExcelModel.setMarginAmount(Optional.ofNullable(rsp.getMarginAmount()).map(MarginConvert::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        marginBaseInfoListExcelModel.setBackAmount(Optional.ofNullable(rsp.getBackAmount()).map(MarginConvert::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        marginBaseInfoListExcelModel.setDeductAmount(Optional.ofNullable(rsp.getDeductAmount()).map(MarginConvert::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        marginBaseInfoListExcelModel.setCanBackAmount(Optional.ofNullable(rsp.getCanBackAmount()).map(MarginConvert::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
        return marginBaseInfoListExcelModel;
    }

    private static BigDecimal mithrasLong2BigDecimal(Long value) {
        return new BigDecimal(value).divide(new BigDecimal(10000L));
    }
}
