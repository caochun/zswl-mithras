package cn.zswltech.mithras.dto.client.relatedenterprise;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author luyi
 */
@Data
@ApiModel("关联企业删除-请求体")
public class CorpRelatedEnterpriseRemoveREQ {

    @ApiModelProperty("id，根据id或企业名称删除")
    private Long id;

    @ApiModelProperty("企业名称，根据id或企业名称删除")
    private String enterpriseName;

    @ApiModelProperty("客户id")
    private Long clientId;
}
