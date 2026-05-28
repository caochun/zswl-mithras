package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@Data
@ApiModel("租后检查报告-检查附件列表-返回体")
public class AfterLeaseCheckReportFileRSP {
    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("文件分类")
    private String fileType;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("上传人")
    private String creator;

    @ApiModelProperty("上传时间")
    private String createTime;
}
