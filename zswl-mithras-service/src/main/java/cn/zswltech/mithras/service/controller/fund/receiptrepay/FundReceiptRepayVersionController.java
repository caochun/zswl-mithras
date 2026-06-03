package cn.zswltech.mithras.service.controller.fund.receiptrepay;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.receiptrepay.FundReceiptRepayVersionApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchDetailRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchReceiptDownloadREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchReceiptListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.BatchSubmitREQ;
import cn.zswltech.mithras.dto.fund.receiptrepay.version.CreateBatchREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.service.auth.rule.fund.FundReceiptRepayAuthMoneyManagerRule;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CacheEnum;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CreateBatchType;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayVersionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static cn.zswltech.mithras.service.constant.ResultMsg.CONCURRENT_OPERATION;

/**
 * 资金收付款审批相关接口
 *
 * @author wangchuanhao
 * @date 2023/2/20 10:54 AM
 */
@RestController
@Slf4j
public class FundReceiptRepayVersionController implements FundReceiptRepayVersionApi {

    @Resource
    private FundReceiptRepayVersionService fundReceiptRepayVersionService;
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private FundReceiptRepayAuthMoneyManagerRule fundReceiptRepayAuthMoneyManagerRule;

    @Override
    public R<Void> submit(SinglePkREQ req) {
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, -1L);
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), "ALL");
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            fundReceiptRepayVersionService.submit(req.getId());
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<Long> createBatch(CreateBatchREQ req) {
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, -1L);
        CreateBatchType batchType = CreateBatchType.of(req.getBatchType());
        if (Objects.isNull(batchType)) {
            throw new MithrasException("创建批次的类型不合法：" + req.getBatchType());
        }
        if (CreateBatchType.BATCH.equals(batchType) && CollectionUtils.isEmpty(req.getReceiptIdList())) {
            throw new MithrasException("批量还款选中的付款数据不能为空");
        }
        return R.ok(fundReceiptRepayVersionService.createBatch(req));
    }

    @Override
    public R<Void> batchSubmit(BatchSubmitREQ req) {
        fundReceiptRepayAuthMoneyManagerRule.check(BusinessModuleEnum.FUND_RECEIPT_REPAY, -1L);
        String lockKey = CacheEnum.EFFECT_SUBMIT_LOCK.buildKey(BusinessModuleEnum.FUND_RECEIPT_REPAY.name(), "ALL");
        boolean getLockFlag = redisDistLock.tryLockWithoutReleaseTime(lockKey, 1000L);
        if (!getLockFlag) {
            throw new MithrasException(CONCURRENT_OPERATION);
        }
        try {
            fundReceiptRepayVersionService.batchSubmit(req);
        } finally {
            redisDistLock.unlock(lockKey);
        }
        return R.ok();
    }

    @Override
    public R<BatchDetailRSP> batchDetail(SinglePkREQ req) {
        return R.ok(fundReceiptRepayVersionService.batchDetail(req.getId()));
    }

    @Override
    public R<List<BatchReceiptListRSP>> batchReceiptList(SinglePkREQ req) {
        return R.ok(fundReceiptRepayVersionService.batchReceiptRspList(req.getId(), null));
    }

    @Override
    public void batchReceiptDownload(BatchReceiptDownloadREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资金收付款批量审批列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            fundReceiptRepayVersionService.batchReceiptDownload(req, httpServletResponse.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出资金收付款批量列表发生未知异常", e);
            throw new MithrasException("导出资金收付款批量列表发生未知异常");
        }
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        if (StringUtils.isBlank(req.getModule())) {
            req.setModule(BusinessModuleEnum.FUND_RECEIPT_REPAY.name());
        }
        return R.ok(fundReceiptRepayVersionService.selectPage(req));
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(SinglePkREQ req) {
        return R.ok(fundReceiptRepayVersionService.comparePreVersion(req.getId()));
    }

}
