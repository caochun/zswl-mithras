package cn.zswltech.mithras.ftp.oldftp.model;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description ftp_quarterly_enterprise_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
public class FtpQuarterlyEnterprisePricing extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属指引id
    */
    @TableField("guidance_id")
    private Long guidanceId;

    /**
    * 国有、其他
    */
    @TableField("enterprise_type")
    private String enterpriseType;

    /**
    * 项目分类
    */
    @TableField("project_classify")
    private String projectClassify;

    /**
    * credit_term
    */
    @TableField("credit_term")
    private String creditTerm;

    /**
    * 利率值
    */
    @TableField("percent_value")
    private Integer percentValue;

    @TableField("site")
    private String site;

    @Override
    public void setMainId(Long id) {
        this.guidanceId = id;
    }

    @Override
    public Long getMainId() {
        return this.guidanceId;
    }
}
