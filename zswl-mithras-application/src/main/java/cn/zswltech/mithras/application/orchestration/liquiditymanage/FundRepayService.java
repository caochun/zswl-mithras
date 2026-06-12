package cn.zswltech.mithras.application.orchestration.liquiditymanage;

import cn.zswltech.mithras.dto.liquiditymanage.financingrepay.FinancingRepayPlanModifyREQ;
import cn.zswltech.mithras.dto.liquiditymanage.financingrepay.FinancingRepayWriteOffModifyREQ;
import cn.zswltech.mithras.liquidity.service.FundRepayApplicationService;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingRepayActualMapper;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingRepayActualMapper;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.workflow.persistence.model.prepare.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.workflow.persistence.mapper.prepare.FinancingRepayActualProcessDetailMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.workflow.process.prepare.FinancingRepayActualProcessDetailService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author bigbear
 * @date 2024/12/17 19:15
 * @description
 */
@Slf4j
@Service
public class FundRepayService implements FundRepayApplicationService {

    @Resource
    private FinancingRepayActualProcessDetailMapper processDetailMapper;
    @Resource
    private FundDirectFinancingRepayActualMapper directRepayActualMapper;
    @Resource
    private FundFinancingRepayActualMapper repayActualMapper;
    @Resource
    private FundFinancingRepayActualService repayActualService;
    @Resource
    private FundDirectFinancingRepayActualService directRepayActualService;
    @Resource
    private FinancingRepayActualProcessDetailService processDetailService;

    @Override
    public void planModify(FinancingRepayPlanModifyREQ req) {
        FinancingRepayActualProcessDetail processDetail = processDetailMapper.selectById(req.getId());
        if (processDetail == null) {
            throw new MithrasException("还款计划不存在");
        }
        if ("DK".equalsIgnoreCase(req.getFinancingType())) {
            List<FundFinancingRepayActual> repayActualList = repayActualMapper.selectList(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                    .eq(FundFinancingRepayActual::getFinancingId, req.getFinancingId())
            );
            if (!repayActualList.isEmpty()) {
                for (FundFinancingRepayActual repayActual : repayActualList) {
                    if (req.getFinancingRepayActualId().equals(repayActual.getId())) {
                        repayActual.setIsConfirmed(req.getIsConfirmed());
                        repayActualService.updateById(repayActual);
                    }
                }
            }
            processDetail.setIsConfirmed(req.getIsConfirmed());
            processDetailService.updateById(processDetail);
        } else if ("ZR".equalsIgnoreCase(req.getFinancingType())) {
            List<FundDirectFinancingRepayActual> directRepayActualList = directRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                    .eq(FundDirectFinancingRepayActual::getFinancingId, req.getFinancingId())
            );
            if (!directRepayActualList.isEmpty()) {
                for (FundDirectFinancingRepayActual directRepayActual : directRepayActualList) {
                    if (req.getFinancingRepayActualId().equals(directRepayActual.getId())) {
                        directRepayActual.setIsConfirmed(req.getIsConfirmed());
                        directRepayActualService.updateById(directRepayActual);
                    }
                }
            }
            processDetail.setIsConfirmed(req.getIsConfirmed());
            processDetailService.updateById(processDetail);
        }
    }


    @Override
    public void writeOffModify(FinancingRepayWriteOffModifyREQ req) {
        FinancingRepayActualProcessDetail processDetail = processDetailMapper.selectById(req.getId());
        if (processDetail == null) {
            throw new MithrasException("还款计划不存在");
        }
        if ("DK".equalsIgnoreCase(req.getFinancingType())) {
            List<FundFinancingRepayActual> repayActualList = repayActualMapper.selectList(Wrappers.<FundFinancingRepayActual>lambdaQuery()
                    .eq(FundFinancingRepayActual::getFinancingId, req.getFinancingId())
            );
            if (!repayActualList.isEmpty()) {
                for (FundFinancingRepayActual repayActual : repayActualList) {
                    if (req.getFinancingRepayActualId().equals(repayActual.getId())) {
                        repayActual.setIsPaid(req.getIsPaid());
                        repayActualService.updateById(repayActual);
                    }
                }
            }
            processDetail.setIsPaid(req.getIsPaid());
            processDetailService.updateById(processDetail);
        } else if ("ZR".equalsIgnoreCase(req.getFinancingType())) {
            List<FundDirectFinancingRepayActual> directRepayActualList = directRepayActualMapper.selectList(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                    .eq(FundDirectFinancingRepayActual::getFinancingId, req.getFinancingId())
            );
            if (!directRepayActualList.isEmpty()) {
                for (FundDirectFinancingRepayActual directRepayActual : directRepayActualList) {
                    if (req.getFinancingRepayActualId().equals(directRepayActual.getId())) {
                        directRepayActual.setIsPaid(req.getIsPaid());
                        directRepayActualService.updateById(directRepayActual);
                    }
                }
            }
            processDetail.setIsPaid(req.getIsPaid());
            processDetailService.updateById(processDetail);
        }
    }

}
