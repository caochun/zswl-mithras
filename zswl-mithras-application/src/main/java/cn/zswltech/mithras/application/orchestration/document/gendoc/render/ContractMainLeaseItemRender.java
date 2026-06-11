package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractSubTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.mapper.model.FileTemplate;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeaseItem;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemCommonService;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description 租赁合同-租赁物清单
 */
@Component
public class ContractMainLeaseItemRender extends AbstractContractRender<ContractBaseInfo> {
    @Resource
    private LeaseItemCommonService leaseItemCommonService;

    @Override
    public String render(OutputStream outputStream, ContractBaseInfo contractBaseInfo) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        // 获取数据
        // 承租人列表
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        // 主承租人
        ContractTenantry mainTenanry = contractTenantryMap.values()
                .stream()
                .filter(t -> LesseeTypeEnum.MAIN_LESSSEE.name().equals(t.getLesseeType()))
                .findFirst()
                .orElse(null);
        // 租赁物清单
        List<ContractLeaseItem> contractLeaseItemList = businessDataRepository.listContractLeaseItem(contractBaseInfo.getId());

        renderMap.put("contractCode", contractBaseInfo.getContractCode());
        renderMap.put("mainLesseeName", Optional.ofNullable(mainTenanry)
                .map(ContractTenantry::getLesseeName)
                .orElse(""));
        if (StrUtil.isNotBlank(contractBaseInfo.getItemListHeader())) {
            List<String> headerList = JSONUtil.toList(contractBaseInfo.getItemListHeader(), String.class);
            renderMap.put("leaseItemTable", renderLeaseItemTable(leaseItemCommonService.preHandleHeader(headerList), contractLeaseItemList));
        }

