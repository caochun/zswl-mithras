package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author junke
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageReq extends VersionBaseREQ {
    @ApiModelProperty("分页，默认1")
    private Integer page = 1;
    @ApiModelProperty("页大小， 默认20")
    private Integer pageSize = 20;
}
