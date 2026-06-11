package cn.zswltech.mithras.others.service.contract;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.mapper.contract.ContractMortgageItemMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractPledgeItemMapper;
import cn.zswltech.mithras.contract.model.contract.ContractMortgageItem;
import cn.zswltech.mithras.contract.model.contract.ContractPledgeItem;
import cn.zswltech.mithras.foundation.util.Util;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 导出抵押、质押物清单
 *
 * @author wangchuanhao
 * @date 2023/3/17 11:00 AM
 */
public class ExportMortgagePledgeTest extends ApplicationTest {

    private static final String DIR = "/Users/wang/Desktop/线上抵押质押清单文件";

    @Resource
    private ContractPledgeItemMapper contractPledgeItemMapper;
    @Resource
    private ContractMortgageItemMapper contractMortgageItemMapper;

    @Test
    public void exportPledge() throws Exception {
        List<ContractPledgeItem> contractPledgeItemList = contractPledgeItemMapper.selectList(Wrappers.lambdaQuery());
        Map<Long, List<ContractPledgeItem>> cpiMap = contractPledgeItemList.stream().collect(Collectors.groupingBy(ContractPledgeItem::getPledgeId));
        for (Map.Entry<Long, List<ContractPledgeItem>> cpiEntry : cpiMap.entrySet()) {
            ExcelWriter excelWriter = new ExcelWriter();
            excelWriter.writeHeadRow(ListUtil.toList("序号", "种类", "评估价值（元）"));
            for (ContractPledgeItem contractPledgeItem : cpiEntry.getValue()) {
                excelWriter.writeRow(ListUtil.toList(
                        Optional.ofNullable(contractPledgeItem.getSequence()).map(String::valueOf).orElse(""),
                        Optional.ofNullable(contractPledgeItem.getCategory()).map(String::valueOf).orElse(""),
                        Optional.ofNullable(contractPledgeItem.getAssessedValue()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("")
                ));
            }
            excelWriter.flush(new FileOutputStream(String.format("%s/%s_质押物清单.xlsx", DIR, cpiEntry.getKey())), true);
        }

    }

    @Test
    public void exportMortgage() throws Exception {
        List<ContractMortgageItem> contractMortgageItemList = contractMortgageItemMapper.selectList(Wrappers.lambdaQuery());
        Map<Long, List<ContractMortgageItem>> cmiMap = contractMortgageItemList.stream().collect(Collectors.groupingBy(ContractMortgageItem::getMortgageId));
        for (Map.Entry<Long, List<ContractMortgageItem>> cpiEntry : cmiMap.entrySet()) {
            ExcelWriter excelWriter = new ExcelWriter();
            // 序号	种类	识别号类型	名称	唯一识别号	供应商	数量	计量单位	购置日期	账面原值（元）	评估原值（元）	账面净值（元）	评估净值（元）	发票号	存放地点
            excelWriter.writeHeadRow(ListUtil.toList("序号", "种类", "识别号类型", "名称", "唯一识别号", "供应商", "数量", "计量单位", "购置日期", "账面原值（元）", "评估原值（元）", "账面净值（元）", "评估净值（元）", "发票号", "存放地点"));
            for (ContractMortgageItem contractMortgageItem : cpiEntry.getValue()) {
                excelWriter.writeRow(ListUtil.toList(
                        Optional.ofNullable(contractMortgageItem.getSequence()).map(String::valueOf).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getCategory()).map(String::valueOf).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getUniqueIdentifyCodeType()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getName()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getUniqueIdentifyCode()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getSupplier()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getQuantity()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getUnit()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getPurchaseDate()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getOriginalBookValue()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getAssessedValue()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getOriginalBookNetValue()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getAssessedNetValue()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getInvoiceCode()).orElse(""),
                        Optional.ofNullable(contractMortgageItem.getStoragePlace()).orElse("")
                ));
            }
            excelWriter.flush(new FileOutputStream(String.format("%s/%s_抵押物清单.xlsx", DIR, cpiEntry.getKey())), true);
        }
    }

}
