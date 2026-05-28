package cn.zswltech.mithras.report.mapper.base.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-客户表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrClientBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户表id
     */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户编号
    */
    @TableField(value = "client_code", updateStrategy = FieldStrategy.IGNORED)
    private String clientCode;

    /**
    * 客户名称
    */
    @TableField(value = "client_name", updateStrategy = FieldStrategy.IGNORED)
    private String clientName;

    /**
    * 中征码
    */
    @TableField(value = "zhong_zheng_code", updateStrategy = FieldStrategy.IGNORED)
    private String zhongZhengCode;

    /**
    * 存续状态
    */
    @TableField(value = "continuous_status", updateStrategy = FieldStrategy.IGNORED)
    private String continuousStatus;

    /**
    * 组织机构类型
    */
    @TableField(value = "org_type", updateStrategy = FieldStrategy.IGNORED)
    private String orgType;

    /**
    * 注册地址
    */
    @TableField(value = "register_address", updateStrategy = FieldStrategy.IGNORED)
    private String registerAddress;

    /**
    * 行政区划
    */
    @TableField(value = "region_code", updateStrategy = FieldStrategy.IGNORED)
    private String regionCode;

    /**
    * 成立日期
    */
    @TableField(value = "establish_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate establishDate;

    /**
    * 营业许可证到期日
    */
    @TableField(value = "biz_license_end_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate bizLicenseEndDate;

    /**
    * 业务范围
    */
    @TableField(value = "biz_scope", updateStrategy = FieldStrategy.IGNORED)
    private String bizScope;

    /**
    * 行业分类
    */
    @TableField(value = "industry_type", updateStrategy = FieldStrategy.IGNORED)
    private String industryType;

    /**
    * 经济类型
    */
    @TableField(value = "economy_type", updateStrategy = FieldStrategy.IGNORED)
    private String economyType;

    /**
    * 企业规模
    */
    @TableField(value = "org_scale", updateStrategy = FieldStrategy.IGNORED)
    private String orgScale;

    /**
    * 注册资本币种
    */
    @TableField(value = "register_currency_type", updateStrategy = FieldStrategy.IGNORED)
    private String registerCurrencyType;

    /**
    * 注册资本（单位：0.0001元）
    */
    @TableField(value = "register_capital", updateStrategy = FieldStrategy.IGNORED)
    private Long registerCapital;

    /**
    * 法人代表
    */
    @TableField(value = "corp_represent", updateStrategy = FieldStrategy.IGNORED)
    private String corpRepresent;

    /**
    * 法人证件类型
    */
    @TableField(value = "corp_cert_type", updateStrategy = FieldStrategy.IGNORED)
    private String corpCertType;

    /**
    * 法人证件号码
    */
    @TableField(value = "corp_cert_code", updateStrategy = FieldStrategy.IGNORED)
    private String corpCertCode;

    /**
     * 生效日期
     */
    @TableField(value = "effect_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate effectDate;

    @Override
    public Set<String> ignoreCompareFieldNames() {
        Set<String> set = new HashSet<>();
        set.add("effectDate");
        set.addAll(super.ignoreCompareFieldNames());
        return set;
    }

    public String genBusinessKey() {
        return String.valueOf(clientId);
    }


}
