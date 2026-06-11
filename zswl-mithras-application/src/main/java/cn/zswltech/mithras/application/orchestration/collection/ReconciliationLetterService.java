package cn.zswltech.mithras.application.orchestration.collection;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.style.StyleUtil;
import cn.zswltech.gruul.biz.service.SystemConfigService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.mithras.dto.collection.CollectionReconciliationLetterREQ;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.margin.mapper.model.MarginRecordInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.collection.application.bo.ReconciliationLetterBO;
import cn.zswltech.mithras.collection.application.facade.CollectionReconciliationLetterApplicationService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ReconciliationLetterService
 * @Description 对账函
 * @Author jackerhe
 * @Date 2023/3/22 5:40 下午
 * @Version 1.0
 **/

@Service
@Slf4j
public class ReconciliationLetterService implements CollectionReconciliationLetterApplicationService {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SystemConfigService systemConfigService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public void exportExcel(CollectionReconciliationLetterREQ req, OutputStream outputStream) {

        //Workbook workbook = WorkbookUtil.createBook(Boolean.TRUE);
        ExcelWriter excelWriter = ExcelUtil.getWriter(true);
        StyleSet styleSet = excelWriter.getStyleSet();
        //自动换行
        styleSet.setWrapText();
        styleSet.setBorder(BorderStyle.NONE, IndexedColors.BLACK);
        Map<Long, ReconciliationLetterBO> longReconciliationLetterBOMap = statisticsClientsLetter(req.getDate());
        if(ObjectUtil.isEmpty(longReconciliationLetterBOMap)){
            return;
        }
        Map<Long, String> clientId2NameMap = id2NameService.clientId2Name(longReconciliationLetterBOMap.values().stream().map(ReconciliationLetterBO::getClientId).collect(Collectors.toList()));

        String dateTime = String.format("%s年%s月%s日",req.getDate().getYear(), req.getDate().getMonthValue(), req.getDate().getDayOfMonth());
        excelWriter.renameSheet(String.format("%s年%s月函证公司汇总",req.getDate().getYear(), req.getDate().getMonthValue()));
        List<String> names = longReconciliationLetterBOMap.values().stream().map(ReconciliationLetterBO::getClientName).collect(Collectors.toList());
        excelWriter.getSheet().setDefaultColumnWidth(30);
        excelWriter.writeCellValue(0, 0, "承租人名称");
        for(int i = 0; i < names.size(); i++){
            excelWriter.writeCellValue(0, i+1, names.get(i));
        }

