package cn.zswltech.mithras.dto.contract.rent;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName ContractRentActualRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/8/30 5:33 下午
 * @Version 1.0
 **/
@Data
public class ContractRentActualRSP extends ListBaseRSP {

    @ApiModelProperty("借据id")
    private Long receiptId;

    @ApiModelProperty("现金流编号")
    private String cashFlowCode;

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("租金（毫厘）")
    private Long rent;

    @ApiModelProperty("本金（毫厘）")
    private Long principal;

    @ApiModelProperty("利息（毫厘）")
    private Long interest;

    @ApiModelProperty("剩余本金（毫厘）")
    private Long remainingPrincipal;
    
    @ApiModelProperty("是否已收款")
    private Boolean received;

    @ApiModelProperty("收款时间")
    private LocalDate receivedDate;

    @ApiModelProperty("收款金额")
    private Long receivedAmount;
}
