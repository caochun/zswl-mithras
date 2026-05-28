package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.Map;

/**
 * @author jackerhe
 */
@Data
@ApiModel("上传文件-预上传地址获取-返回体")
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadPresignedRSP {

    @ApiModelProperty("文件上传地址")
    Map<String, URL> fileUrls;

}
