package cn.zswltech.mithras.customer.externaldata.environmentpenalty.model;

import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * @description 环保处罚
 * @author yeqing
 * @date 2022-06-23
 */
@Data
@TableName("environment_penalty")
public class EnvironmentPenalty  {

    @TableId(type = IdType.AUTO)
    /**
    * 主键
    */
    private Long id;

    /**
    * 客户名称
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 处罚日期
    */
    @TableField("penalty_time")
    @IncludeNull
    private LocalDateTime penaltyTime;

    /**
    * 决定文书号
    */
    @TableField("punish_number")
    @IncludeNull
    private String punishNumber;

    /**
    * 处罚事由
    */
    @TableField("reason")
    @IncludeNull
    private String reason;

    /**
    * 处罚结果
    */
    @TableField("result")
    @IncludeNull
    private String result;

    /**
    * 处罚金额（元）
    */
    @TableField("amount")
    @IncludeNull
    private Long amount;

    /**
    * 处罚单位
    */
    @TableField("department_name")
    @IncludeNull
    private String departmentName;

    /**
    * 数据来源
    */
    @TableField("source")
    @IncludeNull
    private String source;

    /**
    * 执行情况
    */
    @TableField("info")
    @IncludeNull
    private String info;

    /**
    * 详情url
    */
    @TableField("detail_url")
    @IncludeNull
    private String detailUrl;

}
