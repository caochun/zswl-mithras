package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("上传文件-请求体")
public class AppFileUploadREQ {
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty("文件")
    private MultipartFile file;

}
