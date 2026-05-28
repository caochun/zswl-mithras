package cn.zswltech.mithras.dto.client.commerceinfo;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("法人工商信息新增-请求体")
public class CorpCommerceInfoDetailREQ extends VersionBaseREQ {

    @NotNull
    @ApiModelProperty(value = "客户id", required = true)
    private Long clientId;

    @ApiModelProperty(value = "发起人", required = true)
    private Long startUserId;

    /*@ApiModelProperty(value = "客户详情入口")
    private Boolean isClientDetail = Boolean.FALSE;*/
}
