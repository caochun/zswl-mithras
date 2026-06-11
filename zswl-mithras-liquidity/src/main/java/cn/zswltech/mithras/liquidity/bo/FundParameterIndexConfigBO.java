package cn.zswltech.mithras.liquidity.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 流动性指标参数配置
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Data
public class FundParameterIndexConfigBO {

    @ApiModelProperty(value = "指标名称")
    private String indexName;

    @ApiModelProperty(value = "指标注释")
    private String indexDisplay;

    @ApiModelProperty(value = "红色-预警等级")
    private String redLevel;

    @ApiModelProperty(value = "红色-符号")
    private String redSign;

    @ApiModelProperty(value = "红色-值")
    private BigDecimal redValue;

    @ApiModelProperty(value = "黄色-预警等级")
    private String yellowLevel;

    @ApiModelProperty(value = "黄色-符号")
    private String yellowSign;

    @ApiModelProperty(value = "黄色-值")
    private BigDecimal yellowValue;

//    @ApiModelProperty(value = "指标内容")
//    private List<FundParameterIndexConfigBO.IndexParam> indexValue;
//
//    @Data
//    public static class IndexParam {
//
//        /**
//         * 预警等级 {@link LiquidityColorEnum#name()}
//         */
//        @ApiModelProperty(value = "预警等级")
//        private String level;
//
//        @ApiModelProperty(value = "符号")
//        private String sign;
//
//        @ApiModelProperty(value = "值")
//        private Long value;
//
//    }
}
