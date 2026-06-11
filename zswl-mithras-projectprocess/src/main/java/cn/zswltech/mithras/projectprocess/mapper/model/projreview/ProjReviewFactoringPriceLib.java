package cn.zswltech.mithras.projectprocess.mapper.model.projreview;

import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.persistence.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 保理报价方案版本表
 * @date 2022-07-20
 */
@Data
public class ProjReviewFactoringPriceLib extends ProjReviewFactoringPrice implements Serializable, ILib{

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

}
