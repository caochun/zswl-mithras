package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author yibin
 */
@Data
public class FileTemplateReplaceREQ {
    @NotNull
    @ApiModelProperty("文件")
    private MultipartFile file;

    @NotNull
    @ApiModelProperty("模板文件id")
    private Long id;
}
