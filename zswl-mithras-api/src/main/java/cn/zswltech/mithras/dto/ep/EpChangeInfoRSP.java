package cn.zswltech.mithras.dto.ep;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


/**
 *  企业变更RSP
 *
 * @author ZHANGXIN
 */
@Data
public class EpChangeInfoRSP  {

    @TableId(value = "id")
    private Long id;

    /**
     * 企业编码
     */
    private String enterpriseCode;

    /**
     * 变更事项
     */
    private Integer change;

    /**
     * 变更事项描述
     */
    private String changeNote;

    /**
     * 变更前内容
     */
    private String beforeChange;

    /**
     * 变更后内容
     */
    private String afterChange;

    /**
     * 变更日期
     */
    private String changeDate;

    /**
     * 发布时间
     */
    private String insertTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * JSID
     */
    private Long jsid;

    /**
     * DataJson
     */
    private String dataJson;

    /**
     * 是否历史
     */
    private Integer ifHistory;
}
