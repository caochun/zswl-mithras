package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名风险规模-请求体")
public class BlackGrayBatchQueryFileREQ {

    @ApiModelProperty(value = "企业名称")
    private String businessType;

    @ApiModelProperty(value = "企业名称")
    private List<String> businessTypeList;

    @ApiModelProperty(value = "文件")
    @NotNull(message = "文件不能为空")
    private MultipartFile file;

}
