package cn.zswltech.mithras.dto.contractcp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-16
 **/
@Data
@ApiModel("现金流-返回体")
public class ContractRentActualInfoRSP {

    @ApiModelProperty("记录id")
    private Long recordId;

    @ApiModelProperty("编号")
    private String rentCode;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("现金流项目")
    private String cashItem;

    @ApiModelProperty("日期")
    private LocalDate rentDate;

    @ApiModelProperty("现金流金额")
    private Long cashFlowAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;

    @ApiModelProperty("记录来源")
    private String recordSource;
}
