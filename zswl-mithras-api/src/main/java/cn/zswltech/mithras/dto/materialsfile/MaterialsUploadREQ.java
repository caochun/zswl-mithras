package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-07-21
 **/

@Data
@ApiModel("资料上传-请求体")
public class MaterialsUploadREQ {
    @NotNull
    @ApiModelProperty("资料文件")
    private MultipartFile file;

    @NotNull
    @ApiModelProperty("归属id")
    private Long belongId;

    @NotNull
    @ApiModelProperty("资料类型")
    private String materialsType;

    @NotNull
    @ApiModelProperty("业务类型")
    private String businessType;
}
