package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@ApiModel("五级分类-手动初分返回体")
@Data
public class AssetManualDivisionRSP {

    @ApiModelProperty(value = "是否第一次, 0非第一次，1第一次")
    private Integer firstFlag;

    private List<String> addClientNames;

    private List<String> deleteClientNames;

    private List<String> changeClientNames;

}
