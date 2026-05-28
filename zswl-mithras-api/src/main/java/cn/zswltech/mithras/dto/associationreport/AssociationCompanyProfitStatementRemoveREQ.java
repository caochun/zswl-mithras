package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 公司利润表数据表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("公司利润表数据表删除-请求体")
public class AssociationCompanyProfitStatementRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
