package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 客户 信息 基表
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:43 PM
 */
@Data
public class ClientBaseModel extends BaseModel implements IEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("client_id")
    private Long clientId;

    @Override
    public void setMainId(Long id) {
        setClientId(id);
    }

    @Override
    public Long getMainId() {
        return getClientId();
    }
}
