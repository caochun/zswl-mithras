package cn.zswltech.mithras.dto.contract.account;
import cn.zswltech.mithras.dto.contract.ContractSingleIdREQ;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 合同-收款账户表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-收款账户表删除-请求体")
public class ContractAccountRemoveREQ extends ContractSingleIdREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;



}
