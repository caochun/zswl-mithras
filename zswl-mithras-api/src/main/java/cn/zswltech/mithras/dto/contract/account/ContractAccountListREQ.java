package cn.zswltech.mithras.dto.contract.account;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/2
 * @description
 */
@Data
@ApiModel("合同-账户列表请求体")
public class ContractAccountListREQ extends VersionBaseREQ {
    @ApiModelProperty("合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    @ApiModelProperty("账户用途")
    @NotBlank(message = "账户用途不能为空")
    private String accountUse;
}
