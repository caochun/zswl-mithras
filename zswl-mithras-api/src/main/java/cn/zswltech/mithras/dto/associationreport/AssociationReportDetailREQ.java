package cn.zswltech.mithras.dto.associationreport;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2025/4/22
 * @description
 */
@Data
public class AssociationReportDetailREQ extends VersionBaseREQ {
    @ApiModelProperty("报表实例id")
    @NotBlank(message = "报表实例id不能为空")
    private String reportInstanceId;



}
