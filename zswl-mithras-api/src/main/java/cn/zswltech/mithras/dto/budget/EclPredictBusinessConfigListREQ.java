package cn.zswltech.mithras.dto.budget;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description ecl_预测业务配置表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("ecl_预测业务配置表列表-请求体")
public class EclPredictBusinessConfigListREQ extends PageReq {

    @ApiModelProperty(value = "预测id")
    private Long executePredictId;

}
