package cn.zswltech.mithras.third.externaldata.tianyancha.model;

import cn.zswltech.mithras.third.externaldata.common.persistence.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 失信人
 *
 * @author wangchuanhao
 * @date 2022/6/20 10:54 PM
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tyc_dishonest")
public class TycDishonest extends ExternalDataBaseModel {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long Id;

    /**
     * 法⼈、负责⼈姓名
     */
    @TableField("business_entity")
    private String businessEntity;

    /**
     * 省份地区
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 法院
     */
    @TableField("court_name")
    private String courtName;

    /**
     * 未履⾏部分
     */
    @TableField("unperform_part")
    private String unperformPart;

    /**
     * 法定负责⼈/主要负责⼈信息
     */
    @TableField("staff_json")
    private String staffJson;

    /**
     * 失信⼈类型，0代表⼈，1代表公司
     */
    @TableField("type")
    private String type;

    /**
     * 已履⾏部分
     */
    @TableField("performed_part")
    private String performedPart;

    /**
     * 失信⼈名称
     */
    @TableField("iname")
    private String iname;

    /**
     * 失信被执⾏⼈⾏为具体情形
     */
    @TableField("disrupt_type_name")
    private String disruptTypeName;

    /**
     * 案号
     */
    @TableField("case_code")
    private String caseCode;

    /**
     * 身份证号码/组织机构代码
     */
    @TableField("card_num")
    private String cardNum;

    /**
     * 履⾏情况
     */
    @TableField("performance")
    private String performance;

    /**
     * ⽴案时间
     */
    @TableField("reg_date")
    private LocalDateTime regDate;

    /**
     * 发布时间
     */
    @TableField("publish_date")
    private LocalDateTime publishDate;

    /**
     * 做出执⾏的依据单位
     */
    @TableField("gist_unit")
    private String gistUnit;

    /**
     * ⽣效法律⽂书确定的义务
     */
    @TableField("duty")
    private String duty;

    /**
     * 执⾏依据⽂号
     */
    @TableField("gist_id")
    private String gistId;

}
