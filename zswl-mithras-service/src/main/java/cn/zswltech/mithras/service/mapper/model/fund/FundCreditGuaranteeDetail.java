package cn.zswltech.mithras.service.mapper.model.fund;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 授信担保详情
 * @date 2022-12-22
 */
@Data
public class FundCreditGuaranteeDetail extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属授信id
     */
    @TableField("credit_id")
    private Long creditId;

    /**
     * 担保机构id
     */
    @TableField("guarantee_agency_id")
    private Long guaranteeAgencyId;

    /**
     * 担保金额
     */
    @TableField("guarantee_amount")
    private Long guaranteeAmount;

}
