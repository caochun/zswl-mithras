package cn.zswltech.mithras.customer.model.client;

import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("new_corp_address_info")
public class NewCorpAddressInfo extends CorpAddressInfo implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

//    /**
//     * 权限级别, 管护权为3，申办权为2，查看权为1
//     */
//    @TableField("level")
//    private Integer level;
//
//    /**
//     * 客户状态
//     */
//    @TableField("client_status")
//    private String clientStatus;
//
//    /**
//     * 是否已释放
//     */
//    @TableField(value = "is_released")
//    private Integer isReleased;

    @Override
    public void setMainId(Long id) {
        setClientId(id);
    }

    @Override
    public Long getMainId() {
        return getClientId();
    }
}
