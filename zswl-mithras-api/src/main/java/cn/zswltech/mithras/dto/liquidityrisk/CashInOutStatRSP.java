package cn.zswltech.mithras.dto.liquidityrisk;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author luyi
 */
@Data
public class CashInOutStatRSP {
    /**
     * 融资渠道
     */
    private List<String> financialChannel;
    /**
     * 融资编码
     */
    private String financialCode;
    /**
     * 融资金额
     */
    private Long financialAmount;
    /**
     * 现金流出时间
     */
    private LocalDate cashOutDate;
    /**
     * 现金流出金额
     */
    private Long cashOutTotal;
    /**
     * 现金流出本金
     */
    private Long cashOutPrincipal;

    /**
     * 现金流出利息
     */
    private Long cashOutInterest;
    /**
     * 金额不足
     */
    private Boolean lackBalance;

    /**
     * 日期错配
     */
    private Boolean mismatchBalance;

    private Long financingId;

    private String financingType;


    /**
     * 现金流入列表
     */
    private List<InRecord> inRecordList = new ArrayList<>();


    @Accessors(chain = true)
    @Data
    public static class InRecord {
        /**
         * 项目名称
         */
        private String projName;
        /**
         * 合同编号
         */
        private String contractCode;
        /**
         * 合同总金额
         */
        private Long contractAmount;
        /**
         * 现金流入时间
         */
        private LocalDate cashInDate;
        /**
         * 现金流入金额
         */
        private Long cashInTotal;
        /**
         * 现金流入本金
         */
        private Long cashInPrincipal;
        /**
         * 现金流入利息
         */
        private Long cashInInterest;

    }
}
