package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author
 * @description 黑灰名单记录表
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表列表-请求体")
public class BlackGrayWarehouseRecordUploadREQ {

    @ApiModelProperty(value = "黑灰名单记录表")
    private MultipartFile file;

    /**
     * 来源
     **/
    @ApiModelProperty(name = "来源 BlackGraySourceEnum")
    @NotNull(message = "来源不能为空")
    private String source;

    @ApiModelProperty(name = "黑灰标识")
    @NotNull(message = "黑灰标识不能为为空")
    private String blackGrayType;

}
