package cn.zswltech.mithras.dto.contract.price;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhaozhengkang
 * @description
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("报价方案详情-请求体")
public class ContractPriceDetailREQ extends VersionBaseREQ {
    @ApiModelProperty("所属的contractId")
    private Long contractId;
}
