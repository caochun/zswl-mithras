package cn.zswltech.mithras.dto.materialsfile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @create: 2022-07-27
 **/
@Data
@ApiModel("集团授信立项资料清单列表-请求体")
public class GroupCreditEstablishMaterialsListListREQ {
    @NotNull
    @ApiModelProperty("groupCreditEstablishId")
    private Long groupCreditEstablishId;
}
