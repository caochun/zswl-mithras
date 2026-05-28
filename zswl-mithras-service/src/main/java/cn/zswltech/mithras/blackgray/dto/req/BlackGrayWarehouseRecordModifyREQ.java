package cn.zswltech.mithras.blackgray.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表编辑-请求体")
public class BlackGrayWarehouseRecordModifyREQ {

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

    @ApiModelProperty(value = "金融企业业务类型")
    private String customiseBusinessType;

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
    @ApiModelProperty(value = "申请原因下拉")
    private List<String> applyReasonType;

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
    private String groupBlackGrayType;

    /**
    * 申请时间
    */
    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;

    /**
    *
    */
    @ApiModelProperty(value = "")
    private String applyOrganization;

    /**
    * 出库时间
    */
    @ApiModelProperty(value = "出库时间")
    private LocalDateTime planOutboundTime;

    /**
    * 入库原因
    */
    @ApiModelProperty(value = "入库原因")
    private String warehouseReason;

    /**
    * 自动出库标识 0自动出库， 1手动出库
    */
    @ApiModelProperty(value = "自动出库标识 0自动出库， 1手动出库")
    private Integer autoOutboundStatus;

    /**
     * 入库文件key
     */
    @ApiModelProperty(name = "入库文件key")
    private List<String> warehouseFileKeys;

    /**
     * 整改文件key
     */
    @ApiModelProperty(name = "整改文件key")
    private List<String> rectifyFileKeys;

    /**
    * 记录状态
    */
    @ApiModelProperty(value = "记录状态")
    private String recordStatus;

    @ApiModelProperty(value = "入库时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date warehouseTime;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private Integer reportFlag;

}
