package cn.zswltech.mithras.service.mapper.model.associationreport;

import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description 实体经济服务数据(流程节点记录版本表)
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
public class AssociationEntityEconomyServiceLib extends AssociationEntityEconomyService implements ILib {


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

    @Override
    public void setMainId(Long id) {
        setOriginId(id);
    }

    @Override
    public Long getMainId() {
        return getOriginId();
    }

}
