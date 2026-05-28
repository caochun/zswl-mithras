package cn.zswltech.mithras.report.mapper.procsnap.model;

import cn.zswltech.mithras.report.mapper.base.model.CrClientBase;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.Accessors;


/**
 * @description 征信报送-客户表
 * @author wang
 * @date 2022-10-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrClientProcSnap extends CrClientBase {

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

    /**
     * 是否报送标识，默认为1
     */
    @TableField("report_flag")
    private Integer reportFlag;

}
