package cn.zswltech.mithras.dto.fund.financing.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Data
@ApiModel("融资管理-实际贷款时间修改-请求体")
public class FundFinancingActualLoanDateModifyREQ {
    @ApiModelProperty("融资id")
    @NotNull(message = "融资id不能为空")
    private Long financingId;

    @ApiModelProperty("实际贷款时间 yyyy-MM-dd")
    @NotBlank(message = "实际贷款时间不能为空")
    private String actualLoanDate;
}
