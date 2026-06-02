package cn.zswltech.mithras.third.repository.tyc.resp;

import lombok.Data;

import java.util.List;

/**
 * 股权出质
 * http://open.tianyancha.com/open/845
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:15 PM
 */
@Data
public class TycEquityInfoResp extends TycListBaseResp<TycEquityInfoResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 质权⼈列表
         */
        private List<PledgeeListDTO> pledgeeList;

        /**
         * 股权出质设⽴登记⽇期
         */
        private Long regDate;

        /**
         * 出质⼈
         */
        private String pledgor;

        /**
         * 质权⼈证照/证件号码
         */
        private String certifNumberR;

        /**
         * 质权⼈
         */
        private String pledgee;

        /**
         * 登记编号
         */
        private String regNumber;

        /**
         * 出质⼈证照/证件号码
         */
        private String certifNumber;

        /**
         * 公司列表
         */
        private List<CompanyListDTO> companyList;

        /**
         * 出质股权标的企业
         */
        private TargetCompanyDTO targetCompany;

        /**
         * 出质⼈列表
         */
        private List<PledgorListDTO> pledgorList;

        /**
         * 出质股权数额
         */
        private String equityAmount;

        /**
         * 股权出质id
         */
        private Long id;

        /**
         * 状态
         */
        private String state;

        /**
         * 股权出质设⽴发布⽇期
         */
        private Long putDate;

    }

    @Data
    public static class TargetCompanyDTO {

        /**
         * 公司名
         */
        private String name;

        /**
         * id
         */
        private String id;
    }

    @Data
    public static class PledgeeListDTO {

        /**
         * 公司名
         */
        private String name;

        /**
         * id
         */
        private String id;
    }

    @Data
    public static class CompanyListDTO {
        /**
         * 公司名
         */
        private String name;

        /**
         * id
         */
        private String id;
    }

    @Data
    public static class PledgorListDTO {

        /**
         * 公司名
         */
        private String name;

        /**
         * id
         */
        private String id;
    }
}
