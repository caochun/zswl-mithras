package cn.zswltech.mithras.dto.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同管理-合同起租提交审批-请求体")
public class ContractFlowStartRentREQ extends ContractFlowBasicREQ {
    @NotNull(message = "实际起租日不能为空")
    @ApiModelProperty(name = "实际起租日")
    private LocalDate actualLeaseDate;
}
