package cn.zswltech.mithras.third.tianyancha.client.resp;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

/**
 * @author luyi
 */
@Data
public class TycBaseInfo {

    /**
     * 人员规模，e.g: 5000-9999人
     */
    private String staffNumRange;

    /**
     * 经营开始时间。e.g：847900800000
     */
    private Long fromTime;
    /**
     * 法人类型，1 人 2 公司
     */
    private Integer type;
    /**
     * 股票名。e.g：中航重机
     */
    private String bondName;
    /**
     * 企业id。e.g：11684584
     */
    private Long id;
    /**
     * 是否是小微企业 0不是 1是。e.g：0
     */
    private Integer isMicroEnt;

    /**
     * 股票曾用名。e.g：力源液压->G力源->力源液压
     */
    private String usedBondName;

    /**
     * 注册号，e.g：520000000005018
     */
    private String regNumber;

    /**
     * 企业评分。e.g：9696
     */
    private Long percentileScore;

    /**
     * 注册资本。e.g：77800.32万人民币
     */
    private String regCapital;

    /**
     * 企业名。e.g：中航重机股份有限公司
     */
    private String name;

    /**
     * 登记机关。e.g：贵阳市市场监督管理局贵州双龙航空港经济区分局
     */
    private String regInstitute;

    /**
     * 注册地址。e.g：贵州双龙航空港经济区机场路9号太升国际A栋3单元5层
     */
    private String regLocation;

    /**
     * 行业。e.g：汽车制造业
     */
    private String industry;

    /**
     * 核准时间。e.g：1582646400000
     */
    private Long approvedTime;
    /**
     * 参保人数。e.g：9023
     */
    private Integer socialStaffNum;

    /**
     * 企业标签。e.g：企业集团;存续;融资轮次;上市信息;项目品牌;投资机构;曾用名
     */
    private String tags;
    /**
     * 纳税人识别号。e.g：91520000214434146R
     */
    private String taxNumber;

    /**
     * 经营范围。e.g：法律、法规、国务院决定规定禁止的不得经营；法律、法规、国务院决定规定应当许可（审批）的，经审批机关批准后凭许可（审批）文件经营;法律、法规、国务院决定规定无需许可（审批）的，市场主体自主选择经营。（股权投资及经营管理；军民共用液压件、液压系统、锻件、铸件、换热器、飞机及航空发动机附件，汽车零备件的研制、开发、制造、修理及销售；经营本企业自产机电产品、成套设备及相关技术的出口业务；经营本企业生产、科研所需的原辅材料、机械设备、仪器仪表、备品备件、零配件及技术的进口业务；开展本企业进料加工和“三来一补”业务。液压、锻件、铸件、换热器技术开发、转让和咨询服务；物流；机械冷热加工、修理修配服务。）
     */
    private String businessScope;
    /**
     * 英文名。e.g：AVIC Heavy Machinery Co.,Ltd.
     */
    private String property3;

    /**
     * 简称。e.g：中航重机
     */
    private String alias;
    /**
     * 组织机构代码。e.g：214434146
     */
    private String orgNumber;

    /**
     * 企业状态。e.g：存续
     */
    private String regStatus;

    /**
     * 成立日期。e.g：847900800000
     */
    @Alias("estiblishTime")
    private Long establishTime;

    /**
     * 更新时间。e.g：1620622963000
     */
    @Alias("updateTimes")
    private Long updateTime;
    /**
     * 股票类型。e.g：A股
     */
    private String bondType;
    /**
     * 法人。e.g：姬苏春
     */
    private String legalPersonName;

    /**
     * 经营结束时间
     */
    private Long toTime;
    /**
     * 实收注册资金。e.g：77800.32万人民币
     */
    private String actualCapital;
    /**
     * 企业类型。其他股份有限公司(上市)
     */
    private String companyOrgType;
    /**
     * 组成形式，1-个人经营、2-家庭经营
     */
    private Integer compForm;
    /**
     * 省份简称。e.g：gz
     */
    private String base;
    /**
     * 统一社会信用代码。e.g：91520000214434146R
     */
    private String creditCode;
    /**
     * 曾用名，e.g:贵州力源液压股份有限公司;
     */
    private String historyNames;
    /**
     * 曾用名，"historyNameList": [
     * "贵州力源液压股份有限公司"
     * ]
     */
    private List<String> historyNameList;
    /**
     * 股票号。e.g：600765
     */
    private String bondNum;
    /**
     * 注册资本币种 人民币 美元 欧元 等。e.g：人民币
     */
    private String regCapitalCurrency;
    /**
     * 实收注册资本币种 人民币 美元 欧元 等。e.g：人民币
     */
    private String actualCapitalCurrency;
    /**
     * 吊销日期
     */
    private Long revokeDate;
    /**
     * 吊销原因
     */
    private String revokeReason;
    /**
     * 注销日期
     */
    private Long cancelDate;
    /**
     * 注销原因
     */
    private String cancelReason;
    /**
     * 市，e.g：毕节市
     */
    private String city;
    /**
     * 区。e.g：威宁彝族回族苗族自治县
     */
    private String district;
    /**
     * 国民经济行业分类
     */
    private IndustryAll industryAll;

    @Data
    public static class IndustryAll {
        /**
         * 国民经济行业分类门类。e.g：改装汽车制造
         */
        private String category;
        /**
         * 国民经济行业分类大类。e.g：汽车制造业
         */
        private String categoryBig;
        /**
         * 国民经济行业分类中类。e.g：制造业
         */
        private String categoryMiddle;
        /**
         * 国民经济行业分类小类
         */
        private String categorySmall;
    }
}
