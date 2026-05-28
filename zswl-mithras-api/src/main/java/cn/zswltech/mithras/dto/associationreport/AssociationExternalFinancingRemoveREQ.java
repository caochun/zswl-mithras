package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 金融局报送-对外融资信息清单表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("金融局报送-对外融资信息清单表删除-请求体")
public class AssociationExternalFinancingRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
