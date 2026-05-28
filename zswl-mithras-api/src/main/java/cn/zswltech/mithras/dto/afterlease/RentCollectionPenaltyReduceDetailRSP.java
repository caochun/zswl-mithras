package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 租金催收首页列表请求体
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("租金催收首页列表返回体")
@Data
public class RentCollectionPenaltyReduceDetailRSP {

    private Long id;

    @ApiModelProperty("备注")
    private String notes;

    private List<RentCollectionPenaltyReduceItem> items;

    @Data
    public static class RentCollectionPenaltyReduceItem {

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "减免信息id")
        private Long reduceBaseId;

        @ApiModelProperty("收款id")
        private Long collectionId;

        /**
         * 客户id
         */
        @ApiModelProperty(value = "客户id")
        private Long clientId;

        private String clientName;

        /**
         * 合同id
         */
        @ApiModelProperty(value = "合同id")
        private Long contractId;

        /**
         * 合同编号
         */
        @ApiModelProperty(value = "合同编号")
        private String contractCode;

        /**
         * 借据id
         */
        @ApiModelProperty(value = "借据id")
        private Long receiptId;

        /**
         * 借据编号
         */
        @ApiModelProperty(value = "借据编号")
        private String receiptCode;

        /**
         * 期项
         */
        @ApiModelProperty(value = "期项")
        private Integer phase;

        /**
         * 合同金额
         */
        @ApiModelProperty(value = "合同金额")
        private Long applyCreditAmount;

        /**
         * 计划收款金额
         */
        @ApiModelProperty(value = "计划收款金额")
        private Long planCollectionAmount;

        /**
         * 计划收款日期
         */
        @ApiModelProperty(value = "计划收款日期")
        private LocalDate planCollectionDate;

        /**
         * 实收金额
         */
        @ApiModelProperty(value = "实收金额")
        private Long collectionAmount;

        /**
         * 罚息截止日
         */
        @ApiModelProperty(value = "罚息截止日")
        private LocalDate penaltyCloseDate;

        /**
         * 应收罚息
         */
        @ApiModelProperty(value = "应收罚息")
        private Long penaltyInterest;

        /**
         * 申请减免罚息
         */
        @ApiModelProperty(value = "申请减免罚息")
        private Long reducePenaltyInterest;
    }

}
