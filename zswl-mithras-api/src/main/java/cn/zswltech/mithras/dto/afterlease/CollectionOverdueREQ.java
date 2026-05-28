package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


/**
 * 租后-罚息减免基本表
 * @author jackerhe
 * @date 2022-11-19
 */
@Data
@ApiModel("租后-逾期催收-请求体")
public class CollectionOverdueREQ {

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    @NotNull(message = "合同id不能为空")
    private Long contractId;


}
