package cn.zswltech.mithras.dto.client.bondinfo;

import cn.zswltech.mithras.dto.PageReq;
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
@ApiModel("发债及评级信息列表-请求体")
public class CorpBondInfoListREQ extends PageReq {
    @NotNull
    @ApiModelProperty("clientId")
    private Long clientId;

    @ApiModelProperty(value = "发起人", required = true)
    private Long startUserId;

    /*@ApiModelProperty(value = "客户详情入口")
    private Boolean isClientDetail = Boolean.FALSE;*/
}
