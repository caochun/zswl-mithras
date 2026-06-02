package cn.zswltech.mithras.service.overdue.infrastructure.dao.model;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import cn.zswltech.mithras.service.mapper.model.BaseModel;

/**
 * @description 审判信息
 * @author zhaozhengkang
 * @date 2024-10-30
 */
@Data
@TableName("oc_litigation_trial_info")
public class LitigationTrialInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 所属诉讼登记id
    */
    @TableField("lr_id")
    private Long lrId;

    /**
    * 一审案号
    */
    @TableField("f_case_no")
    private String fcaseNo;

    /**
    * 一审立案时间
    */
    @TableField("f_filing_date")
    private LocalDate ffilingDate;

    /**
    * 一审受理法院
    */
    @TableField("f_accepting_court")
    private String facceptingCourt;

    /**
    * 一审开庭日期
    */
    @TableField("f_hearing_date")
    private LocalDate fhearingDate;

    /**
    * 一审判决日期
    */
    @TableField("f_judgment_date")
    private LocalDate fjudgmentDate;

    /**
    * 二审案号
    */
    @TableField("s_case_no")
    private String scaseNo;

    /**
    * 二审立案时间
    */
    @TableField("s_filing_date")
    private LocalDate sfilingDate;

    /**
    * 二审受理法院
    */
    @TableField("s_accepting_court")
    private String sacceptingCourt;

    /**
    * 二审开庭日期
    */
    @TableField("s_hearing_date")
    private LocalDate shearingDate;

    /**
    * 二审判决日期
    */
    @TableField("s_judgment_date")
    private LocalDate sjudgmentDate;

    /**
    * 再审案号
    */
    @TableField("t_case_no")
    private String tcaseNo;

    /**
    * 再审立案时间
    */
    @TableField("t_filing_date")
    private LocalDate tfilingDate;

    /**
    * 再审受理法院
    */
    @TableField("t_accepting_court")
    private String tacceptingCourt;

    /**
    * 再审开庭日期
    */
    @TableField("t_hearing_date")
    private LocalDate thearingDate;

    /**
    * 再审判决日期
    */
    @TableField("t_judgment_date")
    private LocalDate tjudgmentDate;

    /**
    * 执行案号
    */
    @TableField("execution_no")
    private String executionNo;

    /**
    * 执行时间
    */
    @TableField("execution_date")
    private LocalDate executionDate;

    /**
    * 保全完成日期
    */
    @TableField("preservation_completion_date")
    private LocalDate preservationCompletionDate;

    /**
    * 查封到期日
    */
    @TableField("sealing_expiration_date")
    private LocalDate sealingExpirationDate;

}
