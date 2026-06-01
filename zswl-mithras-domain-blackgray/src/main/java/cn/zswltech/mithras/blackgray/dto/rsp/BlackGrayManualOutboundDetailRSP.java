package cn.zswltech.mithras.blackgray.dto.rsp;

import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundAddREQ;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @description 黑灰名单人工出库表
 * @author
 * @date 2023-11-29
 */
@Data
@ApiModel("黑灰名单人工出库表详情-返回体")
public class BlackGrayManualOutboundDetailRSP {

    @NotNull
    @ApiModelProperty("id")
    private Long id;


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
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String businessType;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    /**
     * 入库时间
     */
    @ApiModelProperty(value = "入库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date warehouseTime;

    /**
     * 出库时间
     */
    @ApiModelProperty(value = "出库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date planOutboundTime;

    /**
     * 申请原因
     */
    @ApiModelProperty(name = "申请原因")
    private List<BlackGrayManualOutboundAddREQ.Reason> applyReason;

    /**
     * 申请文件keys
     */
    @ApiModelProperty(name = "申请文件keys")
    private List<String> applyFileKeys;

    /**
     * 记录状态
     */
    @ApiModelProperty(value = "记录状态")
    private Integer auditStatus;

    /**
     * 报告机构
     */
    @ApiModelProperty(value = "报告机构")
    private String applyOrganization;

    @ApiModelProperty(value = "所属部门")
    private String applyDept;

    @ApiModelProperty(value = "黑灰名单入库机构")
    private String warehouseOrganization;

    /**
     * 创建人、发起人
     */
    @ApiModelProperty(name = "创建人")
    private Long createBy;

    @ApiModelProperty(value = "创建人code")
    private String createByCode;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    /**
     * 审批任务id
     */
    private Long auditTaskId;

    @ApiModelProperty(value = "来源")
    private String source;

    @ApiModelProperty(value = "出库说明")
    private String message;

    @ApiModelProperty(value = "当前处理人")
    private String currentOperator;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private Integer reportFlag;

}
