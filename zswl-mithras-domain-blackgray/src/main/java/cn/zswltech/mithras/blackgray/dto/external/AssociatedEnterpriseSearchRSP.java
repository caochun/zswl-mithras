package cn.zswltech.mithras.blackgray.dto.external;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName VagueEnterpriseSearchREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/6 2:09 下午
 * @Version 1.0
 **/
@Data
public class AssociatedEnterpriseSearchRSP {

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

    private List<AssociatedEnterpriseSearchRSP> children;

}
