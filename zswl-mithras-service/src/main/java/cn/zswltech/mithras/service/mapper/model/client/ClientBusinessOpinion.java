package cn.zswltech.mithras.service.mapper.model.client;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 客户工商信息处理意见表
 * @author vico
 * @date 2023-09-11
 */
@Data
public class ClientBusinessOpinion extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 流程id
    */
    @TableField("flow_id")
    private String flowId;

    /**
     * 流程名称
     */
    @TableField("module_name")
    private String moduleName;

    /**
    * 处理节点名称
    */
    @TableField("node_name")
    private String nodeName;

    /**
     * 合同ID
     **/
    @TableField("contract_id")
    private Long contractId;

    /**
    * 处理意见
    */
    @TableField("opinion")
    private String opinion;

}
