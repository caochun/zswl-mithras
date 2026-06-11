package cn.zswltech.mithras.contract.overdue.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 诉讼登记
 * @author zhaozhengkang
 * @date 2024-10-30
 */
@Data
@TableName("oc_litigation_registration")
public class LitigationRegistration extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 诉讼登记编号
    */
    @TableField("code")
    private String code;

    /**
    * 合同ids
    */
    @TableField("contract_ids")
    private String contractIds;

    /**
    * 合同编号
    */
    @TableField("contract_codes")
    private String contractCodes;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户名称
    */
    @TableField("client_name")
    private String clientName;

    /**
    * 诉讼状态
    */
    @TableField("status")
    private String status;

    @TableField("lock_version")
    private Long lockVersion;
}
