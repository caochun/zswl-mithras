package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class AppCalendarDailyRSP {

    @ApiModelProperty("当日租金应收总额（万元）")
    private Long dailyPlanCollectionAmount;

    @ApiModelProperty("当日已收租金总额（万元）")
    private Long dailyCollectionAmount;

    @ApiModelProperty("当日剩余租金未收总额（万元）")
    private Long dailyUnCollectionAmount;

    @ApiModelProperty("现金流详情")
    private List<CollectionCardData> collectionCardDataList;

    @Data
    public static class CollectionCardData {
        @ApiModelProperty("收款id")
        private Long collectionId;
        @ApiModelProperty("客户名称")
        private String clientName;
        @ApiModelProperty(value = "当前期数/总期数")
        private String repayTimesRate;
        @ApiModelProperty("应收租金（元）")
        private Long planCollectionAmount;
        @ApiModelProperty("未收租金（元）")
        private Long unCollectionAmount;
        @ApiModelProperty("项目主办")
        private String projSponsorUserName;
    }
}
