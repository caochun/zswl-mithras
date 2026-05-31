package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单记录表
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单记录表列表-返回体")
public class BlackGrayWarehouseRecordListRSP {

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
    * 所属集团
    */
    @ApiModelProperty(value = "所属集团")
    private String membershipGroup;

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
     * 风险规模（万元）
     */
    @ApiModelProperty(value = "风险规模（万元）")
    private BigDecimal riskScale;

    /**
     * 申请原因
     */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    /**
     * 出库时间
     */
    @ApiModelProperty(value = "出库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date planOutboundTime;

    /**
     * 入库时间
     */
    @ApiModelProperty(value = "入库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date warehouseTime;

    /**
     * 报告机构
     */
    @ApiModelProperty(value = "报告机构")
    private String applyOrganization;

    /**
    * 申请时间
    */
    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date applyTime;

    /**
    * 记录状态
    */
    @ApiModelProperty(value = "记录状态")
    private Integer auditStatus;

    /**
     * 当前处理人
     */
    @ApiModelProperty(value = "当前处理人")
    private String currentOperator;

    @ApiModelProperty(value = "入库类型")
    private String source;

    private Long createBy;

    @ApiModelProperty(name = "申请原因下拉")
    private List<String> applyReasonType;

    private List<String> applyReasonName;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private Integer reportFlag;


}
