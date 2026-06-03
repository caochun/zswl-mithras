package cn.zswltech.mithras.finance.mapper.model.finance;

import cn.zswltech.mithras.finance.enums.financeoverdue.OverdueRecordTypeEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @description 逾期报送计划表
 * @author vico
 * @date 2025-09-15
 */
@Data
public class FinanceOverdueVersionRelation extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 流程id
     **/
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 单据类型
     * {@link OverdueRecordTypeEnum#name()}
     **/
    @TableField("record_type")
    private String recordType;

    /**
    * 记录id
    */
    @TableField("record_id")
    private Long recordId;



}
