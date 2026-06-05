package cn.zswltech.mithras.budget.application;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.BudgetChooseProjectREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailDeptConfirmInfoREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailDeptConfirmInfoRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthAddREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthCheckREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthListRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthModifyREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthStatisticsRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthBaseRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthCalculateREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthListRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthStatisticsRSP;

import java.util.Collection;
import java.util.List;

public interface BudgetPlanPayDetailApplicationService {

    void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds);

    List<BudgetPlanPayDetailDeptConfirmInfoRSP> listBizDeptConfirmInfo(BudgetPlanPayDetailDeptConfirmInfoREQ req);

    void addDetailMonth(BudgetPlanPayDetailMonthAddREQ req);

    void modifyDetailMonth(BudgetPlanPayDetailMonthModifyREQ req);

    BudgetPlanPayDetailMonthStatisticsRSP statisticsMonth(BudgetPlanPayDetailMonthListREQ req);

    PageR<BudgetPlanPayDetailMonthListRSP> pageListMonth(BudgetPlanPayDetailMonthListREQ req);

    List<Pair<String, Long>> listMonthDetailProjReview(BudgetChooseProjectREQ req);

    List<BudgetPlanPayDetailMonthListRSP> listMonthDetailContract(Long detailId);

    boolean checkPlanPayAmount(BudgetPlanPayDetailMonthCheckREQ req);

    BudgetPlanPayDetailNotMonthStatisticsRSP statisticsNotMonth(BudgetPlanPayDetailNotMonthListREQ req);

    PageR<BudgetPlanPayDetailNotMonthListRSP> pageListNotMonth(BudgetPlanPayDetailNotMonthListREQ req);

    BudgetPlanPayDetailNotMonthBaseRSP getByDetailId(Long budgetPlanPayDetailId);

    Long calculateNotMonth(BudgetPlanPayDetailNotMonthCalculateREQ req);

    Long copyNotMonth(Long detailId);
}
