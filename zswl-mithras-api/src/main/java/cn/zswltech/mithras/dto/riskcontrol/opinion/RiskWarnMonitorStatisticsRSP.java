package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author vico
 * @description 风控舆情监控
 * @date 2023-03-09
 */
@Data
@ApiModel("风控舆情监控列表-返回体")
public class RiskWarnMonitorStatisticsRSP {

    @ApiModelProperty("监控客户数")
    private Integer totalMonitorClient;

    @ApiModelProperty("预警客户数")
    private Integer totalWarnClient;

    @ApiModelProperty("监控卡片")
    private List<RiskWarnMonitorStatisticsBody> opinionCard;

    @Data
    @Builder
    public static class RiskWarnMonitorStatisticsBody {

        @ApiModelProperty("预警类型 RiskOpinionWarnCordType")
        private String cardCode;
        @ApiModelProperty("预警名称")
        private String cardCodeName;

        @ApiModelProperty("预警数")
        private Integer amount;

        @ApiModelProperty("今日新增")
        private Integer todayAdd;

        @ApiModelProperty("今日关闭")
        private Integer todayClose;
    }


}
