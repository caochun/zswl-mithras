package cn.zswltech.mithras.service.mapper.flow.model;

/**
 * @description:
 * @author: huangping
 * @date: 2025/11/20  11:29
 * @version: 1.0
 */

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 流程节点抄送配置 实体类
 * 对应表：flow_node_cc_config
 */
@Data
@TableName("flow_node_cc_config")
public class FlowNodeCcConfig extends BaseModelWithLogicDelete {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 流程标识（如assetClassificationReview）
     */
    private String flowKey;

    /**
     * 流程节点标识（对应流程阶段唯一key）
     */
    private String nodeKey;

    /**
     * 抄送对象类型：job=角色，user=用户
     */
    private String objectType;

    /**
     * 抄送对象ID集合（多个以英文逗号,拼接）
     */
    private String objectIds;

    /**
     * 排序  数字越小越优先
     */
    private Integer sort;

}