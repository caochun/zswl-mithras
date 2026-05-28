package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 基本情况统计((流程节点记录版本表))
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@ApiModel("基本情况统计((流程节点记录版本表))删除-请求体")
public class AssociationBasicSituationLibRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
