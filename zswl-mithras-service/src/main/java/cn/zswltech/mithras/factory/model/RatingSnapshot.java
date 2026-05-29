package cn.zswltech.mithras.factory.model;

import cn.zswltech.mithras.dto.rating.RatingParamFieldRSP;
import cn.zswltech.mithras.dto.rating.RatingParamRSP;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteResult;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 模型快照表
 */

@Data
@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
@TableName("rating_snapshot")
public class RatingSnapshot extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("service_code")
    private String serviceCode;

    /**
     * 评分模型快照json 类型：Map<String, List<RatingParamFieldRSP>>
     * @see RatingParamFieldRSP
     */
    @TableField("content")
    private String content;

    /**
     * 用户选择json
     * @see RatingParamRSP
     */
    @TableField("result")
    private String result;

    /**
     * 记录上一次用户选择json
     * @see RatingParamRSP
     */
    @TableField("last_result")
    private String lastResult;

    /**
     * 评分结果json 类型：DecisionExecuteResult
     * @see DecisionExecuteResult
     */
    @TableField("score")
    private String score;

    @TableField("execute_count")
    private int executeCount;

    @TableField("deleted")
    private Boolean deleted;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
