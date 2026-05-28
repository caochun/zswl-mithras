package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Optional;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@Data
@ApiModel("客户集中度列表-返回体")
public class RiskControlConcentrationClientListRSP {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * 是否浙江省内集团协同业务（1是0否）
     */
    @ApiModelProperty(value = "是否浙江省内集团协同业务（1是0否）")
    private Integer zhejiangInnerGroup;

    /**
     * 所属集团的名称
     */
    @ApiModelProperty(value = "所属集团的名称")
    private String groupName;

    /**
     * 预警状态
     */
    @ApiModelProperty(value = "预警状态")
    private String state;

    /**
     * 剩余本金
     */
    @ApiModelProperty(value = "剩余本金")
    private Long remainingPrincipal;

    @ApiModelProperty("剩余保证金")
    private Long remainingMargin;

    /**
     * 集中度占比 %展示
     */
    @ApiModelProperty(value = "集中度占比 %展示")
    private Long concentrationRatio;

    /**
     * 不良余额
     */
    @ApiModelProperty(value = "不良余额")
    private Long badBalance;

    /**
     * 不良余额占比
     */
    @ApiModelProperty(value = "不良余额占比")
    private Long badBalanceRatio;

    /**
     * @return 剩余敞口
     */
    public Long getStockValue() {
        return Optional.ofNullable(remainingPrincipal).orElse(0L) - Optional.ofNullable(remainingMargin).orElse(0L);
    }

}
