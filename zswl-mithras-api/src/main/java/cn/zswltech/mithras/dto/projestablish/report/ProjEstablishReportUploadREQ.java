package cn.zswltech.mithras.dto.projestablish.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wangchuanhao
 * @date 2022/9/21
 * @description
 */
@Data
@ApiModel("上传报告-请求体")
public class ProjEstablishReportUploadREQ {
    @NotNull(message = "项目评审记录id不能为空")
    @ApiModelProperty("项目评审记录id")
    private Long projEstablishId;

    @NotNull(message = "请选择文件类型")
    @ApiModelProperty("文件类型：立项审批单(REPORT)，业务申请书(PROJ_INFORMATION)")
    private String materialsType;
}
