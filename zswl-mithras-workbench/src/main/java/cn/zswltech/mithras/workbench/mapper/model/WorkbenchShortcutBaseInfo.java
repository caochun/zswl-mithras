package cn.zswltech.mithras.workbench.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 首页工作台-快捷方式-基本信息维护
 * @author vico
 * @date 2023-03-21
 */
@Data
public class WorkbenchShortcutBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("function_code")
    private String functionCode;

    /**
    * name
    */
    @TableField("name")
    private String name;

    /**
    * 快捷参数
    */
    @TableField("short_code")
    private String shortCode;

    @TableField("shortcut_type")
    private Integer shortcutType;

    @TableField("icon")
    private String icon;

}
