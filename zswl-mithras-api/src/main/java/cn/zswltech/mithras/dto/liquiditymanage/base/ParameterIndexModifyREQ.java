package cn.zswltech.mithras.dto.liquiditymanage.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * ParameterIndexREQ
 *
 * @author chenyifei
 * @since 2024/12/16
 */
@Data
@ApiModel(value = "流动性指标配置请求体")
public class ParameterIndexModifyREQ {

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
//    private List<IndexParam> indexValue;
//
//    @Data
//    @ApiModel(value = "流动性指标配置请求体")
//    public static class IndexParam {
//
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
