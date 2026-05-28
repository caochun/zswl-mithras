package cn.zswltech.mithras.dto.client.external.environment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @description 环保处罚
 * @author yeqing
 * @date 2022-06-23
 */
@Data
@ApiModel("外部信息-环保处罚-修改")
public class EnvironmentPenaltyModifyREQ {

    /**
     * 主键
     */
    @ApiModelProperty("id")
    @NotNull
    private Long id;

    /**
     * 客户id
     */
    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

    /**
    * 处罚日期
    */
    @ApiModelProperty("处罚日期")
    private LocalDateTime penaltyTime;

    /**
    * 决定文书号
    */
    @ApiModelProperty("决定文书号")
    private String punishNumber;

    /**
    * 处罚事由
    */
    @ApiModelProperty("处罚事由")
    private String reason;

    /**
    * 处罚结果
    */
    @ApiModelProperty("处罚结果")
    private String result;

    /**
    * 处罚金额（元）
    */
    @ApiModelProperty("处罚金额")
    private Long amount;

    /**
    * 处罚单位
    */
    @ApiModelProperty("处罚单位")
    private String departmentName;

    /**
    * 数据来源
    */
    @ApiModelProperty("数据来源")
    private String source;

    /**
    * 执行情况
    */
    @ApiModelProperty("执行情况")
    private String info;

    /**
    * 详情url
    */
    @ApiModelProperty("详情url")
    private String detailUrl;

}