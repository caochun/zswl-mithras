package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssociationBaseModifyREQ {

    /**
     * 报表实例编号 uuid联合主键：(report_instance_id， row_num)
     */
    @ApiModelProperty(value = "报表实例编号 uuid联合主键：(report_instance_id， row_num)")
    private String reportInstanceId;

    /**
     * 报表实例周期格式：yyyyqq
     */
    @ApiModelProperty(value = "报表实例周期格式：yyyyqq")
    private String reportInstancePeriod;

    /**
     * 批次号
     */
    @ApiModelProperty(value = "批次号")
    private String batchNo;

    /**
     * 版本号
     */
    @ApiModelProperty(value = "版本号")
    private String version;

    /**
     * 操作标识 新增数据为insert，更新数据记录时值为update
     */
    @ApiModelProperty(value = "操作标识 新增数据为insert，更新数据记录时值为update")
    private String op;

    /**
     * 上报时间
     */
    @ApiModelProperty(value = "上报时间")
    private LocalDateTime reportTime;

    /**
     * 写入时间
     */
    @ApiModelProperty(value = "写入时间")
    private LocalDateTime writeTime;

}
