package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("收款核销列表-返回体")
public class CollectionBaseInfoListRSP {
    @ApiModelProperty("收款核销id")
    private Long id;

    @ApiModelProperty("收款核销编号")
    private String code;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty("计划收款日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty("计划收款金额")
    private Long planCollectionAmount;

    @ApiModelProperty("实收日期")
    private LocalDate collectionDate;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;
}
