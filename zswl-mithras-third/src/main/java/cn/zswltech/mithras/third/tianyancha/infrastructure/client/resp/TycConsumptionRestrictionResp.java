package cn.zswltech.mithras.third.tianyancha.infrastructure.client.resp;

import lombok.Data;

import java.util.List;

/**
 * 限制消费令
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:56 PM
 */
@Data
public class TycConsumptionRestrictionResp extends TycListBaseResp<TycConsumptionRestrictionResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 案号
         */
        private String caseCode;

        /**
         * pdf⽂件地址
         */
        private String filePath;

        /**
         * 发布⽇期
         */
        private Long publishDate;

        /**
         * 限制消费者名称
         */
        private String xname;

        /**
         * 限制消费者id
         */
        private String hcgid;

        /**
         * 申请⼈信息
         */
        private String applicant;

        /**
         * 申请⼈id
         */
        private String applicantCid;

        /**
         * 企业信息简称
         */
        private String qyinfoAlias;

        /**
         * 企业信息
         */
        private String qyinfo;

        /**
         * ⽴案时间
         */
        private Long caseCreateTime;

        /**
         * 别名
         */
        private String alias;

        /**
         * 对应表id
         */
        private Long id;

        /**
         * 企业id
         */
        private Long cid;
    }

}