        // 从配置表去数据
        Object operator = Optional.ofNullable(systemConfigService.getConfigValue("reconciliationLetterOperator")).map(Response::getData).orElse("何佳薇");
        Object telephone = Optional.ofNullable(systemConfigService.getConfigValue("reconciliationLetterTelephone")).map(Response::getData).orElse("0571-87131150");
        longReconciliationLetterBOMap.values().forEach(bo -> {
            excelWriter.setSheet(bo.getClientName());
            excelWriter.getSheet().setDefaultColumnWidth(18);
            CellStyle titleStyle = StyleUtil.createDefaultCellStyle(excelWriter.getWorkbook());
            CellStyle mergeStyle = StyleUtil.createDefaultCellStyle(excelWriter.getWorkbook());
            CellStyle borderStyle = StyleUtil.createDefaultCellStyle(excelWriter.getWorkbook());
            setBorderStyle(borderStyle, BorderStyle.THIN);
            setBorderStyle(titleStyle, BorderStyle.NONE);
            setBorderStyle(mergeStyle, BorderStyle.NONE);
            Font titleFont = excelWriter.getWorkbook().createFont();
            titleFont.setFontName("宋体");
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            excelWriter.merge(0, 0, 0, 5, "往来账项对账函", titleStyle);
            excelWriter.writeCellValue(0, 2, "致：");
            excelWriter.merge(2, 2, 1, 2, clientId2NameMap.get(bo.getClientId()), mergeStyle);
            CellStyle longStyle = StyleUtil.createDefaultCellStyle(excelWriter.getWorkbook());
            longStyle.setWrapText(true);
            longStyle.setAlignment(HorizontalAlignment.LEFT);
            setBorderStyle(longStyle, BorderStyle.NONE);
            excelWriter.merge(3, 3, 0, 5, String.format("  为了保持我公司与贵公司的往来账务清晰，我公司现与贵公司对截止到%s的往来账务进行核对。" +
                            "下列数据为本公司账务记录余额，如与贵公司金额相符，请在本函下方“信息证明无误”处签章证明；如有不符，请在“信息不符”处列明不符项目及金额。如存在与本公司有关的未列入本函的其他项目，请在“信息不符”处列出这些" +
                            "项目的金额及详细资料。回函请寄至：",
                    dateTime), longStyle);
            excelWriter.getSheet().getRow(3).setHeight((short) 1500);
            excelWriter.writeCellValue(0, 4, "地址：");
            excelWriter.merge(4, 4, 1, 3, "浙江省杭州市萧山区盈丰街道天人大厦12楼", mergeStyle);
            excelWriter.writeCellValue(0, 5, "联系人及电话：");
            String userAndPhone = String.format("%s %s", ObjectUtil.isEmpty(req.getUserName()) ? operator : req.getUserName(),
                    ObjectUtil.isEmpty(req.getUserPhone()) ? telephone : req.getUserPhone());
            excelWriter.merge(5, 5, 1, 2, userAndPhone, mergeStyle);
            excelWriter.writeCellValue(0, 7, "截止日期");
            excelWriter.merge(7, 7,1,2, "交易内容或款项性质", mergeStyle);
            excelWriter.writeCellValue(3, 7, "贵公司欠");
            excelWriter.writeCellValue(4, 7, "欠贵公司");
            excelWriter.writeCellValue(5, 7, "本公司科目");
            setBorder(excelWriter, 0, 7, true, true, true, true);
            setBorder(excelWriter, 1, 7, true, true, true, true);
            setBorder(excelWriter, 2, 7, true, true, true, true);
            setBorder(excelWriter, 3, 7, true, true, true, true);
            setBorder(excelWriter, 4, 7, true, true, true, true);
            setBorder(excelWriter, 5, 7, true, true, true, true);
            //动态填充
            int i = 8;
            BigDecimal zero = new BigDecimal(0);
            if(ObjectUtil.isNotEmpty(bo.getRentAndNominalPrice()) && bo.getRentAndNominalPrice().compareTo(zero) > 0){
                addRow(i, excelWriter, dateTime, "剩余租金及留购价款", bo.getRentAndNominalPrice().setScale(2, RoundingMode.HALF_UP).toString(), null, "长期应收款", borderStyle);
                i++;
            }
            if(ObjectUtil.isNotEmpty(bo.getBlPrincipalAndInterest()) && bo.getBlPrincipalAndInterest().compareTo(zero) > 0){
                addRow(i, excelWriter, dateTime, "剩余本息",bo.getBlPrincipalAndInterest().setScale(2, RoundingMode.HALF_UP).toString(), null, "长期应收款", borderStyle);
                i++;
            }
            if(ObjectUtil.isNotEmpty(bo.getMargin()) && bo.getMargin().compareTo(zero) > 0){
                addRow(i, excelWriter, dateTime, "保证金", null, bo.getMargin().setScale(2, RoundingMode.HALF_UP).toString(), "其他应付款", borderStyle);
                i++;
            }
            i = i + 2;
            excelWriter.merge(i, i,0, 4,"本函仅为复核账目之用，并非催款结算。若款项在上述日期之后已经付清，仍请及时函复为盼。", mergeStyle);
            i++;
            excelWriter.writeCellValue(4,  ++i,"（公司盖章）");
            excelWriter.writeCellValue(4, ++i, dateTime);
            excelWriter.writeCellValue(4, ++i, "经办人：" + operator);
            setBorder(excelWriter, 0, i, false, false, false, false);
            excelWriter.merge(++i, i,0, 4,"（如本次询函为多页次，请加盖骑缝章）", mergeStyle);
            setBorder(excelWriter, 0, i, true, false, false, false);
            CellStyle redStyle = StyleUtil.createDefaultCellStyle(excelWriter.getWorkbook());
            redStyle.setAlignment(HorizontalAlignment.LEFT);
            Font redFont = excelWriter.getWorkbook().createFont();
            redFont.setColor(Font.COLOR_RED);
            redStyle.setFont(redFont);
            redStyle.setAlignment(HorizontalAlignment.LEFT);
            excelWriter.setStyle(redStyle, 0, i);
            //添加结尾
            excelWriter.writeCellValue(0, ++i, "信息证明无误");
            excelWriter.writeCellValue(3, i, "信息不符及需加证明事项(详细附后)");
            //边框
            setBorder(excelWriter, 0, i, true, false, true, false);
            setBorder(excelWriter, 1, i, false, false, true, false);
            setBorder(excelWriter, 2, i, false, true, true, false);
            setBorder(excelWriter, 3, i, true, false, true, false);
            setBorder(excelWriter, 4, i, false, false, true, false);
            setBorder(excelWriter, 5, i, false, true, true, false);
            i++;
            setBorder(excelWriter, 2, i, false, true, false, false);
            setBorder(excelWriter, 3, i, true, false, false, false);
            setBorder(excelWriter, 5, i, false, true, false, false);
            i++;
            excelWriter.writeCellValue(1, i, "（公司盖章）");
            excelWriter.writeCellValue(4, i, "（公司盖章）");
            setBorder(excelWriter, 2, i, false, true, false, false);
            setBorder(excelWriter, 3, i, true, false, false, false);
            setBorder(excelWriter, 5, i, false, true, false, false);
            excelWriter.writeCellValue(1, ++i, "年      月      日");
            excelWriter.writeCellValue(4, i, "年      月      日");
            setBorder(excelWriter, 2, i, false, true, false, false);
            setBorder(excelWriter, 3, i, true, false, false, false);
            setBorder(excelWriter, 5, i, false, true, false, false);
            excelWriter.writeCellValue(1, ++i, "经办人：");
            excelWriter.writeCellValue(4, i, "经办人：");
            setBorder(excelWriter, 2, i, false, true, false, false);
            setBorder(excelWriter, 3, i, true, false, false, false);
            setBorder(excelWriter, 5, i, false, true, false, false);
            i++;
            setBorder(excelWriter, 0, i, true, false, false, true);
            setBorder(excelWriter, 1, i, false, false, false, true);
            setBorder(excelWriter, 2, i, false, true, false, true);
            setBorder(excelWriter, 3, i, true, false, false, true);
            setBorder(excelWriter, 4, i, false, false, false, true);
            setBorder(excelWriter, 5, i, false, true, false, true);
        });
        // 写入到输出流
        excelWriter.flush(outputStream, true);
    }

