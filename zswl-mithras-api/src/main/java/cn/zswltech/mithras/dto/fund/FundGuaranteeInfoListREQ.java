package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description fund_guarantee_info
 * @date 2022-12-13
 */
@Data
@ApiModel("担保信息列表-请求体")
public class FundGuaranteeInfoListREQ extends PageReq {
    @ApiModelProperty("所属担保机构id")
    @NotNull(message = "担保机构不能为空")
    private Long agencyId;
}
