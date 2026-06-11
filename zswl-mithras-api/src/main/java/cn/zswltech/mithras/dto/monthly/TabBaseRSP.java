package cn.zswltech.mithras.dto.monthly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/8/2/16:58
 * @description
 */
@Data
public class TabBaseRSP {

    /**
     * 源数据ID
     */
    @ApiModelProperty("源数据ID")
    private Long sourceId;

    /**
     * 关联主表ID
     */
    @ApiModelProperty("关联主表main_id")
    private Long mainId;

    /**
     * 是否推送，默认0未推送
     */
    @ApiModelProperty("是否推送，默认0未推送")
    private Integer isSendCq;

    /**
     * 收入是否已确认
     */
    @ApiModelProperty("收入是否已确认")
    private Integer isConfirmed;

    /**
     * 是否激活，默认1激活
     */
    @ApiModelProperty("是否激活，默认1激活")
    private Integer isEffect;

    /**
     * 批次号
     */
    @ApiModelProperty("批次号")
    private String batchNumber;

    /**
     * tabType MonthlyModuleTypeEnum
     */
    @ApiModelProperty("tab类型 MonthlyModuleTypeEnum")
    private String tabType;

    @ApiModelProperty("是否已经更新 标志已确认之后存在刷新按钮")
    private Integer newUpdated;
}
