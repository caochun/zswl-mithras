package cn.zswltech.mithras.dto.contract.baseinfo;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 合同基本信息表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同基本信息表删除-请求体")
public class ContractBaseInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
