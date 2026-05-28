package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("上传报告-请求体")
public class AfterLeaseReportUploadREQ {
    @NotNull(message = "项目评审记录id不能为空")
    @ApiModelProperty("项目评审记录id")
    private Long adjustId;

    @NotNull(message = "请选择文件类型")
    @ApiModelProperty("文件类型 AfterLeaseMaterialsEnum")
    private String materialsType;
}
