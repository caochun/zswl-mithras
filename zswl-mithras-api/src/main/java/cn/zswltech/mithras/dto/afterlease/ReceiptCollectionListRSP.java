package cn.zswltech.mithras.dto.afterlease;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName ReceiptCollectionListRSP
 * @Description
 * @Author jackerhe
 * @Date 2022/11/19 10:26 上午
 * @Version 1.0
 **/
@Data
@ApiModel("催收-借据卡-逾期情况汇总")
public class ReceiptCollectionListRSP {

    /**
     * 期项
     */
    @ApiModelProperty("期项")
    private Integer phase;

    /**
     * 计划收款日期
     */
    @ApiModelProperty("计划收款日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private LocalDate planCollectionDate;

    /**
     * 本金
     */
    @ApiModelProperty("本金")
    private Long principal;

    /**
     * 利息
     */
    @ApiModelProperty("利息")
    private Long interest;

    /**
     * 累计罚息
     */
    @ApiModelProperty("累计罚息")
    private Long penaltyInterest;

    @ApiModelProperty("逾期天数")
    private Long overdueDays;

    /**
     * 核销状态
     */
    @ApiModelProperty("核销状态")
    private String writeOffStatus;

}
