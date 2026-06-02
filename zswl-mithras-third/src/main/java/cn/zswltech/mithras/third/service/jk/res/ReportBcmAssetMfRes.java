package cn.zswltech.mithras.third.service.jk.res;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资产负债表
 * risk_metric_factor
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmAssetMfRes extends JinKongBasicRes {

    private Content data;

    @Data
    public class Content
    {
        private List<AssetData> data;

        private int page_num;

        private int page_size;

        private int result_num;

        private String sql;

        private int total_num;

    }

    @Data
    public static class AssetData {
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
         * 期末余额
         */
        private BigDecimal fendbalance;
        /**
         * 年初余额
         */
        private BigDecimal fbeginbalance;
        /**
         * 币别
         */
        private String fcurrency;
        /**
         * 最后更新时间
         */
        private String fupdatetime;
    }
}
