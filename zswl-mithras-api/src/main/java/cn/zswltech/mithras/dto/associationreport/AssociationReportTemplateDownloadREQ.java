package cn.zswltech.mithras.dto.associationreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationReportTemplateDownloadREQ {
    @ApiModelProperty("报表类型code")
    @NotBlank(message = "报表类型不能为空")
    private String reportCategoryCode;
}
