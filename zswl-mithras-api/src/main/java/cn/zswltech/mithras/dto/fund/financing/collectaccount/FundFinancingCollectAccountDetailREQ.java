package cn.zswltech.mithras.dto.fund.financing.collectaccount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 
 * @author: jackerhe 
 * @date: 2023/2/20 2:57 下午
 **/
@Data
@ApiModel("融资管理-对方收款账户-请求体")
public class FundFinancingCollectAccountDetailREQ {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

}
