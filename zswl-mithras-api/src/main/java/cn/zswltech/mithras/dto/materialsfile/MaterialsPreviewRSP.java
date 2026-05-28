package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-08-23
 **/
@ApiModel("资料清单预览-返回体")
@Data
public class MaterialsPreviewRSP {
    @ApiModelProperty("文件预览类型")
    private String previewType;

    @ApiModelProperty("图片url")
    private String url;
}
