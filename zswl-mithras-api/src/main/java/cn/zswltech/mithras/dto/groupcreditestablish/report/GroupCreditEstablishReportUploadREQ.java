package cn.zswltech.mithras.dto.groupcreditestablish.report;

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
public class GroupCreditEstablishReportUploadREQ {
    @NotNull(message = "集团授信立项id不能为空")
    @ApiModelProperty("集团授信立项id")
    private Long groupCreditEstablishId;

    @NotNull(message = "请选择文件类型")
    @ApiModelProperty("文件类型：立项审批单(REPORT)，业务申请书(PROJ_INFORMATION)")
    private String materialsType;
}
