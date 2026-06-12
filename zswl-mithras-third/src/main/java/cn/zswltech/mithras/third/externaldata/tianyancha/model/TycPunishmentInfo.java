package cn.zswltech.mithras.third.externaldata.tianyancha.model;

import cn.zswltech.mithras.third.externaldata.common.model.ExternalDataBaseModel;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 天眼查_行政处罚
 * @author yeqing
 * @date 2022-06-21
 */
@Data
public class TycPunishmentInfo extends ExternalDataBaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 处罚单位
    */
    @TableField("department_name")
    private String departmentName;

    /**
    * 处罚事由/违法行为类型
    */
    @TableField("reason")
    private String reason;

    /**
    * 处罚依据（source=信⽤中国时返回数据）
    */
    @TableField("evidence")
    private String evidence;

    /**
    * 处罚状态（source=信⽤中国时返回数据）
    */
    @TableField("punish_status")
    private String punishStatus;

    /**
    * 备注（source=国家市场监督管理总局时返回数据）
    */
    @TableField("remark")
    private String remark;

    /**
    * 数据来源
    */
    @TableField("source")
    private String source;

    /**
    * 处罚类别1（source=信⽤中国时返回数据）
    */
    @TableField("type")
    private String type;

    /**
    * 处罚结果/内容
    */
    @TableField("content")
    private String content;

    /**
    * 日期
    */
    @TableField("decision_date")
    private String decisionDate;

    /**
    * 法定代表⼈（source=国家市场监督管理总局时返回数据）
    */
    @TableField("legal_person_name")
    private String legalPersonName;

    /**
    * 处罚名称（source=信⽤中国时返回数据）
    */
    @TableField("punish_name")
    private String punishName;

    /**
    * 决定⽂书号
    */
    @TableField("punish_number")
    private String punishNumber;

    /**
    * 处罚类别2（source=信⽤中国时返回数据）
    */
    @TableField("type_second")
    private String typeSecond;

}