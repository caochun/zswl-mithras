package cn.zswltech.mithras.dto.incomeSharing;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author yupengfei
 * @date 2024/6/7 15:00
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IncomeSharingListRSP {

    @ApiModelProperty(value = "借据id")
    private Long receiptId;

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "业务分类")
    private String businessType;

    @ApiModelProperty(value = "项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty(value = "项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty(value = "所属部门id")
    private Long belongDeptId;

    @ApiModelProperty(value = "所属部门名称")
    private String belongDeptName;

    @ApiModelProperty(value = "合同状态")
    private String contractStatus;

    @ApiModelProperty(value = "起租日期")
    private LocalDate startRentTime;

    @ApiModelProperty(value = "收入分摊方式")
    private String incomeConfirmType;

    @ApiModelProperty(value = "是否逾期:OVERDUE(逾期)，NOT_OVERDUE(未逾期)")
    private String overdueType;

    @ApiModelProperty(value = "逾期开始日期")
    private LocalDate overdueStartTime;

    @ApiModelProperty(value = "含税收入合计")
    private Long incomeSum;

    @ApiModelProperty(value = "不含税收入合计")
    private Long incomeWithoutTaxSum;

    @ApiModelProperty(value = "已确认收入合计")
    private Long confirmedIncomeSum;

    @ApiModelProperty(value = "未确认收入合计")
    private Long unconfirmedIncomeSum;

    @ApiModelProperty(value = "月度含税收入合计")
    private Long monthlyIncomeSum;

    @ApiModelProperty(value = "月度不含税收入合计")
    private Long monthlyIncomeWithoutTaxSum;

    @ApiModelProperty(value = "月度已确认收入合计")
    private Long monthlyConfirmedIncomeWithoutTaxSum;
}
