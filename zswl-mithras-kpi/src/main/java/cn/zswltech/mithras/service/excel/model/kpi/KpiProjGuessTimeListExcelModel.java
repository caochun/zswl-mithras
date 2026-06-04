package cn.zswltech.mithras.service.excel.model.kpi;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流入明细下载
 * @author: jackerhe
 * @date: 2023/5/17 3:44 下午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class KpiProjGuessTimeListExcelModel extends ExcelModel {

    //核算月份
    @SimpleExcelHeader(headerName = "核算月份", headerOrder = 10)
    private String calculateDate;

    //利润-当期值
    @SimpleExcelHeader(headerName = "项目利润-当期值", headerOrder = 20, columnStyle = ColumnStyleEnum.MONEY)
    private String profitCurrent;

    //利润-累计值
    @SimpleExcelHeader(headerName = "项目利润-累计值", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private String profitTotal;

    //奖金-当期值
    @SimpleExcelHeader(headerName = "项目奖金-当期值", headerOrder = 40, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusCurrent;

    //奖金-累计值
    @SimpleExcelHeader(headerName = "项目奖金-累计值", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusTotal;

}
