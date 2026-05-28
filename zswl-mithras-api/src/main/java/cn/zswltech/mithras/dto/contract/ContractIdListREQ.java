package cn.zswltech.mithras.dto.contract;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 合同-担保措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-担保措施列表-请求体")
@NoArgsConstructor
@AllArgsConstructor
public class ContractIdListREQ extends VersionBaseREQ {

    @ApiModelProperty(value = "合同id")
    private Long contractId;

}
