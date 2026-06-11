package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @description:
 * @author: lizhao
 * @date: 2023/06/04 13:36
 */
@ApiModel("机构公共请求体")
@Data
public class FundOrganizationCommonREQ {

    @ApiModelProperty("统一社会信用代码")
    private String uscCode;

    @ApiModelProperty("机构简称")
    private String abbreviation;
}
