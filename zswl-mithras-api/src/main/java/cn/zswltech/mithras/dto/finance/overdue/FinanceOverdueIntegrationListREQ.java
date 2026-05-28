package cn.zswltech.mithras.dto.finance.overdue;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 应收逾期集成表
 * @author vico
 * @date 2025-09-15
 */
@Data
@ApiModel("应收逾期集成表列表-请求体")
public class FinanceOverdueIntegrationListREQ extends PageReq {

    @ApiModelProperty(value = "收款编号")
    private String collectionCode;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
     * 收款编号
     */
    @ApiModelProperty(value = "任务id编号")
    @NotNull(message = "计划id不能为空")
    private Long overdueReportId;
    /**
     * 单据状态
     */
    @ApiModelProperty(value = "单据状态 OverdueRecordStatueEnum")
    private String recordStatus;


    /**
     * 单据账龄起算日
     */
    @ApiModelProperty(value = "单据账龄起算日")
    private LocalDate recordStartDateFrom;
    @ApiModelProperty(value = "单据账龄起算日")
    private LocalDate recordStartDateTo;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @ApiModelProperty("版本号")
    private String businessVersion;
}
