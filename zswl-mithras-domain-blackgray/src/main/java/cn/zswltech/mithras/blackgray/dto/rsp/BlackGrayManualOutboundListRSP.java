package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description 黑灰名单人工出库表
 * @author
 * @date 2023-11-29
 */
@Data
@ApiModel("黑灰名单人工出库表列表-返回体")
public class BlackGrayManualOutboundListRSP {

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

    @ApiModelProperty(value = "申请机构")
    private String applyOrganization;

    /**
    * 黑灰标识
    */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date createTime;

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

    @ApiModelProperty(value = "实际出库时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date actualOutboundTime;

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

    @ApiModelProperty(value = "黑灰名单入库机构")
    private String warehouseOrganization;

    private Long createBy;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date updateTime;

    /**
     * 审批任务id
     */
    private Long auditTaskId;

    /**
     * 上一处理人
     */
    private String preOperator;

    private String latestMsg;

    @ApiModelProperty(value = "是否上报金控 0 不上报，1上报")
    private Integer reportFlag;

}
