package cn.zswltech.mithras.dto.client.relatedenterprise;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("关联企业变更-请求体")
public class CorpRelatedEnterpriseModifyREQ extends CorpRelatedEnterpriseAddREQ {
    @ApiModelProperty("id，根据id或企业名称修改")
    private Long id;
}
