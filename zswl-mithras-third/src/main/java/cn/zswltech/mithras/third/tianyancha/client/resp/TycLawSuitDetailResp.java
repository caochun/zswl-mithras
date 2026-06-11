package cn.zswltech.mithras.third.tianyancha.client.resp;

import lombok.Data;

import java.util.List;

/**
 * 法律诉讼详情接口
 *
 * @author wangchuanhao
 * @date 2022/6/20 3:09 PM
 */
@Data
public class TycLawSuitDetailResp extends TycBaseResp {

    private Result result;

    @Data
    public static class Result {

        /**
         * 裁判⽇期
         */
        private String judgeDate;

        /**
         * 本案裁判结果
         */
        private String judgeResult;

        /**
         * 标题
         */
        private String title;

        /**
         * 案号
         */
        private String caseno;

        /**
         * uuid
         */
        private String uuid;

        /**
         * 本院认为
         */
        private String courtConsider;

        /**
         * 相关公司
         */
        private List<CompaniesDTO> companies;

        /**
         * 原告诉称
         */
        private String plaintiffRequest;

        /**
         * 本院查明
         */
        private String courtInspect;

        /**
         * 审理经过
         */
        private String trialProcedure;

        /**
         * 相关律所
         */
        private List<LawFirmsDTO> lawFirms;

        /**
         * ⼀审原告诉称
         */
        private String plaintiffRequestOfFirst;

        /**
         * ⼀审被告辩称
         */
        private String defendantReplyOfFirst;

        /**
         * 审判⼈员
         */
        private String trialPerson;

        /**
         * 诉讼正⽂
         */
        private String plaintext;

        /**
         * 当事⼈信息
         */
        private String appellor;

        /**
         * 法院
         */
        private String court;

        /**
         * 源路径
         */
        private String url;

        /**
         * ⼀审法院认为
         */
        private String courtConsiderOfFirst;

        /**
         * ⽂书类型
         */
        private String doctype;

        /**
         * 审判辅助⼈员
         */
        private String trialAssistPerson;

        /**
         * 案件类型
         */
        private String casetype;

        /**
         * 上诉⼈诉称
         */
        private String appellantRequest;

        /**
         * 被告辩称
         */
        private String defendantReply;

        /**
         * ⼀审法院查明
         */
        private String courtInspectOfFirst;

        /**
         * 被上诉⼈辩称
         */
        private String appelleeArguing;
    }

    @Data
    public static class CompaniesDTO {

        /**
         * 公司名
         */
        private String title;

    }

    @Data
    public static class LawFirmsDTO {

        /**
         * 律所名
         */
        private String title;

    }


}
