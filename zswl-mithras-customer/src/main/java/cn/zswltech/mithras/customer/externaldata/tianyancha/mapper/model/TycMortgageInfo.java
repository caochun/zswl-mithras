package cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model;

import cn.zswltech.mithras.customer.externaldata.common.mapper.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_动产抵押
 * @author yeqing
 * @date 2022-06-21
 */
@Data

public class TycMortgageInfo extends ExternalDataBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 被担保债权数额
    */
    @TableField("amount")
    private String amount;

    /**
    * 注销日期
    */
    @TableField("cancel_date")
    private LocalDateTime cancelDate;

    /**
    * 公示日期
    */
    @TableField("publish_date")
    private LocalDateTime publishDate;

    /**
    * 登记日期
    */
    @TableField("reg_date")
    private String regDate;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

    /**
    * 被担保债权种类
    */
    @TableField("type")
    private String type;

    /**
    * 登记机关
    */
    @TableField("reg_department")
    private String regDepartment;

    /**
    * 登记编号
    */
    @TableField("reg_num")
    private String regNum;

    /**
    * 担保范围
    */
    @TableField("scope")
    private String scope;

    /**
    * 债务人履行债务的期限
    */
    @TableField("term")
    private String term;

    /**
    * 天眼查表id
    */
    @TableField("tyc_id")
    private Long tycId;

    /**
    * 注销原因
    */
    @TableField("cancel_reason")
    private String cancelReason;

    /**
    * 状态
    */
    @TableField("status")
    private String status;

    /**
    * 省份
    */
    @TableField("base")
    private String base;

    /**
    * 抵押权人信息
    */
    @TableField("people_info_json")
    private String peopleInfoJson;

    /**
    * 抵押物信息
    */
    @TableField("pawn_info_json")
    private String pawnInfoJson;

    /**
    * 变更信息
    */
    @TableField("change_info_json")
    private String changeInfoJson;

}