package cn.zswltech.mithras.others.service.riskcontrol;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.controller.riskcontrol.RiskControlOpinionMonitorController;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlOpinionMonitor;
import cn.zswltech.mithras.service.mapper.riskcontrol.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.service.mapper.riskcontrol.RiskControlWarnMonitorMapper;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionVersionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.opinion.RiskControlOpinionHandleStatus.PEND_HANDLE;
import static cn.zswltech.mithras.service.enums.opinion.RiskControlOpinionHandleStatus.REJECTED;

/**
 * @description:
 * @author: 舆情相关任务
 * @date: 2023/3/2 13:40
 */
public class RiskControlVersionServiceTest extends ApplicationTest {
    @Resource
    private RiskControlOpinionVersionService riskControlOpinionVersionService;
    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;
    @Resource
    private RiskControlWarnMonitorMapper riskControlWarnMonitorMapper;
    @Resource
    private RiskControlOpinionMonitorController riskControlOpinionMonitorController;

    @Test
    public void testList() {
        riskControlOpinionVersionService.opinionInitiateApproval(riskControlOpinionMonitorMapper.selectBatchIds(ListUtil.toList(1L,
                17805L
        )));

        riskControlOpinionVersionService.warnInitiateApproval(riskControlWarnMonitorMapper.selectBatchIds(ListUtil.toList(1872954885514096642L, 1872954885518290945L
        )));
    }

    //过滤出无需处理舆情
    @Test
    public void filterPublicOpinion() {
        List<RiskControlOpinionMonitor> riskControlOpinionMonitors = riskControlOpinionMonitorMapper.selectList(Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                .in(RiskControlOpinionMonitor::getHandleStatus, PEND_HANDLE.name(), REJECTED.name()));
        Set<String> allClients = riskControlOpinionMonitors.stream().map(RiskControlOpinionMonitor::getCreditCode).collect(Collectors.toSet());
        Set<String> focusClient = riskControlOpinionMonitorController.focusClientUscCodes();
        Set<String> ignoreClient = new HashSet<>();
        allClients.forEach(uscCode -> {
            if (!focusClient.contains(uscCode)) {
                ignoreClient.add(uscCode);
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append("select * from risk_control_opinion_monitor where credit_code in (");
        ignoreClient.forEach(client -> {
            sb.append("'");
            sb.append(client);
            sb.append("',");
        });
        sb.append(");");
        System.out.println(sb.toString());
    }
}
