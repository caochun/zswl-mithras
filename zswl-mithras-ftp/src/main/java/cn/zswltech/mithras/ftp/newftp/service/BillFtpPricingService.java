package cn.zswltech.mithras.ftp.newftp.service;

import cn.zswltech.mithras.ftp.oldftp.bo.BillFtpBO;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceExtDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceExtLib;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpMonthlyGuidanceExtLibService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;

@Service
public class BillFtpPricingService {

    @Resource
    private FtpEffectiveGuidanceQueryService ftpEffectiveGuidanceQueryService;
    @Resource
    private NewFtpMonthlyGuidanceExtLibService newFtpMonthlyGuidanceExtLibService;

    public BillFtpBO getBillFtp(LocalDate targetDate) {
        NewFtpBaseInfo effectOne = ftpEffectiveGuidanceQueryService.getEffectiveMonthlyOrThrow(targetDate);
        NewFtpMonthlyGuidanceExtLib ext = newFtpMonthlyGuidanceExtLibService.getOne(Wrappers.<NewFtpMonthlyGuidanceExtLib>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceExtDraft::getFtpId, effectOne.getId())
                .eq(NewFtpMonthlyGuidanceExtLib::getVersion, effectOne.getNewestVersion())
                .last("limit 1"));
        return new BillFtpBO(ext.getSellingPrice(), ext.getBuyingPrice());
    }
}
