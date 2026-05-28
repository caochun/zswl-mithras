package cn.zswltech.mithras.dto.rating.ratingamount;

import cn.zswltech.mithras.dto.ListBaseRSP;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RatingAmountDetailLibRSP extends ListBaseRSP {


    private Long id;

    private Long clientId;

    private String clientUscCode;

    private Long projReviewId;

    private String projCode;

    private String projName;

    private Long evaluationSubjectId;

    private Boolean projSystem;

    private Boolean materialLeaseItem;

    private String modelCode;

    private String modelName;

    private String projQuota;

    private Long belongDeptId;

    private String processStatus;

    private Boolean ratingStatus;

    private LocalDate effectTime;

    private LocalDate abandonTime;

    private Boolean deleted;

    private Long createBy;

    private String createByName;


}
