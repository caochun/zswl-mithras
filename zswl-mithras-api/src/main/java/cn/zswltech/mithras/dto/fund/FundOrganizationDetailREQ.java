package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 13:36
 */
@ApiModel("机构详情请求体")
@Data
public class FundOrganizationDetailREQ {
    @ApiModelProperty("id")
    @NotNull
    private Long id;
}
