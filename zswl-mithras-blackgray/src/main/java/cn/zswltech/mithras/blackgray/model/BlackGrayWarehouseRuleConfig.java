package cn.zswltech.mithras.blackgray.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @description 黑灰名单库-入库原因参数配置
 * @author 
 * @date 2024-01-18
 */
@Data
@TableName(value = "black_gray_warehouse_rule_config")
public class BlackGrayWarehouseRuleConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
    * 维护机构
    */
    @TableField("org_code")
    private String orgCode;
    /**
    * 规则编号
    */
    @TableField("rule_number")
    private String ruleNumber;
    /**
    * 递增序列
    */
    @TableField("rule_sequence")
    private Integer ruleSequence;
    /**
    * 规则名称
    */
    @TableField("rule_name")
    private String ruleName;
    /**
    * 层级 0金控定义
    */
    @TableField("level")
    private Integer level;
    /**
    * 父id
    */
    @TableField("parent_id")
    private Long parentId;
    /**
    * 所属金控类型主id，即一级id
    */
    @TableField("main_id")
    private Long mainId;
    /**
    * 状态 1启用，0禁用
    */
    @TableField("status")
    private Integer status;
    /**
    * 黑灰标识
    */
    @TableField("black_gray_type")
    private String blackGrayType;
    /**
    * 来源
    */
    @TableField("source")
    private String source;
    /**
     * 适用业务类型
     */
    @TableField("suit_business")
    private String suitBusiness;
    /**
    * 适用机构
    */
    @TableField("suit_org")
    private String suitOrg;
    /**
    * 创建时间
    */
    @TableField("create_time")
    private Date createTime;
    /**
    * 更新时间
    */
    @TableField("update_time")
    private Date updateTime;
    /**
    * 创建人、发起人
    */
    @TableField("create_by")
    private Long createBy;
    /**
    * 最后更新人id
    */
    @TableField("update_by")
    private Long updateBy;
}
