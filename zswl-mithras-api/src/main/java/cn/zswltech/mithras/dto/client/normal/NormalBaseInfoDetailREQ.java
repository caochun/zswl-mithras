package cn.zswltech.mithras.dto.client.normal;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@ApiModel("自然人基本信息详情-请求体")
@Data
public class NormalBaseInfoDetailREQ extends VersionBaseREQ {
    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;
}
