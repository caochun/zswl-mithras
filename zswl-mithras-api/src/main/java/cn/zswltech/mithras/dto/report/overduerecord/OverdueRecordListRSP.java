package cn.zswltech.mithras.dto.report.overduerecord;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * 征信报送-逾期表返回值
 *
 * @author wangchuanhao
 * @date 2023/1/11 11:12 AM
 */
@Data
@ApiModel("征信报送-逾期表返回值")
public class OverdueRecordListRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * {@link cn.zswltech.mithras.report.enums.biz.DataShowTypeEnum}
     */
    @ApiModelProperty(value = "标签")
    private String label;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty("借据编号")
    private String paymentApplyCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("逾期本金")
    private Long overduePrincipal;

    @ApiModelProperty("逾期天数")
    private Integer overdueDay;

    @ApiModelProperty("逾期总额")
    private Long overdueTotal;

    @ApiModelProperty("逾期改变日期")
    private LocalDate overdueChangeDate;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("是否报送")
    private Integer reportFlag;
}