    private void addRow(int i, ExcelWriter excelWriter, String vo, String v1, String v2, String v3, String v4, CellStyle borderStyle){
        excelWriter.writeCellValue( 0, i, vo);
        excelWriter.merge(i, i,1,2, v1, borderStyle);
        excelWriter.writeCellValue( 3, i, v2);
        excelWriter.writeCellValue( 4, i, v3);
        excelWriter.writeCellValue( 5, i, v4);
        excelWriter.setColumnStyleIfHasData(0, i, borderStyle);
        excelWriter.setColumnStyleIfHasData(1, i, borderStyle);
        excelWriter.setColumnStyleIfHasData(2, i, borderStyle);
        excelWriter.setColumnStyleIfHasData(3, i, borderStyle);
        excelWriter.setColumnStyleIfHasData(4, i, borderStyle);
        excelWriter.setColumnStyleIfHasData(5, i, borderStyle);

    }

    private void setBorder(ExcelWriter excelWriter, int x, int y, Boolean left, Boolean right, Boolean top, Boolean bottom){
        CellStyle cellStyle = excelWriter.createCellStyle(x, y);
        if(left){
            cellStyle.setBorderLeft(BorderStyle.THIN);
        }else {
            cellStyle.setBorderLeft(BorderStyle.NONE);
        }
        if(right){
            cellStyle.setBorderRight(BorderStyle.THIN);
        }else {
            cellStyle.setBorderRight(BorderStyle.NONE);
        }
        if(top){
            cellStyle.setBorderTop(BorderStyle.THIN);
        }else {
            cellStyle.setBorderTop(BorderStyle.NONE);
        }
        if(bottom){
            cellStyle.setBorderBottom(BorderStyle.THIN);
        }else {
            cellStyle.setBorderBottom(BorderStyle.NONE);
        }
    }