        InputStream inputStream;
        if (contractTenantryMap.size() == 1) {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_租赁物清单_单一承租人.docx");
        } else {
            inputStream = getBean(FileTemplateService.class).getTemplate("合同-租赁合同", "合同_租赁合同_附属_租赁物清单_共同承租人.docx");
            renderMap.put("jointLesseeName", contractTenantryMap.values().stream().filter(ct -> LesseeTypeEnum.JOINT_LESSEE.name().equals(ct.getLesseeType())).map(ContractTenantry::getLesseeName).collect(Collectors.joining("、")));
            renderMap.put("allLesseeName", contractTenantryMap.values().stream().map(ContractTenantry::getLesseeName).collect(Collectors.joining("、")));
        }
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "1-" + ContractSubTypeEnum.LEASE_ITEM.getSort() + "." + ContractSubTypeEnum.LEASE_ITEM.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private TableRenderData renderLeaseItemTable(List<String> headerList, List<ContractLeaseItem> infoList) {
        // 确定表头
        Set<String> show = new HashSet<>();
        show.add("序号");
        for (ContractLeaseItem contractLeaseItem : infoList) {
            if (StrUtil.isBlank(contractLeaseItem.getRowData())) {
                continue;
            }
            Map<String, Object> cellMap = JSONUtil.toBean(contractLeaseItem.getRowData().replace("\n", "\\n"), Map.class);
            for (Map.Entry<String, Object> entry : cellMap.entrySet()) {
                String headerName = entry.getKey();
                Object cellValue = entry.getValue();
                if (Objects.isNull(cellValue)) {
                    continue;
                }
                if (cellValue instanceof String) {
                    String s = (String) cellValue;
                    if (StrUtil.isBlank(s)) {
                        continue;
                    }
                }
                show.add(headerName);
            }
        }
        // 去掉无需展示的表头
        headerList.removeIf(e -> !show.contains(e));
        List<RowRenderData> tableDataList = new ArrayList<>();
        // 首行
        String[] firstRow = new String[headerList.size()];
        firstRow[0] = "租赁物明细";
        tableDataList.add(Rows.of(firstRow).center().create());
        // 表头行
        tableDataList.add(Rows.of(headerList.toArray(new String[0])).center().create());
        // 数据行（账面原值（元）、账面净值（元）、评估原值（元）和评估净值（元）作为表头的数据进行合计）
        double originalValueTotal = 0L;
        double originalNetValueTotal = 0L;
        double assessedValueTotal = 0L;
        double assessedNetValueTotal = 0L;
        for (int i = 0; i < infoList.size(); i++) {
            ContractLeaseItem contractLeaseItem = infoList.get(i);
            if (StrUtil.isBlank(contractLeaseItem.getRowData())) {
                continue;
            }
            Map<String, Object> cellMap = JSONUtil.toBean(contractLeaseItem.getRowData().replace("\n", "\\n"), Map.class);
            cellMap.put("序号", i + 1);
            List<String> cellList = new ArrayList<>(headerList.size());
            for (String headerName : headerList) {
                Object cellValue = cellMap.get(headerName);
                if (Objects.nonNull(cellValue)) {
                    String s = cellValue.toString();
                    if (Objects.equals(headerName, "账面原值（元）") && NumberUtil.isNumber(s)) {
                        originalValueTotal = originalValueTotal + Double.parseDouble(s);
                        cellList.add(NumberUtil.decimalFormat(",##0.00##", new BigDecimal(s)));
                    } else if (Objects.equals(headerName, "账面净值（元）") && NumberUtil.isNumber(s)) {
                        originalNetValueTotal = originalNetValueTotal + Double.parseDouble(s);
                        cellList.add(NumberUtil.decimalFormat(",##0.00##", new BigDecimal(s)));
                    } else if (Objects.equals(headerName, "评估原值（元）") && NumberUtil.isNumber(s)) {
                        assessedValueTotal = assessedValueTotal + Double.parseDouble(s);
                        cellList.add(NumberUtil.decimalFormat(",##0.00##", new BigDecimal(s)));
                    } else if (Objects.equals(headerName, "评估净值（元）") && NumberUtil.isNumber(s)) {
                        assessedNetValueTotal = assessedNetValueTotal + Double.parseDouble(s);
                        cellList.add(NumberUtil.decimalFormat(",##0.00##", new BigDecimal(s)));
                    } else {
                        cellList.add(s);
                    }
                } else {
                    cellList.add("");
                }
            }
            tableDataList.add(Rows.of(cellList.toArray(new String[0])).center().create());
        }
        // 合计行
        String[] lastRow = new String[headerList.size()];
        lastRow[0] = "合计";
        for (int i = 1; i < headerList.size(); i++) {
            String headerName = headerList.get(i);
            if (Objects.equals(headerName, "账面原值（元）")) {
                lastRow[i] = NumberUtil.decimalFormat(",##0.00##", originalValueTotal);
            }
            if (Objects.equals(headerName, "账面净值（元）")) {
                lastRow[i] = NumberUtil.decimalFormat(",##0.00##", originalNetValueTotal);
            }
            if (Objects.equals(headerName, "评估原值（元）")) {
                lastRow[i] = NumberUtil.decimalFormat(",##0.00##", assessedValueTotal);
            }
            if (Objects.equals(headerName, "评估净值（元）")) {
                lastRow[i] = NumberUtil.decimalFormat(",##0.00##", assessedNetValueTotal);
            }
        }
        tableDataList.add(Rows.of(lastRow).center().create());
        // 每列的宽度
        int headerCount = headerList.size();
        double colWidth = new BigDecimal(24).divide(new BigDecimal(headerCount), 2, RoundingMode.HALF_UP).doubleValue();
        double[] colWidthArray = new double[headerCount];
        for (int i = 0; i < headerCount; i++) {
            colWidthArray[i] = colWidth;
        }
        // 单元格合并规则
        MergeCellRule.MergeCellRuleBuilder mergeCellRuleBuilder = MergeCellRule.builder();
        if (headerCount > 1) {
            mergeCellRuleBuilder.map(MergeCellRule.Grid.of(0, 0), MergeCellRule.Grid.of(0, headerCount - 1));
        }
        return Tables.of(tableDataList.toArray(new RowRenderData[0]))
                .mergeRule(mergeCellRuleBuilder.build())
                .width(24.0D, colWidthArray)
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
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    private FileTemplate getFileTemplate(ContractBaseInfo contractBaseInfo) {
        // 承租人列表
        Map<Long, ContractTenantry> contractTenantryMap = businessDataRepository.getContractTenantryMap(contractBaseInfo.getId());
        FileTemplate fileTemplate;
        if (contractTenantryMap.size() == 1) {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_租赁物清单_单一承租人.docx");
        } else {
            fileTemplate = getBean(FileTemplateService.class).getTemplateRecord("合同-租赁合同", "合同_租赁合同_附属_租赁物清单_共同承租人.docx");
        }
        return fileTemplate;
    }

    @Override
    protected String customTemplateKey(ContractBaseInfo contractBaseInfo) {
        FileTemplate fileTemplate = getFileTemplate(contractBaseInfo);
        return Optional.of(fileTemplate).map(FileTemplate::getFileTemplateKey).orElse(null);
    }
}
