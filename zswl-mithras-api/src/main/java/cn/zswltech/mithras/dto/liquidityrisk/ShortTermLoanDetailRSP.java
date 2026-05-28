package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @ClassName AssetInflowDetailREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/15 4:23 下午
 * @Version 1.0
 **/
@Data
@ApiModel("流动性风险-短期贷款明细查询-返回体")
public class ShortTermLoanDetailRSP {

    /*//融资金额-总计
    @ApiModelProperty(value = "融资金额-总计")
    private Long financingAmountSum;

    //融资应还本金-总计
    @ApiModelProperty(value = "融资应还本金-总计")
    private Long principleAmountSum;

    //融资应还利息-总计
    @ApiModelProperty(value = "融资应还利息-总计")
    private Long interestAmountSum;

    //合计金额-总计
    @ApiModelProperty(value = "合计金额-总计")
    private Long totalAmountSum;*/

    private ShortTermLoanDetailBody sum;

    private ShortTermLoanDetailBody pageSum;

    //现金流明细
    @ApiModelProperty("现金流明细")
    private PageR<ShortTermLoanDetailBody> detailBodies;

    @Data
    public class ShortTermLoanDetailBody{

        //融资机构id
        @ApiModelProperty("融资机构id")
        private List<Long> organizationId;

        //融资机构名称
        @ApiModelProperty("融资机构名称")
        private List<String> organizationName;

        //是否已还款 0,未还款，1已还款
        @ApiModelProperty("是否已还款 0,未还款，1已还款")
        private Integer isRepayment;

        //融资编号
        @ApiModelProperty("融资编号")
        private String financingCode;

        //融资id
        @ApiModelProperty("融资id")
        private Long financingId;

        //融资金额
        @ApiModelProperty("融资金额")
        private Long financingAmount;

        //还本日
        @ApiModelProperty("还本日")
        private LocalDate repaymentDate;

        //剩余日
        @ApiModelProperty("剩余日")
        private long remainingDays;

        //融资应还本金
        @ApiModelProperty("融资应还本金")
        private Long principleAmount;

        //融资应还利息
        @ApiModelProperty("融资应还利息")
        private Long interestAmount;

        //合计金额
        @ApiModelProperty(value = "合计金额")
        private Long totalAmount;

    }

}
