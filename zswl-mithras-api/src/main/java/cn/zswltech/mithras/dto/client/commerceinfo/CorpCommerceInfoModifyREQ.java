package cn.zswltech.mithras.dto.client.commerceinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("法人工商信息新增-请求体")
public class CorpCommerceInfoModifyREQ extends CorpCommerceInfoAddREQ {

    @ApiModelProperty(value = "指标隶属省份")
//    @NotNull(message = "指标隶属省份不能为空") 这个字段需要根据是否是集团控制，不能一刀切
    private String provinceOfAffiliation;
}
