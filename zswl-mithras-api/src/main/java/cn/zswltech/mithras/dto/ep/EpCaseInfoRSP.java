package cn.zswltech.mithras.dto.ep;

import lombok.Data;


/**
 * 立案信息RSP
 *
 * @author ZHANGXIN
 */
@Data
public class EpCaseInfoRSP {

    /**
     * ID
     */

    private Long id;

    private String msgId;

    /**
     * 案号
     */
    private String caseNumber;

    /**
     * 法院名称
     */
    private String courtName;

    /**
     * 法官
     */
    private String judge;

    /**
     * 法官助理
     */
    private String judgeAssistant;

    /**
     * 立案日期
     */
    private String caseDate;

    /**
     * 开庭日期
     */
    private String sessionDate;

    /**
     * 结案日期
     */
    private String endDate;

    /**
     * 地区
     */
    private String area;

    /**
     * 案件状态描述
     */
    private String caseStatusDesc;

    /**
     * 案件状态代码
     */
    private Integer caseStatus;

    /**
     * 链接地址
     */
    private String linkAddress;

    /**
     * 插入时间
     */
    private String insertTime;

    /**
     * 更新时间
     */
    private String  msgUpdateTime;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 信用代码
     */
    private String creditCode;

    /**
     * 唯一标识符
     */
    private Long jsid;
}
