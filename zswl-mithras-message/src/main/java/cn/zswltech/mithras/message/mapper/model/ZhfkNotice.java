package cn.zswltech.mithras.message.mapper.model;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 通知消息
 * @author vico
 * @date 2024-03-12
 */
@Data
public class ZhfkNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 标题
    */
    @TableField("title")
    private String title;

    /**
    * 内容
    */
    @TableField("content")
    private String content;

    /**
    * 1通知，2公告
    */
    @TableField("type")
    private Integer type;

    /**
    * 0正常，1关闭
    */
    @TableField("status")
    private Integer status;

    /**
    * 备注
    */
    @TableField("remark")
    private String remark;

    /**
    * 数据范围（deptid，roleid，userid)
    */
    @TableField("data_scope")
    private String dataScope;

    /**
    * 创建时间
    */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
    * 修改时间
    */
    @TableField("gmt_update")
    private LocalDateTime gmtUpdate;

    /**
    * 处理用户列表，all则为所有用户
    */
    @TableField("deal_user")
    private String dealUser;

    /**
    * 业务模块信息
    */
    @TableField("biz_info")
    private String bizInfo;

    /**
    * redis_stream_offset
    */
    @TableField("record_id")
    private String recordId;

    /**
    * 消息读状态 1 已读，2未读
    */
    @TableField("read_status")
    //private Integer readStatus;

    private String createBy;

    private String updateBy;

}
