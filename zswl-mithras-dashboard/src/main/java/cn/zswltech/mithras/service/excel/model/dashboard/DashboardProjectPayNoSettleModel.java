package cn.zswltech.mithras.service.excel.model.dashboard;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayNoSettleModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 20)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "业务模式", headerOrder = 30)
    private String leaseTypeDisplay;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 40)
    private String riskControlIndustryClassifyDisplay;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 50)
    private String contractCode;

    @SimpleExcelHeader(headerName = "保证金", headerOrder = 60)
    private String earnestAmount;

    @SimpleExcelHeader(headerName = "存量风险敞口(元)", headerOrder = 70)
    private String stockRiskExposure;

    @SimpleExcelHeader(headerName = "投放金额(元)", headerOrder = 80)
    private String actualPayAmount;

    @SimpleExcelHeader(headerName = "剩余金额(元)", headerOrder = 90)
    private String balanceAmount;

    @SimpleExcelHeader(headerName = "剩余本金(元)", headerOrder = 100)
    private String principalBalanceAmount;

    @SimpleExcelHeader(headerName = "剩余利息(元)", headerOrder = 110)
    private String interestBalanceAmount;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 120)
    private String clientName;

    @SimpleExcelHeader(headerName = "国标行业分类", headerOrder = 130)
    private String industryTypeDisplay;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 140)
    private String lesseeNames;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 150)
    private String guarantorNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 160)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 170)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 180)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "地区分类", headerOrder = 190)
    private String regionalProjectClassifyDisplay;

    @SimpleExcelHeader(headerName = "是否关联方", headerOrder = 200)
    private Integer isRelated;

    @SimpleExcelHeader(headerName = "投放月份", headerOrder = 210)
    private LocalDate actualPayMonth;

    @SimpleExcelHeader(headerName = "起租日", headerOrder = 220)
    private LocalDate actualLeaseDate;

    @SimpleExcelHeader(headerName = "租赁期限（月）", headerOrder = 230)
    private String leaseDuration;

    @SimpleExcelHeader(headerName = "剩余期限（月）", headerOrder = 240)
    private String remainingLeaseDuration;

    @SimpleExcelHeader(headerName = "IRR", headerOrder = 250)
    private String irr;
}