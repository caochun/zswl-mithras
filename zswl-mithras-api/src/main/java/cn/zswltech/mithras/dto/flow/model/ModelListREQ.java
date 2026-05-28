package cn.zswltech.mithras.dto.flow.model;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模型列表查询
 *
 * @author wangchuanhao
 * @date 2022/10/25 2:01 PM
 */
@Data
public class ModelListREQ extends PageReq {

    @ApiModelProperty("模型key")
    private String modelKey;

    @ApiModelProperty("模型名称")
    private String modelName;

}
