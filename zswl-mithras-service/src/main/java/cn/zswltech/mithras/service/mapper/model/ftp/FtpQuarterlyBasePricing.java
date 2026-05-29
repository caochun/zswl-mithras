package cn.zswltech.mithras.service.mapper.model.ftp;
import cn.zswltech.mithras.common.model.IEntity;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.common.model.BaseModel;

/**
 * @description ftp_quarterly_base_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
public class FtpQuarterlyBasePricing extends BaseModel implements Serializable, IEntity {

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
    * 孟月、仲月、季月
    */
    @TableField("month_type")
    private String monthType;

    /**
    * 国有、其他
    */
    @TableField("enterprise_type")
    private String enterpriseType;

    /**
    * 1年期、1-3年、3年以上
    */
    @TableField("credit_term")
    private String creditTerm;

    /**
    * 项目分类
    */
    @TableField("project_classify")
    private String projectClassify;

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
