package cn.zswltech.mithras.third.datashare.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @description data_share_manager
 * @author vico
 * @date 2022-08-03
 */
@Data
public class DataShareCodeDict implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private String userId;

    @TableField("phone")
    private String phone;

    //创建时间
    @TableField("create_time")
    private Date createTime;

    //修改时间
    @TableField("update_time")
    private Date updateTime;

}
