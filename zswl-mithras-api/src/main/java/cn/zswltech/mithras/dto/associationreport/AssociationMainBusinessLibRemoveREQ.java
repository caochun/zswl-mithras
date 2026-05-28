package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 金融协会报送-主要业务清单表(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("金融协会报送-主要业务清单表(流程节点记录版本表)删除-请求体")
public class AssociationMainBusinessLibRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
