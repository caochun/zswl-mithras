package cn.zswltech.mithras.dto.flow.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 审批流里请求文件列表-返回体
 *
 * @author wangchuanhao
 * @date 2022/11/23 10:48 AM
 */
@Data
@ApiModel("审批流里请求文件列表-返回体")
public class FlowFileListRSP {

    @ApiModelProperty("报告文件id")
    private Long id;
    @ApiModelProperty("报告名称")
    private String typeName;
    @ApiModelProperty("材料类型")
    private String materialsType;
    @ApiModelProperty("文档名称")
    private String fileName;
    @ApiModelProperty("上传人")
    private String creator;
    @ApiModelProperty("上传时间")
    private String createTime;

}
