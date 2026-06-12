package cn.zswltech.mithras.third.externaldata.tianyancha.model;

import cn.zswltech.mithras.third.externaldata.common.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_法律诉讼
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycLawSuit extends ExternalDataBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 文书类型
    */
    @TableField("doc_type")
    private String docType;

    /**
    * 天眼查url（web）
    */
    @TableField("lawsuit_url")
    private String lawsuitUrl;

    /**
    * 天眼查url（h5）
    */
    @TableField("lawsuit_h5_url")
    private String lawsuitH5Url;

    /**
    * 案件名称
    */
    @TableField("title")
    private String title;

    /**
    * 审理法院
    */
    @TableField("court")
    private String court;

    /**
    * 裁判日期
    */
    @TableField("judge_time")
    private String judgeTime;

    /**
    * uuid
    */
    @TableField("uuid")
    private String uuid;

    /**
    * 案号
    */
    @TableField("case_no")
    private String caseNo;

    /**
    * 案件类型
    */
    @TableField("case_type")
    private String caseType;

    /**
    * 案由
    */
    @TableField("case_reason")
    private String caseReason;

    /**
    * 涉案方
    */
    @TableField("case_persons_json")
    private String casePersonsJson;

    /**
    * 案件金额
    */
    @TableField("case_money")
    private String caseMoney;

    /**
    * 发布日期
    */
    @TableField("submit_time")
    private LocalDateTime submitTime;

    /**
    * 天眼查id
    */
    @TableField("tyc_id")
    private Long tycId;

    /**
     * 详情json
     */
    @TableField("detail_json")
    private String detailJson;

}