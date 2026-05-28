package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表删除-请求体")
public class BlackGrayWarehouseRecordRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private List<Long> ids;

}
