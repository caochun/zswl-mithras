package cn.zswltech.mithras.dto.contract.settle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/31
 * @description
 */
@Data
@ApiModel("合同结清补充协议删除-请求体")
public class ContractSettleExtraFileRemoveREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;

    @NotNull(message = "补充协议文件id不能为空")
    @ApiModelProperty("补充协议文件id")
    private Long fileId;
}
