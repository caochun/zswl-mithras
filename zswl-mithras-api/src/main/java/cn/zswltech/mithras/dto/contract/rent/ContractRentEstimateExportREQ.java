package cn.zswltech.mithras.dto.contract.rent;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/19
 * @description
 */
@Data
@ApiModel("导出概算租金表-请求体")
public class ContractRentEstimateExportREQ extends VersionBaseREQ {
    @NotNull(message = "主合同id不能为空")
    @ApiModelProperty("主合同id")
    private Long contractId;
}
