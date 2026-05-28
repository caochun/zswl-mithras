package cn.zswltech.mithras.dto.contract.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("实际租金表期限-返回体")
public class ContractRentPhaseRSP {

    private String label;

    @ApiModelProperty("期限")
    private Integer value;

}
