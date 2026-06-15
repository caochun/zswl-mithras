package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.customer.enums.OrgScaleType;
import cn.zswltech.mithras.associationreport.enums.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationEntityEconomyServiceService;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationEntityEconomyService;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationReport;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.dashboard.application.GuanYuanOperationService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.PayIncomeDTO;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2025/4/18
 * @description 服务实体经济情况表
 */
@Slf4j
@Component
public class AssociationEntityEconomyData extends AbstractDataStore<AssociationEntityEconomyService> {
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;
    @Resource
    private GuanYuanOperationService guanYuanOperationService;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        LocalDate dataDate = DateUtil.ensureQuarterLastDay(year, period);
        // 国资快报
        Map<String, Long> gzkbMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.GZKB.display, dataDate.getYear(), dataDate.getMonthValue());
        // 投放收益率表
        List<PayIncomeDTO> payIncomeList = guanYuanOperationService.listPayIncome(LocalDate.of(dataDate.getYear(), 1, 1), dataDate);
        boolean condition1 = CollectionUtil.isNotEmpty(payIncomeList);
        boolean condition2 = CollectionUtil.isNotEmpty(gzkbMap);
        log.info("金融局报送【服务实体经济情况表】自动取值-前置数据校验结果:投放收益率表 = {}, 国资快报 = {}", condition1, condition2);
        return condition1 && condition2;
    }

    @Override
    protected List<AssociationEntityEconomyService> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【服务实体经济情况表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 3) {
            throw new MithrasException("<服务实体经济情况表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司服务实体经济情况（新）（季报）");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<服务实体经济情况表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationEntityEconomyService> list = new LinkedList<>();
            try {
                if (rows.size() < 22 ) {
                    throw new MithrasException("表格格式不正确，缺失必要行");
                }
                list.add(this.convert(rows, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【服务实体经济情况表】数据处理异常", e);
                throw new MithrasException("数据处理异常");
            }
        return list;
    }

    @Override
    protected List<AssociationEntityEconomyService> parseFromSystemData(AssociationReport currentReport) {
        // 是否1季度
        boolean isFirstQuarter = Objects.equals(currentReport.getReportPeriod(), 1);
        // 取上一期
        int[] yearPeriod = this.ensureLastOneYearPeriod(currentReport);
        AssociationReport lastReport = associationReportQueryService.findByCategoryYearPeriod(currentReport.getReportCategoryCode(), yearPeriod[0], yearPeriod[1]);
        if (Objects.isNull(lastReport)) {
            throw new MithrasException(String.format("金融局报送【服务实体经济情况表】自动取值-没有找到上一期报送记录[current:%s]", currentReport.getReportInstanceId()));
        }
        AssociationEntityEconomyService lastReportValue = SpringUtil.getBean(AssociationEntityEconomyServiceService.class).getModelByReportInstanceId(lastReport.getReportInstanceId());
        LocalDate metricDate = this.ensureMetricDate(currentReport);
        // 查询国资快报
        Map<String, Long> gzkbMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.GZKB.display, metricDate.getYear(), metricDate.getMonthValue());
        // 查询投放收益率表
        List<PayIncomeDTO> payIncomeList = guanYuanOperationService.listPayIncome(LocalDate.of(metricDate.getYear(), 1, 1), metricDate);
        if (CollectionUtil.isEmpty(payIncomeList)) {
            log.info("金融局报送【服务实体经济情况表】自动取值-没有找到指定日期的投放收益率表数据[metricDate:{}]", LocalDateTimeUtil.format(metricDate, DatePattern.NORM_DATETIME_PATTERN));
            return Collections.emptyList();
        }
        AssociationEntityEconomyService currentReportValue = new AssociationEntityEconomyService();
        // 本年租赁业务累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagLeasBusiAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagLeasBusiAmtAeop).orElse(BigDecimal.ZERO));
        // 本年租赁业务累计投放额-期末数 = 投放收益率情况表：项目金额（投放金额）合计数
        currentReportValue.setTyagLeasBusiAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 其中：本年制造业租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagMnftLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagMnftLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 其中：本年制造业租赁累计投放额-期末数 = 投放收益率情况表：行业为制造业的项目金额（投放金额）合计数
        currentReportValue.setTyagMnftLeasAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .filter(e -> StrUtil.isNotBlank(e.getIndustryDisplay()) && e.getIndustryDisplay().startsWith("制造业"))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 本年服务产业链租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagIndtLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagIndtLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年服务产业链租赁累计投放额-期末数 = 不填充
        // 本年服务民生消费租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagConsLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagConsLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年服务民生消费租赁累计投放额-期末数 = 投放收益率情况表：风险策略为民生消费类的项目金额（投放金额）合计数
        currentReportValue.setTyagConsLeasAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .filter(e -> StrUtil.equals(e.getRiskControlIndustryClassifyDisplay(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.display()))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 本年科技金融建设租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagSatyLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagSatyLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年科技金融建设租赁累计投放额-期末数 = 投放收益率情况表：行业为科学研究和技术服务业的项目金额（投放金额）合计数
        currentReportValue.setTyagSatyLeasAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .filter(e -> StrUtil.isNotBlank(e.getIndustryDisplay()) && e.getIndustryDisplay().startsWith("科学研究和技术服务业"))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 本年绿色金融建设租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagGrenLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagGrenLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年绿色金融建设租赁累计投放额-期末数 = 投放收益率情况表：风险策略为新能源、新材料、新科技等智能制造、先进装备制造行业的项目金额（投放金额）合计数
        currentReportValue.setTyagGrenLeasAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .filter(e -> StrUtil.equals(e.getRiskControlIndustryClassifyDisplay(), RiskControlIndustryClassify.NEW_MATERIALS.display()))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 本年普惠金融建设租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagIcveLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagIcveLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年普惠金融建设租赁累计投放额-期末数 = 投放收益率情况表：公司类型为小型、微型的项目金额（投放金额）合计数
        currentReportValue.setTyagIcveLeasAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .filter(e -> StrUtil.equalsAny(e.getOrgScaleDisplay(), OrgScaleType.TINY.display(), OrgScaleType.SMALL.display()))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 本年养老金融建设租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagPensLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagPensLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年养老金融建设租赁累计投放额-期末数 = 不填充
        // 本年海洋金融建设租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagOceaLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagOceaLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年海洋金融建设租赁累计投放额-期末数 = 投放收益率情况表：业务组为航运业务部的项目金额（投放金额）合计数（业务部门id = 31）
        currentReportValue.setTyagOceaLeasAmtAeop(Util.millimeterLong2WanBigDecimal(payIncomeList.stream()
                .filter(e -> Objects.nonNull(e.getProjectAmount()))
                .filter(e -> Objects.equals(e.getDeptId(), 31L))
                .mapToLong(PayIncomeDTO::getProjectAmount)
                .sum())
        );
        // 本年开放金融建设租赁累计投放额-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagOpenLeasAmtAbop(isFirstQuarter ? BigDecimal.ZERO : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagOpenLeasAmtAeop).orElse(BigDecimal.ZERO));
        // 本年开放金融建设租赁累计投放额-期末数 = 不填充
        // 本年累计服务客户数-期初数 = 上期期末数，若为1季度则为0
        currentReportValue.setTyagServCustNumAbop(isFirstQuarter ? 0 : Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getTyagServCustNumAeop).orElse(0));
        // 本年累计服务客户数-期末数 = 本年投放客户总数
        currentReportValue.setTyagServCustNumAeop(this.ensureTyagServCustNumAeop(metricDate));
        // 历年累计租赁业务金额（含本年）-期初数 = 上期期末数
        currentReportValue.setOyagLeasBusiAmtAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getOyagLeasBusiAmtAeop).orElse(BigDecimal.ZERO));
        // 历年累计服务客户数（含本年）-期初数 = 上期期末数
        currentReportValue.setOyagServCustNumAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getOyagServCustNumAeop).orElse(0));
        // 本年累计实际缴纳各类税金-期初数 = 上期期末数
        currentReportValue.setThsyTaxpAmtAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getThsyTaxpAmtAeop).orElse(BigDecimal.ZERO));
        // 本年累计实际缴纳各类税金-期末数 = 取企业财务快报（国资快报）
        currentReportValue.setThsyTaxpAmtAeop(Util.millimeterLong2WanBigDecimal(Optional.ofNullable(gzkbMap.get("已交税费总额@本年累计")).orElse(0L)));
        // 其中：（1）增值税-期初数 = 上期期末数
        currentReportValue.setIncrTaxAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getIncrTaxAeop).orElse(BigDecimal.ZERO));
        // 其中：（1）增值税-期末数 = 取企业财务快报（国资快报）
        currentReportValue.setIncrTaxAeop(Util.millimeterLong2WanBigDecimal(Optional.ofNullable(gzkbMap.get("其中:已交增值税@本年累计")).orElse(0L)));
        // （2）企业所得税-期初数 = 上期期末数
        currentReportValue.setCorpInctAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getCorpInctAeop).orElse(BigDecimal.ZERO));
        // （2）企业所得税-期末数 = 取企业财务快报（国资快报）
        currentReportValue.setCorpInctAeop(Util.millimeterLong2WanBigDecimal(Optional.ofNullable(gzkbMap.get("已交所得税@本年累计")).orElse(0L)));
        // （3）其他税金-期初数 = 上期期末数
        currentReportValue.setOthTaxAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getOthTaxAeop).orElse(BigDecimal.ZERO));
        // 历年累计实际缴纳各类税金（含本年）-期初数 = 上期期末数
        currentReportValue.setOtyTaxpAmtAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getOtyTaxpAmtAeop).orElse(BigDecimal.ZERO));
        // 表外业务余额-期初数 = 上期期末数
        currentReportValue.setOfblAmtAbop(Optional.ofNullable(lastReportValue).map(AssociationEntityEconomyService::getOfblAmtAeop).orElse(BigDecimal.ZERO));
        // 表外业务余额-期末数 = 填0
        currentReportValue.setOfblAmtAeop(BigDecimal.ZERO);
        currentReportValue.calculate();
        return Collections.singletonList(currentReportValue);
    }

    @Override
    protected void check(List<AssociationEntityEconomyService> dataList) {

    }

    @Override
    protected IService<AssociationEntityEconomyService> serviceBean() {
        return SpringUtil.getBean(AssociationEntityEconomyServiceService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0006;
    }

    //一行转多行
    private AssociationEntityEconomyService convert(List<List<Object>> rows, Map<String, Map<String, String>> dictNameMap) {
        AssociationEntityEconomyService bean = new AssociationEntityEconomyService();
        bean.setRowNum(1);
        bean.setOp("insert");

        // 第3行：本年累计租赁业务金额
        bean.setTyagLeasBusiAmtAbop(parseBigDecimal(rows.get(3).get(2)));
        bean.setTyagLeasBusiAmtAotc(parseBigDecimal(rows.get(3).get(3)));
        bean.setTyagLeasBusiAmtAeop(parseBigDecimal(rows.get(3).get(4)));

        // 第4行：制造业租赁
        bean.setTyagMnftLeasAmtAbop(parseBigDecimal(rows.get(4).get(2)));
        bean.setTyagMnftLeasAmtAotc(parseBigDecimal(rows.get(4).get(3)));
        bean.setTyagMnftLeasAmtAeop(parseBigDecimal(rows.get(4).get(4)));

        // 第5行：产业链租赁
        bean.setTyagIndtLeasAmtAbop(parseBigDecimal(rows.get(5).get(2)));
        bean.setTyagIndtLeasAmtAotc(parseBigDecimal(rows.get(5).get(3)));
        bean.setTyagIndtLeasAmtAeop(parseBigDecimal(rows.get(5).get(4)));

        // 第6行：民生消费租赁
        bean.setTyagConsLeasAmtAbop(parseBigDecimal(rows.get(6).get(2)));
        bean.setTyagConsLeasAmtAotc(parseBigDecimal(rows.get(6).get(3)));
        bean.setTyagConsLeasAmtAeop(parseBigDecimal(rows.get(6).get(4)));

        // 第7行：科技金融租赁
        bean.setTyagSatyLeasAmtAbop(parseBigDecimal(rows.get(7).get(2)));
        bean.setTyagSatyLeasAmtAotc(parseBigDecimal(rows.get(7).get(3)));
        bean.setTyagSatyLeasAmtAeop(parseBigDecimal(rows.get(7).get(4)));

        // 第8行：绿色金融租赁
        bean.setTyagGrenLeasAmtAbop(parseBigDecimal(rows.get(8).get(2)));
        bean.setTyagGrenLeasAmtAotc(parseBigDecimal(rows.get(8).get(3)));
        bean.setTyagGrenLeasAmtAeop(parseBigDecimal(rows.get(8).get(4)));

        // 第9行：普惠金融租赁
        bean.setTyagIcveLeasAmtAbop(parseBigDecimal(rows.get(9).get(2)));
        bean.setTyagIcveLeasAmtAotc(parseBigDecimal(rows.get(9).get(3)));
        bean.setTyagIcveLeasAmtAeop(parseBigDecimal(rows.get(9).get(4)));

        // 第10行：养老金融租赁
        bean.setTyagPensLeasAmtAbop(parseBigDecimal(rows.get(10).get(2)));
        bean.setTyagPensLeasAmtAotc(parseBigDecimal(rows.get(10).get(3)));
        bean.setTyagPensLeasAmtAeop(parseBigDecimal(rows.get(10).get(4)));

        // 第11行：海洋金融租赁
        bean.setTyagOceaLeasAmtAbop(parseBigDecimal(rows.get(11).get(2)));
        bean.setTyagOceaLeasAmtAotc(parseBigDecimal(rows.get(11).get(3)));
        bean.setTyagOceaLeasAmtAeop(parseBigDecimal(rows.get(11).get(4)));

        // 第12行：开放金融租赁
        bean.setTyagOpenLeasAmtAbop(parseBigDecimal(rows.get(12).get(2)));
        bean.setTyagOpenLeasAmtAotc(parseBigDecimal(rows.get(12).get(3)));
        bean.setTyagOpenLeasAmtAeop(parseBigDecimal(rows.get(12).get(4)));

        // ================= 服务客户板块 =================
        // 第13行：服务客户数（整型字段）
        bean.setTyagServCustNumAbop(parseInt(rows.get(13).get(2)));
        bean.setTyagServCustNumAotc(parseInt(rows.get(13).get(3)));
        bean.setTyagServCustNumAeop(parseInt(rows.get(13).get(4)));

        // ================= 历史数据板块 =================
        // 第14行：历年租赁金额
        bean.setOyagLeasBusiAmtAbop(parseBigDecimal(rows.get(14).get(2)));
        bean.setOyagLeasBusiAmtAotc(parseBigDecimal(rows.get(14).get(3)));
        bean.setOyagLeasBusiAmtAeop(parseBigDecimal(rows.get(14).get(4)));

        // 第15行：历年客户数（整型字段）
        bean.setOyagServCustNumAbop(parseInt(rows.get(15).get(2)));
        bean.setOyagServCustNumAotc(parseInt(rows.get(15).get(3)));
        bean.setOyagServCustNumAeop(parseInt(rows.get(15).get(4)));

        // ================= 税务板块 =================
        // 第16行：实缴税金
        bean.setThsyTaxpAmtAbop(parseBigDecimal(rows.get(16).get(2)));
        bean.setThsyTaxpAmtAotc(parseBigDecimal(rows.get(16).get(3)));
        bean.setThsyTaxpAmtAeop(parseBigDecimal(rows.get(16).get(4)));

        // 第17行：增值税
        bean.setIncrTaxAbop(parseBigDecimal(rows.get(17).get(2)));
        bean.setIncrTaxAotc(parseBigDecimal(rows.get(17).get(3)));
        bean.setIncrTaxAeop(parseBigDecimal(rows.get(17).get(4)));

        // 第18行：企业所得税
        bean.setCorpInctAbop(parseBigDecimal(rows.get(18).get(2)));
        bean.setCorpInctAotc(parseBigDecimal(rows.get(18).get(3)));
        bean.setCorpInctAeop(parseBigDecimal(rows.get(18).get(4)));

        // 第19行：其他税金
        bean.setOthTaxAbop(parseBigDecimal(rows.get(19).get(2)));
        bean.setOthTaxAotc(parseBigDecimal(rows.get(19).get(3)));
        bean.setOthTaxAeop(parseBigDecimal(rows.get(19).get(4)));

        // 第20行：历年税金
        bean.setOtyTaxpAmtAbop(parseBigDecimal(rows.get(20).get(2)));
        bean.setOtyTaxpAmtAotc(parseBigDecimal(rows.get(20).get(3)));
        bean.setOtyTaxpAmtAeop(parseBigDecimal(rows.get(20).get(4)));

        // ================= 表外业务 =================
        // 第21行：表外业务金额
        bean.setOfblAmtAbop(parseBigDecimal(rows.get(21).get(2)));
        bean.setOfblAmtAotc(parseBigDecimal(rows.get(21).get(3)));
        bean.setOfblAmtAeop(parseBigDecimal(rows.get(21).get(4)));
        return bean;
    }

    private Integer parseInt(Object o) {
        return Optional.ofNullable(o).map(Object::toString).map(NumberUtils::toInt).orElse(null);
    }

    private int ensureTyagServCustNumAeop(LocalDate reportDate) {
        LocalDate queryDateFrom = LocalDate.of(reportDate.getYear(), 1, 1);
//        LocalDate queryDateTo = LocalDate.of(reportDate.getYear(), 12, 31);
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailMapper.selectList(
                Wrappers.<PaymentActualDetail>lambdaQuery().ge(PaymentActualDetail::getPaidInDate, queryDateFrom).le(PaymentActualDetail::getPaidInDate, reportDate)
        );
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return 0;
        }
        Set<Long> paymentIds = paymentActualDetailList.stream().map(PaymentActualDetail::getPaymentId).collect(Collectors.toSet());
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoMapper.selectBatchIds(paymentIds);
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return 0;
        }
        return (int) paymentBaseInfoList.stream().map(PaymentBaseInfo::getClientId).distinct().count();
    }
}
