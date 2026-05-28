package cn.zswltech.mithras.dto.budget;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 资产减值预测表
 * @author vico
 * @date 2025-10-14
 */
@Data
@ApiModel("资产减值预测表列表-请求体")
public class EclExecutePredictBaseInfoDetailREQ extends PageReq {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

}
