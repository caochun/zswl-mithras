package cn.zswltech.mithras.third.ep.model;

import cn.zswltech.mithras.foundation.annotation.NotCompareColumn;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author ZHANGXIN
 */
@Data
@Accessors(chain = true)
@TableName("ep_courtannounce")
public class EpCourtAnnounce {


    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 公告类型。
     */
    @TableId("announcement_type")
    private String announcementType;

    /**
     * 案件号。
     */
    @TableId("case_number")
    private String caseNumber;

    /**
     * 法院名称。
     */
    @TableId("court_name")
    private String courtName;

    /**
     * 当事人名称。
     */
    @TableId("party_name")
    private String partyName;

    /**
     * 原告。
     */
    @TableId("plaintiff")
    private String plaintiff;

    /**
     * 省份信息。
     */
    @TableId("state")
    private String state;

    /**
     * 省份代码。
     */
    @TableId("state_code")
    private Integer stateCode;

    /**
     * 发布日期。
     */
    @TableId("publ_date")
    private Date publDate;

    /**
     * 发布页码。
     */
    @TableId("publ_page")
    private String publPage;

    /**
     * 法律程序级别。
     */
    @TableId("process_level")
    private String processLevel;

    /**
     * 法官姓名。
     */
    @TableId("judge_name")
    private String judgeName;

    /**
     * 插入时间。
     */
    @TableId("insert_time")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableId("msg_update_time")
    private Date msgUpdateTime;

    /**
     * 公告内容。
     */

    @TableId("content")
    private String content;

    /**
     * 公司名称。
     */
    @TableId("company_name")
    private String companyName;

    /**
     * 信用代码。
     */
    @TableId("credit_code")
    private String creditCode;

    /**
     * 唯一标识符。
     */
    @TableId("jsid")
    private Integer jsid;

    /**
     * 消息id
     */
    @TableId("msg_id")
    private String msgId;

    @NotCompareColumn
    @TableField(value = "create_by", updateStrategy = FieldStrategy.NEVER)
    private Long createBy;

    @NotCompareColumn
    @TableField("update_by")
    private Long updateBy;

}
