package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 基本情况统计表
 * @author hspcadmin
 * @date 2025-08-22
 */
@Data
@ApiModel("基本情况统计表删除-请求体")
public class AssociationBasicSituationRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
