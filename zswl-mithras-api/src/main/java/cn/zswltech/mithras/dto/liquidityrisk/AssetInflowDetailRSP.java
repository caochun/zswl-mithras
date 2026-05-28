package cn.zswltech.mithras.dto.liquidityrisk;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @ClassName AssetInflowDetailREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/5/15 4:23 下午
 * @Version 1.0
 **/
@Data
@ApiModel("流动性风险-流入明细-返回体")
public class AssetInflowDetailRSP {

    //合同金额-总计
    /*@ApiModelProperty(value = "合同金额-总计")
    private Long applyCreditAmountSum;

    //本金-总计
    @ApiModelProperty(value = "本金-总计")
    private Long principalSum;

    //利息-总计
    @ApiModelProperty(value = "利息-总计")
    private Long interestSum;

    //首期租金-总计
    @ApiModelProperty(value = "首期租金-总计")
    private Long downPaymentSum;

    //保证金-总计
    @ApiModelProperty(value = "保证金-总计")
    private Long earnestMoneySum;

    //服务费/咨询费-总计
    @ApiModelProperty(value = "服务费/咨询费-总计")
    private Long consultingFeeSum;

    //合计金额-总计
    @ApiModelProperty(value = "合计金额-总计")
    private Long totalAmountSum;

    //预估流入现金流合计-总计
    @ApiModelProperty(value = "预估流入现金流合计-总计")
    private Long estimatedCashInFlowTotalSum;*/

    private AssetInflowDetailBody sum;

    private AssetInflowDetailBody pageSum;

    //现金流明细
    @ApiModelProperty("现金流明细")
    private PageR<AssetInflowDetailBody> detailBodies;

    @Data
    public class AssetInflowDetailBody {

        //项目名称
        @ApiModelProperty("项目名称")
        private String projName;

        //项目名称
        @ApiModelProperty("项目id")
        private Long projId;

        @ApiModelProperty("立项id")
        private Long establishId;

        @ApiModelProperty("评审id")
        private Long reviewId;

        //数据类型
        @ApiModelProperty("数据类型")
        private String dataType;

        //项目类型
        /*@ApiModelProperty("项目类型")
        private String bizType;*/

        //合同id
        @ApiModelProperty("合同id")
        private Long contractId;

        //合同编号
        @ApiModelProperty("合同编号")
        private String contractCode;

        //申报授信金额-合同金额
        @ApiModelProperty(value = "申报授信金额-合同金额")
        private Long applyCreditAmount;

        //现金流入时间
        @ApiModelProperty(value = "现金流入时间")
        private LocalDate flowInDate;

        //本金
        @ApiModelProperty(value = "本金")
        private Long principal;

        //利息
        @ApiModelProperty(value = "利息")
        private Long interest;

        //首期租金
        @ApiModelProperty(value = "首期租金")
        private Long downPayment;

        //保证金
        @ApiModelProperty(value = "保证金")
        private Long earnestMoney;

        //服务费/咨询费
        @ApiModelProperty(value = "服务费/咨询费")
        private Long consultingFee;

        //合计金额
        @ApiModelProperty(value = "合计金额")
        private Long totalAmount;

        //预估流入现金流合计
        @ApiModelProperty(value = "预估流入现金流合计")
        private Long estimatedCashInFlowTotal;

    }

}
