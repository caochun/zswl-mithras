package cn.zswltech.mithras.customer.externaldata.common.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 外部数据 基类
 *
 * @author wangchuanhao
 * @date 2022/6/21 1:33 PM
 */
@Data
public class ExternalDataBaseModel extends BaseModel {

    @TableField("client_id")
    private Long clientId;

}
