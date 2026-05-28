package cn.zswltech.mithras.blackgray.dto.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @description black_gray_break_business
 * @author
 * @date 2023-11-28
 */
@Data
@ApiModel("black_gray_break_business列表-返回体")
public class BlackGrayBreakBusinessListRSP {

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date planOutboundTime;

    /**
    * 拟开展业务规模（万元）
    */
    @ApiModelProperty(value = "拟开展业务规模（万元）")
    private Double proposeBusinessScale;

    /**
    * 申请原因
    */
    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    /**
    * 突破流程状态
    */
    @ApiModelProperty(value = "突破流程状态")
    private Integer auditStatus;

    /**
     * 当前处理人
     */
    @ApiModelProperty(value = "当前处理人")
    private String currentOperator;

    @ApiModelProperty(value = "申请时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd yyyy-MM-dd HH:")
    private Date createTime;

    @ApiModelProperty(value = "申请机构")
    private String applyOrganization;

    private Long createBy;

}
