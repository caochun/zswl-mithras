package cn.zswltech.mithras.blackgray.dto.external;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author: jackerhe 
 * @date: 2024/1/17 11:38 上午
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class AssociatedEnterpriseBatchSearchRSP extends AssociatedEnterpriseSearchRSP{

    @ApiModelProperty("是否主企业，1 是，0不是")
    private Integer isAffiliated;

    /**
     * 集团名称
     */
    @ApiModelProperty(value = "所属集团名称")
    private String groupEnterpriseName;

    /**
     * 集团信用代码
     */
    @ApiModelProperty(value = "集团信用代码")
    private String groupCreditCode;
}
