package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author junke
 */
@ApiModel("融租易APP我的项目合同信息-返回体")
@Data
public class AppProContractDetailRSP {
    /**
     * id
     */
    @ApiModelProperty(value = "合同id")
    private Long id;

    @ApiModelProperty("项目详情")
    private List<BasicInfoRSP> basicInfoRSPList;

    @Data
    public static class BasicInfoRSP {
        @ApiModelProperty(value = "剩余租金")
        private Long remainingRent;

        @ApiModelProperty(value = "剩余本金")
        private Long remainingPrincipal;

        @ApiModelProperty(value = "风险敞口")
        private Long riskExposure;

        @ApiModelProperty("借据编号")
        private String receiptCode;

        @ApiModelProperty(value = "当月应收租金总额")
        private Long planCollectionAmount;

        @ApiModelProperty(value = "当月已收租金总额")
        private Long collectionAmount;

        @ApiModelProperty(value = "当月未收租金总额")
        private Long unCollectionAmount;

        @ApiModelProperty(value = "当前期限/总期限")
        private String leaseMonthRate;

        @ApiModelProperty(value = "当前期数/总期数")
        private String repayTimesRate;


        @ApiModelProperty("还款详情")
        private List<CollectionBaseInfoRSP> collectionBaseInfoRSPList;
    }

    @Data
    public static class CollectionBaseInfoRSP {
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
}
