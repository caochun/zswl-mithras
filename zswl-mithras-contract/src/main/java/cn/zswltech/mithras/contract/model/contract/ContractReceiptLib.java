package cn.zswltech.mithras.contract.model.contract;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 合同明细-借据
 * @author vico
 * @date 2022-08-22
 */
@Data
public class ContractReceiptLib extends ContractReceipt implements Serializable, ILib {

    private static final long serialVersionUID = 1L;

    /**
     * 变更编号
     * 版本号
     */
    @TableField("version")
    private String version;

    /**
     * 临时数据表id
     * 需要用来比对数据 或者 流程拒绝时全量回写
     */
    @TableField("origin_id")
    private Long originId;

    /**
     * 记录原数据更新时间、创建时间等
     */
    @TableField("data_create_time")
    private LocalDateTime dataCreateTime;
    @TableField("data_create_by")
    private Long dataCreateBy;
    @TableField("data_update_time")
    private LocalDateTime dataUpdateTime;
    @TableField("data_update_by")
    private Long dataUpdateBy;

    /**
     * 版本标志，0无效，1有效...业务自扩展
     * {@link VersionTypeConstants}
     */
    @TableField("version_type")
    private Integer versionType;

    /**
     * 不含税租金（元）
     */
    @TableField("rent_excluding_tax")
    private Long rentExcludingTax;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private Long taxRate;

    /**
     * 税额（元）
     */
    @TableField("tax")
    private Long tax;

    /**
     * 不含税利息
     */
    @TableField("excluding_interest_tax")
    private Long excludingInterestTax;
}
