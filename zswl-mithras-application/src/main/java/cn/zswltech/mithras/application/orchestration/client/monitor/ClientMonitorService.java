package cn.zswltech.mithras.application.orchestration.client.monitor;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.application.monitor.dto.*;
import cn.zswltech.mithras.dto.client.client.ClientListREQ;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorMapper;
import cn.zswltech.mithras.riskcontrol.monitor.ClientMonitorOpinionDetailRsp;
import cn.zswltech.mithras.riskcontrol.monitor.ClientMonitorWarnDetailRsp;
import cn.zswltech.mithras.riskcontrol.monitor.DeptPieDataDto;
import cn.zswltech.mithras.riskcontrol.monitor.WarnCountDto;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.application.orchestration.riskcontrol.opinion.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.riskcontrol.warning.RiskControlWarnMonitorService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/12/23 16:49
 */
@Service
public class ClientMonitorService {

    @Resource
    private ClientMapper clientMapper;

    @Resource
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;

    @Resource
    private RiskControlWarnMonitorService riskControlWarnMonitorService;

    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;

    @Resource
    private RiskControlWarnMonitorMapper riskControlWarnMonitorMapper;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private ClientService clientService;

    public PageR<ClientMonitorListRsp> clientMonitorList(ClientMonitorListReq req) {
        // 控权限
        ClientListREQ clientListREQ = new ClientListREQ();
        clientListREQ.setClientName(req.getClientName());
        clientListREQ.setClientType(ClientType.CORPORATION.name());
        clientListREQ.setShowApprovalFlag(true);
        clientListREQ.setPageSize(99999);
        Page<Client> clients = clientService.newPageList(clientListREQ);
        if(ObjectUtil.isEmpty(clients.getRecords())){
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        Set<String> authorizedUsccs = clients.getRecords().stream().map(Client::getUscCode).collect(Collectors.toSet());
        Map<String, Integer> opCountMap = riskControlOpinionMonitorMapper.selectCountByClient(authorizedUsccs).stream()
                .collect(Collectors.toMap(WarnCountDto::getUscc, WarnCountDto::getWarnCount));
        Set<String> warnUsccs = riskControlWarnMonitorMapper.selectRiskClientUsccs(authorizedUsccs);
        Set<Long> clientIds = clients.getRecords().stream()
                .filter(client -> ObjectUtil.isEmpty(req.getOnlyWarn())
                        || (opCountMap.containsKey(client.getUscCode()) && warnUsccs.contains(client.getUscCode())))
                .map(Client::getId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(clientIds)) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        Page<ClientMonitorListRsp> page = clientMapper.listMonitorClient(new Page<>(req.getPage(), req.getPageSize()), req.getClientName(), clientIds);
        Map<String, ClientMonitorListRsp> map = page.getRecords().stream().collect(Collectors.toMap(ClientMonitorListRsp::getUscc, item -> item, (v1, v2) -> v1));

        // 查询红黄灯预警数量
        Map<String, Integer> redWarnCountMap = riskControlWarnMonitorService
                .selectCountByClient(map.keySet(), "3").stream()
                .collect(Collectors.toMap(WarnCountDto::getUscc, WarnCountDto::getWarnCount));
        Map<String, Integer> yellowWarnCountMap = riskControlWarnMonitorService
                .selectCountByClient(map.keySet(), "2").stream()
                .collect(Collectors.toMap(WarnCountDto::getUscc, WarnCountDto::getWarnCount));

        page.getRecords().forEach(item -> {
            item.setRedWarnCount(redWarnCountMap.getOrDefault(item.getUscc(), 0));
            item.setYellowWarnCount(yellowWarnCountMap.getOrDefault(item.getUscc(), 0));
            item.setOpCount(opCountMap.getOrDefault(item.getUscc(), 0));
        });
        return PageR.of(page);
    }

    //客户监控预警列表
    PageR<ClientMonitorWarnListRsp> clientMonitorWarnList(ClientMonitorWarnListReq req) {
        // 控权限
        ClientListREQ clientListREQ = new ClientListREQ();
        clientListREQ.setClientName(req.getClientName());
        clientListREQ.setClientType(ClientType.CORPORATION.name());
        clientListREQ.setShowApprovalFlag(true);
        clientListREQ.setPageSize(99999);
        Page<Client> clients = clientService.newPageList(clientListREQ);
        if (ObjectUtil.isEmpty(clients.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Page<RiskControlWarnMonitor> warnList = riskControlWarnMonitorService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskControlWarnMonitor>lambdaQuery()
                .in(RiskControlWarnMonitor::getCreditCode, clients.getRecords().stream().map(Client::getUscCode).collect(Collectors.toList()))
                .like(ObjectUtil.isNotEmpty(req.getClientName()), RiskControlWarnMonitor::getChiName, req.getClientName())
                .like(ObjectUtil.isNotEmpty(req.getWarnTitle()), RiskControlWarnMonitor::getTitle, req.getWarnTitle())
                .like(ObjectUtil.isNotEmpty(req.getWarnCode()), RiskControlWarnMonitor::getWarnCode, req.getWarnCode())
                .in(ObjectUtil.isNotEmpty(req.getWarnLevels()), RiskControlWarnMonitor::getWarnLevel, req.getWarnLevels())
                .eq(ObjectUtil.isNotEmpty(req.getWarnStatus()), RiskControlWarnMonitor::getHandleStatus, req.getWarnStatus()));
        List<ClientMonitorWarnListRsp> warnListRsps = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(warnList.getRecords())) {
            warnList.getRecords().forEach(e -> {
                ClientMonitorWarnListRsp rsp = new ClientMonitorWarnListRsp();
                rsp.setId(e.getId());
                rsp.setClientName(e.getChiName());
                rsp.setWarnTitle(e.getTitle());
                rsp.setWarnCode(e.getWarnCode());
                rsp.setWarnStatus(e.getHandleStatus());
                rsp.setUscc(e.getCreditCode());
                rsp.setDataTime(e.getDataTime());
                rsp.setWarnLevel(e.getWarnLevel());
                warnListRsps.add(rsp);
            });
        }
        return PageR.of(warnListRsps, warnList.getTotal());
    }

    //客户监控逾期列表
    PageR<ClientMonitorOpinionListRsp> clientMonitorOpinionList(ClientMonitorOpinionListReq req) {
        // 控权限
        ClientListREQ clientListREQ = new ClientListREQ();
        clientListREQ.setClientName(req.getClientName());
        clientListREQ.setClientType(ClientType.CORPORATION.name());
        clientListREQ.setShowApprovalFlag(true);
        clientListREQ.setPageSize(99999);
        Page<Client> clients = clientService.newPageList(clientListREQ);
        if (ObjectUtil.isEmpty(clients.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Page<RiskControlOpinionMonitor> opinionMonitorPage = riskControlOpinionMonitorService.page(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                .in(RiskControlOpinionMonitor::getCreditCode, clients.getRecords().stream().map(Client::getUscCode).collect(Collectors.toList()))
                .like(ObjectUtil.isNotEmpty(req.getClientName()), RiskControlOpinionMonitor::getChiName, req.getClientName())
                .eq(ObjectUtil.isNotEmpty(req.getOpinionStatus()), RiskControlOpinionMonitor::getHandleStatus, req.getOpinionStatus()));
        List<ClientMonitorOpinionListRsp> opinionListRsps = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(opinionMonitorPage.getRecords())) {
            opinionMonitorPage.getRecords().forEach(e -> {
                ClientMonitorOpinionListRsp rsp = new ClientMonitorOpinionListRsp();
                rsp.setId(e.getId());
                rsp.setClientName(e.getChiName());
                rsp.setUscc(e.getCreditCode());
                rsp.setTitle(e.getTitle());
                rsp.setWarnStar(e.getWarnStar());
                rsp.setWarnLevel(e.getWarnLevel());
                rsp.setDataTime(e.getInfoPublDate());
                rsp.setHandleResult(e.getHandleStatus());
                opinionListRsps.add(rsp);
            });
        }
        return PageR.of(opinionListRsps, opinionMonitorPage.getTotal());
    }



    /**
     * 统计相关指标
     * @return
     */
    public ClientMonitorStatisticRsp clientMonitorStatistic() {
        // 统计监控客户数
        Integer totalMonitorClient = clientMapper.countMonitorClient();

        // 统计预警客户数
        Integer totalWarnClient = riskControlWarnMonitorMapper.countDealingClient();

        // 统计今日新增预警客户数
        Integer newWarnClient = riskControlWarnMonitorMapper.countNewWarnClient();
        // 统计今日预警全关闭客户数
        Integer closeWarnClient = riskControlWarnMonitorMapper.countCloseWarnClient();

        // 统计舆情客户数
        Integer totalOpClient = riskControlOpinionMonitorMapper.countDealingClient();

        // 统计今日新增舆情客户数
        Integer newOpClient = riskControlOpinionMonitorMapper.countNewOpClient();

        // 统计今日舆情全关闭客户数
        Integer closeOpClient = riskControlOpinionMonitorMapper.countCloseOpClient();

        ClientMonitorStatisticRsp rsp = new ClientMonitorStatisticRsp();
        rsp.setMonitorClientCount(totalMonitorClient);
        rsp.setWarnClientCount(totalWarnClient);
        rsp.setNewWarnClientCount(newWarnClient);
        rsp.setCloseWarnClientCount(closeWarnClient);
        rsp.setOpClientCount(totalOpClient);
        rsp.setNewOpClientCount(newOpClient);
        rsp.setCloseOpClientCount(closeOpClient);
        return rsp;
    }

    public ClientMonitorRiskLineChartRsp clientMonitorRiskLineChart() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dayInx = LocalDate.now().minusDays(5);
        // 查询预警近5天数据
        List<RiskControlWarnMonitor> warnList = riskControlWarnMonitorMapper.selectLatest5Day();
        Map<LocalDate, List<RiskControlWarnMonitor>> warnGroup = warnList.stream().collect(Collectors.groupingBy(item -> item.getCreateTime().toLocalDate()));
        Map<String, Integer> warnLineData = new LinkedHashMap<>();
        for(int i = 0; i < 5; i++){
            if(warnGroup.containsKey(dayInx)){
                warnLineData.put(dayInx.format(formatter), warnGroup.get(dayInx).size());
            }else{
                warnLineData.put(dayInx.format(formatter), 0);
            }
            dayInx = dayInx.plusDays(1);
        }
        // 查询舆情近5天数据
        List<RiskControlOpinionMonitor> opinionList = riskControlOpinionMonitorMapper.selectLatest5Day();
        Map<String, List<RiskControlOpinionMonitor>> opinionGroup = opinionList.stream().collect(Collectors.groupingBy(item -> item.getCreateTime().toLocalDate().format(formatter)));
        Map<String, Integer> opLineData = new HashMap<>();
        dayInx = LocalDate.now().minusDays(5);
        for(int i = 0; i < 5; i++){
            if(opinionGroup.containsKey(dayInx.format(formatter))){
                opLineData.put(dayInx.format(formatter), opinionGroup.get(dayInx.format(formatter)).size());
            }else{
                opLineData.put(dayInx.format(formatter), 0);
            }
            dayInx = dayInx.plusDays(1);
        }
        ClientMonitorRiskLineChartRsp rsp = new ClientMonitorRiskLineChartRsp();
        rsp.setWarnLineData(warnLineData);
        rsp.setOpLineData(opLineData);
        return rsp;
    }

    public ClientMonitorPieChartRsp clientMonitorPieChart() {
        ClientMonitorPieChartRsp rsp = new ClientMonitorPieChartRsp();
        // 查询未关闭的预警数
        List<DeptPieDataDto> warnList =riskControlWarnMonitorMapper.selectHandlingWarn();
        for (DeptPieDataDto deptPieDataDto : warnList) {
            if(ObjectUtil.isEmpty(deptPieDataDto.getDeptName())){
                deptPieDataDto.setDeptName("其他");
            }
        }
        BigDecimal totalWarn = new BigDecimal(warnList.size());
        Map<String, List<DeptPieDataDto>> warnGroup = warnList.stream().collect(Collectors.groupingBy(DeptPieDataDto::getDeptName));
        Map<String, BigDecimal> warnPieData = warnGroup.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, item -> new BigDecimal(item.getValue().size()).divide(totalWarn, 2, RoundingMode.HALF_UP)));


