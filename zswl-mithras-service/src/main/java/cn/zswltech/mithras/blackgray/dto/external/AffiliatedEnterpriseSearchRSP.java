package cn.zswltech.mithras.blackgray.dto.external;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName 所属企业
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/6 2:09 下午
 * @Version 1.0
 **/
@Data
public class AffiliatedEnterpriseSearchRSP {

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
