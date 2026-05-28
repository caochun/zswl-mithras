package cn.zswltech.mithras.dto.fund.financing.pledge;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author: jackerhe
 * @date: 2023/2/20 2:48 下午
 **/
@Data
@ApiModel("融资管理-根据项目id获取合同列表-返回体")
public class FundFinancingPledgeContractListRSP {

    /**
     * 项目评审id
     */
    @ApiModelProperty("合同id")
    private Long contractId;
    /**
     * 项目名称
     */
    @ApiModelProperty("合同编号")
    private String contractCode;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @ApiModelProperty("业务类型。租赁、保理、转租赁")
    private String bizType;

    /**
     * 计划付款金额-合同金额
     */
    @ApiModelProperty("合同金额")
    private Long applyCreditAmount;

    /**
     * 实际起租日
     **/
    @ApiModelProperty("实际起租日")
    private LocalDate actualLeaseDate;

    /**
     * 合同结清时间
     */
    @ApiModelProperty("合同结清时间")
    private LocalDateTime settleTime;

    /**
     * 剩余未还本金
     */
    @ApiModelProperty("剩余未还本金")
    private Long remainingUnpaidPrincipal;

}
