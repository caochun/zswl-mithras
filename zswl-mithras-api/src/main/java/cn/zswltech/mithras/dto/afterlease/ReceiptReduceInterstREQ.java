package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 通知财务系统收款
 * @author jackerhe
 * @date 2022/11/17 3:09 PM
 */
@ApiModel("通知财务系统收款请求体")
@Data
public class ReceiptReduceInterstREQ {

    @ApiModelProperty("合同ID")
    @NotNull(message = "合同ID不能为空")
    private Long contractId;

}
