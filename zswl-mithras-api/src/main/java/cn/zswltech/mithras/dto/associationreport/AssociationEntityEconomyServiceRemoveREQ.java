package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 实体经济服务数据表
 * @author vico
 * @date 2025-04-18
 */
@Data
@ApiModel("实体经济服务数据表删除-请求体")
public class AssociationEntityEconomyServiceRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
