package cn.zswltech.mithras.riskcontrol.warning;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 风控预警监测
 * @author vico
 * @date 2024-12-23
 */
@Data
@Accessors(chain = true)
public class RiskControlWarnMonitor extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 客户名称
    */
    @TableField("chi_name")
    private String chiName;

    /**
    * 标题
    */
    @TableField("title")
    private String title;

    /**
    * 统一社会信用代码
    */
    @TableField("credit_code")
    private String creditCode;

    /**
    * 预警日期
    */
    @TableField("data_time")
    private LocalDateTime dataTime;

    /**
    * 链接地址
    */
    @TableField("link_address")
    private String linkAddress;

    /**
    * 预警信号：1：绿灯；2：黄灯；3：红灯
    */
    @TableField("warn_level")
    private Integer warnLevel;

    /**
    * 处理状态
    */
    @TableField("handle_status")
    private String handleStatus;

    /**
    * 处置意见
    */
    @TableField("advisement")
    private String advisement;

    /**
    * noticed
    */
    @TableField("noticed")
    private Integer noticed;

    /**
    * 最近通知时间
    */
    @TableField("notice_time")
    private LocalDate noticeTime;

    /**
     * 预警编号
     */
    @TableField("warn_code")
    private String warnCode;

    /**
    * 去重字段
    */
    @TableField("rule_code")
    private String ruleCode;

    /**
     * 风险类型
     */
    @TableField("risk_type")
    private String riskType;

    @TableField(exist = false)
    private Long belongDeptId;

    //处置方式 0 处理， 1 关闭
    @TableField("handle_result")
    private Integer handleResult;

    // 对接慧眼新增字段
    //慧眼数据唯一标识
    @TableField("source_xinsight_id")
    private Long sourceXinsightId;

    //数据来源: FHC-金控, XINSIGHT-慧眼
    @TableField("data_source")
    private String dataSource;

    // 创建唯一键的方法
    public String getUniqueKey() {
        return (StrUtil.isNotEmpty(title) ? title : "") + "|" +
                (StrUtil.isNotEmpty(creditCode) ? creditCode : "") + "|" +
                (dataTime != null ? dataTime.toString() : "");
    }
}
