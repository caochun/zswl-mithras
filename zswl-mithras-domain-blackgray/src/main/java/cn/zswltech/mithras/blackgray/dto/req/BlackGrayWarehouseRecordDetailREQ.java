package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author
 * @description 黑灰名单记录表
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表列表-请求体")
public class BlackGrayWarehouseRecordDetailREQ {

    @ApiModelProperty(value = "黑灰名单记录表id")
    private Long blackGrayRecordId;

}
