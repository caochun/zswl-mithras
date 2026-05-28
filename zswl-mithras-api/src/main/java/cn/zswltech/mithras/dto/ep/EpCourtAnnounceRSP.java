package cn.zswltech.mithras.dto.ep;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * 法院公告RSP
 *
 * @author ZHANGXIN
 */
@Data
public class EpCourtAnnounceRSP {
    /**
     * 主键ID。
     */
    @TableId("id")
    private Long id;

    /**
     * 公告类型。
     */
    private String announcementType;

    /**
     * 案件号。
     */
    private String caseNumber;

    /**
     * 法院名称。
     */
    private String courtName;

    /**
     * 当事人名称。
     */
    private String partyName;

    /**
     * 原告。
     */
    private String plaintiff;

    /**
     * 省份信息。
     */
    private String state;

    /**
     * 省份代码。
     */
    private Integer stateCode;

    /**
     * 发布日期。
     */
    private String  publDate;

    /**
     * 发布页码。
     */
    private String publPage;

    /**
     * 法律程序级别。
     */
    private String processLevel;

    /**
     * 法官姓名。
     */
    private String judgeName;

    /**
     * 插入时间。
     */
    private String  insertTime;

    /**
     * 更新时间。
     */
    private String  updateTime;

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
    private Integer jsid;
}
