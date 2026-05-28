package cn.zswltech.mithras.dto.projreview.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/8
 * @description
 */
@Data
@ApiModel("删除报告-请求体")
public class ProjReviewReportRemoveREQ {
    @NotNull(message = "文件id不能为空")
    @ApiModelProperty("文件id")
    private Long id;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;
}
