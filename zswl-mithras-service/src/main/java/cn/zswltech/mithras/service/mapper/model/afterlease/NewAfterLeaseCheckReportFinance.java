package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_finance")
public class NewAfterLeaseCheckReportFinance extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 191573306906398473L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_plan_client_id")
    private Long checkPlanClientId;

    @TableField("client_id")
    private Long clientId;

    @TableField("client_project_identity")
    private String clientProjectIdentity;

    @TableField("subject_type")
    private String subjectType;

    @TableField("query_json")
    private String queryJson;

    @TableField("data_json")
    private String dataJson;

    @Override
    public void setMainId(Long id) {
        this.checkPlanClientId = id;
    }

    @Override
    public Long getMainId() {
        return this.checkPlanClientId;
    }
}
