package cn.zswltech.mithras.service.service.lib.client.dto;

import cn.zswltech.mithras.common.annotation.BirCompareColumn;
import lombok.Data;

import java.time.LocalDate;

/**
 * @Description: 工商信息对比
 * @Author: huangping
 * @Date: 2025/11/25  16:42
 * @Version: 1.0
 */

@Data
public class CorpCommerceInfoCompareDTO {

    /*----全量必须校验字段start*/
    //国标行业分类
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.ALL)
    private String industryType;


    //风控行业分类
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.ALL)
    private String riskControlIndustryClassify;

    //经济类型
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.ALL)
    private String economyType;


    /*----全量必须校验字段end*/

    /*----境内客户判断字段-----start*/
    //成立日期
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private LocalDate establishDate;

    //核准日期：approvalDate
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private LocalDate approvalDate;
    //营业许可证到期日：bizLicenseEndDate
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private LocalDate bizLicenseEndDate;

    //存续状态：continuousStatus
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String continuousStatus;
    //组织机构类型：orgType
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String orgType;

    //企业规模：orgScale
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String orgScale;

    //注册资本(元)：registerCapital
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private Long registerCapital;

    //注册资本币种：registerCurrencyType
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String registerCurrencyType;


    //实收资本(元)：realCapital
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private Long realCapital;

    //企业性质：enterpriseNature
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String enterpriseNature;


    //是否由上市公司控股：ownershipType
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String ownershipType;

    //是否关联方：isRelated
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private Integer isRelated;

    //业务范围：bizScope
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private String bizScope;

    //是否集团公司：groupFlag
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private Integer groupFlag;

    //所属集团：belongGroupClientId
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC)
    private Long belongGroupClientId;


    /*境内+非集团公司必填 start*/
    //法人代表：corpRepresent
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC_UNGROUP)
    private String corpRepresent;
    //法人性别：corpGender
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC_UNGROUP)
    private String corpGender;
    //法人证件类型：corpCertType
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC_UNGROUP)
    private String corpCertType;
    @BirCompareColumn(comparLv = BirCompareColumn.CompareLv.DOMESTIC_UNGROUP)
    //法人证件号码：corpCertCode
    private String corpCertCode;
    //指标隶属省份：provinceOfAffiliation  这个字段没有存储版本 无法比对
    /*----境内客户判断字段-----end*/
}
