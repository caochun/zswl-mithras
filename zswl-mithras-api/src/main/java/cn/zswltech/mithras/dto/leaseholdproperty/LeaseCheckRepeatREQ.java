package cn.zswltech.mithras.dto.leaseholdproperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yangxiong
 * @description 租赁物查重保存实体，暂时一个字段用一个类，方便后续拓展字段
 * @since 2023-09-25
 */
@Data
public class LeaseCheckRepeatREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "记录ID不能为空")
    private Long id;

    @ApiModelProperty(value = "中登网查重日期")
    @NotNull(message = "中登网查重日期不能为空")
    private String checkRepeatDate;
}
