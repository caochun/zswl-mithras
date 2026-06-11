package cn.zswltech.mithras.projectprocess.projlifecycle.dto.persistence;

import lombok.Data;

/**
 * 项目全周期统计dto
 *
 * @author wangchuanhao
 * @date 2022/12/7 12:53 PM
 */
@Data
public class ProjLifecycleStatisticsDTO {

    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 立项阶段数量
     */
    private Long establishCount;

    /**
     * 评审阶段数量
     */
    private Long reviewCount;

    /**
     * 合同阶段数量
     */
    private Long contractCount;

    /**
     * 合同结清阶段数量
     */
    private Long contractSettleCount;

    /**
     * 新增总数量
     */
    private Long addTotalCount;

    /**
     * 新增立项数量
     */
    private Long addEstablishCount;

    /**
     * 新增评审数量
     */
    private Long addReviewCount;

    /**
     * 新增合同数量
     */
    private Long addContractCount;

    /**
     * 新增合同结清数量
     */
    private Long addContractSettleCount;

}
