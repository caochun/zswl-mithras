package cn.zswltech.mithras.dto.riskcontrol;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author zhaozhengkang
 * @description 集团集中度
 * @date 2023-03-02
 */
@Data
@ApiModel("集团集中度列表-返回体")
public class RiskControlConcentrationGroupListRSP {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 所属集团的client_id
     */
    @ApiModelProperty(value = "所属集团的client_id")
    private Long groupId;

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
     * 数据时点
     */
    @ApiModelProperty(value = "数据时点")
    private LocalDateTime dateTimePoint;

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
     * 是否关联方
     */
    @ApiModelProperty(value = "是否关联方")
    private Integer isRelated;

    /**
     * 注册所在省
     */
    @ApiModelProperty(value = "注册所在省")
    private String province;

    /**
     * 风控行业分类
     */
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    /**
     * 资产五级分类结果
     */
    @ApiModelProperty(value = "资产五级分类结果")
    private String assertClassifyResult;

    /**
     * 是否浙江省内集团协同业务
     */
    @ApiModelProperty(value = "是否浙江省内集团协同业务")
    private Integer zhejiangInnerGroup;

    /**
     * @return 剩余敞口
     */
    public Long getStockValue() {
        return Optional.ofNullable(remainingPrincipal).orElse(0L) - Optional.ofNullable(remainingMargin).orElse(0L);
    }


}
