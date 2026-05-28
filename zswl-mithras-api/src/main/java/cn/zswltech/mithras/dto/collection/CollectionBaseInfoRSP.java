package cn.zswltech.mithras.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/
@Data
@ApiModel("收款核销详情-返回体")
public class CollectionBaseInfoRSP {
    @ApiModelProperty("所属合同")
    private CollectionBaseInfoRSP.ContractInfo contractInfo;

    @ApiModelProperty("收款核销code")
    private String collectionCode;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("现金流项目")
    private String cashFlowItem;

    @ApiModelProperty("计划收款日")
    private LocalDate planCollectionDate;

    @ApiModelProperty("现金流金额")
    private Long cashFlowAmount;

    @ApiModelProperty("本金")
    private Long principal;

    @ApiModelProperty("利息")
    private Long interest;

    @ApiModelProperty("罚息")
    private Long penaltyInterest;


    @ApiModelProperty("罚息是否修改 0：未修改 1：已修改")
    private Integer penaltyInterestUpdate;

    @ApiModelProperty("罚息是否修改 0：可修改 1：不可修改")
    private Integer penaltyInterestChange;

    @Data
    public static class ContractInfo{
        @ApiModelProperty("合同编号")
        private String contractCode;

        private Long contractId;

        @ApiModelProperty("客户名称")
        private String clientName;

        @ApiModelProperty("项目名称")
        private String projName;

        @ApiModelProperty("类别")
        private String contractType;

        @ApiModelProperty("项目主办")
        private String projSponsorUserName;

        @ApiModelProperty("业务部门")
        private String bizDept;

        @ApiModelProperty("合同状态")
        private String contractStatus;
    }
}
