package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@ApiModel("资料清单删除-请求体")
@Data
public class MaterialsListRemoveREQ {
    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private List<Long> ids;

    @NotNull
    @ApiModelProperty(value = "businessType", required = true)
    private String businessType;
}
