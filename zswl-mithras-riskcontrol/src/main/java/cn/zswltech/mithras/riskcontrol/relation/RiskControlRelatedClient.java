package cn.zswltech.mithras.riskcontrol.relation;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 金控关联方名录
 * @author zhaozhengkang
 * @date 2023-03-08
 */
@Data
public class RiskControlRelatedClient extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 系统内客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 金控关联方企业名称
    */
    @TableField("client_name")
    private String clientName;

    /**
    * 统一社会信用代码
    */
    @TableField("uscd")
    private String uscd;

    /**
    * 关联方类型
    */
    @TableField("related_party_type")
    private String relatedPartyType;

    /**
    * 关联关系说明
    */
    @TableField("description")
    private String description;
    /**
     * 关联关系父类型
     */
    @TableField("parent_relation_type")
    private String parentRelationType;
    /**
     * 关联关系子类型
     */
    @TableField("sub_relation_type")
    private String subRelationType;

}
