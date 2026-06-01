package cn.zswltech.mithras.service.service.dashboard.guanyuandata.boss;

import cn.zswltech.mithras.service.service.dashboard.guanyuandata.GuanYuanColumnPopulate;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2024/5/20
 * @description
 */
@Data
public class OverdueProjectDTO implements GuanYuanColumnPopulate {
    /**
     * 项目评审id
     */
    private Long projReviewId;
    /**
     * 项目名称
     */
    private String projName;
    /**
     * 逾期总金额
     */
    private Long totalOverdueAmount;
    /**
     * 最大逾期天数
     */
    private Integer maxOverdueDays;
    /**
     * 部门id
     */
    private Long bizDeptId;
    /**
     * 部门名称
     */
    private String bizDeptName;
    /**
     * 项目主办id
     */
    private Long projSponsorUserId;
    /**
     * 项目主办名称
     */
    private String projSponsorUserName;

    @Override
    public void populate(Map<String, String> map) {
        this.setProjReviewId(Optional.ofNullable(map.get("proj_review_id")).map(Long::valueOf).orElse(null));
        this.setProjName(Optional.ofNullable(map.get("proj_name")).orElse(null));
        this.setTotalOverdueAmount(Optional.ofNullable(map.get("total_overdue_amount")).map(Long::valueOf).orElse(null));
        this.setMaxOverdueDays(Optional.ofNullable(map.get("max_overdue_days")).map(Integer::valueOf).orElse(null));
        this.setBizDeptId(Optional.ofNullable(map.get("biz_dept_id")).map(Long::valueOf).orElse(null));
        this.setProjSponsorUserId(Optional.ofNullable(map.get("proj_sponsor_user_id")).map(Long::valueOf).orElse(null));
        this.setBizDeptName(Optional.ofNullable(map.get("biz_dept_name")).orElse(null));
        this.setProjSponsorUserName(Optional.ofNullable(map.get("proj_sponsor_user_name")).orElse(null));
    }
}
