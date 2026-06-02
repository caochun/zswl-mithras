package cn.zswltech.mithras.service.controller.capital;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.capital.BankFlowProcessingCenterProjectApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.capital.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.capital.BankFlowProcessingCenterService;
import cn.zswltech.mithras.service.service.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author yangxiong
 * @date 2024/5/18/16:47
 * @description
 */
@RestController
public class BankFlowProcessingCenterProjController implements BankFlowProcessingCenterProjectApi {


    private static final Logger log = LoggerFactory.getLogger(BankFlowProcessingCenterProjController.class);
    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;
    @Resource
    private BankFlowProcessingCenterService bankFlowProcessingCenterService;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public R<PageR<BankFlowProcessingCenterListRSP>> selectBankCenterByTab(BankFlowProcessingCenterListREQ req) {
        return R.ok(bankFlowProcessingCenterService.tabList(req));
    }

    @Override
    public R<List<String>> projectCashFlowCodeList(CashFlowCodeListREQ req) {
        return R.ok(bankFlowProcessingCenterService.projectCashFlowCodeList(req));
    }

    @Override
    public R<BankFlowProcessingCenterProjDetailRSP> projAmountDetail(ProjAmountDetailREQ req) {
        return R.ok(bankFlowProcessingCenterService.projAmountDetail(req));
    }

    @Override
    public R<Void> batchWriteOff(BankFlowProcessingCenterProjWriteOffREQ req) {
        log.info("此次核销流水ID列表：【{}】", req.getFinanceFlowIds());
        if (CharSequenceUtil.isBlank(redisTemplate.opsForValue().get(GlobalConstants.FLOW_WRITE_OFF_LOCK))) {
            try {
                log.info("{}批量核销加锁中...........", LocalDateTime.now());
                //先获取锁，给内部业务争取时间改变数据状态
                redisTemplate.opsForValue()
                        .set(GlobalConstants.FLOW_WRITE_OFF_LOCK, GlobalConstants.FLOW_WRITE_OFF_LOCK, 2L, TimeUnit.SECONDS);
                log.info("{}批量核销加锁成功...........", LocalDateTime.now());
                // 增加核销校验
//                financeFlowAutoWriteOffService.check(req);
                financeFlowAutoWriteOffService.handleWriteOff(req);
                log.info("{}批量核销成功", LocalDateTime.now());
            } catch (MithrasException e) {
                throw e;
            } catch (Exception e) {
                log.error("", e);
                throw new MithrasException(ResultMsg.CONCURRENT_OPERATION);
            } finally {
                //把数据还回去，理论上应该需要校验id的正确性，暂时不管
                financeFlowRecordService.lambdaUpdate()
                        .in(FinanceFlowRecord::getId, req.getFinanceFlowIds())
                        .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                        .update();
            }
        }
        return R.ok();
    }

    @Override
    public R<List<String>> manualPullFlow(BankFlowProcessingCenterManualPullFlowREQ req) {
        if (ObjectUtil.isEmpty(req.getBeginTime())) {
            req.setBeginTime(LocalDateTime.now().minusMonths(3));
        }
        return R.ok(financeFlowRecordService.fullSync(req.getBeginTime(), req.getEndTime()));
    }

    @Override
    public R<Void> noProcessingRequire(NoProcessingRequireREQ req) {
        bankFlowProcessingCenterService.noProcessingRequire(req);
        return R.ok();
    }

    @Override
    public R<Void> delete(FinanceFlowDeleteREQ req) {
        bankFlowProcessingCenterService.delete(req);
        return R.ok();
    }

    @Override
    public R<List<BankCenterSubTableProjectListRSP>> subListByCashFlowId(BankCenterSubTableProjectListREQ req) {
        return R.ok(bankFlowProcessingCenterService.subListByCashFlowId(req.getFinancingFlowIdList()));
    }

    @Override
    public R<Void> nettingRefund(BankFlowNettingRefundREQ req) {
        bankFlowProcessingCenterService.nettingRefund(req);
        return R.ok();
    }

    @Override
    public R<Void> confirmIncome(BankFlowConfirmIncomeREQ req) {
        bankFlowProcessingCenterService.confirmIncome(req);
        return R.ok();
    }

    @Override
    public R<List<BankFlowContractReceiptListRSP>> getReceiptCode(BankFlowContractReceiptREQ req) {
        return R.ok(bankFlowProcessingCenterService.getReceiptCode(req));
    }

    @Override
    public R<Void> restoreBankFlow(@Valid MultiplePkREQ req) {
        bankFlowProcessingCenterService.restore(req.getIds());
        return R.ok();
    }
}
