package cn.zswltech.mithras.dto.margin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-08-15
 **/

@Data
public class MarginBaseInfoRSP {

    @ApiModelProperty("所属合同")
    private ContractInfo contractInfo;

    @ApiModelProperty("保证金code")
    private String marginCode;

    @ApiModelProperty("收款日期")
    private LocalDate collectionDate;

    @ApiModelProperty("计划收款日期")
    private LocalDate planMarginDate;

    @ApiModelProperty("计划收取保证金")
    private Long planMarginAmount;

    @ApiModelProperty("保证金金额")
    private Long marginAmount;

    @ApiModelProperty("已退金额")
    private Long backAmount;

    @ApiModelProperty("已抵扣金额")
    private Long deductAmount;

    @ApiModelProperty("可退金额")
    private Long canBackAmount;

    @ApiModelProperty("累计应收金额")
    private Long totalReceivableAmount;

    @ApiModelProperty("未收金额")
    private Long notReceivableAmount;

    @Data
    public static class ContractInfo{
        @ApiModelProperty("合同id")
        private Long contractId;

        @ApiModelProperty("合同编号")
        private String contractCode;

        @ApiModelProperty("客户名称")
        private String clientName;

        @ApiModelProperty("项目名称")
        private String projName;

        @ApiModelProperty("类别")
        private String contractType;

        @ApiModelProperty("项目主办")
        private String projSponsorUserName;

        @ApiModelProperty("业务部门")
        private String bizDept;

        @ApiModelProperty("合同状态")
        private String contractStatus;

    }

}
