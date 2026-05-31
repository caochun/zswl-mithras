package cn.zswltech.mithras.service.providence.entity;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Jim
 * @version 1.0.0
 * @descripition:
 * @date 2024/12/24 14:11
 */
@Data
@Accessors(chain = true)
@TableName("bill_overdue")
public class BillOverdue extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 序号
    private Integer seqNo;
    // 机构名称
    private String orgName;
    // 机构编号
    private String orgCode;
    // 机构类型 企业 enterprise  financialInstitution
    private String orgType;
    // 持续逾期开始时间 yyyy-mm-dd
    private String overdueStartDate;
    // 统计日期
    private String busiDate;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }
}
