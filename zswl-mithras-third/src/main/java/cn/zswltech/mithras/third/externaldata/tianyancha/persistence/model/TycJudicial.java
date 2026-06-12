package cn.zswltech.mithras.third.externaldata.tianyancha.persistence.model;

import cn.zswltech.mithras.third.externaldata.common.persistence.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_司法协助
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycJudicial extends ExternalDataBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 执行通知书文号
    */
    @TableField("execute_notice_num")
    private String executeNoticeNum;

    /**
    * 执行人公司id
    */
    @TableField("executed_person_cid")
    private Long executedPersonCid;

    /**
    * 公示日期
    */
    @TableField("publicity_date")
    private String publicityDate;

    /**
    * 股权被执行的企业
    */
    @TableField("stock_executed_company")
    private String stockExecutedCompany;

    /**
    * 被执行人hgid
    */
    @TableField("executed_person_hid")
    private Long executedPersonHid;

    /**
    * 股权被执行的企业id
    */
    @TableField("stock_executed_cid")
    private Long stockExecutedCid;

    /**
    * 被执行人
    */
    @TableField("executed_person")
    private String executedPerson;

    /**
    * 司法协助基本信息id
    */
    @TableField("ass_id")
    private String assId;

    /**
    * 股权数额
    */
    @TableField("equity_amount")
    private String equityAmount;

    /**
    * 天眼查id
    */
    @TableField("tyc_id")
    private Long tycId;

    /**
    * 类型
    */
    @TableField("type_state")
    private String typeState;

    /**
    * 执⾏⼈类型，2-⼈，1-公司
    */
    @TableField("executed_person_type")
    private String executedPersonType;

    /**
    * 执行法院
    */
    @TableField("executive_court")
    private String executiveCourt;

}