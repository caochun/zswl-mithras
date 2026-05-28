package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.service.mapper.model.type.handler.ListLongTypeHandler;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "client_transfer_weight", autoResultMap = true)
public class ClientTransferWeight extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;


    /**
     * 客户id
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 项目编号
     */
    @TableField(value = "proj_code")
    private String projCode;


    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 项目资料归档状态
     */
    @TableField(value = "proj_archive_status")
    private String projArchiveStatus;

    /**
     * 风险移交比例
     */
    @TableField(value = "risk_transfer_value")
    private Integer riskTransferValue;

    /**
     * 收益移交比例
     */
    @TableField(value = "income_transfer_value")
    private Integer incomeTransferValue;

    /**
     * 批次编号
     */
    @TableField("batch_no")
    private String batchNo;

    /**
     * 移至部门id
     */
    @TableField("to_dept_id")
    private Long toDeptId;

    /**
     * 移至用户id
     */
    @TableField("to_sponsor_id")
    private Long toSponsorId;

    @TableField(value = "to_cosponsor_ids", typeHandler = ListLongTypeHandler.class)
    private List<Long> toCosponsorIds;

}
