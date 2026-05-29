package cn.zswltech.mithras.service.mapper.temp;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 用于模拟客户立项校验
 * @author yeqing
 * @date 2022-06-27
 */
@Data
@TableName("tmp_client_project")
public class TmpClientProject extends BaseModel {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 为1生效，为0不生效（审批拒绝）
    */
    @TableField("status")
    private Integer status;

}