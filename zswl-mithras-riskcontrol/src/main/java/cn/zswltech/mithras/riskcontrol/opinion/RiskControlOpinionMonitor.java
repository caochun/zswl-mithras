package cn.zswltech.mithras.riskcontrol.opinion;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author vico
 * @description risk_control_opinion_monitor 舆情信息表
 * @date 2023-03-09
 */
@Data
@Accessors(chain = true)
public class RiskControlOpinionMonitor extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;

    @TableField("title")
    private String title;

    @TableField("chi_name")
    private String chiName;

    @TableField("credit_code")
    private String creditCode;

    @TableField("info_publ_Date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime infoPublDate;

    @TableField("link_address")
    private String linkAddress;

    @TableField("major_org_code")
    private String majorOrgCode;

    //    @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]")
    @TableField("warn_star")
    private Integer warnStar;

    //    @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯]")
    @TableField("warn_level")
    private Integer warnLevel;

    @TableField("handle_status")
    private String handleStatus;

    @TableField("advisement")
    private String advisement;

    /**
     * 工商舆情&基础舆情的类别
     * 1 基础舆情  2 工商舆情
     */
    @TableField("risk_type")
    private Integer riskType;

    /**
     * 工商舆情类型：
     * 企业变更
     * 开庭公告
     * 法院公告
     * 立案信息
     */
    @TableField("new_type_opinion")
    private String newTypeOpinion;

    //处置方式 0 处理， 1 关闭
    @TableField("handle_result")
    private Integer handleResult;

    //关联关系类型
    @TableField("relation_type")
    private String relationType;

    //关联关系
    @TableField("relation_type_name")
    private String relationTypeName;

    //关联客户
    @TableField("relate_company_name")
    private String relateCompanyName;

    //情感方向
    @TableField("emotion")
    private String emotion;

    //情感重要度
    @TableField("importance")
    private String importance;

    //最近通知时间
    @TableField("notice_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime noticeTime;

    //新闻来源
    @TableField("source_name")
    private String sourceName;

    // 对接慧眼新增字段
    //慧眼数据ID
    @TableField("source_xinsight_id")
    private Long sourceXinsightId;

    //数据来源: FHC-金控, XINSIGHT-慧眼
    @TableField("data_source")
    private String dataSource;

    //新闻链接地址
    @TableField("news_url")
    private String newsUrl;

    // 新增字段:关联主体统一社会信用代码
    @TableField("relation_company_code")
    private String relateCompanyCode;

    // 新增字段:描述说明
    @TableField("relation_description")
    private String relationDescription;

    // 创建唯一键的方法
    public String getUniqueKey() {
        return (StrUtil.isNotEmpty(title) ? title : "") + "|" +
                (StrUtil.isNotEmpty(creditCode) ? creditCode : "") + "|" +
                (infoPublDate != null ? infoPublDate.toString() : "");
    }
}
