package cn.zswltech.mithras.service.service.third.jk.res;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmBalanceMfRes extends JinKongBasicRes {
    private Content data;


    @lombok.Data
    public class Content
    {
        private List<Data> data;

        private int page_num;

        private int page_size;

        private int result_num;

        private String sql;

        private int total_num;

    }


    @lombok.Data
    public static class Data {
        /**
         * 组织编码
         */
        private String org_code;

        /**
         * 组织名称
         */
        private String org_name;

        /**
         * 账套币种
         */
        private String basecurrency_name;

        /**
         * 财年
         */
        private Integer f_year;
        /**
         * 期间
         */
        private Integer f_period;
        /**
         * 期间-年月
         */
        private String year_period;
        /**
         * 科目编码
         */
        private String acct_no;
        /**
         * 科目名称
         */
        private String acct_name;
        /**
         * 辅助核算id
         */
        private String fassgrpid;
        /**
         * 期初余额
         */
        private BigDecimal begin_bal;
        /**
         * 本期借方发生
         */
        private BigDecimal debit_cur_prd;
        /**
         * 本期贷方发生
         */
        private BigDecimal credit_cur_prd;
        /**
         * 上年同期借方发生
         */
        private BigDecimal debit_ly_prd;
        /**
         * 上年同期贷方发生
         */
        private BigDecimal credit_ly_prd;
        /**
         * 本年借方发生
         */
        private BigDecimal debit_ytd;
        /**
         * 本年贷方发生
         */
        private BigDecimal credit_ytd;
        /**
         * 上年累计借方发生
         */
        private BigDecimal debit_lytd;
        /**
         * 上年累计贷方发生
         */
        private BigDecimal credit_lytd;
        /**
         * 期末余额
         */
        private BigDecimal end_bal;
        /**
         * 插入时间
         */
        private String insert_time;
        /**
         * 分区字段，yyyyMM
         */
        private String dt;
    }
}
