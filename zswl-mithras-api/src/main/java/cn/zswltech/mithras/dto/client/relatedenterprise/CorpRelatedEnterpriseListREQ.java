package cn.zswltech.mithras.dto.client.relatedenterprise;

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
@ApiModel("关联企业列表-请求体")
public class CorpRelatedEnterpriseListREQ extends PageReq {

    @NotNull
    @ApiModelProperty("clientId")
    private Long clientId;

    @ApiModelProperty("持股比例:shareholdingRatio,注册资本：registerCapital,投资金额：investAmount")
    private String orderField;

    @ApiModelProperty("倒排：desc，正排：asc")
    private String orderType;

    @ApiModelProperty(value = "发起人", required = true)
    private Long startUserId;

    /*@ApiModelProperty(value = "客户详情入口")
    private Boolean isClientDetail = Boolean.FALSE;*/
}
