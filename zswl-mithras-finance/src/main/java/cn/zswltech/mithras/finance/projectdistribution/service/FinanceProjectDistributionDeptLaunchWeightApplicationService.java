package cn.zswltech.mithras.finance.projectdistribution.service;

import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptLaunchWeightAddREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptLaunchWeightListREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptLaunchWeightModifyREQ;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionDeptLaunchWeightRemoveREQ;
import cn.zswltech.mithras.finance.projectdistribution.mapper.model.FinanceProjectDistributionDeptLaunchWeight;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface FinanceProjectDistributionDeptLaunchWeightApplicationService {

    void add(FinanceProjectDistributionDeptLaunchWeightAddREQ req);

    void modify(FinanceProjectDistributionDeptLaunchWeightModifyREQ req);

    Page<FinanceProjectDistributionDeptLaunchWeight> list(FinanceProjectDistributionDeptLaunchWeightListREQ req);

    void remove(FinanceProjectDistributionDeptLaunchWeightRemoveREQ req);
}
