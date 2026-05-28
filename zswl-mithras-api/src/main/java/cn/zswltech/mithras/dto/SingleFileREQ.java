package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Data
@ApiModel("文件上传-请求体")
public class SingleFileREQ {
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty("上传文件")
    private MultipartFile file;
}
