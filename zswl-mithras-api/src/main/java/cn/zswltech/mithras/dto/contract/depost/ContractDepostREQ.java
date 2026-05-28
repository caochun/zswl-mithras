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
@ApiModel("保证金退抵-请求体")
public class ContractDepostREQ {
    @NotNull
    @ApiModelProperty("所属的contractId")
    private Long contractId;
}
