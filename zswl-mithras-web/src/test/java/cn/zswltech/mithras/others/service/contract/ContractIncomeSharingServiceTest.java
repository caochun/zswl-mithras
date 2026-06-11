package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.application.orchestration.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.monthly.event.MonthlyManageUpdateEvent;
import cn.zswltech.mithras.application.orchestration.monthly.listener.MonthlyManageUpdateListener;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/4/18
 * @description
 */
public class ContractIncomeSharingServiceTest extends ApplicationTest {
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    public void export() {
        LocalDate start = LocalDate.of(2024, 6, 1);
        LocalDate end = LocalDate.of(2024, 6, 30);
        contractIncomeSharingService.export(FileUtil.getOutputStream("/Users/mockorz/实际利率法-202406.xlsx"), IncomeConfirmTypeEnum.AIR, start, end);
        contractIncomeSharingService.export(FileUtil.getOutputStream("/Users/mockorz/剩余本金法-202406.xlsx"), IncomeConfirmTypeEnum.RP, start, end);
    }

    @Test
    public void calculateIncomeSharingTest() {
        contractIncomeSharingService.calculateIncomeSharing(1530L);
    }

    @Resource
    private MonthlyManageUpdateListener monthlyManageUpdateListener;
    @Test
    public void updateAirTest() {
        MonthlyManageUpdateEvent event = new MonthlyManageUpdateEvent("剩余本金法-变更", new MonthlyManageUpdateEvent.DataObject(MonthlyModuleTypeEnum.AIR.name(), 4679L, null));
        monthlyManageUpdateListener.onApplicationEvent(event);
    }


}
