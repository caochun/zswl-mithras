package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("业务数据主键id-请求体")
public class SinglePkREQ extends VersionBaseREQ {
    @NotNull(message = "业务数据主键id不能为空")
    @ApiModelProperty("业务数据主键id")
    private Long id;
}
