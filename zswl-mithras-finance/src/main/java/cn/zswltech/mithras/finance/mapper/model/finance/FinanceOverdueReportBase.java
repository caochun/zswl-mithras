package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.finance.enums.financeoverdue.OverduePlanStatueEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
public class FinanceOverdueReportBase extends BaseModelWithLogicDelete implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 计划月份
    */
    @TableField("plan_date")
    private LocalDate planDate;

    /**
    * 报送状态
     * {@link OverduePlanStatueEnum#name()}
    */
    @TableField("report_status")
    private String reportStatus;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }

}
