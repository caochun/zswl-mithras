package cn.zswltech.mithras.dto.flow.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 审批流里请求文件列表-请求体
 *
 * @author wangchuanhao
 * @date 2022/11/23 10:48 AM
 */
@Data
@ApiModel("审批流里请求文件列表-请求体")
public class FlowFileListREQ {

    @ApiModelProperty("流程id")
    @NotBlank
    private String processInstanceId;

    @ApiModelProperty("资料类型")
    private String materialsType;

    @ApiModelProperty("资料类型列表")
    private List<String> materialsTypeList;

}
