package cn.zswltech.mithras.others.kpi;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.kpi.service.KpiProjGuessCalculateService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjGuessBaseInfo;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjGuessDivide;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessDivideService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/6/19
 * @description
 */
public class KpiProjGuessTest extends ApplicationTest {
    @Resource
    private KpiProjGuessService kpiProjGuessService;
    @Resource
    private KpiProjGuessCalculateService kpiProjGuessCalculateService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private KpiProjGuessBaseInfoService kpiProjGuessBaseInfoService;
    @Resource
    private KpiProjGuessDivideService kpiProjGuessDivideService;
    @Resource
    private ContractReceiptMapper contractReceiptMapper;

    @Test
    public void calculateTest() {
        kpiProjGuessService.calculate(2278L, 2023, 6);
    }

    @Test
    public void newCalculate() {
        kpiProjGuessCalculateService.calculate(null, LocalDate.of(2024,6, 30), true);
    }

    //导入某月
    @Test
    public void importExcel() {
        String fileName = "/Users/vico/Downloads/绩效数据.xlsx";
        int year = 2024;
        int month = 6;
        List<List<Object>> dataList = ExcelUtil.getReader(fileName).read(0);
        String contractCode;
        String receiptCode;
        List<String> errorList = new ArrayList<>();
        List<KpiProjGuessBaseInfo> baseInfos = kpiProjGuessBaseInfoService.list(Wrappers.<KpiProjGuessBaseInfo>lambdaQuery()
                .eq(KpiProjGuessBaseInfo::getCalculateDateYear, year)
                .eq(KpiProjGuessBaseInfo::getCalculateDateMonth, month));
        List<KpiProjGuessDivide> guessDivideList = kpiProjGuessDivideService.list(Wrappers.<KpiProjGuessDivide>lambdaQuery()
                .in(KpiProjGuessDivide::getKpiProjGuessId, baseInfos.stream().map(KpiProjGuessBaseInfo::getId).collect(Collectors.toSet())));
        Map<Long, List<KpiProjGuessDivide>> baseId2Divide =
                guessDivideList.stream().collect(Collectors.groupingBy(KpiProjGuessDivide::getKpiProjGuessId));
        Map<Long, List<KpiProjGuessBaseInfo>> contractId2BaseInfo = baseInfos.stream().collect(Collectors.groupingBy(KpiProjGuessBaseInfo::getContractId));
        Map<String, Long> contractCode2Id = contractBaseInfoService.listByIds(contractId2BaseInfo.keySet()).stream()
                .collect(Collectors.toMap(ContractBaseInfo::getContractCode, ContractBaseInfo::getId, (a, b) -> a));
        Map<String, Long> receiptCode2Id = contractReceiptMapper.selectList(Wrappers.<ContractReceipt>lambdaQuery()
                .in(ContractReceipt::getId, baseInfos.stream().map(KpiProjGuessBaseInfo::getReceiptId).collect(Collectors.toList())))
                .stream().collect(Collectors.toMap(ContractReceipt::getReceiptCode, ContractReceipt::getId, (a, b) -> a));
        for (List<Object> row : dataList) {
            contractCode = String.valueOf(row.get(2));
            List<KpiProjGuessBaseInfo> guessBaseInfoList = contractId2BaseInfo.get(contractCode2Id.get(contractCode));
            if(ObjectUtil.isEmpty(guessBaseInfoList)) {
                errorList.add(contractCode);
                continue;
            }
            KpiProjGuessBaseInfo baseInfo = guessBaseInfoList.get(0);
            receiptCode = String.valueOf(row.get(5));
            if(ObjectUtil.isNotEmpty(receiptCode)) {
                Long receiptId = receiptCode2Id.get(receiptCode);
                for (KpiProjGuessBaseInfo e : guessBaseInfoList) {
                    if (ObjectUtil.equals(e.getReceiptId(), receiptId)){
                        baseInfo = e;
                        break;
                    }
                }
            }
            baseInfo.setBonusTotal(LongUtil.other2Long(String.valueOf(row.get(15))));
            baseInfo.setPaymentTotal(LongUtil.other2Long(String.valueOf(row.get(18))));
            //修改子类
            List<KpiProjGuessDivide> guessDivides = baseId2Divide.get(baseInfo.getId());
            if(ObjectUtil.isNotEmpty(guessDivides)) {
                for (KpiProjGuessDivide e : guessDivides) {
                    e.setProfitTotal(baseInfo.getBonusTotal());
                    e.setBonusTotal(new BigDecimal(baseInfo.getBonusTotal().toString()).multiply(BigDecimal.valueOf(e.getDivideWeight())).divide(new BigDecimal("1000000"), 2, RoundingMode.HALF_UP).longValue());
                    e.setPaymentTotal(new BigDecimal(baseInfo.getPaymentTotal().toString()).multiply(BigDecimal.valueOf(e.getDivideWeight())).divide(new BigDecimal("1000000"), 2, RoundingMode.HALF_UP).longValue());
                }
            }
        }
        kpiProjGuessBaseInfoService.updateBatchById(baseInfos);
        kpiProjGuessDivideService.updateBatchById(guessDivideList);
        System.out.println("-----------");
        System.out.println(errorList);
        System.out.println("-----------");

    }
}
