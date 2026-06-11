package cn.zswltech.mithras.application.orchestration.facade.capital;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.capital.application.CapitalWriteOffApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.capital.write_off.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.writeoff.WriteOffBusinessModelEnum;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowTabMainInfo;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowTabRecord;
import cn.zswltech.mithras.third.financialshare.mapper.FinanceFlowTabMainInfoMapper;
import cn.zswltech.mithras.third.financialshare.mapper.FinanceFlowTabRecordMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.capital.application.writeoff.strategy.ManualWriteOffStrategyContext;
import cn.zswltech.mithras.capital.application.writeoff.strategy.ManualWriteOffStrategyInterface;
import cn.zswltech.mithras.application.orchestration.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowTabMainInfoService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowTabRecordService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.D;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/9/20 09:48
 * @description
 */
@Slf4j
@Service
public class CapitalWriteOffFacade implements CapitalWriteOffApplicationService {

    @Override
    public R<Void> manualWriteOff(ManualWriteOffREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).manualWriteOff(req.getBatchNumber());
        return R.ok();
    }

    @Override
    public R<CheckBeforeImportRSP> checkBeforeImport(CheckBeforeImportREQ req) {
        return R.ok(ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).checkBeforeImport(req));
    }

    @Override
    public R<List<FlowMatchResultRSP>> flowMatchResult(CheckBeforeImportREQ req) {
        return R.ok(ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).flowMatchResult(req));
    }

    @Override
    public R<Void> deleteBankFlow(DeleteFlowREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).deleteBankFlow(req);
        return R.ok();
    }

    @Override
    public R<Void> addBankFlow(AddFlowREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).addBankFlow(req);
        return R.ok();
    }

    @Override
    public R<Void> updateBusinessFlow(UpdateBusinessFlowREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).updateBusinessFlow(req);
        return R.ok();
    }

    @Override
    public R<Void> addBusinessFlow(AddBusinessFlowREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).addBusinessFlow(req);
        return R.ok();
    }

    @Override
    public R<Void> deleteBusinessFlow(DeleteBusinessFlowREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).deleteBusinessFlow(req);
        return R.ok();
    }

    @Override
    public R<Void> rematchTab(RematchTabREQ req) {
        ManualWriteOffStrategyInterface instance = ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel());
        instance.reMatch(req);
        return R.ok();
    }

    @Override
    public R<Void> deleteTab(DeleteTabREQ req) {
        ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel()).deleteTab(req);
        return R.ok();
    }

    @Override
    public R<FlowMatchResultRSP> singleTab(SingleTabREQ req) {
        ManualWriteOffStrategyInterface instance = ManualWriteOffStrategyContext.getInstance(req.getWriteOffBusinessModel());
        return R.ok(instance.singleTab(req));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> releaseBankFlow(ReleaseBankFlowREQ req) {
        // 找到当前批次的所有tab信息
        List<FinanceFlowTabMainInfo> tabMainInfoList = SpringUtil.getBean(FinanceFlowTabMainInfoMapper.class).findWithLogicDelete(req.getBatchNumber());
        if (CollUtil.isEmpty(tabMainInfoList)) {
            return R.ok();
        }
        // 找到所有的流水信息
        List<Long> mainTabIdList = tabMainInfoList.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList());
        List<FinanceFlowTabRecord> financeFlowTabRecords = SpringUtil.getBean(FinanceFlowTabRecordMapper.class).findWithLogicDelete(mainTabIdList);
        Assert.isTrue(CollUtil.isNotEmpty(financeFlowTabRecords), () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        // 归还流水
        SpringUtil.getBean(FinanceFlowRecordService.class).lambdaUpdate()
                .in(FinanceFlowRecord::getId, financeFlowTabRecords.stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList()))
                .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                .update();
        // 更新tab状态
        SpringUtil.getBean(FinanceFlowTabMainInfoService.class).lambdaUpdate()
                .set(FinanceFlowTabMainInfo::getIsExpired, YesOrNoNumberEnum.YES.getCode())
                .in(FinanceFlowTabMainInfo::getBatchNumber, req.getBatchNumber())
                .update();
        return R.ok();
    }
}
