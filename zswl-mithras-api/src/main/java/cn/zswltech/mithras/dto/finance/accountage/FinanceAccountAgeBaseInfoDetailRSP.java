package cn.zswltech.mithras.dto.finance.accountage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 帐龄主表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄主表详情-返回体")
public class FinanceAccountAgeBaseInfoDetailRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 截止日期
    */
    @ApiModelProperty(value = "截止日期")
    private LocalDate deadline;

    /**
    * 核算组织编码 默认 10000396
    */
    @ApiModelProperty(value = "核算组织编码 默认 10000396")
    private String accountancyOrganizationNumber;

    /**
    * 核算组织名称 默认 浙江浙商融资租赁有限公司
    */
    @ApiModelProperty(value = "核算组织名称 默认 浙江浙商融资租赁有限公司")
    private String accountancyOrganizationName;

    /**
    * 状态
    */
    @ApiModelProperty(value = "状态 financialAccountAgeRecordStatus")
    private String status;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除，1-已删除")
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    private String createByName;

}
