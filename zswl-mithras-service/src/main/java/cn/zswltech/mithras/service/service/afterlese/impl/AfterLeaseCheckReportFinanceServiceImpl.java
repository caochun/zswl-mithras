package cn.zswltech.mithras.service.service.afterlese.impl;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceSaveREQ;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckReportFinanceMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportFinance;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckReportFinanceLib;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckReportFinanceService;
import cn.zswltech.mithras.afterlease.application.lib.AfterLeaseCheckReportFinanceLibService;
import cn.zswltech.mithras.afterlease.application.lib.handler.impl.AfterLeaseCheckReportFinanceLibHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/11/23
 * @description
 */
@Service
public class AfterLeaseCheckReportFinanceServiceImpl extends ServiceImpl<NewAfterLeaseCheckReportFinanceMapper, NewAfterLeaseCheckReportFinance> implements AfterLeaseCheckReportFinanceService {
    @Resource
    private AfterLeaseCheckReportFinanceLibService afterLeaseCheckReportFinanceLibService;
    @Resource
    private AfterLeaseCheckReportFinanceLibHandler afterLeaseCheckReportFinanceLibHandler;

    @Override
    public void removeByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportFinance> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportFinance::getCheckPlanClientId, checkPlanClientId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void saveSnapshot(AfterLeaseCheckReportFinanceSaveREQ req) {
        AfterLeaseCheckReportFinanceREQ queryREQ = new AfterLeaseCheckReportFinanceREQ();
        queryREQ.setCheckPlanClientId(req.getCheckPlanClientId());
        queryREQ.setClientId(req.getClientId());
        queryREQ.setClientProjectIdentity(req.getClientProjectIdentity());
        queryREQ.setSubjectType(req.getSubjectType());
        NewAfterLeaseCheckReportFinance exist = this.getSpecificSnapshot(queryREQ);
        NewAfterLeaseCheckReportFinance reportFinance = new NewAfterLeaseCheckReportFinance();
        if (Objects.nonNull(exist)) {
            reportFinance.setId(exist.getId());
        }
        reportFinance.setCheckPlanClientId(req.getCheckPlanClientId());
        reportFinance.setClientId(req.getClientId());
        reportFinance.setClientProjectIdentity(req.getClientProjectIdentity());
        reportFinance.setSubjectType(req.getSubjectType());
        reportFinance.setQueryJson(req.getQueryJsonData());
        reportFinance.setDataJson(req.getResultJsonData());
        this.saveOrUpdate(reportFinance);
    }

    @Override
    public NewAfterLeaseCheckReportFinance getSpecificSnapshot(AfterLeaseCheckReportFinanceREQ req) {
        if (StrUtil.isBlank(req.getVersion())) {
            LambdaQueryWrapper<NewAfterLeaseCheckReportFinance> query = Wrappers.lambdaQuery();
            query.eq(NewAfterLeaseCheckReportFinance::getCheckPlanClientId, req.getCheckPlanClientId());
            query.eq(NewAfterLeaseCheckReportFinance::getClientId, req.getClientId());
            query.eq(NewAfterLeaseCheckReportFinance::getClientProjectIdentity, req.getClientProjectIdentity());
            query.eq(NewAfterLeaseCheckReportFinance::getSubjectType, req.getSubjectType());
            return this.getOne(query);
        } else {
            LambdaQueryWrapper<NewAfterLeaseCheckReportFinanceLib> query = Wrappers.lambdaQuery();
            query.eq(NewAfterLeaseCheckReportFinanceLib::getVersion, req.getVersion());
            query.eq(NewAfterLeaseCheckReportFinanceLib::getCheckPlanClientId, req.getCheckPlanClientId());
            query.eq(NewAfterLeaseCheckReportFinanceLib::getClientId, req.getClientId());
            query.eq(NewAfterLeaseCheckReportFinanceLib::getClientProjectIdentity, req.getClientProjectIdentity());
            query.eq(NewAfterLeaseCheckReportFinanceLib::getSubjectType, req.getSubjectType());
            NewAfterLeaseCheckReportFinanceLib checkProjectReportFinanceLib = afterLeaseCheckReportFinanceLibService.getOne(query);
            if (Objects.isNull(checkProjectReportFinanceLib)) {
                return null;
            }
            return afterLeaseCheckReportFinanceLibHandler.actualLib2Entity(checkProjectReportFinanceLib);
        }
    }

    @Override
    public List<NewAfterLeaseCheckReportFinance> listByCheckPlanClientId(Long checkPlanClientId) {
        LambdaQueryWrapper<NewAfterLeaseCheckReportFinance> query = Wrappers.lambdaQuery();
        query.eq(NewAfterLeaseCheckReportFinance::getCheckPlanClientId, checkPlanClientId);
        return this.list(query);
    }
}
