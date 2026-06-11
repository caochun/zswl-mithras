package cn.zswltech.mithras.dto.rating.decision;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DecisionExecuteResult {

    /**
     * 客户评级字段
     */
    @ApiModelProperty(value = "初评得分")
    private String firstScore;

    @ApiModelProperty(value = "调整后得分")
    private String score;

    @ApiModelProperty(value = "定性得分")
    private String qualitativeScore;

    @ApiModelProperty(value = "定量得分")
    private String quantitativeScore;

    @ApiModelProperty(value = "模型得分")
    private String modelScore;

    @ApiModelProperty(value = "违约率")
    private String defaultRate;

    @ApiModelProperty(value = "区域得分")
    private String areaScore;

    /**
     * 债项评级字段
     */
    @ApiModelProperty(value = "客户限额")
    private String clientQuota;

    @ApiModelProperty(value = "集团限额")
    private String groupQuota;

    @ApiModelProperty(value = "评估主体评级调整系数")
    private String ratingAdjustFactor;

    @ApiModelProperty(value = "租赁物价值")
    private String leaseItemPrice;

    @ApiModelProperty(value = "增信措施调整价值")
    private String creditMeasurePrice;

    /**
     * 指标得分
     */
    @ApiModelProperty(value = "系统取数指标得分")
    private List<Var> varList;

    @ApiModelProperty(value = "业务填报指标得分")
    private List<Param> paramList;



    @ApiModelProperty(value = "评分卡版本")
    private String scoreCardVersion;

    @ApiModelProperty(value = "评分卡名称")
    private String scoreCardName;

    @ApiModelProperty(value = "评分卡编号")
    private String scoreCardCode;

    @ApiModelProperty(value = "标的物得分")
    private String subjectVesselScore;

    @ApiModelProperty(value = "客户综合得分")
    private String custScore;


    @Data
    public static class Var {
        private String code;
        private String name;
        private Object value;
        private String group;
    }

    @Data
    public static class Param {
        private String code;
        private String name;
        private Object value;
        private String group;
        private String source;
    }

}
