package cn.zswltech.mithras.application.orchestration.document.gendoc.render.contract.baoli.wz;

import cn.hutool.core.date.DatePattern;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.persistence.model.FileTemplate;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractFactoringPrice;
import cn.zswltech.mithras.contract.model.contract.ContractRentEstimate;
import cn.zswltech.mithras.contract.core.ContractRentEstimateService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.LocalDateTimeUtil.format;
import static cn.hutool.core.util.NumberUtil.div;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.zswltech.mithras.foundation.constant.GlobalConstants.MONEY_MULTIPLE;
import static java.math.RoundingMode.HALF_UP;

/**
 * @author yibin
 */
@Component
public class WzBaoLiTransferConfirmRender extends AbstractContractRender<ContractBaseInfo> {
    @Override
    public String render(OutputStream outputStream, ContractBaseInfo baseInfo) throws Exception {
        String filename = "2-应收账款转让申请暨确认书.docx";
        InputStream inputstream = getBean(FileTemplateService.class).getTemplate("合同-保理合同", filename);
        //填充数据准备
        Map<String, Object> renderMap = new HashMap<>(16);
        renderMap.put(CONFIRM_CODE, baseInfo.getContractCode().replace("保理", "申"));
        renderMap.put(CONTRACT_CODE, baseInfo.getContractCode());
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "CREDITOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> renderMap.put(CREDITOR_NAME, tenantry.getLesseeName()));
        //债务人
        getBean(ContractTenantryService.class).listByContractId(baseInfo.getId())
                .stream().filter(e -> "DEBTOR".equals(e.getLesseeType())).findFirst()
                .ifPresent(tenantry -> renderMap.put(DEBTOR_NAME, tenantry.getLesseeName()));
        ContractFactoringPrice factoringPrice = businessDataRepository.getContractFactoringPrice(baseInfo.getId());
        renderMap.put(RATE_PERCENT, div(String.valueOf(factoringPrice.getFactoringRatePercent()), MONEY_MULTIPLE).setScale(2, HALF_UP).stripTrailingZeros().toPlainString());
        renderMap.put(RENT_ESTIMATE_TABLE, rentEstimateTable(baseInfo));
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputstream).render(renderMap);
        template.writeAndClose(outputStream);
        return filename;
    }

    protected TableRenderData rentEstimateTable(ContractBaseInfo baseInfo) {
        List<ContractRentEstimate> rentEstimateList = getBean(ContractRentEstimateService.class).listByContractId(baseInfo.getId(), null)
                .stream()
                .filter(e -> Objects.nonNull(e.getRent()))
                .sorted(Comparator.comparing(ContractRentEstimate::getCashFlowPhase))
                .collect(Collectors.toList());
        List<RowRenderData> tableDataList = new ArrayList<>();
        tableDataList.add(Rows.of("序号", "支付日", "支付金额", "序号", "支付日", "支付金额").textBold().center().create());
        Rows.RowBuilder builder = Rows.of();
        int i = 0;
        for (; i < rentEstimateList.size(); i++) {
            ContractRentEstimate actual = rentEstimateList.get(i);
            builder.addCell(Cells.of(actual.getCashFlowPhase().toString()).create());
            builder.addCell(Cells.of(format(actual.getCashFlowDate(), DatePattern.NORM_DATE_PATTERN)).create());
            Long rent = actual.getRent();
            if (null != rent) {
                builder.addCell(Cells.of(toYuan(actual.getRent())).create());
            }
            //奇数
            if (i > 0 && (i & 1) == 1) {
                tableDataList.add(builder.create());
                builder = Rows.of();
            }
        }
        //奇数个
        if ((rentEstimateList.size() & 1) == 1) {
            builder.addCell(Cells.of("").create());
            builder.addCell(Cells.of("").create());
            builder.addCell(Cells.of("").create());
            tableDataList.add(builder.create());
        }
        RowRenderData[] tableDataArray = new RowRenderData[tableDataList.size()];
        tableDataList.toArray(tableDataArray);
        return Tables.of(tableDataArray).center().create();

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
     * 保理费率
     */
    public static final String RATE_PERCENT = "factoringRatePercent";
    /**
     * 债权人
     */
    public static final String CREDITOR_NAME = "creditorName";
    /**
     * 联系人姓名
     */
    public static final String RENT_ESTIMATE_TABLE = "rentEstimateTable";


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
        String filename = "2-应收账款转让申请暨确认书.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        String filename = "2-应收账款转让申请暨确认书.docx";
        FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保理合同", filename);
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
