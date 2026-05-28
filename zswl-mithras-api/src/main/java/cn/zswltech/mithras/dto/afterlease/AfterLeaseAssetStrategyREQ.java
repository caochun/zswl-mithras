package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 资产管理策略
 * @author: jackerhe
 * @date: 2024/4/19 4:15 下午
 **/
@Data
public class AfterLeaseAssetStrategyREQ extends PageReq {

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("计划类型，参考枚举AfterLeaseCheckPlanTypeEnum")
    private String planType;

    @ApiModelProperty("检查方式-下次跟进形式")
    private String checkWay;

    @ApiModelProperty("本次租后截止时间-下次跟进时间")
    private LocalDate deadLineFrom;
    private LocalDate deadLineTo;

}
