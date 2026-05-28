package cn.zswltech.mithras.service.overdue.infrastructure.dao.model;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description 诉讼等级被告信息
 * @author zhaozhengkang
 * @date 2024-10-30
 */
@Data
@TableName("oc_litigation_defendant")
public class LitigationDefendant extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属诉讼登记id
    */
    @TableField("lr_id")
    private Long lrId;

    /**
    * 被告名称
    */
    @TableField("name")
    private String name;

    /**
    * 合同角色
    */
    @TableField("role")
    private String role;

    /**
    * 证件类型
    */
    @TableField("certificate_type")
    private String certificateType;

    /**
    * 证件号码
    */
    @TableField("certificate_number")
    private String certificateNumber;

}
