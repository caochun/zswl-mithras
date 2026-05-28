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
@ApiModel("下载报告-请求体")
public class ProjReviewReportDownloadREQ {
    @NotNull(message = "报告记录id不能为空")
    @ApiModelProperty("报告记录id")
    private Long recordId;
}
