package cn.zswltech.mithras.service.mapper.flow.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author luyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("flow_contract_node_time")
public class ContractNodeTime extends BaseModel implements Serializable {
    private static final long serialVersionUID = 2129843017182169873L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long reviewId;

    private Long contractId;

    private String timeJson;

}
