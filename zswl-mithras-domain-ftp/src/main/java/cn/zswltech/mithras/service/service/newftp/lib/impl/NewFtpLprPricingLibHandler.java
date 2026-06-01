package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.zswltech.mithras.dto.newftp.NewFtpDetailLprPricingListRSP;
import cn.zswltech.mithras.service.enums.ftp.FtpFrequency;
import cn.zswltech.mithras.service.enums.newftp.NewFtpSubModule;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpLprPricingDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpLprPricingLib;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 16:56
 */
@Service
public class NewFtpLprPricingLibHandler
        extends NewFtpLibAbstractHandler<NewFtpLprPricingLib, NewFtpLprPricingDraft, NewFtpDetailLprPricingListRSP> {

    @Override
    public Set<String> compareIgnoreFieldNames() {
        HashSet<String> fields = new HashSet<>();
        return fields;
    }
    @Override
    protected NewFtpLprPricingLib entity2Lib(NewFtpLprPricingDraft f) {
        return BeanUtil.copyProperties(f, NewFtpLprPricingLib.class);
    }

    @Override
    protected NewFtpLprPricingDraft lib2Entity(NewFtpLprPricingLib t) {
        return BeanUtil.copyProperties(t, NewFtpLprPricingDraft.class);
    }

    @Override
    protected NewFtpDetailLprPricingListRSP lib2Rsp(NewFtpLprPricingLib f) {
        NewFtpDetailLprPricingListRSP newFtpDetailLprPricingListRSP = BeanUtil.copyProperties(f, NewFtpDetailLprPricingListRSP.class);
        if (FtpFrequency.MONTH.name().equals(f.getFrequency())) {
            newFtpDetailLprPricingListRSP.setLprDate(f.getLprDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN)));

        } else if (FtpFrequency.SEASON.name().equals(f.getFrequency())) {
            newFtpDetailLprPricingListRSP.setLprDate(String.format("%s-Q%s", f.getLprDate().getYear(), (f.getLprDate().getMonthValue() + 2) / 3));
        }
        return newFtpDetailLprPricingListRSP;
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.LPR_PRICING;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }

}
