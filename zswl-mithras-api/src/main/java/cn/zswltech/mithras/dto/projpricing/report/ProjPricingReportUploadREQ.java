package cn.zswltech.mithras.dto.projpricing.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/9
 * @description
 */
@Data
@Accessors(chain = true)
@ApiModel("上传报告-请求体")
public class ProjPricingReportUploadREQ {
    @NotNull(message = "不能为空")
    @ApiModelProperty("项目定价记录id")
    private Long projPricingId;

    @NotNull(message = "请选择文件类型")
    @ApiModelProperty("文件类型，只能选择'尽调报告'或'其他'")
    private String materialsType;
}
