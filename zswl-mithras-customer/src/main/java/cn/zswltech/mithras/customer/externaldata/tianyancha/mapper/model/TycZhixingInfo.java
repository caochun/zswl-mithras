package cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model;

import cn.zswltech.mithras.customer.externaldata.common.mapper.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_被执行人
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycZhixingInfo extends ExternalDataBaseModel {

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
    * 身份证号/组织机构代码
    */
    @TableField("party_card_num")
    private String partyCardNum;

    /**
    * 被执行人名称
    */
    @TableField("pname")
    private String pname;

    /**
    * 执行法院
    */
    @TableField("exec_court_name")
    private String execCourtName;

    /**
    * 创建时间
    */
    @TableField("case_create_time")
    private LocalDateTime caseCreateTime;

    /**
    * 执行标的（元）
    */
    @TableField("exec_money")
    private String execMoney;

}