package cn.zswltech.mithras.dto.projreview.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("生成报告-请求体")
public class ProjReviewReportGenerateREQ {
    @NotNull(message = "项目评审记录id不能为空")
    @ApiModelProperty("项目评审记录id")
    private Long projReviewId;
}
