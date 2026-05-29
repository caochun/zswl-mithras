package cn.zswltech.mithras.service.mapper.model.kpi;

import cn.zswltech.mithras.service.enums.kpi.KpiProvisionStatusEnum;
import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效-拨备表
 * @author vico
 * @date 2023-06-20
 */
@Data
public class KpiProvisionBaseInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 创建月份
    */
    @TableField("provision_date")
    private LocalDate provisionDate;

    /**
    * 状态
     * {@link KpiProvisionStatusEnum#name()}
    */
    @TableField("provision_status")
    private String provisionStatus;

}
