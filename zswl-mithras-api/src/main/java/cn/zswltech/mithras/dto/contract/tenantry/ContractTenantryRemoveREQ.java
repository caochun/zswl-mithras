package cn.zswltech.mithras.dto.contract.tenantry;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 合同-租赁报价方案表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-承租人/债权人/债务人表删除-请求体")
public class ContractTenantryRemoveREQ extends ContractSingleIdREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
