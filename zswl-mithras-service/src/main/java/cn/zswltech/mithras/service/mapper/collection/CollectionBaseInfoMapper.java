package cn.zswltech.mithras.service.mapper.collection;

import cn.zswltech.mithras.dto.collection.CollectionBaseInfoREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionListREQ;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionListRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardClientOverviewOverdueRSP;
import cn.zswltech.mithras.service.mapper.dto.CollectionContractSettleDTO;
import cn.zswltech.mithras.service.mapper.dto.CollectionNextRentParam;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardProjectInfoOverdueQuery;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.service.bo.DeptRemainingPrincipalBO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @create: 2022-08-18
 **/
public interface CollectionBaseInfoMapper extends CustomBaseMapper<CollectionBaseInfo> {

    Page<CollectionBaseInfo> pageList(Page<CollectionBaseInfo> page, @Param("dto") CollectionBaseInfoREQ req);

    List<CollectionBaseInfo> overdueList();

    List<CollectionBaseInfo> getEndTimeByReceipt(@Param("receiptIds") Collection<Long> receiptIds);

    List<CollectionBaseInfo> specifyClientOverdueList(@Param("clientId") Long clientId);

    void insertList(@Param("infos") List<CollectionBaseInfo> infos);

    List<CollectionContractSettleDTO> getLastRentDateByContractIds(@Param("array") List<Long> array);

    List<CollectionBaseInfo> sumAmount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<CollectionBaseInfo> sumContractAmount(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<CollectionBaseInfo> getLastRentByContractIdsAndTime(@Param("receiptIds") Collection<Long> receiptIds,@Param("planDate") LocalDate planDate);

    Page<CollectionFlowCenterBusinessCollectionListRSP> collectionList(Page<CollectionBaseInfo> page, @Param("dto") CollectionFlowCenterBusinessCollectionListREQ req);

    List<CollectionBaseInfo> overdueListWithoutGracePeriod();

    Page<DashboardClientOverviewOverdueRSP> overduePageQuery(Page<DashboardClientOverviewOverdueRSP> overdueRspPage, @Param("query") DashboardProjectInfoOverdueQuery query, @Param("clientIds") Set<Long> clientIds);

    List<CollectionBaseInfo> contractNextRentList(@Param("dto") CollectionNextRentParam req);

    @Select("select sum(collection_amount) from collection_base_info where cash_flow_item = 'FIRST_RENT' and write_off_status = 'WRITE_OFF_COMPLETED'")
    long totalFirstRentCollection();

    @Select("select sum(collection_principal) from collection_base_info where cash_flow_item = 'RENT' and write_off_status = 'WRITE_OFF_COMPLETED' and phase > 0")
    long totalRentPrincipalCollection();

//    long calculatePrincipleBalanceFromBudget(@Param("targetDate") LocalDate targetDate, @Param("ignoreClientIds") Collection<Long> ignoreClientIds, @Param("receiptId") Long receiptId, @Param("receiptStartDate") LocalDate receiptStartDate);

    List<DeptRemainingPrincipalBO> calculateRemainingPrincipalGroupByDeptId(@Param("targetDate") LocalDate targetDate);

    @Select("select ifnull(sum(ifnull(interest, 0)),0) from collection_base_info where receipt_id = #{receiptId} and plan_collection_date >= #{startDate} and plan_collection_date <= #{endDate}")
    long calculatePlanInterest(@Param("receiptId") Long receiptId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<CollectionBaseInfo> getReceiptNextRent(@Param("receiptIds") Collection<Long> receiptIds,@Param("planDate") LocalDate planDate);
}
