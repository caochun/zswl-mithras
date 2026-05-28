package cn.zswltech.mithras.report.mapper.fullsnap.model;

import cn.zswltech.mithras.report.mapper.base.model.CrRepayPlanBase;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * @description 征信报送-还款计划表
 * @author wang
 * @date 2022-10-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrRepayPlanFullSnap extends CrRepayPlanBase {

    /**
     * 批次id
     */
    @TableField("batch_id")
    private Long batchId;

    /**
     * 批次号
     */
    @TableField("batch_no")
    private String batchNo;

}
