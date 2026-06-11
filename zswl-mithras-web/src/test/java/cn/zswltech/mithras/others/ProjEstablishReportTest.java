package cn.zswltech.mithras.others;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.ProjEstablishReportRender;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.RowRenderData;
import com.deepoove.poi.data.Rows;
import com.deepoove.poi.data.Tables;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * word文档生成
 *
 * @author wangchuanhao
 * @date 2022/7/4 1:44 PM
 */
public class ProjEstablishReportTest extends ApplicationTest {

    @Resource
    private ProjEstablishReportRender reportRender;

    @Test
    public void render() throws Exception {
        String osFileName = "/Users/wang/Desktop/输出报告.docx";
        reportRender.render(new FileOutputStream(osFileName), 17L);
    }

    public static void main(String[] args) throws IOException {
        String outputFileName = "/Users/wang/Desktop/生成文档.docx";
        ClassPathResource fileSystemResource = new ClassPathResource("doc/模板-租赁立项报告.docx");
        XWPFTemplate template = XWPFTemplate.compile(fileSystemResource.getFile()).render(
                new HashMap<String, Object>(){{
                    put("projName", "朗迅科技8000万元租赁项目");
                    put("reportCreateDate", LocalDateTimeUtil.format(LocalDate.now(), "yyyy年MM月dd日"));

                    put("lesseeInfoWithOrder", "1）\t杭州芯云半导体技术有限公司\n" +
                            "2）\t芯云半导体（诸暨）有限公司");
                    put("businessType", "售后回租");
                    put("creditAmount", "8000");
                    put("projectManager", "陈钢");
                    put("businessDeptMaster", "赵佳萍");
                    put("businessDeptLeader", "鲁素萍");
                    put("riskControlManager", "");

                    put("projSource", "由赵佳萍自主开发");
                    put("projBackgroud", "本项目为双承租人+担保人结构，授信主体为杭州朗迅科技有限公司（以下简称“朗迅科技”）+杭州芯云半导体技术有限公司（以下简称“杭州芯云”），因租赁物件在芯云半导体（诸暨）有限公司（以下简称“诸暨芯云”）名下，故将其纳入交易结构。\n" +
                            "担保措施上，朗迅科技+实控人徐振提供担保，徐振持朗迅38.39%的股份，是第一大股东。\n" +
                            "股权关系：朗迅100%持股杭州芯云，杭州芯云100%持股诸暨芯云。\n" +
                            "朗迅科技于2010年成立，以集成电路产业教学产品起家，目前与600多所院校建立合作关系，围绕集成电路设计、制造、封装、测试等环节，提供一站式教学解决方案。\n" +
                            "朗迅是全国职业院校技能大赛“集成电路开发及应用”赛事唯一技术支持企业，是全国集成电路EDA开发应用技术技能大赛唯一运营服务（技术支持）单位，还是教育部1+X证书制度《集成电路开发与测试》、《集成电路设计与验证》、《集成电路封装与测试》唯一培训评价组织。公司成立浙江省市两级院士专家工作站，多次获评国家高新技术企业，获评中国创新创业大赛优秀企业，技术实力在国内领先。\n" +
                            "凭借朗迅10多年积累的研发实力，公司于2021年进军芯片测试领域，杭州芯云+诸暨芯云，定位为独立第三方集成电路测试服务商，向芯片设计公司提供测试方案、晶圆测试、芯片测试等服务，收入毛利率约60%，净利率约30%。\n" +
                            "由于国内知名芯片测试机构大多进入美国实体名单管控，无法获得进口设备和技术，因此芯云肩负了组建大规模国产化芯片测试基地的重任，公司主要核心技术来源于自主研发，不受美国“卡脖子”制约。成立一年多来，获得多家风投机构投资，目前正在股改，中信证券领投，且作为上市辅导机构，预计于明年提交资料，登陆创业板。\n" +
                            "在体量上，诸暨芯云2.2万平米新基地在今年4月投入使用，可容纳机台300套，满产产值约8亿，第一大客户为华为海思，与芯云签订包产协议，承包不低于70%的产能。\n" +
                            "随着诸暨工厂的竣工，客户需购入大量设备，因此有资金需求。");
                    put("businessDept", "基础设施业务部");
                    put("projSponsorUser", "陈钢");
                    put("projCosponsorUser", "赵佳萍");


                    put("leaseMonthCount", "3");
                    put("creditAmountLoop", "否");
                    put("lesseeInfoWithComma", "杭州芯云半导体技术有限公司、芯云半导体（诸暨）有限公司");
                    put("leaseTypesWithComma", "回租");
                    put("fundsPurpose", "购买芯片测试设备");
                    put("earnestMoney", "授信金额2%");
                    put("consultingFee", "授信金额1.78%");
                    put("nominalPrice", "100元");
                    put("rateType", "固定");
                    put("leaseRatePercent", "5.6%");
                    put("repayTimesTotal", "12期，付款时，申请使用部分银票，剩余部分为电汇（根据资金部统筹安排）");
                    put("repayTimesYearly", "3");
                    put("guaranteeInfoWithOrder", "1.杭州朗迅科技有限公司提供担保（股权关系方面：朗迅100%持股杭州芯云，杭州芯云100%持股诸暨芯云）；\n" +
                            "2.实控人徐振提供担保（持朗迅38.39%股份，系第一大股东）；");
                    put("rentalCalcType", "等额租金");
                    put("pledgorMortgage", "无");
                    put("irrPercent", "1)全电汇不低于7.00%\n" +
                            "2)电汇+银票，不低于7.3%");
                    put("downPayment", "见还款计划表");

//                    List<Map<String, Object>> publicInformationListSection = new ArrayList<>();
//                    put("publicInformationListSection", publicInformationListSection);
//                    Map<String, Object> piMap1 = new HashMap<String, Object>() {{
//                        put("pi_cilentName", "1）杭州朗迅科技有限公司");
//                        put("pi_lawSuitTable", Tables.of().create());
//                        put("pi_lawSuitText", "无涉诉信息。");
//                        put("pi_zhixingTable", Tables.of().create());
//                        put("pi_zhixingText", "无被执行信息。");
//                        put("pi_zhongdengTable", Tables.of().create());
//                        put("pi_zhongdengText", "无登记信息。");
//                    }};
//                    Map<String, Object> piMap2 = new HashMap<String, Object>() {{
//                        put("pi_cilentName", "2）诸暨芯云教育发展有限公司");
//                        put("pi_lawSuitTable", Tables.of().create());
//                        put("pi_lawSuitText", "无涉诉信息。");
//                        put("pi_zhixingTable", Tables.of().create());
//                        put("pi_zhixingText", "无被执行信息。");
//                        put("pi_zhongdengTable", Tables.of().create());
//                        put("pi_zhongdengText", "无登记信息。");
//                    }};
//                    Map<String, Object> piMap3 = new HashMap<String, Object>() {{
//                        put("pi_cilentName", "3）杭州芯云半导体技术有限公司");
//                        put("pi_lawSuitTable", Tables.of().create());
//                        put("pi_lawSuitText", "无涉诉信息。");
//                        put("pi_zhixingTable", Tables.of().create());
//                        put("pi_zhixingText", "无被执行信息。");
//                        put("pi_zhongdengTable", Tables.of().create());
//                        put("pi_zhongdengText", "无登记信息。");
//                    }};
//                    publicInformationListSection.add(piMap1);
//                    publicInformationListSection.add(piMap2);
//                    publicInformationListSection.add(piMap3);

                    List<RowRenderData> keyManagerTableDataList = new ArrayList<>();
                    keyManagerTableDataList.add(Rows.of("姓名", "职务", "主要履历").center().create());
                    keyManagerTableDataList.add(Rows.of("", "", "").center().create());
                    keyManagerTableDataList.add(Rows.of("", "", "").center().create());
                    RowRenderData[] keyManagerTableDataArray = new RowRenderData[keyManagerTableDataList.size()];
                    keyManagerTableDataList.toArray(keyManagerTableDataArray);
//                    put("keyManagerTable", Tables.of(keyManagerTableDataArray)
//                            .create());

                    List<Map<String, Object>> clientAnalyzeListSection = new ArrayList<>();
                    put("clientAnalyzeListSection", clientAnalyzeListSection);
                    Map<String, Object> caMap1 = new HashMap<>();
                    caMap1.put("ca_clientAnalyzeTitle", "三、承租人一分析：杭州芯云半导体技术有限公司");
                    caMap1.put("ca_corpSection", new HashMap<String, Object>() {{
                        put("ca_c_title", "承租人一名称");
                        put("ca_c_clientName", "杭州芯云半导体技术有限公司");
                        put("ca_c_industryType", "集成电路芯片测试");
                        put("ca_c_orgScale", "小型");
                        put("ca_c_establishDate", "2020-05-11");
                        put("ca_c_bizLicenseCode", "91330108MA2HXBHQ77");
                        put("ca_c_registerCapital", "4000万元");
                        put("ca_c_realCapital", "4000万元");
                        put("ca_c_registerAddress", "浙江省杭州市滨江区浦沿街道六和路368号一幢（南）一楼F1066室");
                        put("ca_c_workAddress", "浙江省杭州市滨江区浦沿街道六和路368号一幢（南）一楼F1066室");
                        put("ca_c_businessScope", "集成电路芯片及产品销售；集成电路芯片设计及服务；集成电路芯片及产品制造等");
                        put("ca_c_keyManagerTable", Tables.of(keyManagerTableDataArray)
                                .create());
                        put("ca_c_shareholderTable", Tables.of().create());
                        put("ca_c_relatedEnterpriseTable", Tables.of().create());
                        put("ca_c_mortgageTable", Tables.of().create());
                        put("ca_c_equityTable", Tables.of().create());
                        put("ca_c_punishmentTable", Tables.of().create());
                        put("ca_c_environmentPenaltyTable", Tables.of().create());
                        put("ca_c_abnormalTable", Tables.of().create());
                        put("ca_c_judicialTable", Tables.of().create());
                        put("ca_c_consumptionRestrictionTable", Tables.of().create());
                        put("ca_c_zhixingTable", Tables.of().create());
                        put("ca_c_dishonestTable", Tables.of().create());
                        put("ca_c_zhongdengTable", Tables.of().create());
                    }});
                    caMap1.put("ca_normalSection", false);
                    Map<String, Object> caMap2 = new HashMap<>();
                    caMap2.put("ca_clientAnalyzeTitle", "六、担保人二（自然人）分析：徐振");
                    caMap2.put("ca_corpSection", false);
                    caMap2.put("ca_normalSection", new HashMap<String, Object>(){{
                        put("ca_n_clientName", "徐振");
                        put("ca_n_certNumber", "330721197710274056");
                        put("ca_n_gender", "男");
                        put("ca_n_marriageType", "已婚");
                    }});
                    clientAnalyzeListSection.add(caMap1);
                    clientAnalyzeListSection.add(caMap2);

                }});
        template.writeAndClose(new FileOutputStream(outputFileName));
    }

}
