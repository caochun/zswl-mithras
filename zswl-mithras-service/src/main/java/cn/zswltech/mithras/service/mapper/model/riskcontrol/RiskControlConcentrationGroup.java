package cn.zswltech.mithras.service.mapper.model.riskcontrol;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 集团集中度
 * @author zhaozhengkang
 * @date 2023-03-02
 */
@Data
public class RiskControlConcentrationGroup extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属集团的client_id
    */
    @TableField("group_id")
    private Long groupId;

    /**
    * 所属集团的名称
    */
    @TableField("group_name")
    private String groupName;

    /**
    * 预警状态
    */
    @TableField("state")
    private String state;

    /**
    * 数据时点
    */
    @TableField("date_time_point")
    private LocalDate dateTimePoint;

    /**
    * 剩余本金
    */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    /**
     * 剩余保证金
     */
    @TableField("remaining_margin")
    private Long remainingMargin;

    /**
    * 集中度占比 %展示
    */
    @TableField("concentration_ratio")
    private Long concentrationRatio;

    /**
    * 不良余额
    */
    @TableField("bad_balance")
    private Long badBalance;

    /**
    * 不良余额占比
    */
    @TableField("bad_balance_ratio")
    private Long badBalanceRatio;

    /**
    * 是否关联方
    */
    @TableField("is_related")
    private Integer isRelated;

    /**
    * 注册所在省
    */
    @TableField("province")
    private String province;

    /**
    * 风控行业分类
    */
    @TableField("risk_control_industry_classify")
    private String riskControlIndustryClassify;

    /**
    * 资产五级分类结果
    */
    @TableField("assert_classify_result")
    private String assertClassifyResult;

    /**
    * 是否浙江省内集团协同业务
    */
    @TableField("zhejiang_inner_group")
    private Integer zhejiangInnerGroup;

}
