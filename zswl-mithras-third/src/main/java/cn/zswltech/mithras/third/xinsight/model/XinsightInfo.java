package cn.zswltech.mithras.third.xinsight.model;

import lombok.Data;

import java.util.Date;

/**
 * 慧眼系统 XINSIGHT_NEW_ALLINFO视图  对应实体类
 * @author shaokang
 * @date 2026/1/5
 */
@Data
public class XinsightInfo {
    private Long tmStamp;        // 对应TMSTAMP字段         自增且唯一字段，用于增量同步
    private Long clientId;       // 对应CLIENTID字段        客户id
    private String newsTitle;    // 对应NEWSTITLE字段       舆情标题
    private String symbol;       // 对应SYMBOL字段          统一社会信用代码
    private String sjitName;     // 对应SJITNAME字段        客户名称
    private Date publishTime;   // 对应PUBLISHTIME字段      信息发布日期
    private String xnewsUrl;    // 对应XNEWSURL字段         相关链接地址
    private String releTypeCode; // 对应RELETYPECODE字段    关联关系类型
    private String releTypeCn;  // 对应RELETYPECN字段       关联关系描述
    private String symbolComp;  // 对应SYMBOLCOMP字段       关联客户名称
    private Integer moduleId;   // 对应MODULEID字段         预警星级: MODULEID=40 三星/红色 MODULEID=41 二星/黄色 MODULEID=42 一星/绿色
    private String emotionName; // 对应EMOTIONNAME字段      情感方向: 负面---预警  中性---一般  正面---正面
    private Date xdfwDate;      // 对应XDFWDATE字段         最近通知时间
    private String newsSource;  // 对应NEWSSOURCE字段       新闻来源
    private Integer configId;   // 对应CONFIGID字段         舆情类型
    private Integer isDel;      // 对应ISDEL字段            是否删除(辅助查询字段)
}
