package cn.zswltech.mithras.dto.collection;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
/**
 * @description 票据管理表
 * @author vico
 * @date 2023-06-05
 */
@Data
@ApiModel("票据管理表删除-请求体")
public class BillManagementRemoveREQ {

    @NotNull
    @ApiModelProperty("id")
    private Long id;

}
