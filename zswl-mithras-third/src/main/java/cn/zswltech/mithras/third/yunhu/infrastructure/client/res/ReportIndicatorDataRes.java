package cn.zswltech.mithras.third.yunhu.infrastructure.client.res;

import cn.zswltech.mithras.third.jinkong.infrastructure.client.res.JinKongBasicRes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/10/9
 * @description 国资快报返回参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportIndicatorDataRes extends JinKongBasicRes {
    private Content data;

    @lombok.Data
    public static class Content {
        private List<ReportIndicatorDataRes.Data> data;

        private int page_num;

        private int page_size;

        private int result_num;

        private String sql;

        private int total_num;
    }

    @lombok.Data
    public static class Data {
        /**
         * 社会信用代码
         */
        private String credit_code;
        /**
         * 核算组织组织编码
         */
        private String account_code;
        /**
         * 合并单体类型（0:单体，1:合并）
         */
        private String report_type;
        /**
         * 组织代码
         */
        private String org_number;
        /**
         * 组织名称
         */
        private String org_name;
        /**
         * 科目编码
         */
        private String indicator_code;
        /**
         * 科目名称
         */
        private String indicator_name;
        /**
         * 币种编码
         */
        private String currency_code;
        /**
         * 币种名称
         */
        private String currency_name;
        /**
         * 年份
         */
        private String fyear;
        /**
         * 期间
         */
        private String fperiod;
        /**
         * 年月yyyy-MM
         */
        private String year_period;
        /**
         * 本月数
         */
        private BigDecimal month_amount;
        /**
         * 本年累计数/期末数
         */
        private BigDecimal current_amount;
        /**
         * 上年同期累计数/期末数
         */
        private BigDecimal lytd_amt;
        /**
         * 时间分区
         */
        private String dt;
    }
}
