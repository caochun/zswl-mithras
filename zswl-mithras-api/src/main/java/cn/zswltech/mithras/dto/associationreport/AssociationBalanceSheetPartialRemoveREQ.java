package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 资产负债表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("资产负债表删除-请求体")
public class AssociationBalanceSheetPartialRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
