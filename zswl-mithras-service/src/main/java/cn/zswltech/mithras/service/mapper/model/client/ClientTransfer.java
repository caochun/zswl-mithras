package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.type.handler.ListLongTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author luyi
 * @description client_transfer
 * @date 2022-10-28
 */
@Data
@TableName(value = "client_transfer", autoResultMap = true)
public class ClientTransfer extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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

    /**
     * 公海客户立项ID
     */
    @TableField(value = "proj_establish_ids", typeHandler = ListLongTypeHandler.class)
    private List<Long> projEstablishIds;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 客户编码
     */
    @TableField("client_code")
    private String clientCode;

    /**
     * 客户类型
     */
    @TableField("client_type")
    private String clientType;

    /**
     * 所属部门id
     */
    @TableField("belong_dept_id")
    private Long belongDeptId;

    /**
     * 所属经理id
     */
    @TableField("belong_sponsor_id")
    private Long belongSponsorId;

    /**
     * 转交状态
     */
    @TableField("transfer_status")
    private String transferStatus;

    /**
     * 申请原因
     */
    @TableField("description")
    private String description;

    /**
     * 正式移交日期
     */
    @TableField("transfer_date")
    private LocalDate transferDate;
}
