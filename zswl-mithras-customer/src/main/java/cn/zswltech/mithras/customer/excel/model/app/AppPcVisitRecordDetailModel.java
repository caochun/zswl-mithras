package cn.zswltech.mithras.customer.excel.model.app;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class AppPcVisitRecordDetailModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "拜访时间", headerOrder = 10)
    private LocalDateTime checkInDate;

    @SimpleExcelHeader(headerName = "拜访人", headerOrder = 20)
    private String createdName;

    @SimpleExcelHeader(headerName = "部门", headerOrder = 30)
    private String deptName;

    @SimpleExcelHeader(headerName = "拜访对象", headerOrder = 40)
    private String clientName;

    @SimpleExcelHeader(headerName = "打卡地点/补卡地点", headerOrder = 50)
    private String checkInLocation;

    @SimpleExcelHeader(headerName = "拜访类型", headerOrder = 60)
    private String visitType;

    @SimpleExcelHeader(headerName = "打卡类型", headerOrder = 70)
    private String visitWay;

    @SimpleExcelHeader(headerName = "拜访阶段", headerOrder = 80)
    private String visitPhase;

    @SimpleExcelHeader(headerName = "关联项目编号", headerOrder = 90)
    private String projCode;

    @SimpleExcelHeader(headerName = "关联合同编号", headerOrder = 95)
    private String contractCode;

    @SimpleExcelHeader(headerName = "租后检查计划", headerOrder = 100)
    private String checkPlanName;

}