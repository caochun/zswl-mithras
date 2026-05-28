package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/7/3/13:34
 * @description
 */
@Data
public class KpiPerformanceManageMainListRSP {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "年度")
    private Long year;

    @ApiModelProperty(value = "是否启用")
    private Integer status;

    @ApiModelProperty(value = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建人姓名")
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "最后更新人")
    private Long updateBy;

    @ApiModelProperty(value = "最后更新人姓名")
    private String updateByName;

    @ApiModelProperty(value = "最后更新时间")
    private String updateTime;
}
