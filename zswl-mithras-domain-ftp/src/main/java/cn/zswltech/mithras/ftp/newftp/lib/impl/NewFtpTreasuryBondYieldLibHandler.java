package cn.zswltech.mithras.ftp.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailTreasuryBondYieldListRSP;
import cn.zswltech.mithras.ftp.enums.FtpFrequency;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpSubModule;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpTreasuryBondYieldDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpTreasuryBondYieldLib;
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
public class NewFtpTreasuryBondYieldLibHandler
        extends NewFtpLibAbstractHandler<NewFtpTreasuryBondYieldLib, NewFtpTreasuryBondYieldDraft, NewFtpDetailTreasuryBondYieldListRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        return fields;
    }
    @Override
    protected NewFtpTreasuryBondYieldLib entity2Lib(NewFtpTreasuryBondYieldDraft f) {
        return BeanUtil.copyProperties(f, NewFtpTreasuryBondYieldLib.class);
    }

    @Override
    protected NewFtpTreasuryBondYieldDraft lib2Entity(NewFtpTreasuryBondYieldLib t) {
        return BeanUtil.copyProperties(t, NewFtpTreasuryBondYieldDraft.class);
    }

    @Override
    protected NewFtpDetailTreasuryBondYieldListRSP lib2Rsp(NewFtpTreasuryBondYieldLib f) {
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
                newFtpDetailTreasuryBondYieldListRSP.setDate(String.format("%s-Q%s", f.getDate().getYear(), (f.getDate().getMonthValue() + 2) / 3));
                break;
            default:
        }
        return newFtpDetailTreasuryBondYieldListRSP;
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.TREASURY_BOND_YIELD;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

}
