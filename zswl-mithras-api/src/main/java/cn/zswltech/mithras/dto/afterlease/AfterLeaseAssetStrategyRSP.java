package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产管理策略
 * @author: jackerhe
 * @date: 2024/4/19 4:15 下午
 **/
@Data
public class AfterLeaseAssetStrategyRSP {

    @ApiModelProperty("计划id")
    private Long planId;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("风险敞口（元）")
    private Long riskExposure;

    @ApiModelProperty("投放日期")
    private LocalDate paymentDate;

    @ApiModelProperty("五级分类结果")
    private String classifyResult;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @ApiModelProperty("主办名称-项目经理")
    private String sponsorName;

    @ApiModelProperty("协查风控经理id")
    private Long riskManagerId;

    @ApiModelProperty("协查风控经理名称")
    private String riskManagerName;

    @ApiModelProperty("检查方式-下次跟进形式")
    private String checkWay;

    @ApiModelProperty("本次租后截止时间-下次跟进时间")
    private LocalDate deadLine;

    @ApiModelProperty("跟进频率")
    private Integer term;

    @ApiModelProperty("跟进频率名称")
    private String termName;

    @ApiModelProperty("上次检查填报时间")
    private String lastCheckWay;

    @ApiModelProperty("上次跟进时间")
    private LocalDate lastEndDate;

    @ApiModelProperty("计划状态")
    private String approvalStatus;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
    
}
