package cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class TycDataList {

    private String total;

    private List<CompanyList> items;


    @Data
    public static class CompanyList{

        /**
         * 企业状态。e.g：存续
         */
        private String regStatus;

        /**
         * 成立日期。e.g："2001-06-05 00：00：00.0"
         */
        private LocalDateTime establishTime;

        /**
         * 注册资本。e.g：77800.32万人民币
         */
        private String regCapital;

        /**
         * 机构类型-1：公司；2：香港企业；3：社会组织；4：律所；5：事业单位；6：基金会；7-不存在法人、注册资本、统一社会信用代码、经营状态;8：台湾企业；9-新机构
         */
        private Integer companyType;

        /**
         * 匹配原因
         */
        private String matchType;

        /**
         * 法人类型，1 人 2 公司
         */
        private Integer type;

        /**
         * 法人。e.g：姬苏春
         */
        private String legalPersonName;

        /**
         * 注册号，e.g：520000000005018
         */
        private String regNumber;

        /**
         * 统一社会信用代码。e.g：91520000214434146R
         */
        private String creditCode;

        /**
         * 企业名。e.g：中航重机股份有限公司
         */
        private String name;

        /**
         * 企业id。e.g：11684584
         */
        private Long id;

        /**
         * 组织机构代码。e.g：214434146
         */
        private String orgNumber;

        /**
         * 省份简称。e.g：北京
         */
        private String base;

    }

}
