package cn.zswltech.mithras.report.mapper.procsnap.model;

import cn.zswltech.mithras.report.mapper.base.model.CrSpecialTradeBase;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * @description 征信报送-特定交易表
 * @author wang
 * @date 2022-10-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrSpecialTradeProcSnap extends CrSpecialTradeBase {

    /**
     * 是否报送
     */
    @TableField("report_flag")
    private Integer reportFlag;

    /**
     * 审批流对应的business_key
     * 用于判断是否处于审批中 和 圈定一次审批所属内容
     */
    @TableField("proc_business_key")
    private Long procBusinessKey;

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
