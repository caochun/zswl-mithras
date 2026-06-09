package cn.zswltech.mithras.third.xinsight.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 慧眼系统 FINCHINAXINSIGHT.risk_control_warn_monitor视图  实体类
 * @author shaokang
 * @date 2026/1/22
 */
@Data
public class XinsightWarnMonitor {

    private Long tmStamp;           // tmstamp          自增且唯一，用于增量同步
    private String clientId;        // client_id        客户id
    private Integer warnCode;       // warn_code        预警编号
    private String title;           // title            标题
    private String creditCode;      // credit_code      统一社会信用代码
    private LocalDateTime dataTime; // data_time        预警日期
    private String linkAddress;     // link_address     链接地址
    private Integer warnLevel;      // warn_level       预警信号：1：2：3：
    private String warnLevelCn;     // warn_level_cn    预警信号描述: 绿；黄；红
    private LocalDateTime noticeTime; // notice_time    最近通知时间
    private String riskType;        // risk_type        风险类型
    private String isDel;           // isdel            是否删除(辅助查询)
}
