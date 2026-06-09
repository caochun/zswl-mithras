package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.contract.application.dto.FinancialCostsBO;
import cn.zswltech.mithras.contract.core.application.ContractRentActualService;
import cn.zswltech.mithras.service.service.contract.impl.ContractReceiptServiceImpl;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yupengfei
 * @date 2024/4/15 19:38
 */
@Component
@Slf4j
public class ContractActualRentRender extends AbstractContractRender<ContractBaseInfo> {

    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractReceiptServiceImpl contractReceiptService;
    @Resource
    private OssClient ossClient;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        //查询实际租金列表数据
        Wrapper<ContractRentActual> queryWrapper = Wrappers.<ContractRentActual>lambdaQuery().eq(ContractRentActual::getContractId, contractBaseInfo.getId());
        List<ContractRentActual> contractRentActualList = contractRentActualService.list(queryWrapper);
        if (CollectionUtils.isEmpty(contractRentActualList)) {
            throw new MithrasException("没有找到实际租金表数据，无法生成实际租金表");
        }
        //起租状态的合同只有一个借据
        List<ContractReceipt> contractReceipts = contractReceiptService.listByContractId(contractBaseInfo.getId());
        if (CollectionUtils.isEmpty(contractReceipts) && contractReceipts.size() != 1) {
            throw new MithrasException("该合同存在多张实际租金表");
        } else {
            ContractReceipt contractReceipt = contractReceipts.get(0);
            //2.税率：直租税率：13%    非直租税率：6%
            BigDecimal taxRate = LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType()) ? GlobalConstants.TAX_RATE_ZHI_ZU : GlobalConstants.TAX_RATE_FEI_ZHI_ZU;
            //合同起租财务成本详情
            FinancialCostsBO financialCosts = contractReceiptService.getFinancialCosts(contractBaseInfo, contractRentActualList, taxRate, contractReceipt.getId());

            // 渲染参数
            Map<String, Object> renderMap = new HashMap<>();
            //1.起租日期
            LocalDate date = contractBaseInfo.getActualLeaseDate();
            String actualLeaseDate = "【   】年【   】月【   】日";
            if (!Objects.isNull(date)) {
                actualLeaseDate = "【" + date.getYear() + "】年【" + date.getMonth().getValue() + "】月【" + date.getDayOfMonth() + "】日";
            }

            renderMap.put(RenderParameterKeyHolder.ACTUAL_LEASE_DATE, actualLeaseDate);
            //2.租赁成本 单位：元
            renderMap.put(RenderParameterKeyHolder.CAPITAL_SUM, Util.toYuan(financialCosts.getCapitalSum()));
            //租赁成本 单位：元（大写）
            BigDecimal capitalSum = NumberUtil.div(financialCosts.getCapitalSum().toString(), GlobalConstants.MONEY_MULTIPLE, 2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.CAPITAL_SUM_CN, NumberChineseFormatter.format(capitalSum.doubleValue(), true, true));

            //3.租赁利息 单位：元
            renderMap.put(RenderParameterKeyHolder.INTEREST_SUM, Util.toYuan(financialCosts.getInterestSum()));
            //租赁利息 单位：元（大写）
            BigDecimal interestSum = NumberUtil.div(financialCosts.getInterestSum().toString(), GlobalConstants.MONEY_MULTIPLE, 2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.INTEREST_SUM_CN, NumberChineseFormatter.format(interestSum.doubleValue(), true, true));

            //4.租金总额 单位：元
            renderMap.put(RenderParameterKeyHolder.RENT_SUM, Util.toYuan(financialCosts.getRentSum()));
            //租金总额 单位：元（大写）
            BigDecimal renSum = NumberUtil.div(financialCosts.getRentSum().toString(), GlobalConstants.MONEY_MULTIPLE, 2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.RENT_SUM_CN, NumberChineseFormatter.format(renSum.doubleValue(), true, true));

            //5.不含税租赁利息 单位：元
            renderMap.put(RenderParameterKeyHolder.EXCLUDING_INTEREST_TAX, Util.toYuan(Util.mithrasLongDecimalTwo(financialCosts.getExcludingInterestTax().longValue())));

            //6.税额 单位：元
            renderMap.put(RenderParameterKeyHolder.TAX_BALANCE, Util.toYuan(Util.mithrasLongDecimalTwo(financialCosts.getTax().longValue())));

            //7.不含税租金
            renderMap.put(RenderParameterKeyHolder.RENT_EXCLUDING_TAX, Util.toYuan(Util.mithrasLongDecimalTwo(financialCosts.getRentExcludingTax().longValue())));

