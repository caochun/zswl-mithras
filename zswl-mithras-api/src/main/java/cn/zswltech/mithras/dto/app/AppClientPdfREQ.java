package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 生效
 *
 * @author zhouning
 * @date 2024/10/14 11:56 PM
 */
@Data
@ApiModel("融租易APP我的客户拜访详情-请求体")
public class AppClientPdfREQ {

    @ApiModelProperty("上传照片附件")
    private List<MultipartFile> imageFiles;

    @ApiModelProperty("客户id")
    private Long belongId;

    @ApiModelProperty("业务类型")
    private String businessType;

    @ApiModelProperty("资料类型")
    private String materialsType;

    @ApiModelProperty("资料子类型")
    private String materialsSubType;

}
