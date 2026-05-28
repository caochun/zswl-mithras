package cn.zswltech.mithras.service.mapper.model.finance;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @date 2023/6/16
 * @description 科目余额辅助核算表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_subject_balance_assist")
public class FinanceSubjectBalanceAssist extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 年份
     */
    @TableField(value = "year")
    private Integer year;

    /**
     * 月份
     */
    @TableField(value = "month")
    private Integer month;

    /**
     * 主键
     */
    @TableField(value = "fentryid")
    private Long fentryid;
    /**
     * 横表
     */
    @TableField(value = "fid")
    private String fid;

    /**
     * fname
     */
    @TableField(value = "fname")
    private String fname;

    /**
     * dim_fnumber
     **/
    @TableField(value = "dim_fnumber")
    private String dimFnumber;

    /**
     * dim_name
     **/
    @TableField(value = "dim_name")
    private String dimName;

    /**
     * fvalue
     **/
    @TableField(value = "fvalue")
    private Long fvalue;


}
