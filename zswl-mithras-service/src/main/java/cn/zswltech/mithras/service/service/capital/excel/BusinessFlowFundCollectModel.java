package cn.zswltech.mithras.service.service.capital.excel;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @description: 资金端-收款导出使用
 * @author: huangping
 * @date: 2025/12/5  9:44
 * @version: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessFlowFundCollectModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "核销状态", headerOrder = 10)
    private String writeOffStatusDisplay;


    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 20)
    private String financingRoute;


    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 30)
    private String financingCode;


    @SimpleExcelHeader(headerName = "现⾦流项⽬", headerOrder = 50)
    private String cashFlowItemDisplay;

    @SimpleExcelHeader(headerName = "应收日期", headerOrder = 60)
    private String date;


    @SimpleExcelHeader(headerName = "已收金额（元）", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal actualVerifyAmount;


    @SimpleExcelHeader(headerName = "最近收款日", headerOrder = 80, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate cashFlowDate;


}
