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
@ApiModel("资料清单列表-请求体")
public class MaterialsListListREQ {
    @NotNull
    @ApiModelProperty("belongIds")
    private List<Long> belongIds;

    @NotNull
    @ApiModelProperty(value = "businessType", required = true)
    private String businessType;

}
