package cn.zswltech.mithras.associationreport.excel;

import lombok.Data;

/**
 * @date 2025/4/22
 * @description 业务情况表
 */
@Data
public class AssociationReportBaseModel {

    /**
     * 报表实例id | uuid
     */
   /* @ApiModelProperty("report_instance_id")
    private String reportInstanceId;

    *//**
     * 报表周期 | yyyyqq格式
     *//*
    @ApiModelProperty("report_instance_period")
    private String reportInstancePeriod;

    *//**
     * 批次号 | 0000-9999
     *//*
    @ApiModelProperty("batch_no")
    private String batchNo;

    *//**
     * 版本号 | 周期版本
     *//*
    @ApiModelProperty("version")
    private String version;

    *//**
     * 操作标识 | insert/update
     *//*
    @ApiModelProperty("op")
    private String op;

    *//**
     * 上报时间
     *//*
    @ApiModelProperty("report_time")
    private LocalDateTime reportTime;

    *//**
     * 入库时间
     *//*
    @ApiModelProperty("write_time")
    private LocalDateTime writeTime;*/
}
