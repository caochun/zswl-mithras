package cn.zswltech.mithras.dto.basedata;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/9/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseDataExchangeRateQueryREQ extends PageReq {
    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "月份")
    private Integer month;

    @ApiModelProperty(value = "是否草稿，0-否，1-是")
    private Integer isDraft;
}
