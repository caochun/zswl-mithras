package cn.zswltech.mithras.third.jinkong.client.res;

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
public class ReportBcmCashflowMfRes extends JinKongBasicRes {

    private Content data;

    @Data
    public class Content
    {
        private List<CashflowData> data;

        private int page_num;

        private int page_size;

        private int result_num;

        private String sql;

        private int total_num;

    }

    @Data
    public static class CashflowData {
        /**
         * id
         */
        private String fid;

        /**
         * 组织编码
         */
        private String forgnumber;

        /**
         * 组织名称
         */
        private Integer forgname;
        /**
         * 年份
         */
        private Integer fyear;
        /**
         * 期间
         */
        private String fperiod;
        /**
         * 项目编码
         */
        private String fprojectnumber;
        /**
         * 项目名称
         */
        private String fprojectname;
        /**
         * 本期发生数
         */
        private BigDecimal fcurramount;
        /**
         * 本年累计数
         */
        private BigDecimal fyearamount;
        /**
         * 上年同期累计数
         */
        private BigDecimal fpreamount;
        /**
         * 币别
         */
        private String fcurrency;
        /**
         * 最后更新时间
         **/
        private String fupdatetime;

        private String organization;
    }
}
