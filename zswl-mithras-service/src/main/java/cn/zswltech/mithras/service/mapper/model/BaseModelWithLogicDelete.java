package cn.zswltech.mithras.service.mapper.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2024/4/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseModelWithLogicDelete extends BaseModel {
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "deleted")
    private Integer deleted;
}
