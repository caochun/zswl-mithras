package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@Data
@ApiModel("租后检查报告-检查附件上传-请求体")
public class AfterLeaseCheckReportFileREQ {
    @NotNull(message = "检查计划客户记录id不能为空")
    @ApiModelProperty("检查计划客户记录id")
    private Long id;

    @NotNull(message = "文件不能为空")
    @ApiModelProperty("上传文件")
    private MultipartFile file;

    @NotBlank(message = "文件类型不能为空")
    @ApiModelProperty("文件类型")
    private String fileType;
}
