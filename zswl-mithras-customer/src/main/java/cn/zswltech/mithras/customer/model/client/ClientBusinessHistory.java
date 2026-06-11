package cn.zswltech.mithras.customer.model.client;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 客户工商信息历史表
 * @author vico
 * @date 2023-09-07
 */
@Data
public class ClientBusinessHistory extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 客户名称
    */
    @TableField("tyc_name")
    private String tycName;

    /**
    * 法人代表
    */
    @TableField("tyc_corp_represent")
    private String tycCorpRepresent;

    /**
     * 股东信息
     */
    @TableField("tyc_share_holder_info")
    private String tycShareHolderInfo;

}
