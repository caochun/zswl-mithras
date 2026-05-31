package cn.zswltech.mithras.blackgray.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 黑灰名单库
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单库编辑-请求体")
public class BlackGrayLibraryModifyREQ {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

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
    * 黑灰名单数据摘要（企业名称统一社会信用代码业务类型）加密后获得
    */
    @ApiModelProperty(value = "黑灰名单数据摘要（企业名称统一社会信用代码业务类型）加密后获得")
    private String dataDigest;

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

    /**
    * 申请时间
    */
    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;

    /**
    * 入库时间
    */
    @ApiModelProperty(value = "入库时间")
    private LocalDateTime warehousTime;

    /**
    * 出库时间
    */
    @ApiModelProperty(value = "出库时间")
    private LocalDateTime planOutboundTime;

    /**
    * 自动出库标识 0自动出库， 1手动出库
    */
    @ApiModelProperty(value = "自动出库标识 0自动出库， 1手动出库")
    private Integer autoOutboundStatus;

    /**
    * 入库原因
    */
    @ApiModelProperty(value = "入库原因")
    private String warehousReason;

    /**
    * 黑灰记录id
    */
    @ApiModelProperty(value = "黑灰记录id")
    private Long recordId;

}
