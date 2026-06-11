package cn.zswltech.mithras.workflow.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TrackEventExcelExporter extends ExcelModel{

    @SimpleExcelHeader(headerName = "任务名称")
    private String taskName;

    @SimpleExcelHeader(headerName = "任务类型")
    private String taskType;

    @SimpleExcelHeader(headerName = "提出人")
    private String createByName;

    @SimpleExcelHeader(headerName = "计划日期")
    private LocalDate planTime;

    @SimpleExcelHeader(headerName = "处理人")
    private String processor;

    @SimpleExcelHeader(headerName = "提醒频率")
    private String remindFrequency;

    @SimpleExcelHeader(headerName = "起租后X自然日")
    private Integer startRentAfterDay;

    @SimpleExcelHeader(headerName = "任务内容")
    private String taskContent;

    @SimpleExcelHeader(headerName = "任务状态")
    private String taskStatus;

    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称")
    private String projName;

    @SimpleExcelHeader(headerName = "项目编号")
    private String projCode;

    @SimpleExcelHeader(headerName = "创建时间")
    private LocalDateTime createTime;

}


