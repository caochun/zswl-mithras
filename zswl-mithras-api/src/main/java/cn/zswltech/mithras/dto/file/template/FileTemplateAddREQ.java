package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class FileTemplateAddREQ {
    @NotNull
    @ApiModelProperty("文件")
    private MultipartFile file;

    @NotBlank
    @ApiModelProperty("模板类型")
    private String templateType;
}
