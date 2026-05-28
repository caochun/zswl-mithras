package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/9/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LeaseItemListREQ extends PageReq {
    @NotNull(message = "租赁物管理id不能为空")
    @ApiModelProperty("租赁物管理id")
    private Long id;
}
