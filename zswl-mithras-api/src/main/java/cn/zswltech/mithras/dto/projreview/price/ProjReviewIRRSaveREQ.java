package cn.zswltech.mithras.dto.projreview.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/1/11
 * @description
 */
@Data
@ApiModel("项目评审-保存IRR-请求体")
public class ProjReviewIRRSaveREQ {
    @ApiModelProperty("项目评审id")
    @NotNull(message = "项目评审id不能为空")
    private Long projReviewId;

    @ApiModelProperty("irr")
    @NotNull(message = "irr不能为空")
    private Integer irrPercent;
}
