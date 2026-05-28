package cn.zswltech.mithras.dto.flow.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 审批流里删除文件-请求体
 *
 * @author wangchuanhao
 * @date 2022/11/23 10:50 AM
 */
@Data
@ApiModel("审批流里删除文件-请求体")
public class FlowFileRemoveREQ {

    @NotNull(message = "文件id不能为空")
    @ApiModelProperty("文件id")
    private Long id;

    @ApiModelProperty("流程实例id")
    @NotBlank
    private String processInstanceId;

}
