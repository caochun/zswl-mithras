package cn.zswltech.mithras.third.service.model.qiyuesuo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bigbear
 * @date 2024/11/27 14:59
 * @description
 */
@Data
public class DownloadContractResponse {

    @ApiModelProperty(value = "合同文件")
    private MultipartFile file;
}
