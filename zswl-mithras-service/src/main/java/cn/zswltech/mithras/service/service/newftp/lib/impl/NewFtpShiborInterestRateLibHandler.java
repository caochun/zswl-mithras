package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.service.enums.ftp.FtpFrequency;
import cn.zswltech.mithras.service.enums.newftp.NewFtpSubModule;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpShiborInterestRateDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpShiborInterestRateLib;
import cn.zswltech.mithras.service.util.DateUtil;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 16:56
 */
@Service
public class NewFtpShiborInterestRateLibHandler
        extends NewFtpLibAbstractHandler<NewFtpShiborInterestRateLib, NewFtpShiborInterestRateDraft, NewFtpDetailTreasuryBondYieldListRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        return fields;
    }
    @Override
    protected NewFtpShiborInterestRateLib entity2Lib(NewFtpShiborInterestRateDraft f) {
        return BeanUtil.copyProperties(f, NewFtpShiborInterestRateLib.class);
    }

    @Override
    protected NewFtpShiborInterestRateDraft lib2Entity(NewFtpShiborInterestRateLib t) {
        return BeanUtil.copyProperties(t, NewFtpShiborInterestRateDraft.class);
    }

    @Override
    protected NewFtpDetailTreasuryBondYieldListRSP lib2Rsp(NewFtpShiborInterestRateLib f) {
        NewFtpDetailTreasuryBondYieldListRSP newFtpDetailTreasuryBondYieldListRSP = BeanUtil.copyProperties(f, NewFtpDetailTreasuryBondYieldListRSP.class);
        FtpFrequency ftpFrequency = ofNullable(FtpFrequency.of(f.getFrequency())).orElseThrow(() -> new MithrasException("暂不支持"));
        switch (ftpFrequency) {
            case DAY:
                newFtpDetailTreasuryBondYieldListRSP.setDate(f.getDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                break;
            case MONTH:
                newFtpDetailTreasuryBondYieldListRSP.setDate(f.getDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));
                break;
            case SEASON:
                newFtpDetailTreasuryBondYieldListRSP.setDate(String.format("%s-Q%s", f.getDate().getYear(), DateUtil.ensureQuarter(f.getDate().getMonthValue())));
                break;
            default:
        }
        return newFtpDetailTreasuryBondYieldListRSP;
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.SHIBOR_INTEREST;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

}
