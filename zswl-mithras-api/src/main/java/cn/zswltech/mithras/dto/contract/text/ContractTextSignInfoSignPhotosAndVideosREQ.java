package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author bigbear
 * @date 2024/12/9 21:25
 * @description
 */
@Data
public class ContractTextSignInfoSignPhotosAndVideosREQ {

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;
}
