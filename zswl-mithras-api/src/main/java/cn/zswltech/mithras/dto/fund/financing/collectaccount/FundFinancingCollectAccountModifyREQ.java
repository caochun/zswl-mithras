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
@ApiModel("融资管理-修改对方收款账户-请求体")
public class FundFinancingCollectAccountModifyREQ {

    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 客户名称
     */
    @ApiModelProperty("客户名称")
    private String clientName;
    /**
     * 账户名称
     */
    @ApiModelProperty("账户名称")
    private String accountName;
    /**
     * 银行账号
     */
    @ApiModelProperty("银行账号")
    private String accountNum;
    /**
     * 开户行
     */
    @ApiModelProperty("开户行")
    private String accountAddress;

}
