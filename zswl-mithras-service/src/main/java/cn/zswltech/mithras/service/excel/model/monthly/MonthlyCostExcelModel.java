package cn.zswltech.mithras.service.excel.model.monthly;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class MonthlyCostExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "月份", headerOrder = 0)
    private String yearAndMonth;

    @SimpleExcelHeader(headerName = "融资渠道", headerOrder = 20)
    private String organizationName;

    @SimpleExcelHeader(headerName = "融资金额/元", headerOrder = 30)
    private BigDecimal financingAmountExcel;

    @SimpleExcelHeader(headerName = "日利率/%", headerOrder = 50)
    private BigDecimal dailyRateExcel;

    @SimpleExcelHeader(headerName = "融资利率/%", headerOrder = 40)
    private BigDecimal financingRateExcel;

    @SimpleExcelHeader(headerName = "累计计提资金成本(含税)/元", headerOrder = 60)
    private BigDecimal totalCapitalCostExcel;

    @SimpleExcelHeader(headerName = "累计计提资金成本税后(不含税)/元", headerOrder = 70)
    private BigDecimal totalCapitalCostAfterTaxExcel;

    @SimpleExcelHeader(headerName = "当期计提资金成本(含税)/元", headerOrder = 80)
    private BigDecimal termCapitalCostExcel;

    @SimpleExcelHeader(headerName = "当期计提资金成本(不含税)/元", headerOrder = 90)
    private BigDecimal termCapitalCostAfterTaxExcel;

    @SimpleExcelHeader(headerName = "期初利息余额/元", headerOrder = 91)
    private BigDecimal beginOfPeriodInterestBalance;

    @SimpleExcelHeader(headerName = "期末利息余额/元", headerOrder = 92)
    private BigDecimal endOfPeriodInterestBalance;

    @SimpleExcelHeader(headerName = "钆差金额/元", headerOrder = 93)
    private BigDecimal financingCostDiff;

    @SimpleExcelHeader(headerName = "资产类型", headerOrder = 10)
    private String propertyTypeDisplay;

    @SimpleExcelHeader(headerName = "融资余额/元", headerOrder = 100)
    private BigDecimal remainingAmountExcel;

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 110)
    private String financingCode;
    
}
