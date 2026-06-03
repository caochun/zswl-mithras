package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangxiong
 * @date 2024/8/5/19:07
 * @description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyFreshSingleREQ {

    @ApiModelProperty(value = "主数据ID")
    private Long mainId;

    /**
     * 这个用来推送单条记录
     */
    @ApiModelProperty(value = "批次号")
    private Long singleRecordId;

    /**
     * {@link cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum}
     */
    @ApiModelProperty(value = "TabType")
    private String tabType ;
}
