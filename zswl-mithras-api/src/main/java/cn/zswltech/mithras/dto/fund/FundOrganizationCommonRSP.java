package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: lizhao
 * @date: 2023/06/04 13:36
 */
@ApiModel("机构公共返回体")
@Data
public class FundOrganizationCommonRSP {

    @ApiModelProperty(value = "机构代码")
    private String institutionCode;
}
