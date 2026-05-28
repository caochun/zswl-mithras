package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 高管信息一览表
 * @author hspcadmin
 * @date 2025-08-26
 */
@Data
@ApiModel("高管信息一览表删除-请求体")
public class AssociationSeniorExecutiveInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
