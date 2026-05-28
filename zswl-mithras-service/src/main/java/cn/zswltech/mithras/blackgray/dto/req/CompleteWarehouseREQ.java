package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 黑灰名单撞库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单撞库-请求体")
public class CompleteWarehouseREQ {

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    @NotNull(message = "企业名称不能为空")
    private String enterpriseName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    @NotNull(message = "统一社会信用代码不能为空")
    private String unifiedSocialCreditCode;

    /**
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    @NotNull(message = "业务类型不能为空")
    private String businessType;

    /**
     * 观察期
     */
    @ApiModelProperty(name = "period_under_observation")
    private String periodUnderObservation;

    /**
     * 风险规模（万元）
     */
    @ApiModelProperty(name = "risk_scale")
    private BigDecimal riskScale;

    /**
     * 所属集团
     */
    @ApiModelProperty(name = "membership_group")
    private String membershipGroup;

    /**
     * 所属集团
     */
    @ApiModelProperty(name = "group_credit_code")
    private String groupCreditCode;

    /**
     * 是否集团主企业
     */
    @ApiModelProperty(name = "group_leader_flag")
    private Boolean groupLeaderFlag;

    /**
     * 黑灰标识
     * {@link BlackGrayTypeEnum#name()}
     */
    @ApiModelProperty(name = "black_gray_type")
    private String blackGrayType;

    /**
     * 黑灰标识数字，BLACK=2, GRAY=1，用于聚合、排序，方便查询
     */
    @ApiModelProperty(name = "black_gray_type_num")
    private Integer blackGrayTypeNum;

    /**
     * 优先级排序，越小优先级越高
     */
    @ApiModelProperty(name = "black_gray_sort")
    private Integer blackGraySort;

    /**
     * 申请时间
     */
    @ApiModelProperty(name = "apply_time")
    private Date applyTime;

    /**
     * 申请机构
     */
    @ApiModelProperty(name = "apply_organization")
    private String applyOrganization;

    /**
     * 申请部门
     */
    @ApiModelProperty(name = "apply_dept")
    private String applyDept;

    /**
     * 入库时间
     */
    @ApiModelProperty(name = "warehouse_time")
    private Date warehouseTime;

    /**
     * 共享类型 0金融企业黑名单， 1金控黑名单
     **/
    @ApiModelProperty(name = "share_type")
    private Integer shareType;

    /**
     * 来源
     * {@link BlackGraySourceEnum#name(}
     **/
    @ApiModelProperty(name = "source")
    private String source;

}
