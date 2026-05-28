package cn.zswltech.mithras.dto.contract.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/7/29
 * @description
 */
@Data
public class ContractTextInfoREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

    @NotEmpty(message = "合同文本类型不能为空")
    @ApiModelProperty("合同文本类型")
    private List<String> textTypeList;
}
