package cn.zswltech.mithras.service.mapper.model.trackEvent;

import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.contract.ContractAccount;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TrackEventInfoLib extends TrackEventInfo implements ILib {

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
