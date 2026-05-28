package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinanceInfoREQ {
    @NotNull(message = "机构/产品ID不能为空")
    @ApiModelProperty("机构/产品的id")
    private Long id;

    @NotNull(message = "是否直融标识不能为空")
    @ApiModelProperty("是否直融，0-否，1-是")
    private Integer isDirect;
}