            //8.承租人
            Wrapper<ContractTenantry> contractTenantryQuery = Wrappers.<ContractTenantry>lambdaQuery()
                    .eq(ContractTenantry::getContractId, contractBaseInfo.getId())
                    .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name());
            ContractTenantry contractTenantry = contractTenantryService.getOne(contractTenantryQuery);
            renderMap.put(RenderParameterKeyHolder.MAIN_LESSEE_NAME, contractTenantry.getLesseeName());

            //9.租金表
            renderMap.put(RenderParameterKeyHolder.RENT_ACTUAL_TABLE, renderRentActualTable(contractRentActualList));

            //10.合同编号
            renderMap.put(RenderParameterKeyHolder.CONTRACT_CODE, contractBaseInfo.getContractCode());

            //11.出租人签署年月日
            renderMap.put(RenderParameterKeyHolder.YEAR, "{{year}}");
            renderMap.put(RenderParameterKeyHolder.MONTH, "{{month}}");
            renderMap.put(RenderParameterKeyHolder.DAY, "{{day}}");

            // 渲染文档
            XWPFTemplate template = XWPFTemplate.compile(getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_实际租金表.docx")).render(renderMap);
            template.writeAndClose(outputStream);
            return "实际租金表" + GlobalConstants.OFFICE_WORD_SUFFIX;
        }
    }

    public String render2(OutputStream outputStream, ContractBaseInfo contractBaseInfo, MaterialsList materialsList) throws Exception {
        log.info("合同起租生成最终的实际租金表........");
        Map<String, Object> renderMap = new HashMap<>(8);
        renderMap.put(RenderParameterKeyHolder.YEAR, LocalDate.now().getYear());
        renderMap.put(RenderParameterKeyHolder.MONTH, LocalDate.now().getMonthValue());
        renderMap.put(RenderParameterKeyHolder.DAY, LocalDate.now().getDayOfMonth());
        // 渲染文档
        InputStream inputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream is = null;
        try {
            //获取已生成的实际租金表文档填充
            inputStream = ossClient.downLoad(join("/", materialsList.getOssFilename()));
            XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
            template.write(byteArrayOutputStream);
            //新文件填充后更新实际租金表文档
            is = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ossClient.upLoad(is, join("/", materialsList.getOssFilename()), false);
            template.writeAndClose(outputStream);
        } catch (Exception e) {
            throw new MithrasException("实际租金表流程结束后填充发生未知异常："+e);
        } finally {
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
            if (Objects.nonNull(byteArrayOutputStream)) {
                byteArrayOutputStream.close();
            }
            if (Objects.nonNull(is)) {
                is.close();
            }
        }
        return "实际租金表" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private TableRenderData renderRentActualTable(List<ContractRentActual> infoList) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("期数", "租金支付日", "租金", "其中：租金", null).center().create());
        tableDataList.add(Rows.of(null, null, null, "租赁成本", "租赁利息").center().create());
        for (ContractRentActual info : infoList) {
            tableDataList.add(Rows.of(
                    Optional.of(info.getCashFlowPhase().toString()).orElse(""),
                    Optional.ofNullable(info.getCashFlowDate()).map(LocalDateTimeUtil::formatNormal).orElse(""),
                    Optional.ofNullable(info.getRent()).map(this::toYuan2Digit).orElse(""),
                    Optional.ofNullable(info.getPrincipal()).map(this::toYuan2Digit).orElse(""),
                    Optional.ofNullable(info.getInterest()).map(this::toYuan2Digit).orElse("")
            ).center().create());
        }
        //合计
        tableDataList.add(Rows.of("合计", "",
                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getRent())).mapToLong(ContractRentActual::getRent).sum()),
                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getPrincipal())).mapToLong(ContractRentActual::getPrincipal).sum()),
                this.toYuan(infoList.stream().filter(m -> Objects.nonNull(m.getInterest())).mapToLong(ContractRentActual::getInterest).sum())
        ).center().create());

        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray)
                .mergeRule(MergeCellRule.builder()
                        .map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(1, 0))
                        .map(MergeCellRule.Grid.of(0, 1), MergeCellRule.Grid.of(1, 1))
                        .map(MergeCellRule.Grid.of(0, 2), MergeCellRule.Grid.of(1, 2))
                        .map(MergeCellRule.Grid.of(0, 3), MergeCellRule.Grid.of(0, 4))
                        .build())
                .width(15.44D, new double[]{2.19D, 2.75D, 3.75D, 3.5D, 3.25D})
                .create();
    }

    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        List<ContractTenantry> contractTenantryList = contractTenantryService.listByContractId(contractBaseInfo.getId());
        return contractTenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toSet());
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_实际租金表.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_实际租金表.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        //起租日期
        public static final String ACTUAL_LEASE_DATE = "actualLeaseDate";
        //租赁成本 单位：元（大写）
        public static final String CAPITAL_SUM_CN = "capitalSumCN";
        //租赁成本 单位：元
        public static final String CAPITAL_SUM = "capitalSum";
        //租赁利息 单位：元（大写）
        public static final String INTEREST_SUM_CN = "interestSumCN";
        //租赁利息 单位：元
        public static final String INTEREST_SUM = "interestSum";
        //不含税租赁利息 单位：元
        public static final String EXCLUDING_INTEREST_TAX = "excludingInterestTax";
        //税额 单位：元
        public static final String TAX_BALANCE = "taxBalance";
        //租金总额 单位：元（大写）
        public static final String RENT_SUM_CN = "rentSumCN";
        //租金总额 单位：元
        public static final String RENT_SUM = "rentSum";
        //不含税租金
        public static final String RENT_EXCLUDING_TAX = "rentExcludingTax";
        //承租人
        public static final String MAIN_LESSEE_NAME = "mainLesseeName";
        //租金表
        public static final String RENT_ACTUAL_TABLE = "rentActualTable";
        //合同编号
        public static final String CONTRACT_CODE = "contractCode";
        //签署日期-年
        public static final String YEAR = "year";
        //签署日期-月
        public static final String MONTH = "month";
        //签署日期-日
        public static final String DAY = "day";

    }
}
