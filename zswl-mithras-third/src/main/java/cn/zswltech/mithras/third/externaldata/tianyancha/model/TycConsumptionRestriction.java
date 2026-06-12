package cn.zswltech.mithras.third.externaldata.tianyancha.model;

import cn.zswltech.mithras.third.externaldata.common.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_限制消费令
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycConsumptionRestriction extends ExternalDataBaseModel {


    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 案号
    */
    @TableField("case_code")
    private String caseCode;

    /**
    * pdf文件地址
    */
    @TableField("file_path")
    private String filePath;

    /**
    * 发布日期
    */
    @TableField("publish_date")
    private LocalDateTime publishDate;

    /**
    * 限制消费者名称
    */
    @TableField("xname")
    private String xname;

    /**
    * 限制消费者id
    */
    @TableField("hcgid")
    private String hcgid;

    /**
    * 申请人信息
    */
    @TableField("applicant")
    private String applicant;

    /**
    * 申请人id
    */
    @TableField("applicant_cid")
    private String applicantCid;

    /**
    * 企业信息
    */
    @TableField("qyinfo_alias")
    private String qyinfoAlias;

    /**
    * 立案时间
    */
    @TableField("case_create_time")
    private LocalDateTime caseCreateTime;

    /**
    * 别名
    */
    @TableField("alias")
    private String alias;

    /**
    * 天眼查id
    */
    @TableField("tyc_id")
    private Long tycId;

    /**
    * 企业id
    */
    @TableField("cid")
    private Long cid;

}