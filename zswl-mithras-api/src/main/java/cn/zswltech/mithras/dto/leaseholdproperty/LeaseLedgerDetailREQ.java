package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @description 台账界面详情页请求参数实体
 * @since 2023-09-20
 */
@Data
public class LeaseLedgerDetailREQ {

    @ApiModelProperty(value = "记录ID")
    @NotNull(message = "记录ID不能为空")
    private Long id;
}
