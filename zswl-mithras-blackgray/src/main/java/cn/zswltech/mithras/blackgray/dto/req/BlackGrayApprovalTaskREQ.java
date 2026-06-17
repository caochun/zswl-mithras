package cn.zswltech.mithras.blackgray.dto.req;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel("黑灰名单审批列表入参")
public class BlackGrayApprovalTaskREQ extends PageReq {

    // 点击单条查看时传入id
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
     * 业务类型
     */
    @ApiModelProperty(value = "业务类型")
    private String proposedBusinessType;

    /**
     * 报告机构
     */
    @ApiModelProperty(value = "报告机构")
    private String applyOrganization;

    /**
     * 黑灰标识
     */
    @ApiModelProperty(value = "黑灰标识")
    private String blackGrayType;

    @ApiModelProperty(value = "申请时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTimeFrom;
    @ApiModelProperty(value = "申请时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTimeTo;

    /**
     * 审批状态
     * @see cn.zswltech.mithras.blackgray.enums.AuditStatusEnum
     */
    @ApiModelProperty(value = "审批状态[0:待提交,1:审批中,2:已撤回,3:已驳回,4:已完成]")
    private Integer auditStatus;

    @ApiModelProperty(value = "当前操作人")
    private String currentOperator;

}
