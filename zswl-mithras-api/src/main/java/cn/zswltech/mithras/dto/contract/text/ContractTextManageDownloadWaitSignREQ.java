package cn.zswltech.mithras.dto.contract.text;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/9 11:41
 * @description
 */
@Data
public class ContractTextManageDownloadWaitSignREQ {

    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;

    @ApiModelProperty(value = "资料记录ID列表")
    @NotEmpty(message = "资料记录ID列表不能为空")
    private List<Long> fileRecordIds;
}
