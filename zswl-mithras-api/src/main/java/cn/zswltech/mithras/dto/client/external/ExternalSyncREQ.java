package cn.zswltech.mithras.dto.client.external;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 客户数据 外部信息 同步接口
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:20 PM
 */
@Data
@ApiModel("外部信息-数据同步")
public class ExternalSyncREQ {

    @NotNull
    @ApiModelProperty("客户id")
    private Long clientId;

}
