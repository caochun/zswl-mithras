package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportCategoryEnum;
import cn.zswltech.mithras.associationreport.service.AssociationDictionaryService;
import cn.zswltech.mithras.associationreport.service.AssociationTop10ClientConcentrationService;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationTop10ClientConcentration;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/4/21
 * @description
 */
@Slf4j
@Component
public class AssociationTop10ClientConcentrationStoreData extends AbstractDataStore<AssociationTop10ClientConcentration> {
    @Resource
    private ClientService clientService;
    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;

    @Override
    public boolean storeFromSystemJobCheck(int year, int period) {
        LocalDate dataDate  = DateUtil.ensureQuarterLastDay(year, period);
        Map<String, Long> assetMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.CAPITAL_BALANCE.display, dataDate.getYear(), dataDate.getMonthValue());
        boolean condition = CollectionUtil.isNotEmpty(assetMap);
        log.info("金融局报送【最大十家客户（含集团）集中度统计表】自动取值-前置数据校验结果:资产负债表 = {}", condition);
        return condition;
    }

    @Override
    protected List<AssociationTop10ClientConcentration> parseFromExcel(InputStream inputStream) {
        List<List<Object>> rows = ExcelUtil.getReader(inputStream).read();
        if (CollectionUtil.isEmpty(rows)) {
            return Collections.emptyList();
        }
        log.info("金融局报送【最大十家客户（含集团）集中度统计表】-Excel解析结果:{}", JSONUtil.toJsonStr(rows));
        // 校验是否符合导入模板
        if (rows.size() < 4) {
            // 4行是因为序号行存在单元格合并，3、4两行合成了1行
            throw new MithrasException("<最大十家客户（含集团）集中度统计表>导入文件格式有误，请下载系统模板进行导入");
        }
        Object row1col1 = rows.get(0).get(0);
        Object row2col1 = rows.get(1).get(0);
        Object row3col1 = rows.get(2).get(0);
        boolean row1col1check = Objects.nonNull(row1col1) && StrUtil.equals(row1col1.toString(), "融资租赁公司最大十家客户（含集团）集中度统计表");
        boolean row2col1check = Objects.nonNull(row2col1) && StrUtil.equals(row2col1.toString(), "填报单位：");
        boolean row3col1check = Objects.nonNull(row3col1) && StrUtil.equals(row3col1.toString(), "序号");
        if (!row1col1check || !row2col1check || !row3col1check) {
            throw new MithrasException("<最大十家客户（含集团）集中度统计表>导入文件格式有误，请下载系统模板进行导入");
        }
        // 查询一下字典，备用
        Map<String, Map<String, String>> dictNameMap = SpringUtil.getBean(AssociationDictionaryService.class).getDisplay2CodeMap();
        // 开始处理Excel数据
        List<AssociationTop10ClientConcentration> list = new LinkedList<>();
        for (int i = 4; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (Objects.equals("合计", row.get(0))) {
                break;
            }
            try {
                list.add(this.convert(i-3, row, dictNameMap));
            } catch (Exception e) {
                log.error("金融局报送【最大十家客户（含集团）集中度统计表】-第{}行数据处理异常", (i-3), e);
                throw new MithrasException("数据处理发生异常");
            }
        }
        return list;
    }

    @Override
    protected List<AssociationTop10ClientConcentration> parseFromSystemData(AssociationReport associationReport) {
        // 取到计算数据的截止日期
        LocalDate targetDate = this.ensureMetricDate(associationReport);
        // 取所有生效的法人客户
        LambdaQueryWrapper<Client> query = Wrappers.lambdaQuery();
        query.eq(Client::getClientType, ClientType.CORPORATION.name());
        query.eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name());
        List<Client> clientList = SpringUtil.getBean(ClientMapper.class).selectList(query);
        if (CollectionUtil.isEmpty(clientList)) {
            return Collections.emptyList();
        }
        Map<Long, Client> clientMap = clientList.stream().collect(Collectors.toMap(Client::getId, e -> e));
        // 取剩余本金
        List<Long> clientIds = clientList.stream().map(Client::getId).collect(Collectors.toList());
        Map<Long, Long> remainingPrincipalMap = clientService.getClientRemainingPrincipalMap(clientIds, targetDate);
        List<Map.Entry<Long, Long>> sortList = remainingPrincipalMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        // 从后往前取最多10个
        List<Long> targetClientIds = new LinkedList<>();
        for (int i = sortList.size() - 1; i >= 0; i--) {
            if (targetClientIds.size() == 10) {
                break;
            }
            targetClientIds.add(sortList.get(i).getKey());
        }
        log.info("金融局报送【最大十家客户（含集团）集中度统计表】自动取值，剩余本金排行前十的客户id:{}", JSONUtil.toJsonStr(targetClientIds));
        // 取风险敞口
        Map<Long, Long> stockExposureMap = clientService.clientStockRiskExposureMap(targetClientIds, targetDate);
        // 取资产负债表：所有者权益（或股东权益）合计@期末余额
        LocalDate metricDate = this.ensureMetricDate(associationReport);
        Map<String, Long> assetValueMap = riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, RiskMetricFactorTable.CAPITAL_BALANCE.display, metricDate.getYear(), metricDate.getMonthValue());
        Long v = assetValueMap.get("所有者权益（或股东权益）合计@期末余额");
        // 处理数据
        List<AssociationTop10ClientConcentration> result = new ArrayList<>(targetClientIds.size());
        for (Long clientId : targetClientIds) {
            AssociationTop10ClientConcentration currentReportValue = new AssociationTop10ClientConcentration();
            // 客户名称
            currentReportValue.setCustName(clientMap.get(clientId).getClientName());
            Long remainingPrincipal = remainingPrincipalMap.get(clientId);
            if (Objects.isNull(remainingPrincipal) || remainingPrincipal <= 0) {
                continue;
            }
            // 剩余本金
            currentReportValue.setOnblToptCustLeasBal(Util.millimeterLong2WanBigDecimal(remainingPrincipal));
            // 占净资产比例
            if (Objects.nonNull(v) && v != 0) {
                currentReportValue.setOnblOnar(BigDecimal.valueOf(remainingPrincipal).divide(BigDecimal.valueOf(v), 8, RoundingMode.HALF_UP));
            }
            // 信用风险敞口
            Long riskExposure = stockExposureMap.get(clientId);
            if (Objects.nonNull(riskExposure)) {
                currentReportValue.setCredExps(Util.millimeterLong2WanBigDecimal(riskExposure));
            }
            // 保证金余额 = 剩余本金 - 信用风险敞口
            long marginBalance = remainingPrincipal - riskExposure;
            if (marginBalance >= 0) {
                currentReportValue.setDeitOth(Util.millimeterLong2WanBigDecimal(marginBalance));
            }
            result.add(currentReportValue);
        }
        return result;
    }

    @Override
    protected void check(List<AssociationTop10ClientConcentration> dataList) {

    }

    @Override
    protected IService<AssociationTop10ClientConcentration> serviceBean() {
        return SpringUtil.getBean(AssociationTop10ClientConcentrationService.class);
    }

    @Override
    protected AssociationReportCategoryEnum category() {
        return AssociationReportCategoryEnum.J0011;
    }

    private AssociationTop10ClientConcentration convert(int rowNum, List<Object> row, Map<String, Map<String, String>> dictNameMap) {
        AssociationTop10ClientConcentration associationTop10ClientConcentration = new AssociationTop10ClientConcentration();
        associationTop10ClientConcentration.setRowNum(rowNum);
        // 序号
        associationTop10ClientConcentration.setOnum(Optional.ofNullable(row.get(0)).map(Object::toString).orElse(null));
        // 客户名称
        associationTop10ClientConcentration.setCustName(Optional.ofNullable(row.get(1)).map(Object::toString).orElse(null));
        // 表内业务-前十大客户租赁余额
        associationTop10ClientConcentration.setOnblToptCustLeasBal(Optional.ofNullable(row.get(2)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表内业务-占净资产比例
        associationTop10ClientConcentration.setOnblOnar(Optional.ofNullable(row.get(3)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表外业务-担保
        associationTop10ClientConcentration.setOfblGuar(Optional.ofNullable(row.get(4)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 表外业务-其他
        associationTop10ClientConcentration.setOfblOth(Optional.ofNullable(row.get(5)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-合格质物
        associationTop10ClientConcentration.setDeitQulfSbim(Optional.ofNullable(row.get(6)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-合格保证
        associationTop10ClientConcentration.setDeitQulfAsue(Optional.ofNullable(row.get(7)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 扣减项-其他
        associationTop10ClientConcentration.setDeitOth(Optional.ofNullable(row.get(8)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        // 信用风险敞口
        associationTop10ClientConcentration.setCredExps(Optional.ofNullable(row.get(9)).filter(e -> NumberUtil.isNumber(e.toString())).map(e -> new BigDecimal(e.toString())).orElse(null));
        return associationTop10ClientConcentration;
    }
}
