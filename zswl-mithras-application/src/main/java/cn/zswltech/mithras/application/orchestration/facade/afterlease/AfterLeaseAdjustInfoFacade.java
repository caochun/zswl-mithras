package cn.zswltech.mithras.application.orchestration.facade.afterlease;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoApplicationService;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonAddSubAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.auth.BusinessModuleEnum;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.Valid;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoService;

/**
* 租后调整信息表
* @author vico
* @date 2022-11-08
*/
@Service
public class AfterLeaseAdjustInfoFacade implements AfterLeaseAdjustInfoApplicationService {

    @Resource
    private AfterLeaseAdjustInfoService afterLeaseAdjustInfoService;

    @Override
    public R<AfterLeaseAdjustInfoAddRSP> add(AfterLeaseAdjustInfoAddREQ req) {
        return R.ok(afterLeaseAdjustInfoService.add(req));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = CommonAddSubAuthCheckerNew.class, businessModule = "ADJUST")
    public R<Void> modify(AfterLeaseAdjustInfoModifyREQ req){
        afterLeaseAdjustInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<AfterLeaseAdjustInfoListRSP>> list(@Valid AfterLeaseAdjustInfoListREQ req){
        if(ObjectUtil.isNotNull(req) && ObjectUtil.isNotNull(req.getEndTime())){
            req.setEndTime(req.getEndTime().plusDays(1L));
        }
        Page<AfterLeaseAdjustInfoListRSP> data = afterLeaseAdjustInfoService.list(req);
        return R.ok(PageR.of(data.getRecords(), data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<AfterLeaseAdjustDetailRSP> detail(@Valid AfterLeaseAdjustDetailREQ req) {
        return R.ok(afterLeaseAdjustInfoService.detail(req.getAdjustId()));
    }
}