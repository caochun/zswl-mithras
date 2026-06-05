package cn.zswltech.mithras.service.application.fund.receiptrepay;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.fund.application.receiptrepay.api.FundReceiptRepayBaseInfoApplicationService;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.fund.FundReceiptRepayModifyMainAuthChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.fund.application.lib.receiptrepay.service.FundReceiptRepayBaseInfoLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description 收付款
 * @date 2023-02-20
 */
@Service
public class FundReceiptRepayBaseInfoFacade implements FundReceiptRepayBaseInfoApplicationService {

    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundReceiptRepayBaseInfoLibService fundReceiptRepayBaseInfoLibService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;


    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = FundReceiptRepayModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.FUND_RECEIPT_REPAY)
    public R<Void> modify(FundReceiptRepayBaseInfoModifyREQ req) {
        fundReceiptRepayBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<FundReceiptRepayBaseInfoListSumRSP> list(FundReceiptRepayBaseInfoListREQ req) {
        if (CharSequenceUtil.isBlank(req.getRepayMonth())) {
            req.setRepayMonth(LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue()));
        }
        PageR<FundReceiptRepayBaseInfoListRSP> data = fundReceiptRepayBaseInfoService.list(req);
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        PageR<FundReceiptRepayBaseInfoListRSP> sumDate = fundReceiptRepayBaseInfoService.list(req);
        return R.ok(fundReceiptRepayBaseInfoService.buildResult(data, sumDate.getList()));
    }

    @Override
    public R<FundDirectFinancingBaseInfoDetailRSP> directDetail(FundReceiptRepayBaseInfoDetailREQ req) {
        return R.ok(fundReceiptRepayBaseInfoService.directDetail(req.getId()));
    }


    @Override
    public R<FundReceiptRepayBaseInfoDetailRSP> detail(FundReceiptRepayBaseInfoDetailREQ req) {
        return R.ok(fundReceiptRepayBaseInfoService.detail(req));
    }

    @Override
    public R<List<FundFinancingPledgeListRSP>> pledgeDetail(FundReceiptRepayBaseInfoDetailREQ req) {
        if (StringUtils.isNotBlank(req.getVersion())) {
            FundReceiptRepayBaseInfoLib lib = fundReceiptRepayBaseInfoLibService.getOne(Wrappers.<FundReceiptRepayBaseInfoLib>lambdaQuery()
                    .eq(FundReceiptRepayBaseInfoLib::getOriginId, req.getId())
                    .eq(FundReceiptRepayBaseInfoLib::getVersion, req.getVersion())
                    .last("LIMIT 1")
            );
            FundFinancingPledgeListREQ pledgeListReq = new FundFinancingPledgeListREQ();
            pledgeListReq.setFinancingId(lib.getFinancingId());
            pledgeListReq.setVersion(lib.getFinancingVersion());
            List<FundFinancingPledgeListRSP> rspList = fundFinancingPledgeInfoService.list(pledgeListReq);
            return R.ok(rspList);
        }
        FundReceiptRepayBaseInfo baseInfo = fundReceiptRepayBaseInfoService.getById(req.getId());
        FundFinancingPledgeListREQ pledgeListReq = new FundFinancingPledgeListREQ();
        pledgeListReq.setFinancingId(baseInfo.getFinancingId());
        pledgeListReq.setVersion(baseInfo.getFinancingVersion());
        List<FundFinancingPledgeListRSP> rspList = fundFinancingPledgeInfoService.list(pledgeListReq);
        return R.ok(rspList);
    }

    @Override
    public R<Map<String, DiffValue>> directCompare(FundReceiptRepayBaseInfoDetailREQ req) {
        return R.ok(fundReceiptRepayBaseInfoService.directCompare(req.getId()));
    }

    @Override
    public R<FundReceiptRepayBaseInfoListRSP> financingType(FundReceiptRepayBaseInfoDetailREQ req) {
        FundReceiptRepayBaseInfo byId = fundReceiptRepayBaseInfoService.getById(req.getId());
        FundReceiptRepayBaseInfoListRSP rsp = new FundReceiptRepayBaseInfoListRSP();
        rsp.setId(byId.getId());
        rsp.setFinancingType(byId.getFinancingType());
        rsp.setFinancingId(byId.getFinancingId());
        return R.ok(rsp);
    }


}