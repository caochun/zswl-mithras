package cn.zswltech.mithras.service.mapper.model.dashboard;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 融租易-观远用户映射表
 * @author yangxiong
 * @TableName guanyuan_user_mapping
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value ="guanyuan_user_mapping")
public class GuanyuanUserMapping extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 观远用户账户名
     */
    @TableField(value = "guanyuan_user_account")
    private String guanyuanUserAccount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}