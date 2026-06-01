package cn.zswltech.mithras.service.mapper.model.client;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 天眼查 基类
 *
 * @author wangchuanhao
 * @date 2022/6/21 1:33 PM
 */
@Data
public class TycBaseModel extends BaseModel {

    @TableField("client_id")
    private Long clientId;

}
