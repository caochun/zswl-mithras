package cn.zswltech.mithras.service.excel.model.monthly;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class MonthlyAIRExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "借据编号", headerOrder = 3)
    private String receiptCode;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 1)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 2)
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 0)
    private String clientName;

    @SimpleExcelHeader(headerName = "租赁类型", headerOrder = 8)
    private String leaseType;

    @SimpleExcelHeader(headerName = "税率/%", headerOrder = 10)
    private BigDecimal taxRateExcel;

    @SimpleExcelHeader(headerName = "本月收入金额（含税）/元", headerOrder = 7)
    private BigDecimal incomeSumExcel;

    @SimpleExcelHeader(headerName = "本月收入金额（不含税）/元", headerOrder = 6)
    private BigDecimal incomeWithoutTaxSumExcel;

    @SimpleExcelHeader(headerName = "当前是否逾期", headerOrder = 11)
    private String overdueType;

    @SimpleExcelHeader(headerName = "月份", headerOrder = 5)
    private String yearAndMonth;

    @SimpleExcelHeader(headerName = "实际起租日", headerOrder = 4)
    private LocalDate actualLeaseDate;
    
}
