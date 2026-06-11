package cn.zswltech.mithras.workbench.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 工作台-快捷功能
 * @author vico
 * @date 2023-03-16
 */
@Data
public class WorkbenchShortcuts extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 菜单id
    */
    @TableField("menu_ids")
    private String menuIds;

    /**
    * 用户id
    */
    @TableField("user_id")
    private Long userId;

    /**
    * 功能标识 1常用，0 其他
    */
    @TableField("function_flag")
    private Integer functionFlag;

}
