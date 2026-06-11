package cn.zswltech.mithras.dto.ep;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 开庭公告RSP
 *
 * @author ZHANGXIN
 */
@Data
public class EpCourtSessionRSP {


    /**
     * 主键ID。
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 法院名称。
     */
    private String courtName;

    /**
     * 法庭名称。
     */
    private String courtRoom;

    /**
     * 法院部门。
     */
    private String department;

    /**
     * 合法日期。
     */
    private String lawfulDay;

    /**
     * 日程日期。
     */
    private String scheduleDate;

    /**
     * 案号。
     */
    private String caseNumber;

    /**
     * 案由。
     */
    private String subjectMatter;

    /**
     * 案由代码。
     */
    private String subjectMatterCode;

    /**
     * 原告。
     */
    private String plaintiff;

    /**
     * 被告。
     */
    private String defendant;

    /**
     * 当事人名称。
     */
    private String partyName;

    /**
     * 首席法官。
     */
    private String chiefJudge;

    /**
     * 省份信息。
     */
    private String state;

    /**
     * 省份代码。
     */
    private String stateCode;

    /**
     * 插入时间。
     */
    private String insertTime;

    /**
     * 更新时间。
     */
    private String updateTime;

    /**
     * 公告内容。
     */
    private String content;

    /**
     * 公司名称。
     */
    private String companyName;

    /**
     * 信用代码。
     */
    private String creditCode;

    /**
     * 唯一标识符。
     */
    private Long jsid;
}
