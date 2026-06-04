package cn.zswltech.mithras.service.gendoc.render.contract.baoli.yzgt;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataLpr;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.basedata.service.BaseDataLprService;
import cn.zswltech.mithras.contract.core.application.ContractRentEstimateService;
import cn.zswltech.mithras.contract.core.application.ContractTenantryService;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.LocalDateTimeUtil.format;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.service.util.StringUtil.mysqlLimitOne;

/**
 * @author yibin
 */
@Component
public class YzGtBaoLiTransferConfirmRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "1-2应收账款转让申请暨确认书.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(CONFIRM_CODE, baseInfo.getContractCode().replace("保理", "申"));
        renderMap.put(CONTRACT_CODE, baseInfo.getContractCode());
        //多债权人
        List<ContractTenantry> tenantryList = getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).sorted(Comparator.comparingLong(ContractTenantry::getId)).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(tenantryList)) {
            ContractTenantry tenantry = tenantryList.get(0);
            renderMap.put(CREDITOR_NAME_1, tenantry.getLesseeName());
            //
            if (tenantryList.size() > 1) {
                tenantry = tenantryList.get(1);
                renderMap.put(CREDITOR_NAME_2, tenantry.getLesseeName());
            }
        }
        //债务人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "DEBTOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> {
                    renderMap.put(DEBTOR_NAME, tenantry.getLesseeName());
                    List<CorpAddressInfoLib> addressInfoLibList = businessDataRepository.getCorpAddressInfo(tenantry.getLesseeId());
                    String corpRegistryAddress = getCorpRegistryAddress(addressInfoLibList);
                    //注册地址
                    renderMap.put(DEBTOR_REGISTER_ADDRESS, corpRegistryAddress);
                    //联系人信息
                    CorpContactInfoLib mainContact = getMainContact(tenantry.getLesseeId());
                    if (null != mainContact) {
                        renderMap.put(DEBTOR_CONTACT_NAME, mainContact.getName());
                        renderMap.put(DEBTOR_CONTACT_MAIL, mainContact.getMail());
                        renderMap.put(DEBTOR_CONTACT_MOBILE, mainContact.getTelephone());
                    }
                });
        //合同金额
        ContractFactoringPrice factoringPrice = businessDataRepository.getContractFactoringPrice(baseInfo.getId());
        renderMap.put(CONTRACT_AMOUNT, toYuan(factoringPrice.getContractAmount()));
        //咨询费
        renderMap.put(CONSULT_FEE, toYuan(factoringPrice.getConsultingFee()));
        //融资比例
        renderMap.put(FINANCING_PERCENT, percentValue(factoringPrice.getFactoringFinancingProportion()));
        //保理融资期限（月）
        renderMap.put(FINANCING_MONTHS, factoringPrice.getFactoringCreditTerm());
        //保理费率
        if (RateType.FIXED.name().equals(factoringPrice.getRateType())) {
            renderMap.put(FEE_RATE, percentValue(factoringPrice.getFactoringRatePercent()));
            renderMap.put(LPR_PERCENT, percentValue(factoringPrice.getLprPercent()));
            renderMap.put(LPR_ADD_PERCENT, percentValue(factoringPrice.getLprAddPercent()));
        }
        //最新LPR
        BaseDataLpr lpr = getBean(BaseDataLprService.class).getOne(Wrappers.<BaseDataLpr>lambdaQuery().last(mysqlLimitOne()).orderByDesc(BaseDataLpr::getLprDate));
        if (null != lpr) {
            renderMap.put(LPR_YEAR, lpr.getLprDate().getYear());
            renderMap.put(LPR_MONTH, lpr.getLprDate().getMonthValue());
            renderMap.put(LPR_DAY, lpr.getLprDate().getDayOfMonth());
        }
        //保证金
        renderMap.put(EARNEST_MONEY, toYuan(factoringPrice.getEarnestMoney()));
        //总期数
        List<ContractRentEstimate> rentEstimateList = getBean(ContractRentEstimateService.class).listByContractId(baseInfo.getId(), null)
                .stream()
                .filter(e -> Objects.nonNull(e.getRent()))
                .sorted(Comparator.comparing(ContractRentEstimate::getCashFlowPhase))
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(rentEstimateList)) {
            renderMap.put(TOTAL_TERM, rentEstimateList.get(rentEstimateList.size() - 1).getCashFlowPhase());
        }
        //概算表
        renderMap.put(RENT_ESTIMATE_TABLE, rentEstimateTable(rentEstimateList));
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputstream).render(renderMap);
        template.writeAndClose(outputStream);
        return filename;
    }

    protected TableRenderData rentEstimateTable(List<ContractRentEstimate> rentEstimateList) {
        List<RowRenderData> tableDataList = new ArrayList<>();
        long rentTotal = 0L;
        long interestTotal = 0L;
        long principalTotal = 0L;
        tableDataList.add(Rows.of("序号", "支付日", "保理预付款本息", "保理预付款利息", "保理预付款本金").textBold().center().create());
        for (ContractRentEstimate actual : rentEstimateList) {
            Rows.RowBuilder builder = Rows.of(
                    String.valueOf(actual.getCashFlowPhase()),
                    format(actual.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN),
                    toYuan(actual.getRent()),
                    toYuan(actual.getInterest()),
                    toYuan(actual.getPrincipal())
            );
            tableDataList.add(builder.center().create());
            rentTotal += actual.getRent() == null ? 0L : actual.getRent();
            interestTotal += actual.getInterest() == null ? 0L : actual.getInterest();
            principalTotal += actual.getPrincipal() == null ? 0L : actual.getPrincipal();
        }
        //
        tableDataList.add(Rows.of("合计", "", toYuan(rentTotal), toYuan(interestTotal), toYuan(principalTotal)).create());
        // 单元格合并规则
        MergeCellRule.MergeCellRuleBuilder mergeCellRuleBuilder = MergeCellRule.builder();
        mergeCellRuleBuilder.map(MergeCellRule.Grid.of(rentEstimateList.size() + 1, 0), MergeCellRule.Grid.of(rentEstimateList.size() + 1, 1));
        //
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).mergeRule(mergeCellRuleBuilder.build()).center().create();
    }

    public static final String CONFIRM_CODE = "confirmCode";

    /**
     * 合同编号
     */
    public static final String CONTRACT_CODE = "contractCode";

    /**
     * 债务人
     */
    public static final String DEBTOR_NAME = "debtorName";

    /**
     * 注册地址
     */
    public static final String DEBTOR_REGISTER_ADDRESS = "debtorRegisterAddress";

    /**
     * 债权人
     */
    public static final String CREDITOR_NAME_1 = "creditorName1";
    public static final String CREDITOR_NAME_2 = "creditorName2";
    /**
     * 联系人姓名
     */
    public static final String RENT_ESTIMATE_TABLE = "rentEstimateTable";

    /**
     * 联系人姓名
     */
    public static final String DEBTOR_CONTACT_NAME = "debtorContactName";

    /**
     * 联系人手机号
     */
    public static final String DEBTOR_CONTACT_MOBILE = "debtorContactMobile";

    /**
     * 联系人邮箱
     */
    public static final String DEBTOR_CONTACT_MAIL = "debtorContactMail";

    /**
     * 合同金额
     */
    public static final String CONTRACT_AMOUNT = "contractAmount";

    /**
     * 保理融资期限
     */
    public static final String FINANCING_MONTHS = "financingMonths";

    /**
     * 融资比例
     */
    public static final String FINANCING_PERCENT = "financingPercent";

    /**
     * 保理费率。固定利率时
     */
    public static final String FEE_RATE = "feeRate";
    public static final String LPR_PERCENT = "lprPercent";
    public static final String LPR_ADD_PERCENT = "lprAddPercent";

    /**
     * lpr年
     */
    public static final String LPR_YEAR = "lprYear";

    /**
     * lpr月
     */
    public static final String LPR_MONTH = "lprMonth";
    /**
     * lpr日
     */
    public static final String LPR_DAY = "lprDay";
    /**
     * 总期数
     */
    public static final String TOTAL_TERM = "totalTerm";

    /**
     * 手续费
     */
    public static final String CONSULT_FEE = "consultFee";

    /**
     * 保证金
     */
    public static final String EARNEST_MONEY = "earnestMoney";


    @Override
    protected Set<Long> signClientIds(ContractBaseInfo contractBaseInfo) {
        return null;
    }

    @Override
    protected boolean needSignByMyself() {
        return false;
    }

    @Override
    protected boolean customShowFile(ContractBaseInfo contractBaseInfo) {
        String filename = "1-2应收账款转让申请暨确认书.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "1-2应收账款转让申请暨确认书.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
