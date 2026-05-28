package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 资产管理策略
 * @author: jackerhe
 * @date: 2024/4/19 4:15 下午
 **/
@Data
public class AfterLeaseAssetStrategyModifyREQ {

    @ApiModelProperty("id")
    @NotNull
    private Long planId;

    @ApiModelProperty("id")
    private Long checkPlanClientId;

    @ApiModelProperty("跟进频率")
    private String term;

    @ApiModelProperty("检查方式-下次跟进形式")
    private String checkWay;

    @ApiModelProperty("本次租后截止时间-下次跟进时间")
    private LocalDate deadLine;

    @ApiModelProperty("检查人员Id")
    private Long riskManagerId;

    @ApiModelProperty("检查人员")
    private String checkPerson;

}
