package cn.zswltech.mithras.third.repository.tyc.resp;

import lombok.Data;

import java.util.List;

/**
 * 动产抵押
 * http://open.tianyancha.com/open/844
 *
 * @author wangchuanhao
 * @date 2022/6/20 4:20 PM
 */
@Data
public class TycMortgageInfoResp extends TycListBaseResp<TycMortgageInfoResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 基本信息
         */
        private BaseInfoDTO baseInfo;

        /**
         * 抵押权⼈信息
         */
        private List<PeopleInfoDTO> peopleInfo;

        /**
         *
         */
        private List<PawnInfoListDTO> pawnInfoList;

        /**
         *
         */
        private List<ChangeInfoListDTO> changeInfoList;
    }

    @Data
    public static class BaseInfoDTO {

        /**
         * 被担保债权数额
         */
        private String amount;

        /**
         * 注销⽇期
         */
        private Long cancelDate;

        /**
         * 公示⽇期
         */
        private Long publishDate;

        /**
         * 登记⽇期
         */
        private String regDate;

        /**
         * 备注
         */
        private String remark;

        /**
         * 被担保债权种类
         */
        private String type;

        /**
         * 概况备注（已废弃）
         */
        private String overviewRemark;

        /**
         * 概况债务⼈履⾏债务的期限（已废弃）
         */
        private String overviewTerm;

        /**
         * 概况数额（已废弃）
         */
        private String overviewAmount;

        /**
         * 登记机关
         */
        private String regDepartment;

        /**
         * 登记编号
         */
        private String regNum;

        /**
         * 概况担保的范围（已废弃）
         */
        private String overviewScope;

        /**
         * 担保范围
         */
        private String scope;

        /**
         * 概况种类（已废弃）
         */
        private String overviewType;

        /**
         * 债务⼈履⾏债务的期限
         */
        private String term;

        /**
         * 表id
         */
        private Long id;

        /**
         * 注销原因
         */
        private String cancelReason;

        /**
         * 状态
         */
        private String status;

        /**
         * 省份
         */
        private String base;
    }

    @Data
    public static class PeopleInfoDTO {
        /**
         * 抵押权⼈证照/证件类型
         */
        private String liceseType;

        /**
         * 抵押权⼈名称
         */
        private String peopleName;

        /**
         * 证照/证件号码
         */
        private String licenseNum;
    }

    @Data
    public static class PawnInfoListDTO {

        /**
         * 名称
         */
        private String pawnName;

        /**
         * 所有权归属
         */
        private String ownership;

        /**
         * 备注
         */
        private String remark;

        /**
         * 数量、质量、状况、所在地等情况
         */
        private String detail;
    }

    @Data
    public static class ChangeInfoListDTO {

        /**
         * 变更内容
         */
        private String changeContent;

        /**
         * 变更⽇期
         */
        private Long changeDate;

    }

}
