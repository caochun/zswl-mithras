package cn.zswltech.mithras.dto.contract.guarantor;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 合同-担保措施
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-担保措施删除-请求体")
public class ContractGuarantorRemoveREQ extends ContractSingleIdREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
