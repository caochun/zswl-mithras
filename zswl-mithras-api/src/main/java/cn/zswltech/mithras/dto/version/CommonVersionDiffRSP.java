package cn.zswltech.mithras.dto.version;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 客户版本差异比较
 *
 * @author wangchuanhao
 * @date 2022/6/30 9:27 PM
 */
@ApiModel("差异比较-返回体")
@Data
public class CommonVersionDiffRSP {

    @ApiModelProperty("旧版本数据")
    private Map<String, List> oldData;

    @ApiModelProperty("新版本数据")
    private Map<String, List<Map<String, DiffValue>>> newData;

    @ApiModelProperty("模块change标志")
    private Map<String, Boolean> moduleChanged;

}
