package cn.zswltech.mithras.third.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName ExternalExceptionInfo
 * @Description 三方请求失败记录
 * @Author jackerhe
 * @Date 2022/10/17 2:07 下午
 * @Version 1.0
 **/
@Data
public class ExternalExceptionInfo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    //业务数据
    @TableField("biz_info")
    private String bizInfo;

    //业务模块
    @TableField("biz_model")
    private String bizModel;

    //业务主键
    @TableField("biz_key")
    private String bizKey;

    //错误信息
    @TableField("exception")
    private String exception;

    //状态 0 失败需要重新发送 1成功
    @TableField("status")
    private int status;

    @TableField("gmt_create")
    private LocalDateTime gmt_create;

    //重试次数
    @TableField("retry_count")
    private String retryCount;

}
