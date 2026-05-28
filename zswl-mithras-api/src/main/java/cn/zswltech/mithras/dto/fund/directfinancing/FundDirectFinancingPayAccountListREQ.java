package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 直接融资-还款账户
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-还款账户列表-请求体")
public class FundDirectFinancingPayAccountListREQ extends PageReq {

    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

}
