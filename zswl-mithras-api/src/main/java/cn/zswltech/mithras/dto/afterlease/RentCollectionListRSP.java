package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 租金催收首页列表返回体
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("租金催收首页列表返回体")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentCollectionListRSP {

    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("业务类型（租赁、保理、转租赁、债权转让）")
    private String bizType;

    @ApiModelProperty("主客户id")
    private Long clientId;

    @ApiModelProperty("主客户名称")
    private String clientName;

    @ApiModelProperty("借据编号")
    private String receiptCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("合同状态")
    private String contractStatus;

    @ApiModelProperty("借据状态(START_RENT：起租、SETTLE：结清、OVERDUE：逾期)")
    private String paymentState;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;

    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;

    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;

    @ApiModelProperty(value = "收款卡片列表")
    private List<CollectionCardData> collectionCardList;

    /**
     * 收款卡片数据
     */
    @ApiModel("收款卡片数据")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CollectionCardData {

        @ApiModelProperty("合同id")
        private Long contractId;

        @ApiModelProperty("收款id")
        private Long collectionId;

        @ApiModelProperty(value = "计划收款日期")
        private LocalDate planCollectionDate;

        @ApiModelProperty(value = "实际收款日期")
        private LocalDate collectionDate;

        @ApiModelProperty(value = "期项")
        private Integer phase;

        @ApiModelProperty(value = "计划收款金额")
        private Long planCollectionAmount;

        @ApiModelProperty(value = "实收金额")
        private Long collectionAmount;

        @ApiModelProperty(value = "未收金额")
        private Long unCollectionAmount;

        @ApiModelProperty(value = "累计罚息")
        private Long penaltyInterest;

        @ApiModelProperty(value = "罚息减免金额")
        private Long penaltyInterestDeductionAmount;

        @ApiModelProperty(value = "实收罚息")
        private Long collectionPenaltyInterest;

        @ApiModelProperty(value = "是否逾期")
        private Boolean isOverDue;

        @ApiModelProperty(value = "未收罚息")
        private Long unCollectionPenaltyInterest;

        @ApiModelProperty(value = "当前期数/总期数")
        private String repayTimesRate;

        @ApiModelProperty(value = "是否通知过苍穹系统收款（通知过则显示小铃铛）")
        private Boolean noticeFinancialFlag;

        @ApiModelProperty(value = "卡片状态（已收款(绿色)：PAID、待收款（黄色，计划收款日期7天内）：PENDING、已逾期（红色）：OVERDUE、未到期（灰色）：NOT_YET_EXPIRED")
        private String state;

        @ApiModelProperty(value = "标签列表（减免：DEDUCTION、逾期：OVERDUE、已通知：NOTIFIED）")
        private List<String> tagList;

    }

}
