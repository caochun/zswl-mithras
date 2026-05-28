package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionFlowCenterBusinessCollectionListRSP {

    @ApiModelProperty("id")
    private Integer collectionId;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    private Long bizDeptId;

    @ApiModelProperty("业务部门")
    private String bizDept;

    @ApiModelProperty("计划收款日期")
    private LocalDate planCollectionDate;

    @ApiModelProperty("计划收款金额")
    private Long planCollectionAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("最新一笔收款日期，无核销不显示")
    private LocalDate collectionDate;

    @ApiModelProperty("实收金额")
    private Long collectionAmount;

    @ApiModelProperty("收款核销编号")
    private String code;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务类型。租赁、保理、转租赁")
    private String bizType;

    @ApiModelProperty("租赁类型。直租、回租、经营性租赁")
    private String leaseType;

    @ApiModelProperty("退抵锁标识")
    private String retreatLock;
}
