package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author luyi
 */
@Data
@ApiModel("资料清单下载-请求体")
public class MaterialsListDownloadREQ {
    @NotNull
    @ApiModelProperty("记录id")
    private List<Long> ids;
}
