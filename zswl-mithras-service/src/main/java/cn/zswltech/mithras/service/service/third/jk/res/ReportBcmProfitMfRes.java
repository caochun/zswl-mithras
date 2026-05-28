package cn.zswltech.mithras.service.service.third.jk.res;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2023/8/10 11:28 上午
 * 利润表返回体
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmProfitMfRes extends JinKongBasicRes {

    private Content data;

    @Data
    public class Content
    {
        private List<ProfitData> data;

        private int page_num;

        private int page_size;

        private int result_num;

        private String sql;

        private int total_num;

    }

    @Data
    public static class ProfitData {
        /**
         * id
         */
        private String fid;

        /**
         * 组织编码
         */
        private String forgnumber;
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
    }
}
