package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单突破申请
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("黑灰名单突破申请详情-返回体")
public class BlackGrayBreakBusinessDetailRSP {

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 黑灰名单id
     */
    @ApiModelProperty(value = "黑灰名单id")
    private Long blackGrayId;

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
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    /**
     * 拟开展业务类型
     */
    @ApiModelProperty(value = "拟开展业务类型")
    private String proposedBusinessType;

    /**
     * 原计划出库时间
     */
    @ApiModelProperty(value = "原计划出库时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date planOutboundTime;

    /**
     * 拟开展业务规模（万元）
     */
    @ApiModelProperty(value = "拟开展业务规模（万元）")
    private BigDecimal proposeBusinessScale;

    /**
     * 申请原因
     */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    /**
     * 申请原因
     */
    @ApiModelProperty(value = "申请原因附件")
    private List<String> applyFileKeys;

    /**
     * 审批任务id
     */
    private Long auditTaskId;


    /**
     * 创建人、发起人
     */
    @ApiModelProperty(name = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建人code")
    private String createByCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    @ApiModelProperty(value = "审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成]")
    private Integer auditStatus;

    @ApiModelProperty(value = "来源")
    private String source;


}
