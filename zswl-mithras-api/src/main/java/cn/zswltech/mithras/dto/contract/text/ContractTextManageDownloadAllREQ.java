package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/11/19 10:26
 * @description
 */
@Data
@ApiModel(value = "合同文本管理-合同文本下载请求参数")
public class ContractTextManageDownloadAllREQ {

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

}