        // 查询未处理或关闭的舆情数
        List<DeptPieDataDto> opinionList = riskControlOpinionMonitorMapper.selectHandlingOpinion();
        for (DeptPieDataDto deptPieDataDto : opinionList) {
            if(ObjectUtil.isEmpty(deptPieDataDto.getDeptName())){
                deptPieDataDto.setDeptName("其他");
            }
        }
        BigDecimal totalOp = new BigDecimal(opinionList.size());
        Map<String, List<DeptPieDataDto>> opinionGroup = opinionList.stream().collect(Collectors.groupingBy(DeptPieDataDto::getDeptName));
        Map<String, BigDecimal> opPieData = opinionGroup.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, item -> new BigDecimal(item.getValue().size()).divide(totalOp, 2, RoundingMode.HALF_UP)));
        rsp.setWarnPieData(warnPieData);
        rsp.setOpPieData(opPieData);
        return rsp;
    }

    public List<ClientMonitorWarnDetailRsp> clientMonitorWarnDetail(Long clientId) {
        return riskControlWarnMonitorMapper.selectMonitorWarnDetail(clientId);
    }

    public List<ClientMonitorOpinionDetailRsp> clientMonitorOpDetail(Long clientId) {
        return riskControlOpinionMonitorMapper.selectMonitorOpDetail(clientId);
    }
}
