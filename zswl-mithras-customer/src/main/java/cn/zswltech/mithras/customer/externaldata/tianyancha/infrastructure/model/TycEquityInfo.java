package cn.zswltech.mithras.customer.externaldata.tianyancha.infrastructure.model;

import cn.zswltech.mithras.customer.externaldata.common.infrastructure.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

/**
 * @description 天眼查_股权出质
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycEquityInfo extends ExternalDataBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 质权人列表
    */
    @TableField("pledgee_json")
    private String pledgeeJson;

    /**
    * 股权出质设立登记日期
    */
    @TableField("reg_date")
    private LocalDateTime regDate;

    /**
    * 出质人
    */
    @TableField("pledgor")
    private String pledgor;

    /**
    * 质权人证照/证件号码
    */
    @TableField("certif_number_r")
    private String certifNumberR;

    /**
    * 质权人
    */
    @TableField("pledgee")
    private String pledgee;

    /**
    * 登记编号
    */
    @TableField("reg_number")
    private String regNumber;

    /**
    * certif_number
    */
    @TableField("certif_number")
    private String certifNumber;

    /**
    * 公司列表
    */
    @TableField("company_json")
    private String companyJson;

    /**
    * 出质股权标的企业
    */
    @TableField("target_company_json")
    private String targetCompanyJson;

    /**
    * 出质人列表
    */
    @TableField("pledgor_json")
    private String pledgorJson;

    /**
    * 出质股权数额
    */
    @TableField("equity_amount")
    private String equityAmount;

    /**
    * 天眼查id
    */
    @TableField("tyc_id")
    private Long tycId;

    /**
    * 状态
    */
    @TableField("state")
    private String state;

    /**
    * 股权出质设立发布日期
    */
    @TableField("put_date")
    private LocalDateTime putDate;

}