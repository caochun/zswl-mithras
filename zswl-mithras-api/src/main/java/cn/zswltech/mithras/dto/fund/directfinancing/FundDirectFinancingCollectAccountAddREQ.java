package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-收款账户
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-收款账户新增-请求体")
public class FundDirectFinancingCollectAccountAddREQ {

    /**
     * 融资id
     */
    @NotNull(message = "融资id不能为空")
    @ApiModelProperty("融资id")
    private Long financingId;

    /**
     * 账户名称
     */
    @NotBlank(message = "账户名称不能为空")
    @ApiModelProperty("账户名称")
    private String accountName;

    /**
     * 账号
     */
    @NotBlank(message = "银行账号不能为空")
    @ApiModelProperty("银行账号")
    private String accountNum;

    /**
     * 开户行名称
     */
    @NotBlank(message = "开户行不能为空")
    @ApiModelProperty("开户行")
    private String accountAddress;

}
