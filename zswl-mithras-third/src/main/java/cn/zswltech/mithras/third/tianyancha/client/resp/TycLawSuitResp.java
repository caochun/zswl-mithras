package cn.zswltech.mithras.third.tianyancha.client.resp;

import lombok.Data;

import java.util.List;

/**
 * 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:56 PM
 */
@Data
public class TycLawSuitResp extends TycListBaseResp<TycLawSuitResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * ⽂书类型
         */
        private String docType;

        /**
         * 天眼查url（Web）
         */
        private String lawsuitUrl;

        /**
         * 天眼查url（H5）
         */
        private String lawsuitH5Url;

        /**
         * 案件名称
         */
        private String title;

        /**
         * 审理法院
         */
        private String court;

        /**
         * 裁判⽇期
         */
        private String judgeTime;

        /**
         * uuid
         */
        private String uuid;

        /**
         * 案号
         */
        private String caseNo;

        /**
         * 案件类型
         */
        private String caseType;

        /**
         * 案由
         */
        private String caseReason;

        /**
         * 涉案⽅
         */
        private List<CasePersonsDTO> casePersons;

        /**
         * 案件⾦额
         */
        private String caseMoney;

        /**
         * 发布⽇期
         */
        private Long submitTime;

        /**
         * 对应表ID
         */
        private Long id;

    }

    @Data
    public static class CasePersonsDTO {

        /**
         * 结果标签
         */
        private String result;

        /**
         * 案件身份
         */
        private String role;

        /**
         * id
         */
        private String gid;

        /**
         * 裁判结果对应的情感倾向（1=正⾯；0=中性；-1=负⾯）
         */
        private Integer emotion;

        /**
         * 名称
         */
        private String name;

        /**
         * 类型（1=⼈员；2=公司）
         */
        private String type;
    }

}
