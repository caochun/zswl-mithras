package cn.zswltech.mithras.service.mapper.model.workbench;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 首页工作台-超链接
 * @date 2023-03-17
 */
@Data
public class WorkbenchHyperlink extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * name
     */
    @TableField("name")
    private String name;

    /**
     * address
     */
    @TableField("address")
    private String address;

}
