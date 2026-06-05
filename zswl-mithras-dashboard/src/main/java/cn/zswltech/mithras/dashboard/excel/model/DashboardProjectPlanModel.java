package cn.zswltech.mithras.dashboard.excel.model;

import cn.zswltech.mithras.dto.dashboard.ValueUnitDTO;
import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPlanModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "部门名称", headerOrder = 10)
    private String bizDeptName;
    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;
    @SimpleExcelHeader(headerName = "项目类型", headerOrder = 30)
    private String projectClassifyDisplay;
    @SimpleExcelHeader(headerName = "业务模式", headerOrder = 40)
    private String bizTypeDisplay;
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 50)
    private String contractCode;
    @SimpleExcelHeader(headerName = "融资金额(万元)", headerOrder = 60, columnStyle = ColumnStyleEnum.MONEY)
    private String contractAmount;
    @SimpleExcelHeader(headerName = "手续费(万元)", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String commission;
    @SimpleExcelHeader(headerName = "保证金(万元)", headerOrder = 70, columnStyle = ColumnStyleEnum.MONEY)
    private String earnestMoney;
    @SimpleExcelHeader(headerName = "IRR(%)", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String irr;
    @SimpleExcelHeader(headerName = "投放金额(万元)", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String actualPayAmount;
    @SimpleExcelHeader(headerName = "投放日期", headerOrder = 110)
    private LocalDate actualPayDate;
    @SimpleExcelHeader(headerName = "项目主办名称", headerOrder = 120)
    private String projSponsorUserName;
    @SimpleExcelHeader(headerName = "省份", headerOrder = 130)
    private String provinceDisplay;
    @SimpleExcelHeader(headerName = "市", headerOrder = 140)
    private String cityDisplay;
    @SimpleExcelHeader(headerName = "地区", headerOrder = 150)
    private String districtDisplay;
    @SimpleExcelHeader(headerName = "年限(年)", headerOrder = 160, columnStyle = ColumnStyleEnum.MONEY)
    private String contractLimitYear;

}