    private void setBorderStyle(CellStyle cellStyle, BorderStyle borderStyle){
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderBottom(borderStyle);
    }

    /**
     * 统计各客户项目信息
     **/
    @Override
    public Map<Long, ReconciliationLetterBO> statisticsClientsLetter(LocalDate localDateTime){
        Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .eq(Client::getClientType, ClientType.CORPORATION.name())).stream().collect(Collectors.toMap(Client::getId, e -> e));
        //<clientId, bo>
        Map<Long, ReconciliationLetterBO> reconciliationLetterMap = new HashMap<>();
        //获取用户下所以项目
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.listByClients(new ArrayList<>(clientMap.keySet()));
        //<projReviewId, clientId>
        Map<Long, Long> projReviewMap = projReviewBaseInfos.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId,
                ProjReviewBaseInfo::getClientId));
        //<contractId, projReviewId>
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getProjReviewId, projReviewMap.keySet())
        .ne(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name())
        .le(ObjectUtil.isNotEmpty(localDateTime), ContractBaseInfo::getActualLeaseDate, localDateTime));
        List<Long> collectionIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        //<contractId, contract>
        Map<Long, ContractBaseInfo> contractBaseMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e));
        //获取所有用户数据收付款数据
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractBaseMap.keySet()));
        //查询合同主承租人名称
        Map<Long, Long> contractId2ClientId = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .in(ObjectUtil.isNotEmpty(contractBaseMap.keySet()), ContractTenantry::getContractId, contractBaseMap.keySet()))
                .stream().collect(Collectors.toMap(ContractTenantry::getContractId, ContractTenantry::getLesseeId, (a, b) -> a));
        //查询合同下名义价款剩余金额
        Map<Long, Long> contractNominalPriceRemain = contractBaseInfoService.getContractNominalPriceRemain(collectionIds, localDateTime);
        //获取合同下保证金退款金额
        Map<Long, List<MarginRecordInfo>> contractMarginRecord = marginBaseInfoService.getContractMarginRecord(contractBaseMap.keySet(), ListUtil.toList(RecordTypeEnum.REFUND.name(), RecordTypeEnum.REFUND_MARGIN.name(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.name()), localDateTime);
        collectionBaseInfos.forEach(collectionBaseInfo -> {
            //付款拿合同
            ContractBaseInfo contractBaseInfo = contractBaseMap.get(collectionBaseInfo.getContractId());
            if(ObjectUtil.isNotEmpty(contractBaseInfo)){
                //合同拿项目-客户
                Client client = clientMap.get(contractId2ClientId.get(contractBaseInfo.getId()));
                if(ObjectUtil.isNotEmpty(client)){
                    ReconciliationLetterBO reconciliationLetterBO = reconciliationLetterMap.get(client.getId());
                    if(ObjectUtil.isEmpty(reconciliationLetterBO)){
                        reconciliationLetterBO = new ReconciliationLetterBO();
                        reconciliationLetterBO.setClientId(client.getId());
                        reconciliationLetterBO.setClientName(client.getClientName());
                        reconciliationLetterMap.put(client.getId(), reconciliationLetterBO);
                    }
                    //根据项目类型判断金额
                    calculationReconciliationLetterBO(reconciliationLetterBO, collectionBaseInfo, contractBaseInfo, localDateTime);
                }
            }
        });
        //剩余本金需大于0
        Map<Long, ReconciliationLetterBO> rsp = new HashMap<>();

        reconciliationLetterMap.forEach((key, value) -> {
            if(value.getLastPrincipal().compareTo(BigDecimal.ZERO) > 0){
                rsp.put(key, value);
            }
        });
        //加上名义价款
        contractNominalPriceRemain.forEach((contractId, nominalPrice) -> {
            calculationNominalPrice(rsp.get(contractId2ClientId.get(contractId)), contractBaseMap.get(contractId), contractNominalPriceRemain);
        });
        //减去已核销保证金
        contractMarginRecord.forEach((contractId, margins) -> {
            calculationMargin(rsp.get(contractId2ClientId.get(contractId)), contractBaseMap.get(contractId), contractMarginRecord);
        });
        return rsp;
    }

    /**
     * 宝理：保理本金、保理利息、保理保证金、
     * 直租：融资租赁租金及留购货款（租金+名义价款）、融资租赁保证金（保证金余额）
     * 回租：融资性售后回租租金及留购货款（租金+名义价款）、融资性售后回租保证金（保证金余额）
     *
     **/
    private void calculationReconciliationLetterBO(ReconciliationLetterBO reconciliationLetterBO, CollectionBaseInfo collectionBaseInfo,
                                                   ContractBaseInfo contractBaseInfo, LocalDate localDateTime){
        ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
        if(ObjectUtil.isEmpty(projectBizType)){
            log.info("ReconciliationLetterService calculationReconciliationLetterBO contractBaseInfo : {}", contractBaseInfo);
            return;
        }
        if (isBeforeDate(collectionBaseInfo, localDateTime)) {
            reconciliationLetterBO.setLastPrincipal(calculate(reconciliationLetterBO.getLastPrincipal(), collectionBaseInfo.getPrincipal(),
                    collectionBaseInfo.getCollectionPrincipal()));
        } else {
            reconciliationLetterBO.setLastPrincipal(calculate(reconciliationLetterBO.getLastPrincipal(), collectionBaseInfo.getPrincipal(),
                    collectionBaseInfo.getCollectionPrincipal(), localDateTime));
        }
        switch (projectBizType){
            case BL:
                if (CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem())) {
                    //保理本息
                    if (isBeforeDate(collectionBaseInfo, localDateTime)) {
                        reconciliationLetterBO.setBlPrincipalAndInterest(calculate(reconciliationLetterBO.getBlPrincipalAndInterest(),
                                collectionBaseInfo.getPlanCollectionAmount(),
                                collectionBaseInfo.getCollectionAmount()));
                    } else {
                        reconciliationLetterBO.setBlPrincipalAndInterest(calculate(reconciliationLetterBO.getBlPrincipalAndInterest(),
                                collectionBaseInfo.getPlanCollectionAmount(),
                                collectionBaseInfo.getCollectionAmount(), localDateTime));
                    }
                } else if (CashFlowItemEnum.EARNEST_MONEY.name().equals(collectionBaseInfo.getCashFlowItem())) {
                    if (isBeforeDate(collectionBaseInfo, localDateTime)) {
                        reconciliationLetterBO.setMargin(calculate(reconciliationLetterBO.getMargin(), collectionBaseInfo.getCollectionAmount(),
                                null));
                    } else {
                        /*reconciliationLetterBO.setMargin(calculate(reconciliationLetterBO.getMargin(), collectionBaseInfo.getCollectionAmount(),
                                null, localDateTime));*/
                    }
                }
                break;
            default:
                if (CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem())) {
                    if (isBeforeDate(collectionBaseInfo, localDateTime)) {
                        reconciliationLetterBO.setRentAndNominalPrice(calculate(reconciliationLetterBO.getRentAndNominalPrice(),
                                collectionBaseInfo.getPlanCollectionAmount(), collectionBaseInfo.getCollectionAmount()));
                    } else {
                        reconciliationLetterBO.setRentAndNominalPrice(calculate(reconciliationLetterBO.getRentAndNominalPrice(),
                                collectionBaseInfo.getPlanCollectionAmount(), collectionBaseInfo.getCollectionAmount(), localDateTime));
                    }
                } else if (CashFlowItemEnum.EARNEST_MONEY.name().equals(collectionBaseInfo.getCashFlowItem())) {
                    if (isBeforeDate(collectionBaseInfo, localDateTime)) {
                        reconciliationLetterBO.setMargin(calculate(reconciliationLetterBO.getMargin(), collectionBaseInfo.getCollectionAmount(),
                                null));
                    } else {
                       /* reconciliationLetterBO.setMargin(calculate(reconciliationLetterBO.getMargin(), collectionBaseInfo.getCollectionAmount(),
                                null, localDateTime));*/
                    }
                }
        }
    }

    private void calculationNominalPrice(ReconciliationLetterBO reconciliationLetterBO, ContractBaseInfo contractBaseInfo, Map<Long, Long> contractNominalPriceRemain) {
        ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
        if (ObjectUtil.isEmpty(projectBizType) || ObjectUtil.isEmpty(contractBaseInfo) || ObjectUtil.isEmpty(reconciliationLetterBO)) {
            log.info("ReconciliationLetterService calculationReconciliationLetterBO contractBaseInfo : {}", contractBaseInfo);
            return;
        }

        switch (projectBizType) {
            case BL:
                //保理本息
                reconciliationLetterBO.setBlPrincipalAndInterest(calculate(reconciliationLetterBO.getBlPrincipalAndInterest(),
                        contractNominalPriceRemain.get(contractBaseInfo.getId()),
                        0L));
                break;
            default:
                reconciliationLetterBO.setRentAndNominalPrice(calculate(reconciliationLetterBO.getRentAndNominalPrice(),
                        contractNominalPriceRemain.get(contractBaseInfo.getId()), 0L));
        }
    }

    private void calculationMargin(ReconciliationLetterBO reconciliationLetterBO, ContractBaseInfo contractBaseInfo, Map<Long, List<MarginRecordInfo>> contractMarginRecord) {
        ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
        if (ObjectUtil.isEmpty(projectBizType) || ObjectUtil.isEmpty(contractBaseInfo) || ObjectUtil.isEmpty(reconciliationLetterBO) ) {
            log.info("ReconciliationLetterService calculationMargin contractBaseInfo : {}", contractBaseInfo);
            return;
        }
        List<MarginRecordInfo> marginRecordInfos = contractMarginRecord.get(contractBaseInfo.getId());
        if (ObjectUtil.isEmpty(marginRecordInfos)) {
            return;
        }
        marginRecordInfos.forEach(e -> {
            reconciliationLetterBO.setMargin(calculate(reconciliationLetterBO.getMargin(), null, e.getCollectionAmount()));
        });

    }

    private BigDecimal calculate(BigDecimal old, Long add, Long subtract){
        if(ObjectUtil.isEmpty(old)){
            old = new BigDecimal(0L);
        }
        return old.add(LongUtil.tenThousand2Dollar(LongUtil.null2zero(add).toString())).subtract(LongUtil.tenThousand2Dollar(LongUtil.null2zero(subtract).toString()));
    }

    private BigDecimal calculate(BigDecimal old, Long add, Long subtract, LocalDate localDateTime){
        if(ObjectUtil.isEmpty(old)){
            old = new BigDecimal(0L);
        }
        return old.add(LongUtil.tenThousand2Dollar(LongUtil.null2zero(add).toString()));
    }

    private Boolean isBeforeDate(CollectionBaseInfo collectionBaseInfo, LocalDate localDateTime) {
        if (collectionBaseInfo.getCollectionDate() == null
                || collectionBaseInfo.getCollectionDate().isBefore(localDateTime)
                || collectionBaseInfo.getCollectionDate().isEqual(localDateTime)) {
            return true;
        }
        return false;
    }

}
