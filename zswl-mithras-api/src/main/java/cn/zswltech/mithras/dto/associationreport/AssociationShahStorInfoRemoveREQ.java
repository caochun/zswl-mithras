package cn.zswltech.mithras.dto.associationreport;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 股东股权信息一览表-股东股权信息
 * @author hspcadmin
 * @date 2025-08-25
 */
@Data
@ApiModel("股东股权信息一览表-股东股权信息删除-请求体")
public class AssociationShahStorInfoRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
