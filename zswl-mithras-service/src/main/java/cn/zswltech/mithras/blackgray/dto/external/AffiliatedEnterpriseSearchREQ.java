package cn.zswltech.mithras.blackgray.dto.external;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName VagueEnterpriseSearchREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/6 2:09 下午
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
public class AffiliatedEnterpriseSearchREQ {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String unifiedSocialCreditCode;

}
