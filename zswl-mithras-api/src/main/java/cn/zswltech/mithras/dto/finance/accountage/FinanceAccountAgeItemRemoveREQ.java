package cn.zswltech.mithras.dto.finance.accountage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 帐龄-详情表
 * @author vico
 * @date 2024-09-10
 */
@Data
@ApiModel("帐龄-详情表删除-请求体")
public class FinanceAccountAgeItemRemoveREQ {

    //@NotNull
    @ApiModelProperty("id")
    private List<Long> ids;

    //@NotNull(message = "帐龄id不能为空")
    @ApiModelProperty("帐龄id")
    private Long accountAgeId;

}
