package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @create: 2022-10-24
 **/

@Data
public class ProjectLifecycleContractCardRSP {

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("租赁类型")
    private String bizType;

    @ApiModelProperty("创建时间")
    private LocalDate createTime;

    @ApiModelProperty("合同审批通过时间")
    private LocalDate approveTime;

    @ApiModelProperty("授信金额")
    private Long creditAmount;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("审批状态")
    private String processStatus;

    @ApiModelProperty("变更类型")
    private String updateType;

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("当前节点")
    private String currentNode;

    @ApiModelProperty("放款情况")
    private List<PaymentCardRSP> paymentCard;

    @Data
    public static class PaymentCardRSP{

        @ApiModelProperty("借据id")
        private Long receiptId;

        @ApiModelProperty("借据编号")
        private String receiptCode;

        @ApiModelProperty("核销状态")
        private String writeOffStatus;

        @ApiModelProperty("审批状态")
        private String processStatus;

        @ApiModelProperty("申请付款金额")
        private Long applyPaymentAmount;

        @ApiModelProperty("流程类型")
        private String processType;

        @ApiModelProperty("当前节点")
        private String currentNode;

        @ApiModelProperty("回款情况")
        private CollectionCardRSP collectionCard;

    }
    @Data
    public static class CollectionCardRSP{

        @ApiModelProperty("合同id")
        private Long contractId;

        @ApiModelProperty("已收款比例")
        private Float collectionRate;

        @ApiModelProperty("已收期项")
        private Integer collectionPhase;

        @ApiModelProperty("全部期项")
        private Integer totalPhase;

        @ApiModelProperty("已收本金")
        private Long receivedPrincipal;

        @ApiModelProperty("已收利息")
        private Long receivedInterest;

        @ApiModelProperty("已收罚息")
        private Long receivedPenaltyInterest;

        @ApiModelProperty("产生罚息")
        private Long penaltyInterest;

        @ApiModelProperty("已收金额")
        private Long receivedAmount;

        @ApiModelProperty("应收总额")
        private Long totalAmount;

        @ApiModelProperty("实际租金支付表编号")
        private String rentActualCode;

        @ApiModelProperty("合同状态")
        private String contractStatus;

        @ApiModelProperty("项目code")
        private String projectCode;

    }
}
