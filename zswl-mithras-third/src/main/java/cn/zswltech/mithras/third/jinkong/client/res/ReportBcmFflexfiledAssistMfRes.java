package cn.zswltech.mithras.third.jinkong.client.res;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReportBcmFflexfiledAssistMfRes extends JinKongBasicRes {

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
         * 主键
         */
        private Long fentryid;
        /**
         * 横表
         */
        private Long fid;
        /**
         * 核算项目类型
         */
        private String fflexfield;
        /**
         * 核算项目值
         */
        private Long fvalue;
        /**
         * 名称
         */
        private String fname;
        /**
         * 核算维度合并明细
         */
        private String item_name;
        /**
         * 维度名称
         */
        private String dim_name;
        /**
         * 编码
         */
        private String dim_fnumber;
        /**
         * 原表内容
         */
        private String table_content;
        /**
         * 来源分区
         */
        private String source;
    }
}
