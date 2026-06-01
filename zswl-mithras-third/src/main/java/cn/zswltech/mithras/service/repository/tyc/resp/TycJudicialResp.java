package cn.zswltech.mithras.service.repository.tyc.resp;

import lombok.Data;

import java.util.List;

/**
 * 司法协助
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:56 PM
 */
@Data
public class TycJudicialResp extends TycListBaseResp<TycJudicialResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 执⾏通知书⽂号
         */
        private String executeNoticeNum;

        /**
         * 执⾏⼈公司id
         */
        private Long executedPersonCid;

        /**
         * 公示⽇期
         */
        private String publicityDate;

        /**
         * 股权被执⾏的企业
         */
        private String stockExecutedCompany;

        /**
         * 被执⾏⼈hgid
         */
        private Long executedPersonHid;

        /**
         * 股权被执⾏的企业id
         */
        private Long stockExecutedCid;

        /**
         * 被执⾏⼈
         */
        private String executedPerson;

        /**
         * 司法协助基本信息id
         */
        private String assId;

        /**
         * 股权数额
         */
        private String equityAmount;

        /**
         * 对应表id
         */
        private Long id;

        /**
         * 类型
         */
        private String typeState;

        /**
         * 执⾏⼈类型，2-⼈，1-公司
         */
        private String executedPersonType;

        /**
         * 执⾏法院
         */
        private String executiveCourt;
    }
}
