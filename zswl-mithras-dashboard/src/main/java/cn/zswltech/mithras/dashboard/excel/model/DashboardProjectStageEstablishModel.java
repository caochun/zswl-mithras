package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStageEstablishModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 10)
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "立项状态", headerOrder = 30)
    private String projEstablishStatusDisplay;

    @SimpleExcelHeader(headerName = "审批状态", headerOrder = 40)
    private String projEstablishProcessStatusDisplay;

    @SimpleExcelHeader(headerName = "当前阶段停留天数（工作日）", headerOrder = 50, columnStyle = ColumnStyleEnum.MONEY)
    private String lengthOfStay;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 60)
    private String riskControlIndustryClassifyDisplay;

    @SimpleExcelHeader(headerName = "授信金额", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String creditAmount;

    @SimpleExcelHeader(headerName = "国标行业分类", headerOrder = 80)
    private String industryTypeDisplay;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 90)
    private String bizTypeDisplay;

    @SimpleExcelHeader(headerName = "承租人", headerOrder = 100)
    private String lesseeNames;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 110)
    private String guarantorNames;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 120)
    private String bizDeptName;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 130)
    private String projSponsorUserName;

    @SimpleExcelHeader(headerName = "项目协办", headerOrder = 140)
    private String projCosponsorUserNames;

    @SimpleExcelHeader(headerName = "立项申请时间", headerOrder = 150)
    private LocalDateTime applyTime;

    @SimpleExcelHeader(headerName = "审批耗时（工作日）", headerOrder = 160, columnStyle = ColumnStyleEnum.MONEY)
    private String approveElapsedTime;

}
