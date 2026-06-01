package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库新增-请求体")
public class BlackGrayLibraryAddREQ {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String unifiedSocialCreditCode;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**
     * 观察期
     */
    @ApiModelProperty(value = "观察期")
    private String periodUnderObservation;

    /**
     * 风险规模（万元）
     */
    @ApiModelProperty(value = "风险规模（万元）")
    private Double riskScale;

    /**
     * 所属集团
     */
    @ApiModelProperty(value = "所属集团")
    private String membershipGroup;

    /**
     * 集团是否纳入黑名单
     */
    @ApiModelProperty(value = "集团是否纳入黑名单")
    private Integer blacklistStatus;

    /**
     * 申请原因
     */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    /**
     * 集团黑灰标识
     */
    @ApiModelProperty(value = "集团黑灰标识")
    private String groupBlackgrayType;

}
