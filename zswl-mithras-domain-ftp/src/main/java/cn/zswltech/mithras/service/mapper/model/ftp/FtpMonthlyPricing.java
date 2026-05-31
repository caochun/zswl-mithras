package cn.zswltech.mithras.service.mapper.model.ftp;
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
 * @description 月度ftp定价指导
 * @author zhaozhengkang
 * @date 2023-01-10
 */
@Data
public class FtpMonthlyPricing extends BaseModel implements Serializable, IEntity {

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
    * 企业类型
    */
    @TableField("enterprise_type")
    private String enterpriseType;

    /**
    * 期限
    */
    @TableField("credit_term")
    private String creditTerm;

    /**
    * 项目分类
    */
    @TableField("project_classify")
    private String projectClassify;

    /**
    * 值
    */
    @TableField("value")
    private Integer value;

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
