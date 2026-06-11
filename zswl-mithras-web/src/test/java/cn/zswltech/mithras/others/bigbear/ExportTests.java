package cn.zswltech.mithras.others.bigbear;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.basedata.mapper.AddressDictionaryMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.basedata.mapper.model.AddressDictionary;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/5/14 10:36
 * @description
 */
@Slf4j
@ActiveProfiles("pre")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExportTests {

    @Resource
    private AddressDictionaryMapper  addressDictionaryMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;

    @Test
    public void exportExcel() {
        // 首先将excel 读取进来
        File file = new File("/Users/bigbear/Downloads/公用事业排查底稿-科技取数(1).xlsx");
        // 修改为直接读取为 RelationExcelModel 对象列表
        ExcelReader reader = ExcelUtil.getReader(file);
        List<RelationExcelModel> models = reader.readAll(RelationExcelModel.class);
        // 找到曲区县字典和客户工商信息
        Map<String, String> addressMap = addressDictionaryMapper.selectList(null)
                .stream().collect(Collectors.toMap(AddressDictionary::getCode, AddressDictionary::getDisplay));
        List<String> clientName = models.stream().map(RelationExcelModel::getAssessSubject).collect(Collectors.toList());
        clientName.addAll(models.stream().map(RelationExcelModel::getCustomerName).collect(Collectors.toList()));
        clientName = clientName.stream().distinct().collect(Collectors.toList());

        List<Client> clientList = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .in(Client::getClientName, clientName));
        Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, Function.identity()));
        Map<String, Client> clientNameMap = clientList.stream().collect(Collectors.toMap(Client::getClientName, Function.identity()));

        // 找到曲区县字典和客户工商信息
        Map<Long, CorpAddressInfo> infoMap = corpAddressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
                        .eq(CorpAddressInfo::getAddressType,  "WORK_ADDRESS")
                        .in(CorpAddressInfo::getClientId, clientMap.keySet()))
                .stream().collect(Collectors.toMap(CorpAddressInfo::getClientId, Function.identity()));

        // 获取客户舆情信息
        Map<String, List<RiskControlOpinionMonitor>> opinionMonitorMap = riskControlOpinionMonitorMapper.selectList(Wrappers.<RiskControlOpinionMonitor>lambdaQuery()
                        .in(RiskControlOpinionMonitor::getChiName, clientNameMap.keySet()))
                .stream().collect(Collectors.groupingBy(RiskControlOpinionMonitor::getChiName));

        for (RelationExcelModel model : models) {
            // 优先取主体，如果主体没有取客户名称
            String name = model.getAssessSubject();
            if (StrUtil.isBlank(name)) {
                name = model.getCustomerName();
            }

            // 获取对应的工商信息
            CorpAddressInfo corpAddressInfo = infoMap.get(clientNameMap.get(name).getId());
            if (corpAddressInfo != null) {
                model.setCity(addressMap.get(corpAddressInfo.getCity()));
                model.setCounty(addressMap.get(corpAddressInfo.getDistrict()));
            }

            // 是否有舆情信息
            if (opinionMonitorMap.containsKey(name)) {
                // 遍历取出
                List<RiskControlOpinionMonitor> riskControlOpinionMonitors = opinionMonitorMap.get(name);
                // 一条一条拼接，然后换行
                StringBuilder assessSubjectSentimentInformation = new StringBuilder();
                for (RiskControlOpinionMonitor riskControlOpinionMonitor : riskControlOpinionMonitors) {
                    assessSubjectSentimentInformation.append(riskControlOpinionMonitor.getTitle()).append("\r\n");
                }
                model.setAssessSubjectSentimentInformation(assessSubjectSentimentInformation.toString());
            } else {
                model.setAssessSubjectSentimentInformation("无");
            }
        }

        log.info("开始导出数据...");
        ExcelWriter writer = ExcelUtil.getWriter(file.getAbsoluteFile());
        writer.getStyleSet().getCellStyle().setWrapText(true);
        writer.write(models);
        writer.flush();
    }

    @Data
    public static class RelationExcelModel {
        @Alias("序号")
        private String index;
        @Alias("业务部门")
        private String businessDepartment;
        @Alias("客户名称")
        private String customerName;
        @Alias("评估主体")
        private String assessSubject;
        @Alias("所属集团")
        private String belongGroup;
        @Alias("年度风险策略行业类别")
        private String yearRiskStrategyIndustryCategory;
        @Alias("合同金额(万元)")
        private String contractAmount;
        @Alias("剩余租金(万元)")
        private String remainingRent;
        @Alias("剩余本金(万元)")
        private String remainingPrincipal;
        @Alias("租赁保证金(万元)")
        private String leaseDeposit;
        @Alias("风险敞口(万元)")
        private String riskExposure;
        @Alias("省份")
        private String province;
        @Alias("城市")
        private String city;
        @Alias("区县")
        private String county;
        @Alias("评估主体是否为发债主体")
        private String assessSubjectIsIssue;
        @Alias("评估主体2024年债券发行金额")
        private String assessSubject2024BondIssueAmount;
        @Alias("评估主体2023年评级")
        private String assessSubject2023Rating;
        @Alias("评估主体2024年评级")
        private String assessSubject2024Rating;
        @Alias("评估主体舆情信息")
        private String assessSubjectSentimentInformation;
    }
}
