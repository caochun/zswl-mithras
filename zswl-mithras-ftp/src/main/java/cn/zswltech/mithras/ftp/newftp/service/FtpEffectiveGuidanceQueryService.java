package cn.zswltech.mithras.ftp.newftp.service;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;

@Service
public class FtpEffectiveGuidanceQueryService {

    @Resource
    private NewFtpBaseInfoService newFtpBaseInfoService;

    public NewFtpBaseInfo getEffectiveMonthly(LocalDate targetDate) {
        return newFtpBaseInfoService.getOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                .le(NewFtpBaseInfo::getMonth, targetDate)
                .orderByDesc(NewFtpBaseInfo::getMonth)
                .last(StringUtil.mysqlLimitOne()));
    }

    public NewFtpBaseInfo getEffectiveMonthlyOrThrow(LocalDate targetDate) {
        NewFtpBaseInfo newFtpBaseInfo = getEffectiveMonthly(targetDate);
        if (newFtpBaseInfo == null) {
            throw new MithrasException("未找到生效的FTP");
        }
        return newFtpBaseInfo;
    }
}
