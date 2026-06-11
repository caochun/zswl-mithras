package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author dingqi
 * @date 2023/4/12
 * @description
 */
@ApiModel("担保机构下拉框-请求体")
@Data
public class FundGuaranteeAgencyPullDownREQ {
    @ApiModelProperty("担保机构名称")
    private String guaranteeAgencyName;

    @ApiModelProperty("融资机构id")
    private Long organizationId;
}
