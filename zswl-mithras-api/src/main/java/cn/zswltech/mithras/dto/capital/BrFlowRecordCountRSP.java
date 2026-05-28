package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 保融流水表
 * @author vico
 * @date 2024-06-17
 */
@Data
@ApiModel("保融流水表列表-返回体")
public class BrFlowRecordCountRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Integer notIgnoredCount;

}
