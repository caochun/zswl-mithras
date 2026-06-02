package cn.zswltech.mithras.ftp.model;
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
 * @description ftp_quarterly_customer_principal_pricing
 * @author zhaozhengkang
 * @date 2023-01-09
 */
@Data
public class FtpQuarterlyCustomerPrincipalPricing extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
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
    * 1年期、1-3年、3年以上
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
