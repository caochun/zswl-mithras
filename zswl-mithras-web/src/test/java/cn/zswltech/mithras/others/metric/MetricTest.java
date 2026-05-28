package cn.zswltech.mithras.others.metric;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.emit.config.EmitRemoteCfg;
import cn.zswltech.mithras.metric.emit.model.req.risk.index.RiskIndexReqBody;
import cn.zswltech.mithras.metric.emit.model.req.risk.index.RiskIndexReqSingleBody;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricCurrency;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFrequency;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricUnit;
import cn.zswltech.mithras.metric.mapper.model.GuanyuanColAssist;
import cn.zswltech.mithras.metric.mapper.model.RiskMetric;
import cn.zswltech.mithras.metric.service.GuanyuanColAssistService;
import cn.zswltech.mithras.metric.service.RiskMetricService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yibin
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MetricTest {
    @Resource
    MetricEmitter emitter;
    @Resource
    private EmitRemoteCfg cfg;

    @Resource
    private RiskMetricService riskMetricService;
    @Resource
    private GuanyuanColAssistService guanyuanColAssistService;


    @Test
    public void 金融局管报3() {
        String s = "注册资本（时点数）\n" +
                "实收资本（时点数）\n" +
                "从业人数（时点数）\n" +
                "总资产（时点数）\n" +
                "其中：货币资金\n" +
                "国债\n" +
                "风险资产\n" +
                "租赁资产\n" +
                "其中：（1）经营租赁资产\n" +
                "（2）融资租赁资产\n" +
                "其中：直接租赁资产\n" +
                "售后回租资产\n" +
                "固定收益类证券投资余额（时点数）\n" +
                "发放保理融资款本金（时点数）\n" +
                "净资产（时点数）\n" +
                "总负债（时点数）\n" +
                "对外融资余额（时点数）\n" +
                "其中：（1）对股东负债\n" +
                "（2）短期金融机构借款\n" +
                "（3）长期金融机构借款\n" +
                "（4）发行的债券余额\n" +
                "（5）资产证券化融资余额\n" +
                "（6）其他融资余额\n" +
                "总收入（当年累计数）\n" +
                "其中：（1）经营租赁业务收入\n" +
                "          （2）融资租赁业务收入\n" +
                "                 其中：（1）利息收入\n" +
                "                             （2）费用收入\n" +
                "             (3)其他收入\n" +
                "税前总利润（当年累计数）\n" +
                "净利润（当年累计数）\n" +
                "缴纳税收（当年累计数）\n" +
                "银行借款利率（时点数）\n" +
                "客户数（时点数）\n" +
                "融资租赁业务笔数（当年累计数）\n" +
                "其中：（按照业务类型划分）\n" +
                "直接租赁业务笔数\n" +
                "售后回租业务笔数\n" +
                "其中：（按照重点领域和产业方向划分）\n" +
                "服务中小微企业业务笔数\n" +
                "服务“三农”业务笔数\n" +
                "服务涉海企业业务笔数\n" +
                "服务科技型企业业务笔数\n" +
                "融资租赁投放额（当年累计数）\n" +
                "    其中：（按照业务类型划分）\n" +
                "        直接租赁投放额\n" +
                "        售后回租投放额\n" +
                "    其中：（按照重点领域和产业方向划分）\n" +
                "服务中小微企业投放额\n" +
                "服务“三农”投放额\n" +
                "服务涉海企业投放额\n" +
                "服务科技型企业投放额\n" +
                "租赁物总价款（当年累计数）\n" +
                "对外担保余额（时点数）\n" +
                "对股东担保余额（时点数）\n" +
                "租金余额（时点数）\n" +
                "其中：逾期租金\n" +
                "其中：逾期90天以内租金\n" +
                "逾期90天以上1年以内租金\n" +
                "逾期1年以上租金\n" +
                "融资租赁资产总额（时点数）\n" +
                "其中:建筑工程设备\n" +
                "通用机械设备\n" +
                "工业装备\n" +
                "采矿、冶金专用设备\n" +
                "交通运输设备\n" +
                "农林牧渔设备\n" +
                "印刷、日化及日用品生产专用设备\n" +
                "纺织、服装和皮革加工专用设备\n" +
                "医疗制药设备\n" +
                "通信电子设备\n" +
                "节能环保设备\n" +
                "能源设备\n" +
                "基础设施及不动产\n" +
                "其他\n" +
                "不良资产余额（时点数）\n" +
                "不良资产率\n" +
                "资产减值损失准备（时点数）\n" +
                "关联方的融资租赁业务余额（时点数）";
        ArrayList<String> colList = ListUtil.toList(s.split("\n"));
        String reportName = "金融局管报3";
        assist(colList, reportName);

    }

    @Test
    public void 租赁行业信息填报表_2() {
        String s = "营业收入\n" +
                "  其中：主营业务收入\n" +
                "营业成本\n" +
                "  其中：主营业务成本\n" +
                "营业利润\n" +
                "利润总额\n" +
                "纳税额";
        ArrayList<String> colList = ListUtil.toList(s.split("\n"));
        String reportName = "租赁行业信息填报表2";
        assist(colList, reportName);
    }


    @Test
    public void 租赁行业信息填报表_1() {
        String s = "货币资金\n" +
                "应收账款\n" +
                "流动资产合计\n" +
                "非流动资产合计\n" +
                "资产总额\n" +
                "流动负债\n" +
                "非流动负债合计\n" +
                "负债总额\n" +
                "所有者权益\n" +
                "应收融资租赁款余额\n" +
                "    其中：应收融资租赁款总额\n" +
                "      减：未实现融资收益\n" +
                "融资租赁资产余额\n" +
                "   其中，按融资租赁业务分：\n" +
                "            直接融资租赁\n" +
                "            售后回租租赁\n" +
                "            其他融资租赁\n" +
                "         按租赁物分：\n" +
                "           “双碳 ”（包括但不限于清洁能源、节能环保等绿色产业设备）\n" +
                "            医疗设备\n" +
                "            其他租赁物\n" +
                "         按业务投放地区分：\n" +
                "            省内\n" +
                "            省外\n" +
                "经营性租赁资产余额\n" +
                "不良租赁资产余额\n" +
                "融入资金余额\n" +
                "  其中：向股东借款余额\n" +
                "        向金融机构借款余额\n" +
                "        其他融入资金余额（主要为：   资产证券化                 )\n" +
                "社保缴纳人数";
        ArrayList<String> colList = ListUtil.toList(s.split("\n"));
        String reportName = "租赁行业信息填报表1";
        assist(colList, reportName);
    }

    @Test
    public void 月报1_列初始化() {
        List<String> colList = ListUtil.of("总收入", "     其中:经营租赁业务收入", "          融资租赁业务收入", "             其中：利息收入", "                   费用收入",
                "         其他收入", "租赁资产", "    其中：经营租赁资产", "         融资租赁资产", "            其中：直接租赁资产", "                 售后回租资产",
                "跨省融资租赁资产余额（承租人为省外）", "    其中：跨省售后回租资产余额", "融资租赁投放额", "    其中：直接租赁投放额", "          售后回租投放额",
                "固定收益类证券投资余额", "国债余额", "资产减值损失准备"
        );
        String reportName = "月报1";
        assist(colList, reportName);
    }

    private void assist(List<String> colList, String reportName) {
        List<GuanyuanColAssist> list = new ArrayList<>();
        LocalDate start = LocalDate.of(2019, 12, 1);
        guanyuanColAssistService.remove(Wrappers.<GuanyuanColAssist>lambdaQuery().eq(GuanyuanColAssist::getReportName, reportName));
        for (int i = 0; i < 10 * 12; i++) {
            start = start.plusMonths(1);
            for (int i1 = 0; i1 < colList.size(); i1++) {
                GuanyuanColAssist at = new GuanyuanColAssist();
                at.setMonth(LocalDateTimeUtil.format(start, "yyyy-MM"));
                at.setReportName(reportName);
                at.setColName(colList.get(i1));
                at.setSort(i1);
                list.add(at);
            }
            if (i % 10 == 0) {
                guanyuanColAssistService.saveBatch(list);
                list.clear();
            }
        }
        if (list.size() > 0) {
            guanyuanColAssistService.saveBatch(list);
        }
    }

    @Test
    void testRequireMetric() {
        Set<String> list = emitter.requiredMetrics(LocalDateTimeUtil.parseDate("202301", "yyyyMM"));
        System.out.println(list);

    }

    @Test
    void emitRiskIndex() {
        RiskIndexReqBody riskIndexReqBody = new RiskIndexReqBody();
        riskIndexReqBody.setDataTimeBegin(LocalDateTime.now().minusDays(10));
        riskIndexReqBody.setDataTimeEnd(LocalDateTime.now());
        riskIndexReqBody.setOrgCode(cfg.getAuthOrg());
        riskIndexReqBody.setFrequency("MONTH");
        riskIndexReqBody.setIndexList(ListUtil.of(new RiskIndexReqSingleBody("融资租赁资产规模", "A10000396_JC011", "1000000")));
        emitter.emitRiskIndex(riskIndexReqBody);

    }

    @Test
    @Transactional
    @Rollback(value = false)
    void initMetric() {
        List<RiskMetric> list = new ArrayList<>();

        FileInputStream inputStream = IoUtil.toStream(new File("/Users/luyi/Desktop/zb.xlsx"));
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        List<Sheet> sheets = reader.getSheets();
        sheets = sheets.stream().filter(e -> e.getSheetName().contains("浙商租赁")).collect(Collectors.toList());
        //1
        Sheet sheet = sheets.get(0);
        for (int row = 2; row <= 69; row++) {
            String metricName = sheet.getRow(row).getCell(3).getStringCellValue();
            String metricCode = sheet.getRow(row).getCell(4).getStringCellValue();
            String metricUnit = sheet.getRow(row).getCell(8).getStringCellValue();
            String frequency = sheet.getRow(row).getCell(17).getStringCellValue();
            String currency = sheet.getRow(row).getCell(27).getStringCellValue();
            String completeStr = sheet.getRow(row).getCell(11).getCellType() == CellType.NUMERIC ?
                    String.valueOf(sheet.getRow(row).getCell(11).getNumericCellValue()) : sheet.getRow(row).getCell(11).getStringCellValue();
            Boolean needReport = completeStr.trim().startsWith("1231");

            //
            RiskMetricFrequency riskMetricFrequency = RiskMetricFrequency.ofDisplay(frequency);
            RiskMetricUnit riskMetricUnit = RiskMetricUnit.ofDisplay(metricUnit);
            RiskMetricCurrency riskMetricCurrency = RiskMetricCurrency.CNY;
            RiskMetric m = new RiskMetric();
            m.setMetricCode(metricCode);
            m.setMetricName(metricName);
            if (null != riskMetricFrequency) {
                m.setFrequency(riskMetricFrequency.name());
            }
            m.setCurrency(riskMetricCurrency.name());
            if (null != riskMetricUnit) {
                m.setUnit(riskMetricUnit.name());
            }
            list.add(m);
        }

        sheet = sheets.get(1);
        for (int row = 2; row <= 84; row++) {
            String t = sheet.getRow(row).getCell(0).toString();
            if ("删".equals(t.trim())) {
                continue;
            }
            String metricName = sheet.getRow(row).getCell(1).getStringCellValue();
            String metricCode = sheet.getRow(row).getCell(2).getStringCellValue();
            String metricUnit = sheet.getRow(row).getCell(8).getStringCellValue();
            String frequency = sheet.getRow(row).getCell(9).getStringCellValue();
            String completeStr = sheet.getRow(row).getCell(3).getCellType() == CellType.NUMERIC ?
                    String.valueOf(sheet.getRow(row).getCell(3).getNumericCellValue()) : sheet.getRow(row).getCell(3).getStringCellValue();
            Boolean needReport = completeStr.trim().startsWith("1231");

            //
            RiskMetricFrequency riskMetricFrequency = RiskMetricFrequency.ofDisplay(frequency);
            RiskMetricUnit riskMetricUnit = RiskMetricUnit.ofDisplay(metricUnit);
            RiskMetricCurrency riskMetricCurrency = RiskMetricCurrency.CNY;

            RiskMetric m = new RiskMetric();
            m.setMetricCode(metricCode);
            m.setMetricName(metricName);
            if (null != riskMetricFrequency) {
                m.setFrequency(riskMetricFrequency.name());
            }
            m.setCurrency(riskMetricCurrency.name());
            if (null != riskMetricUnit) {
                m.setUnit(riskMetricUnit.name());
            }
            list.add(m);
        }
        list = list.stream().filter(e -> StrUtil.isNotBlank(e.getMetricName())).collect(Collectors.toList());
        riskMetricService.remove(Wrappers.lambdaQuery());
        riskMetricService.saveBatch(list);
    }
}