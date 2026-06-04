package cn.zswltech.mithras.projectprocess.service.bo;

import cn.zswltech.mithras.projectprocess.enums.InterestWayEnum;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/12/13
 * @description
 */
@Data
public class CashFlowCalculateBO {
    /**
     * 授信金额（毫厘）
     */
    private Long creditAmount = 0L;
    /**
     * 保证金（毫厘）
     */
    private Long earnestMoney = 0L;
    /**
     * 首期租金（毫厘）
     */
    private Long downPayment = 0L;
    /**
     * 服务费/咨询费
     */
    private Long consultingFee = 0L;
    /**
     * 首期利息（毫厘）
     */
    private Long firstInstallmentInterest = 0L;
    /**
     * 手续费（毫厘）
     */
    private Long commission = 0L;
    /**
     * 名义价款（毫厘）
     */
    private Long nominalPrice = 0L;
    /**
     * 起租日期
     */
    private LocalDate startDate;
    /**
     * 利率
     */
    private Integer interestRate;
    /**
     * 还款频率枚举name
     */
    private String repayRate;
    /**
     * 还款期数
     */
    private Integer repayTimes;

    /**
     * 租金计算方式
     */
    private String rentalCalcType;

    /**
     * 支付方式。先付：advanced、后付：afterward
     */
    private String payType;

    /**
     * 利息方式
     * {@link InterestWayEnum#name()}
     */
    private String interestWay;

    /**
     * 期限（月）
     */
    private Integer totalMonth;
}
