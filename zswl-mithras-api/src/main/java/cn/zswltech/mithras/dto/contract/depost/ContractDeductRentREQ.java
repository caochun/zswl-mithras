package cn.zswltech.mithras.dto.contract.depost;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("合同抵扣租金信息id")
public class ContractDeductRentREQ {
    @NotNull
    @ApiModelProperty("id")
    private Long id;
}
