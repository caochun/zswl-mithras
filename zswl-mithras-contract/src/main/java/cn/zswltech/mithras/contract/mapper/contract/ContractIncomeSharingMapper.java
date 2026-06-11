package cn.zswltech.mithras.contract.mapper.contract;

import cn.zswltech.mithras.dto.incomesharing.IncomeSharingDetailREQ;
import cn.zswltech.mithras.dto.incomesharing.IncomeSharingListBO;
import cn.zswltech.mithras.dto.incomesharing.IncomeSharingListREQ;
import cn.zswltech.mithras.dto.incomesharing.IncomeSharingRSP;
import cn.zswltech.mithras.dto.monthly.MonthlyQuery;
import cn.zswltech.mithras.dto.monthly.MonthlyQueryResult;
import cn.zswltech.mithras.contract.model.contract.ContractIncomeSharing;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/4/9
 * @description
 */
public interface ContractIncomeSharingMapper extends CustomBaseMapper<ContractIncomeSharing> {
    Page<MonthlyQueryResult> queryMonthlyData(Page<MonthlyQueryResult> page, @Param("req") MonthlyQuery monthlyQuery);

    List<IncomeSharingListBO> queryList(@Param("req") IncomeSharingListREQ req, @Param("receiptIdList") List<Long> receiptIdList);

    List<IncomeSharingRSP> queryDetail(@Param("req") IncomeSharingDetailREQ req);

    @Select("select ifnull(sum(ifnull(income,0)),0) from contract_income_sharing where receipt_id = #{receiptId} and income_date >= #{startDate} and income_date <= #{endDate} and deleted = 0")
    long sumIncome(@Param("receiptId") Long receiptId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
