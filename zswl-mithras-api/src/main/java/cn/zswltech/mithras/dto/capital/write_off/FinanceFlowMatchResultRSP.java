package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 自动核销预核销记录VO
 * @author bigbear
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FinanceFlowMatchResultRSP {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "源表数据ID")
    private Long sourceId;

    @ApiModelProperty(value = "客户ID")
    private Long clientId;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "源数据名称")
    private String sourceBusinessCode;

    @ApiModelProperty(value = "现金流项目")
    private String cashFlowItem;

    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "应付时间")
    private LocalDate shouldWriteOffTime;

    @ApiModelProperty(value = "应付金额")
    private Long shouldWriteOffAmount;

    @ApiModelProperty(value = "未付金额")
    private Long noWriteOffAmount;

    @ApiModelProperty(value = "本次核销金额")
    private Long thisWriteOffAmount;

    @ApiModelProperty(value = "是否是系统生成")
    private Integer isSystemGenerate;

    @ApiModelProperty(value = "业务模块")
    private String businessModel;

    @ApiModelProperty(value = "关联银行流水集合表ID")
    private Long financeFlowCollectionId;

    @ApiModelProperty(value = "银行流水编号")
    private String bankFlowNo;

    @ApiModelProperty(value = "批次号")
    private String batchNumber;
}
