package cn.zswltech.mithras.application.orchestration.adapter.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.ftp.oldftp.model.FtpMonthlyGuidance;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyGuidanceService;
import cn.zswltech.mithras.workbench.application.WorkbenchShortcutSpecialPort;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class WorkbenchShortcutSpecialPortAdapter implements WorkbenchShortcutSpecialPort {
    @Resource
    private FtpMonthlyGuidanceService ftpMonthlyGuidanceService;

    @Override
    public Optional<Long> getCurrentMonthlyFtpGuidanceId() {
        LocalDate now = LocalDate.now();
        FtpMonthlyGuidance guidance = ftpMonthlyGuidanceService.getOne(Wrappers.<FtpMonthlyGuidance>lambdaQuery()
                .eq(FtpMonthlyGuidance::getYear, now.getYear())
                .eq(FtpMonthlyGuidance::getMonth, now.getMonth()));
        if (ObjectUtil.isEmpty(guidance)) {
            return Optional.empty();
        }
        return Optional.ofNullable(guidance.getId());
    }
}
