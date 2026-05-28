package cn.zswltech.mithras.service.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 流程中的业务数据
 *
 * @author wangchuanhao
 * @date 2022/12/9 12:39 PM
 */
@Data
public class BizProcessData {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程id
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 审批权限（定价审批使用）
     */
    @TableField("auth_level")
    private Integer authLevel;

}
