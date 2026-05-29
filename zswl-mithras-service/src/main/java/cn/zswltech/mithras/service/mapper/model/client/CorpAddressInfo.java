package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.common.annotation.IncludeNull;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author MyBatisPlusGenerater
 * @since 2022-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("corp_address_info")
public class CorpAddressInfo extends ClientBaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 地址类型
     */
    @TableField("address_type")
    private String addressType;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 省份
     */
    @TableField(value = "province", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String province;

    /**
     * 城市
     */
    @TableField(value = "city", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String city;

    /**
     * 区、县
     */
    @TableField(value = "district", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String district;

    /**
     * 详细地址
     */
    @TableField(value = "detail", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String detail;

    /**
     * 区域代码
     */
    @TableField(value = "region_code", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String regionCode;

    @TableField(value = "user_id")
    private Long userId;

    @Override
    public void setMainId(Long id) {
        setClientId(id);
    }

    @Override
    public Long getMainId() {
        return getClientId();
    }
}
