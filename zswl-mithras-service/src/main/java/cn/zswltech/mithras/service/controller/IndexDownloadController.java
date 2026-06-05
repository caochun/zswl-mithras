package cn.zswltech.mithras.system.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.api.IndexDownloadApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.IndexDownloadREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAssetStrategyRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanListRSP;
import cn.zswltech.mithras.dto.capital.BusinessFlowFinanceListRSP;
import cn.zswltech.mithras.dto.client.client.ClientListRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessCollectionListRSP;
import cn.zswltech.mithras.dto.collection.CollectionFlowCenterBusinessPaymentListRSP;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoListRSP;
import cn.zswltech.mithras.dto.flow.search.ProcessListRSP;
import cn.zswltech.mithras.dto.flow.search.ReceiveTaskListRSP;
import cn.zswltech.mithras.dto.ftp.FtpInterestPageListRsp;
import cn.zswltech.mithras.dto.fund.FundCreditLimitDetailRSP;
import cn.zswltech.mithras.dto.fund.FundCreditListRSP;
import cn.zswltech.mithras.dto.fund.FundGuaranteeLimitDetailRSP;
import cn.zswltech.mithras.dto.fund.FundOrganizationListRSP;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectFinancingBaseInfoListRSP;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoListRSP;
import cn.zswltech.mithras.dto.fund.receiptrepay.FundReceiptRepayBaseInfoListSumRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountBalanceDetailListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.AccountSettingListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.FundTransferListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.*;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishBaseInfoListRSP;
import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoListRSP;
import cn.zswltech.mithras.dto.projreview.baseinfo.ProjReviewBaseInfoListRSP;
import cn.zswltech.mithras.dto.stampduty.StampDutyListRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.afterlease.interfaces.AfterLeaseCheckClientController;
import cn.zswltech.mithras.afterlease.interfaces.AfterLeaseCheckPlanBaseController;
import cn.zswltech.mithras.service.controller.capital.BusinessFlowController;
import cn.zswltech.mithras.service.controller.client.ClientController;
import cn.zswltech.mithras.collection.controller.CollectionFlowCenterController;
import cn.zswltech.mithras.service.controller.contract.ContractBaseInfoController;
import cn.zswltech.mithras.service.controller.flow.ProcessController;
import cn.zswltech.mithras.service.controller.flow.TaskController;
import cn.zswltech.mithras.ftp.oldftp.controller.FtpInterestController;
import cn.zswltech.mithras.service.controller.fund.FundCreditController;
import cn.zswltech.mithras.service.controller.fund.FundGuaranteeAgencyController;
import cn.zswltech.mithras.service.controller.fund.FundOrganizationController;
import cn.zswltech.mithras.service.controller.fund.financing.FundFinancingController;
import cn.zswltech.mithras.service.controller.fund.receiptrepay.FundReceiptRepayBaseInfoController;
import cn.zswltech.mithras.liquiditymanage.controller.FundLiquidityBaseController;
import cn.zswltech.mithras.liquiditymanage.controller.FundLiquidityIndexController;
import cn.zswltech.mithras.liquiditymanage.controller.FundTransferController;
import cn.zswltech.mithras.service.controller.projestablish.ProjEstablishBaseInfoController;
import cn.zswltech.mithras.service.controller.projpricing.ProjPricingBaseInfoController;
import cn.zswltech.mithras.service.controller.projreview.ProjReviewBaseInfoController;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.domain.enums.*;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientStatus;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.ProjItemStatus;
import cn.zswltech.mithras.fund.domain.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.domain.enums.OrganizationType;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingProcessStatus;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.ProcessState;
import cn.zswltech.mithras.fund.domain.enums.receiptrepay.ReceiptRepayState;
import cn.zswltech.mithras.contract.enums.overdue.LitigationStatus;
import cn.zswltech.mithras.contract.enums.overdue.PrintingType;
import cn.zswltech.mithras.payment.domain.enums.PaymentFlowItemEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.afterlease.excel.model.AfterLeaseCheckPlanLedgerListExcelModel;
import cn.zswltech.mithras.afterlease.excel.model.AfterLeaseCheckPlanListExcelModel;
import cn.zswltech.mithras.service.fund.direct.controller.FundDirectFinancingBaseInfoController;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.contract.overdue.application.dto.CollectionListDto;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationListDto;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.interfaces.LitigationController;
import cn.zswltech.mithras.service.overdue.interfaces.CollectionController;
import cn.zswltech.mithras.contract.overdue.interfaces.docprinting.DocPrintingController;
import cn.zswltech.mithras.service.service.projfms.ProjProcessState;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isEmpty;
import static cn.hutool.core.collection.ListUtil.of;
import static cn.hutool.core.date.DatePattern.*;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.core.util.ObjectUtil.equal;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.enums.BusinessModuleEnum.*;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * @author yibin
 */
@Slf4j
@RestController
public class IndexDownloadController implements IndexDownloadApi {

    private final static Map<BusinessModuleEnum, Triple<Class<?>, String, List<String>>/*ControllerClass,Method, ignoredFields*/> MAP = MapUtil.of(
            Pair.of(BusinessModuleEnum.CLIENT, Triple.of(ClientController.class, "newList", of("id"))),
            Pair.of(BusinessModuleEnum.PROJ_ESTABLISH, Triple.of(ProjEstablishBaseInfoController.class, "list", of(""))),
            Pair.of(BusinessModuleEnum.PROJ_REVIEW, Triple.of(ProjReviewBaseInfoController.class, "list", of())),
            Pair.of(BusinessModuleEnum.PROJ_PRICING, Triple.of(ProjPricingBaseInfoController.class, "list", of())),
            Pair.of(BusinessModuleEnum.CONTRACT, Triple.of(ContractBaseInfoController.class, "list", of())),
            Pair.of(BusinessModuleEnum.FUND_ORGANIZATION, Triple.of(FundOrganizationController.class, "list", of())),
            Pair.of(FUND_CREDIT, Triple.of(FundCreditController.class, "list", of())),
            Pair.of(FUND_CREDIT_LIMIT, Triple.of(FundCreditController.class, "limitDetail", of())),
            Pair.of(FUND_GUARANTEE_AGENCY_LIMIT, Triple.of(FundGuaranteeAgencyController.class, "limitDetail", of())),
            Pair.of(FUND_FINANCING, Triple.of(FundFinancingController.class, "pageList", of())),
            Pair.of(FUND_DIRECT_FINANCING, Triple.of(FundDirectFinancingBaseInfoController.class, "list", of())),
            Pair.of(BUSINESS_FLOW_PROJ_PAY, Triple.of(CollectionFlowCenterController.class, "paymentList", of())),
            Pair.of(BUSINESS_FLOW_PROJ_COLLECT, Triple.of(CollectionFlowCenterController.class, "collectionList", of())),
            Pair.of(BUSINESS_FLOW_FINANCIAL_PAY, Triple.of(BusinessFlowController.class, "selectList", of())),
            Pair.of(BUSINESS_FLOW_FINANCIAL_COLLECT, Triple.of(BusinessFlowController.class, "selectList", of())),
            Pair.of(ACCOUNT_BALANCE, Triple.of(FundLiquidityBaseController.class, "accountBalanceList", of())),
            Pair.of(ACCOUNT_SETTING, Triple.of(FundLiquidityBaseController.class, "accountSettingList", of())),
            Pair.of(LIQUIDITY_BOARD, Triple.of(FundLiquidityIndexController.class, "manageBoard", of())),
            Pair.of(LIQUIDITY_MISMATCH, Triple.of(FundLiquidityIndexController.class, "manageMismatch", of())),
            Pair.of(LIQUIDITY_RENT_INCOME, Triple.of(FundLiquidityIndexController.class, "manageRentIncome", of())),
            Pair.of(LIQUIDITY_REPAY, Triple.of(FundLiquidityIndexController.class, "manageRepay", of())),
            Pair.of(FUND_TRANSFER, Triple.of(FundTransferController.class, "list", of())),
            Pair.of(BusinessModuleEnum.FUND_RECEIPT_REPAY, Triple.of(FundReceiptRepayBaseInfoController.class, "list", of())),
            Pair.of(BusinessModuleEnum.MY_PROCESS_RECEIVED_AUDITED, Triple.of(TaskController.class, "myReceiveDoneList", of())),
            Pair.of(BusinessModuleEnum.MY_PROCESS_PROCESS_QUERY, Triple.of(ProcessController.class, "list", of())),
            Pair.of(BusinessModuleEnum.AFTER_LEASE_CHECK_PLAN, Triple.of(AfterLeaseCheckPlanBaseController.class, "afterLeaseAssetStrategys", of())),
            Pair.of(NEW_AFTER_LEASE_CHECK_PLAN_LEDGER, Triple.of(AfterLeaseCheckClientController.class, "queryCheckPlanLedgerList", of())),
            Pair.of(BusinessModuleEnum.FTP_INTEREST, Triple.of(FtpInterestController.class, "pageList", of())),
            Pair.of(BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_PLAN, Triple.of(AfterLeaseCheckPlanBaseController.class, "listCheckPlanWithPage", of())),
            Pair.of(BusinessModuleEnum.OVERDUE_COLLECTION,Triple.of(CollectionController.class, "overdueCollectionList", of())),
            Pair.of(BusinessModuleEnum.LITIGATION_REGISTRATION, Triple.of(LitigationController.class, "pageList", of())),
            Pair.of(BusinessModuleEnum.DOC_PRINTING, Triple.of(DocPrintingController.class, "pageList", of()))

    );
    @Autowired
    private HttpServletResponse response;

    @Override
    public void indexDownload(IndexDownloadREQ req) {
        BusinessModuleEnum module = BusinessModuleEnum.of(req.getIndexType());
        if (isNull(module)) {
            err("未知的首页列表");
        }
        Triple<Class<?>, String, List<String>> triple = MAP.get(module);
        if (isNull(triple)) {
            err("不支持的首页下载");
        }
        Object bean = SpringContextHolder.getBean(triple.getLeft());
        Method method = ReflectUtil.getMethodByName(triple.getLeft(), triple.getMiddle());
        Class<?> paramType = method.getParameters()[0].getType();
        //
        if (equal(req.getDownloadType(), "2")) {
            String json = req.getOriginJson();
            JSONObject jo = JSONUtil.parseObj(json);
            jo.set("page", 1);
            jo.set("pageSize", 10000);
            req.setOriginJson(jo.toString());
        }

        Object param = JSONUtil.toBean(req.getOriginJson(), paramType);
        R result = ReflectUtil.invoke(bean, method, param);
        PageR pageR;
        if (module.equals(FUND_FINANCING)) {
            FundFinancingListRSP rsp = (FundFinancingListRSP) result.getData();
            pageR = rsp.getRecords();
        } else if (module.equals(FUND_CREDIT)) {
            FundCreditListRSP rsp = (FundCreditListRSP) result.getData();
            pageR = rsp.getRecords();
        } else if (module.equals(FUND_CREDIT_LIMIT)) {
            FundCreditLimitDetailRSP rsp = (FundCreditLimitDetailRSP) result.getData();
            pageR = PageR.of(rsp.getLimitDetailList(), 10);
        } else if (module.equals(FUND_GUARANTEE_AGENCY_LIMIT)) {
            FundGuaranteeLimitDetailRSP rsp = (FundGuaranteeLimitDetailRSP) result.getData();
            pageR = PageR.of(rsp.getLimitDetailList(), 10);
        } else if(module.equals(FUND_RECEIPT_REPAY)){
            FundReceiptRepayBaseInfoListSumRSP rsp = (FundReceiptRepayBaseInfoListSumRSP)result.getData();
            PageR<FundReceiptRepayBaseInfoListRSP> list = rsp.getList();
            FundReceiptRepayBaseInfoListRSP sumRsp = BeanUtil.copyProperties(rsp, FundReceiptRepayBaseInfoListRSP.class);
            list.getList().add(sumRsp);
            pageR = list;
        } else if(module.equals(ACCOUNT_BALANCE)){
            AccountBalanceDetailListRSP rsp = (AccountBalanceDetailListRSP)result.getData();
            pageR = PageR.of(rsp.getList(), 10);
        } else if(module.equals(ACCOUNT_SETTING)){
            List<AccountSettingListRSP> rsp = (List<AccountSettingListRSP>)result.getData();
            pageR = PageR.of(rsp, 10);
        } else if(module.equals(LIQUIDITY_BOARD)){
            LiquidityBoardDetailSumRSP rsp = (LiquidityBoardDetailSumRSP)result.getData();
            List<LiquidityBoardDetailRSP> list = rsp.getList();
            pageR = PageR.of(list, 10);
        } else if(module.equals(LIQUIDITY_MISMATCH)){
            List<LiquidityMismatchDetailRSP> rsp = (List<LiquidityMismatchDetailRSP>)result.getData();
            pageR = PageR.of(rsp, 10);
        } else if(module.equals(FUND_TRANSFER)){
            FundTransferListRSP rsp = (FundTransferListRSP)result.getData();
            pageR = PageR.of(rsp.getList(), 10);
        }
        else if (module.equals(LIQUIDITY_RENT_INCOME)) {
            List<LiquidityBoardRentIncomeRSP> rsp = (List<LiquidityBoardRentIncomeRSP>)result.getData();
            pageR = PageR.of(rsp, 10);
        }
        else if (module.equals(LIQUIDITY_REPAY)) {
            List<LiquidityBoardRepayPrincipalInterestRSP> rsp = (List<LiquidityBoardRepayPrincipalInterestRSP>)result.getData();
            pageR = PageR.of(rsp, 10);
        }
        else if (module.equals(OVERDUE_COLLECTION)) {
            List<CollectionListDto> rsp = (List<CollectionListDto>)result.getData();
            pageR = PageR.of(rsp, 10);
        }
        else if (module.equals(STAMP_DUTY)) {
            StampDutyListRSP rsp = (StampDutyListRSP) result.getData();
            pageR = rsp.getRecords();
        }
        else {
            pageR = (PageR) result.getData();
        }
        List list = pageR.getList();
        if (list.isEmpty()) {
            err("无数据");
        }
        switch (module) {
            case NEW_AFTER_LEASE_CHECK_PLAN: {
                newAfterLeaseCheckPlan(list);
                break;
            }
            case NEW_AFTER_LEASE_CHECK_PLAN_LEDGER: {
                newAfterLeaseCheckPlanLedger(list);
                break;
            }
            case CLIENT: {
                client(list);
                break;
            }
            case PROJ_ESTABLISH: {
                projEstablish(list);
                break;
            }
            case PROJ_PRICING: {
                projPricing(list);
                break;
            }
            case PROJ_REVIEW: {
                projReview(list);
                break;
            }
            case CONTRACT: {
                contract(list);
                break;
            }
            case FUND_ORGANIZATION: {
                fundOrg(list);
                break;
            }
            case FUND_CREDIT: {
                fundCredit(list);
                break;
            }
            case FUND_CREDIT_LIMIT: {
                fundCreditLimit(list);
                break;
            }
            case FUND_GUARANTEE_AGENCY_LIMIT: {
                fundGuaranteeAgencyLimit(list);
                break;
            }
            case FUND_FINANCING: {
                fundFinancing(list);
                break;
            }
            case FUND_DIRECT_FINANCING: {
                fundDirectFinancing(list);
                break;
            }
            case FUND_RECEIPT_REPAY: {
                fundReceiptRepay(list);
                break;
            }
            case FTP_INTEREST: {
                ftpInterest(list);
                break;
            }
            case MY_PROCESS_RECEIVED_AUDITED: {
                myProcessReceivedAudited(list);
                break;
            }
            case MY_PROCESS_PROCESS_QUERY: {
                myProcessProcessQuery(list);
                break;
            }
            case AFTER_LEASE_CHECK_PLAN: {
                myAfterLeaseCheckPlan(list);
                break;
            }
            case BUSINESS_FLOW_PROJ_PAY: {
                flowProjPay(list);
                break;
            }
            case BUSINESS_FLOW_PROJ_COLLECT: {
                flowProjCollect(list);
                break;
            }
            case BUSINESS_FLOW_FINANCIAL_PAY: {
                flowFlowFinancialPay(list);
                break;
            }
            case BUSINESS_FLOW_FINANCIAL_COLLECT: {
                flowFlowFinancialCollect(list);
                break;
            }
            case OVERDUE_COLLECTION:{
                overdueCollection(list);
                break;
            }
            case LITIGATION_REGISTRATION:{
                litigationRegistration(list);
                break;
            }
            case DOC_PRINTING:{
                docPrinting(list);
                break;
            }
            case ACCOUNT_BALANCE:{
                accountBalance(list);
                break;
            }
            case ACCOUNT_SETTING:{
                accountSetting(list);
                break;
            }
            case LIQUIDITY_BOARD:{
                liquidityBoard(list);
                break;
            }
            case LIQUIDITY_MISMATCH:{
                liquidityMismatch(list);
                break;
            }
            case FUND_TRANSFER:{
                fundTransfer(list);
                break;
            }
            case LIQUIDITY_RENT_INCOME: {
                liquidityRentIncome(list);
                break;
            }
            case LIQUIDITY_REPAY: {
                liquidityRepay(list);
                break;
            }
            default:
                break;
        }
    }

    @SneakyThrows
    private void liquidityRentIncome(List resultList) {
        List<LiquidityBoardRentIncomeRSP> list = (List<LiquidityBoardRentIncomeRSP>) resultList;
        String downloadFileName = URLEncoder.encode("流动性管理-租金流入.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("承租人名称", "合同编号", "本期到期日", "本期应还金额（万元）", "流入账户", "流入账户性质"));
        for (LiquidityBoardRentIncomeRSP c : list) {
            w.writeRow(ListUtil.of(
                    c.getTenantName(),
                    c.getContractCode(),
                    c.getExpireDate(),
                    c.getShouldPayAmount() == null ? "" :
                    new BigDecimal(c.getShouldPayAmount()).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toPlainString(),
                    c.getIncomeAccount(),
                    c.getIncomeAccountProperty()));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void liquidityRepay(List resultList) {
        List<LiquidityBoardRepayPrincipalInterestRSP> list = (List< LiquidityBoardRepayPrincipalInterestRSP>) resultList;
        String downloadFileName = URLEncoder.encode("流动管理-还本付息.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        // 融资机构	融资编号	本期到期日	本期应还金额(万元）	本期应还本金(万元）	本期应还利息(万元）	本金流出账户	本金流出账户性质
        w.writeHeadRow(Arrays.asList("融资机构", "融资编号", "本期到期日", "本期应还金额(万元）", "本期应还本金(万元）", "本期应还利息(万元）", "本金流出账户", "本金流出账户性质"));
        for (LiquidityBoardRepayPrincipalInterestRSP c : list) {
            w.writeRow(ListUtil.of(
                    c.getOrganizationName(),
                    c.getFinancingCode(),
                    c.getExpireDate(),
                    Objects.nonNull(c.getShouldPayAmount()) ?
                    new BigDecimal(c.getShouldPayAmount()).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toPlainString() : "",
                    Objects.nonNull(c.getShouldPayPrincipal()) ?
                    new BigDecimal(c.getShouldPayPrincipal()).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toPlainString() : "",
                    Objects.nonNull(c.getShouldPayInterest()) ?
                    new BigDecimal(c.getShouldPayInterest()).divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toPlainString() : "",
                    c.getPrincipalOutflowAccount(),
                    c.getPrincipalOutflowAccountProperty()
            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void accountSetting(List resultList) {
        List<AccountSettingListRSP> list = (List<AccountSettingListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("回款账户导出.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("融资机构","融资编号","融资金额","账户用途","还款银行","还款账号","账户性质","是否模拟结清","模拟结清日期","模拟结清金额","资金经理"));
        for (AccountSettingListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            Optional.ofNullable(c.getOrganizationName()).map(m -> String.join(",", m)).orElse(null),
                            c.getFinancingCode(),
                            Util.toYuan(c.getFinancingAmount()),
                            Optional.ofNullable(FundFinancingAccountTypeEnum.valueOf(c.getAccountCategory())).map(FundFinancingAccountTypeEnum::display).orElse(null),
                            c.getAccountBank(),
                            c.getAccountNumber(),
                            Optional.ofNullable(BaseDataBankAccountTypeEnum.valueOf(c.getAccountType())).map(BaseDataBankAccountTypeEnum::display).orElse(null),
                            BooleanUtil.isTrue(c.getSimulateSettle()) ? "是" : "否",
                            c.getSettleTime(),
                            Util.toYuan(c.getSettleAmount()),
                            c.getFundManagerName()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());


    }

    @SneakyThrows
    private void accountBalance(List resultList) {
        List<AccountBalanceDetailListRSP.AccountBalanceDetail> list = (List<AccountBalanceDetailListRSP.AccountBalanceDetail>) resultList;
        String downloadFileName = URLEncoder.encode("账户余额表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("日期","开户银行","银行账号","账户性质","提款","租金回流","其它流入","投放","还本付息","还本付息（调整）","刚性支出","其它支出","结余（预估）","结余受限（预估）","结余（实际）","差异"));
        for (AccountBalanceDetailListRSP.AccountBalanceDetail c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getDate(),
                            c.getAccountBank(),
                            c.getAccountNumber(),
                            Optional.ofNullable(BaseDataBankAccountTypeEnum.valueOf(c.getAccountType())).map(BaseDataBankAccountTypeEnum::display).orElse(null),
                            Util.toYuan(c.getDrawingsAmount()),
                            Util.toYuan(c.getRentReflowAmount()),
                            Util.toYuan(c.getOtherFlowAmount()),
                            Util.toYuan(c.getPaymentAmount()),
                            Util.toYuan(c.getRepayAmount()),
                            Util.toYuan(c.getRepayEditAmount()),
                            Util.toYuan(c.getMustExpenseAmount()),
                            Util.toYuan(c.getOtherExpenseAmount()),
                            Util.toYuan(c.getEstimateBalanceAmount()),
                            Util.toYuan(c.getEstimateBalanceLimitAmount()),
                            Util.toYuan(c.getActualBalanceAmount()),
                            Util.toYuan(c.getDiffAmount())
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void liquidityBoard(List resultList) {
        List<LiquidityBoardDetailRSP> list = (List<LiquidityBoardDetailRSP>) resultList;
        String downloadFileName = URLEncoder.encode("流动性看板.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("数据时点","期初余额","预计回收租金","现金流支出","债务偿还","非ABS还款","ABS还款","刚性支出","期末余额","监管户资金","监管户资金的负值","非监管户资金","期间实际或计划投放等支出金额", "期间实际或计划融资等收款金额", "当日最大可用余额"));
        for (LiquidityBoardDetailRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getDate(),
                            Util.toYuan(Optional.ofNullable(c.getInitialBalance()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getExpectedRentRecovery()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getCashFlowExpenditure()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getDebtRepayment()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getNonAbsRepayment()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getAbsRepayment()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getRigidExpenditure()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getEndingBalance()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getSupervisedAccountFunds()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getNegativeSupervisedAccountFunds()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getNonSupervisedAccountFunds()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
//                            Util.toYuan(Optional.ofNullable(c.getDailyLiquidityGap()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
//                            Util.toYuan(Optional.ofNullable(c.getThirtyDayLiquidityCoverageRatio()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getPeriodActualOrPlannedExpenditure()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getPeriodActualOrPlannedFinancingReceipts()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null)),
                            Util.toYuan(Optional.ofNullable(c.getDailyMaxAvailableBalance()).map(LiquidityColorVo::getValue).map(BigDecimal::longValue).orElse(null))
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void liquidityMismatch(List resultList) {
        List<LiquidityMismatchDetailRSP> list = (List<LiquidityMismatchDetailRSP>) resultList;
        String downloadFileName = URLEncoder.encode("错配明细.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("融资编号","融资机构","期项","现金流出时间","现金流出金额(万元)","质押/监管合同编号","现金流入时间","现金流入金额(万元)"));
        for (LiquidityMismatchDetailRSP c : list) {
            if(CollectionUtil.isNotEmpty(c.getCashInFlowList())) {
                for (LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow cashInFlow : c.getCashInFlowList()) {
                    w.writeRow(
                            ListUtil.of(
                                    c.getFinancingCode(),
                                    String.join(",", c.getOrganizationName()),
                                    c.getPhase(),
                                    c.getCashOutflowTime(),
                                    Util.toWanYuan(c.getCashOutflowAmount()),
                                    cashInFlow.getPledgeContractCode(),
                                    cashInFlow.getCashInflowTime(),
                                    Util.toWanYuan(cashInFlow.getCashInflowAmount())
                            )
                    );
                }
            }else {
                w.writeRow(
                        ListUtil.of(
                                c.getFinancingCode(),
                                String.join(",", c.getOrganizationName()),
                                c.getPhase(),
                                c.getCashOutflowTime(),
                                Util.toWanYuan(c.getCashOutflowAmount()),
                                null,
                                null,
                                null
                        )
                );
            }

        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    private void docPrinting(List list) {
        try {
            List<PrintingListDto> orginalList = (List<PrintingListDto>) list;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("用印编号", "用印类型", "用印原因", "申请人",
                    "申请时间"));
            for (PrintingListDto c : orginalList) {
                w.writeRow(ListUtil.of(
                        c.getCode(),
                        PrintingType.valueOf(c.getType()).display(),
                        c.getReason(),
                        c.getCreateByName(),
                        c.getCreateTime()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("用印申请列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());

        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出用印审批发生未知异常", e);
            throw new MithrasException("导出用印审批发生未知异常");
        }
    }

    private void litigationRegistration(List list) {
        try {
            List<LitigationListDto> orginalList = (List<LitigationListDto>) list;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("诉讼登记编号", "合同编号", "客户名称", "诉讼状态",
                    "创建人", "创建时间"));
            for (LitigationListDto c : orginalList) {
                w.writeRow(ListUtil.of(
                        c.getCode(),
                        c.getContractCodes(),
                        c.getClientName(),
                        LitigationStatus.valueOf(c.getStatus()).display(),
                        c.getCreateByName(),
                        c.getCreateTime()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("诉讼登记列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());

        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出逾期催收列表发生未知异常", e);
            throw new MithrasException("导出逾期催收列表发生未知异常");
        }
    }

    private void overdueCollection(List list) {
        try {
            List<CollectionListDto> orginalList = (List<CollectionListDto>) list;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(ListUtil.of("客户名称", "风险敞口", "逾期租金", "逾期罚息",
                    "当前最大逾期天数", "逾期状态", "项目主办", "业务部门", "最新进展", "最近跟进人", "最近跟进时间"));
            for (CollectionListDto c : orginalList) {
                w.writeRow(ListUtil.of(
                        c.getClientName(),
                        c.getRiskExposure()==null?0:c.getRiskExposure()/10000L,
                        c.getOverdueRent() == null ? 0:c.getOverdueRent()/10000L,
                        c.getLateCharge()==null?0:c.getLateCharge()/10000L,
                        c.getCurMaxOverdueDays(),
                        c.getOverdue() ? "逾期" : "未逾期",
                        c.getProjectSponsorName(),
                        c.getBizDeptName(),
                        c.getLatestProgress(),
                        c.getProcessPerson(),
                        c.getProcessTime()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("逾期催收客户列表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            outputStream.write(bos.toByteArray());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出逾期催收列表发生未知异常", e);
            throw new MithrasException("导出逾期催收列表发生未知异常");
        }
    }

    @SneakyThrows
    private void flowFlowFinancialPay(List resultList) {
        List<BusinessFlowFinanceListRSP> list = (List<BusinessFlowFinanceListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("业务流水-资金端.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("核销状态","融资机构","融资编号","期项","现金流项目","应付日期","应付金额（元）","应付本金（元）","应付利息（元）","已付金额（元）","已付本金（元）","已付利息（元）","最近付款日"));
        for (BusinessFlowFinanceListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            Optional.ofNullable(CashFlowState.of(c.getWriteOffStatus())).map(CashFlowState::getDisplay).orElse(""),
                            c.getFinancingRoute(),
                            c.getFinancingCode(),
                            c.getPhase(),
                            Optional.ofNullable(FinanceCashFlowItemEnum.of(c.getCashFlowItem())).map(FinanceCashFlowItemEnum::getDisplay).orElse(""),
                            c.getDate(),
                            Util.toYuan(c.getAmount()),
                            Util.toYuan(c.getPrincipalAmount()),
                            Util.toYuan(c.getInterestAmount()),
                            Util.toYuan(c.getActualVerifyAmount()),
                            Util.toYuan(c.getActualVerifyPrincipalAmount()),
                            Util.toYuan(c.getActualVerifyInterestAmount()),
                            c.getCashFlowDate()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void flowFlowFinancialCollect(List resultList) {
        List<BusinessFlowFinanceListRSP> list = (List<BusinessFlowFinanceListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("业务流水-资金端.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("核销状态","融资机构","融资编号","现金流项目","应收金额（元）","已收金额（元）","应收日期","最近收款日"));
        for (BusinessFlowFinanceListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            Optional.ofNullable(CashFlowState.of(c.getWriteOffStatus())).map(CashFlowState::getDisplay).orElse(""),
                            c.getFinancingRoute(),
                            c.getFinancingCode(),
                            Optional.ofNullable(FinanceCashFlowItemEnum.of(c.getCashFlowItem())).map(FinanceCashFlowItemEnum::getDisplay).orElse(""),
                            Util.toYuan(c.getAmount()),
                            Util.toYuan(c.getActualVerifyAmount()),
                            c.getDate(),
                            c.getCashFlowDate()
                            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void flowProjCollect(List resultList) {
        List<CollectionFlowCenterBusinessCollectionListRSP> list = (List<CollectionFlowCenterBusinessCollectionListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("业务流水-项目端.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("核销状态","客户名称","合同编号","期项","现金流项目","业务部门","应收金额（元）","本金（元）","利息（元）","已收金额（元）","应收日期","最近收款日","现金流编号","项目名称"));
        for (CollectionFlowCenterBusinessCollectionListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            Optional.ofNullable(CollectionWriteOffStatusEnum.of(c.getWriteOffStatus())).map(CollectionWriteOffStatusEnum::getDisplay).orElse(""),
                            c.getClientName(),
                            c.getContractCode(),
                            c.getPhase(),
                            Optional.ofNullable(CashFlowItemEnum.of(c.getCashFlowItem())).map(CashFlowItemEnum::getDisplay).orElse(""),
                            c.getBizDept(),
                            Util.toYuan(c.getPlanCollectionAmount()),
                            Util.toYuan(c.getPrincipal()),
                            Util.toYuan(c.getInterest()),
                            Util.toYuan(c.getCollectionAmount()),
                            c.getPlanCollectionDate(),
                            c.getCollectionDate(),
                            c.getCode(),
                            c.getProjName()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void flowProjPay(List resultList) {
        List<CollectionFlowCenterBusinessPaymentListRSP> list = (List<CollectionFlowCenterBusinessPaymentListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("业务流水-项目端.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("核销状态","客户名称","合同编号","现金流项目","业务部门","应付金额（元）","已付金额（元）","应付日期","最近付款日","现金流编号","项目名称"));
        for (CollectionFlowCenterBusinessPaymentListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            Optional.ofNullable(PaymentWriteOffStatus.valueOf(c.getWriteOffStatus())).map(PaymentWriteOffStatus::getDisplay).orElse(""),
                            c.getClientName(),
                            c.getContractCode(),
                            Optional.ofNullable(PaymentFlowItemEnum.of(c.getCashFlowItem())).map(PaymentFlowItemEnum::getDisplay).orElse(""),
                            c.getBizDept(),
                            Util.toYuan(c.getPaymentAmount()),
                            Util.toYuan(c.getPaidAmount()),
                            c.getApplyPaymentDate(),
                            c.getPaidInDate(),
                            c.getPaymentCode(),
                            c.getProjName()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    private void newAfterLeaseCheckPlan(List apiList) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租后检查计划" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            List<AfterLeaseCheckPlanListRSP> orginalList = (List<AfterLeaseCheckPlanListRSP>) apiList;
            if (CollUtil.isEmpty(orginalList)) {
                EasyExcelFactory.write(response.getOutputStream(), AfterLeaseCheckPlanListExcelModel.class)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .sheet("租后检查计划").doWrite(Collections.emptyList());
                return;
            }
            List<AfterLeaseCheckPlanListExcelModel> list = orginalList.stream().map(obj -> {
                AfterLeaseCheckPlanListExcelModel excelModel = new AfterLeaseCheckPlanListExcelModel();
                // 实体类转化
                excelModel.setPlanName(obj.getPlanName());
                excelModel.setPlanType(Optional.ofNullable(AfterLeaseCheckPlanTypeEnum.of(obj.getPlanType())).map(AfterLeaseCheckPlanTypeEnum::display).orElse(""));
                excelModel.setRiskExposure(obj.getRiskExposure());
                excelModel.setRemainingPrincipal(obj.getRemainingPrincipal());
                excelModel.setRiskControlManagerName(obj.getRiskControlManagerName());
                excelModel.setProjSponsorUserName(obj.getProjSponsorUserName());
                excelModel.setBizDeptName(obj.getBizDeptName());
                excelModel.setCheckWay(Optional.ofNullable(AfterLeaseCheckWayEnum.find(obj.getCheckWay())).map(AfterLeaseCheckWayEnum::display).orElse(""));
                excelModel.setLastCheckWay(Optional.ofNullable(AfterLeaseCheckWayEnum.find(obj.getLastCheckWay())).map(AfterLeaseCheckWayEnum::display).orElse(""));
                if (Objects.nonNull(obj.getDeadLine())) {
                    excelModel.setDeadLine(obj.getDeadLine().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                }
                excelModel.setPlanStatus(Optional.ofNullable(AfterLeaseCheckPlanStatusEnum.find(obj.getPlanStatus())).map(AfterLeaseCheckPlanStatusEnum::display).orElse(""));
                excelModel.setApprovalStatus(Optional.ofNullable(AfterLeaseCheckPlanProcessStatusEnum.of(obj.getApprovalStatus())).map(AfterLeaseCheckPlanProcessStatusEnum::display).orElse(""));
                // 这个时间转化较为复杂
                if (Objects.nonNull(obj.getYear())) {
                    if (Objects.nonNull(obj.getMonth())) {
                        excelModel.setCheckBelongTime(String.format("%d年%d月", obj.getYear(), obj.getMonth()));
                    }
                    if (Objects.nonNull(obj.getQuarter())) {
                        excelModel.setCheckBelongTime(String.format("%d年第%d季度", obj.getYear(), obj.getQuarter()));
                    }
                }
                excelModel.setCurAssigneeNames(obj.getCurAssigneeNames());
                excelModel.setCreateTime(obj.getCreateTime());
                excelModel.setUpdateTime(obj.getUpdateTime());
                return excelModel;
            }).collect(Collectors.toList());

            EasyExcelFactory.write(response.getOutputStream(), AfterLeaseCheckPlanListExcelModel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("租后检查计划").doWrite(list);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出租后检查计划表发生未知异常", e);
            throw new MithrasException("导出租后检查表发生未知异常");
        }
    }

    private void newAfterLeaseCheckPlanLedger(List apiList) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租后检查计划台账" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            List<AfterLeaseCheckLedgerListRSP> orginalList = (List<AfterLeaseCheckLedgerListRSP>) apiList;
            if (CollUtil.isEmpty(orginalList)) {
                EasyExcelFactory.write(response.getOutputStream(), AfterLeaseCheckPlanLedgerListExcelModel.class)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .sheet("租后检查计划台账").doWrite(Collections.emptyList());
                return;
            }
            List<AfterLeaseCheckPlanLedgerListExcelModel> list = orginalList.stream().map(obj -> {
                AfterLeaseCheckPlanLedgerListExcelModel excelModel = new AfterLeaseCheckPlanLedgerListExcelModel();
                BeanUtil.copyProperties(obj, excelModel);
                return excelModel;
            }).collect(Collectors.toList());

            EasyExcelFactory.write(response.getOutputStream(), AfterLeaseCheckPlanLedgerListExcelModel.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet("租后检查计划台账").doWrite(list);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出租后检查计划台账发生未知异常", e);
            throw new MithrasException("导出租后检查计划台账发生未知异常");
        }
    }
    @SneakyThrows
    private void myProcessProcessQuery(List resultList) {
        List<ProcessListRSP> list = (List<ProcessListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("我的流程【流程查询】列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("流程ID", "审批状态", "流程类型", "表单名称", "项目名称", "项目编号", "合同编号",
                "客户名称", "发起人", "申请部门", "最后审批人", "当前节点", "当前审批人", "申请时间", "结束时间"));
        for (ProcessListRSP c : list) {
            ProcessBusinessStatusEnum businessStatusEnum = null;
            if (CharSequenceUtil.isNotBlank(c.getProcessStatus())) {
                businessStatusEnum = ProcessBusinessStatusEnum.getByType(Integer.valueOf(c.getProcessStatus()));
            }
            w.writeRow(ListUtil.of(
                    c.getProcessInstanceId(),
                    Objects.isNull(businessStatusEnum) ? "" : businessStatusEnum.getDisplay(),
                    c.getModelName(),
                    c.getProcessName(),
                    c.getProjName(),
                    c.getProjCode(),
                    c.getContractCode(),
                    c.getClientName(),
                    c.getStartUserName(),
                    c.getStartUserDeptName(),
                    c.getFinalAssigneeName(),
                    c.getCurTaskNames(),
                    c.getCurAssigneeNames(),
                    c.getStartTime(),
                    c.getEndTime()
            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void myProcessReceivedAudited(List resultList) {
        List<ReceiveTaskListRSP> list = (List<ReceiveTaskListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("我的流程【我收到的-已审批】列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("流程ID", "审批状态", "流程类型", "表单名称", "项目名称", "项目编号", "合同编号",
                "客户名称", "当前节点", "当前审批人", "发起人", "申请部门", "申请时间", "处理时间"));
        for (ReceiveTaskListRSP c : list) {
            ProcessBusinessStatusEnum businessStatusEnum = null;
            if (CharSequenceUtil.isNotBlank(c.getProcessStatus())) {
                businessStatusEnum = ProcessBusinessStatusEnum.getByType(Integer.valueOf(c.getProcessStatus()));
            }
            w.writeRow(ListUtil.of(
                    c.getProcessInstanceId(),
                    Objects.isNull(businessStatusEnum) ? "" : businessStatusEnum.getDisplay(),
                    c.getModelName(),
                    c.getProcessName(),
                    c.getProjName(),
                    c.getProjCode(),
                    c.getContractCode(),
                    c.getClientName(),
                    c.getCurTaskNames(),
                    c.getCurAssigneeNames(),
                    c.getStartUserName(),
                    c.getStartUserDeptName(),
                    c.getProcessStartTime(),
                    c.getTaskEndTime()
            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void ftpInterest(List resultList) {
        List<FtpInterestPageListRsp> list = (List<FtpInterestPageListRsp>) resultList;
        String downloadFileName = URLEncoder.encode("FTP计息.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("借据编号", "客户名称", "合同编号", "FTP价格（%）", "当年累计计息（元）", "更新日期", "业务部门", "项目主办"));
        for (FtpInterestPageListRsp c : list) {
            w.writeRow(ListUtil.of(
                    c.getReceiptCode(),
                    c.getClientName(),
                    c.getContractCode(),
                    Optional.ofNullable(c.getLastCashFtp()).map(e -> BigDecimal.valueOf(c.getLastCashFtp()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)).orElse(null),
                    Util.toYuan(c.getTotalInterestAmount()),
                    c.getLastUpdateDate(),
                    c.getBizDeptName(),
                    c.getSponsorUserName()
            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void fundReceiptRepay(List resultList) {
        List<FundReceiptRepayBaseInfoListRSP> list = (List<FundReceiptRepayBaseInfoListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-还本付息.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("融资编号", "融资机构", "融资金额（元）", "本月应还金额（元）", "本月应还本金（元）", "本月应还利息（元）",
                "本息收付款状态", "审批状态", "创建人", "创建时间"));
        for (FundReceiptRepayBaseInfoListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getFinancingCode(),
                            c.getFinancingOrgName(),
                            Util.toYuan(c.getFinancingAmount()),
                            Util.toYuan(c.getMonthRepayAmount()),
                            Util.toYuan(c.getMonthRepayPrincipal()),
                            Util.toYuan(c.getMonthRepayInterest()),
                            isNull(ReceiptRepayState.of(c.getReceiptRepayState())) ? c.getReceiptRepayState() : ReceiptRepayState.of(c.getReceiptRepayState()).display(),
                            isNull(ProcessState.of(c.getProcessState())) ? c.getProcessState() : ProcessState.of(c.getProcessState()).display(),
                            c.getCreateByName(),
                            Optional.ofNullable(c.getCreateTime()).map(m -> m.format(ofPattern(NORM_DATETIME_PATTERN))).orElse("")
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void fundFinancing(List resultList) {
        List<FundFinancingListRSP.FundFinancingList> list = (List<FundFinancingListRSP.FundFinancingList>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-间融管理.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("融资编号", "业务类型", "融资机构", "融资金额（元）", "剩余本金（元）", "综合融资成本", "担保融资金额（元）", "信用融资金额（元）", "实际综合成本", "借款年利率", "利率类型",
                "贷款日", "到期日期", "融资状态", "审批状态", "质押资产", "创建人", "创建时间", "更新时间"));
        for (FundFinancingListRSP.FundFinancingList c : list) {
            w.writeRow(ListUtil.of(
                    c.getFinancingCode(),
                    Optional.ofNullable(FundFinancingBizTypeEnum.finaByName(c.getBusinessType())).map(FundFinancingBizTypeEnum::getDisplay).orElse(""),
                    c.getOrganizationName(),
                    Util.toYuan(c.getFinancingAmount()),
                    Util.toYuan(c.getLastPrincipal()),
                    isNull(c.getComprehensiveFinancingCost()) ? "" : Util.toYuan(Long.valueOf(c.getComprehensiveFinancingCost())) + "%",
                    Util.toYuan(c.getGuaranteeFinancingAmount()),
                    Util.toYuan(c.getCreditFinancingAmount()),
                    isNull(c.getActualComprehensiveCost()) ? "" : Util.toYuan(Long.valueOf(c.getActualComprehensiveCost())) + "%",
                    isNull(c.getInterestRate()) ? "" : Util.toYuan(Long.valueOf(c.getInterestRate())) + "%",
                    StrUtil.isBlank(c.getInterestRateType()) ? "" : Optional.ofNullable(RateType.of(c.getInterestRateType())).map(RateType::display).orElse(""),
                    c.getBorrowDate(),
                    c.getExpireDate(),
                    isBlank(c.getFinancingStatus()) ? "" : FundFinancingStatusEnum.valueOf(c.getFinancingStatus()).display(),
                    isBlank(c.getApprovalStatus()) ? "" : FundFinancingProcessStatus.valueOf(c.getApprovalStatus()).display(),
                    isEmpty(c.getContractCodeList()) ? "无" : join("，", c.getContractCodeList()),
                    c.getCreateUserName(),
                    c.getCreateTime(),
                    c.getUpdateTime())
            );
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void fundDirectFinancing(List resultList) {
        List<FundDirectFinancingBaseInfoListRSP> list = (List<FundDirectFinancingBaseInfoListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-直融管理.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("融资编号", "产品名称", "融资金额（元）", "剩余本金（元）", "综合融资成本(%)", "票面加权利率(%)", "项目类别", "成立日期", "创建人", "创建日期", "变更日期", "融资状态"));

//        w.merge(0, 0, 0, 1, "融资编号", true);
//        w.merge(0, 0, 2, 3, "产品名称", true);
//        w.merge(0, 0, 4, 5, "融资金额（元）", true);
//        w.merge(0, 0, 6, 7, "剩余本金（元）", true);
//        w.merge(0, 0, 8, 9, "综合融资成本(%)", true);
//        w.merge(0, 0, 10, 11, "票面加权利率(%)", true);
//        w.merge(0, 0, 12, 13, "项目类别", true);
//        w.merge(0, 0, 14, 15, "成立日期", true);
//        w.merge(0, 0, 16, 17, "创建人", true);
//        w.merge(0, 0, 18, 19, "创建日期", true);
//        w.merge(0, 0, 20, 21, "变更日期", true);

        for (FundDirectFinancingBaseInfoListRSP c : list) {
            w.writeRow(ListUtil.of(
                    c.getFinancingCode(),
                    c.getProductName(),
                    Util.toYuan(c.getFinancingAmount()),
                    Util.toYuan(c.getRemainingAmount()),
                    Optional.ofNullable(Util.toYuanWithoutSplit(c.getComprehensiveFinancingCost())).map(m -> m + "%").orElse(""),
                    Optional.ofNullable(Util.toYuanWithoutSplit(c.getAverageCouponRate())).map(m -> m + "%").orElse(""),
                    DirectFinancingType.findByName(c.getDirectFinancingType()),
                    c.getDurationFrom(),
                    c.getCreateByName(),
                    c.getCreateTime(),
                    c.getUpdateTime(),
                    Optional.ofNullable(FundFinancingStatusEnum.finaByName(c.getFinancingStatus())).map(FundFinancingStatusEnum::getDisplay).orElse(null))
            );
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void fundCredit(List resultList) {
        List<FundCreditListRSP.FundCreditList> list = (List<FundCreditListRSP.FundCreditList>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-授信管理.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        // 2. 设置全局单元格格式（新增）
        StyleSet style = w.getStyleSet();
        // 强制文本格式（防止长数字科学计数）
        CellStyle textStyle = style.getCellStyle();
        textStyle.setDataFormat(w.getWorkbook().createDataFormat().getFormat("@"));
        w.writeHeadRow(Arrays.asList("授信编号", "授信机构", "额度是否可循环", "授信产品", "授信总额", "已使用额度", "剩余授信额度", "剩余总授信额度（元）", "管理人", "授信到期日"));
        w.writeSecHeadRow(ListUtil.of("授信编号", "授信机构", "额度是否可循环", "授信产品", "总额度（元）", "担保额度（元）", "信用额度（元）", "总额度（元）", "信用额度（元）", "担保额度（元）",
                "总额度（元）", "信用额度（元）", "担保额度（元）", "剩余本金（元）", "管理人", "授信到期日"));

        w.merge(0, 1, 0, 0, "授信编号", true);
        w.merge(0, 1, 1, 1, "授信机构", true);
        w.merge(0, 1, 2, 2, "额度是否可循环", true);
        w.merge(0, 1, 3, 3, "授信产品", true);
        w.merge(0, 0, 4, 6, "授信总额", true);
        w.merge(0, 0, 7, 9, "已使用额度", true);
        w.merge(0, 0, 10, 12, "剩余授信额度", true);
        w.merge(0, 1, 13, 13, "剩余本金（元）", true);
        w.merge(0, 1, 14, 14, "管理人", true);
        w.merge(0, 1, 15, 15, "授信到期日", true);


        for (FundCreditListRSP.FundCreditList c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getCreditCode(),
                            c.getOrganizationName(),
                            isNull(c.getRecyclable()) ? "" : c.getRecyclable() == 1 ? "是" : "否",
                            isBlank(c.getCreditType()) ? "" : FundFinancingBizTypeEnum.valueOf(c.getCreditType()).display(),
                            Util.toYuan(c.getTotalCreditLimit()),
                            Util.toYuan(c.getGuaranteeAmount()),
                            Util.toYuan(c.getCreditLimit()),
                            Util.toYuan(c.getUsedTotalCreditAmount()),
                            Util.toYuan(c.getUsedCreditAmount()),
                            Util.toYuan(c.getUsedGuaranteeAmount()),
                            Util.toYuan(c.getRemainingLimit()),
                            Util.toYuan(c.getRemainingCreditAmount()),
                            Util.toYuan(c.getRemainingGuaranteeAmount()),
                            Util.toYuan(c.getRemainingTotalLimit()),
                            c.getCreateByName(),
                            isNull(c.getEffectiveDateTo()) ? "" : c.getEffectiveDateTo().format(ofPattern(NORM_DATE_PATTERN))
                    ));
        }
        // 设置自动列宽（需要单独调用）
        w.autoSizeColumnAll();
        for (int i = 0; i < 16; i++) {
            // 针对中文优化列宽计算
            int chineseWidth = w.getSheet().getColumnWidth(i) * 3 / 2;
            w.setColumnWidth(i, Math.min(chineseWidth, 30));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }


    @SneakyThrows
    private void fundCreditLimit(List resultList) {
        List<FundCreditLimitDetailRSP.LimitDetail> list = (List<FundCreditLimitDetailRSP.LimitDetail>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-授信限额.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("融资编号", "融资金额(元)", "担保融资额(元)", "信用融资额(元)", "占用额度(元)", "占用担保额度(元)", "占用信用额度(元)", "剩余本金(元)", "剩余担保本金(元)", "剩余信用本金(元)", "合同利率(%)", "贷款日", "到期日", "融资状态", "创建人"));

//        w.merge(0, 0, 0, 1, "授信编号", true);
//        w.merge(0, 0, 2, 3, "授信机构", true);
//        w.merge(0, 0, 4, 4, "额度是否可循环", true);
//        w.merge(0, 0, 6, 7, "授信产品", true);
//        w.merge(0, 0, 8, 9, "授信总额", true);
//        w.merge(0, 0, 10, 11, "已使用额度", true);
//        w.merge(0, 0, 12, 13, "剩余授信额度", true);
//        w.merge(0, 0, 14, 15, "剩余总授信额度", true);
//        w.merge(0, 0, 16, 17, "管理人", true);
//        w.merge(0, 0, 18, 19, "授信到期日", true);

        for (FundCreditLimitDetailRSP.LimitDetail c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getFinancingCode(),
                            Util.toYuan(c.getFinancingAmount()),
                            Util.toYuan(c.getGuaranteeFinancingAmount()),
                            Util.toYuan(c.getCreditFinancingAmount()),
                            Util.toYuan(c.getUsedTotalCreditAmount()),
                            Util.toYuan(c.getUsedGuaranteeAmount()),
                            Util.toYuan(c.getUsedCreditAmount()),
                            Util.toYuan(c.getRemainingAmount()),
                            Util.toYuan(c.getRemainingGuaranteeAmount()),
                            Util.toYuan(c.getRemainingCreditAmount()),
                            isNull(c.getContractRate()) ? "" : Util.toYuan(c.getContractRate()) + "%",
                            c.getBorrowDate(),
                            c.getExpireDate(),
                            FundFinancingStatusEnum.valueOf(c.getFinancingStatus()).display(),
                            c.getCreateByName()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void fundGuaranteeAgencyLimit(List resultList) {
        List<FundGuaranteeLimitDetailRSP.LimitDetail> list = (List<FundGuaranteeLimitDetailRSP.LimitDetail>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-担保限额.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("融资编号", "融资机构","融资金额(元)", "担保融资额(元)", "信用融资额(元)", "剩余本金(元)", "剩余担保本金(元)", "剩余信用本金(元)", "贷款日", "到期日", "融资状态", "创建人"));

//        w.merge(0, 1, 0, 0, "授信编号", true);
//        w.merge(0, 1, 1, 1, "授信机构", true);
//        w.merge(0, 1, 2, 2, "额度是否可循环", true);
//        w.merge(0, 1, 3, 3, "授信产品", true);
//        w.merge(0, 0, 4, 6, "授信总额", true);
//        w.merge(0, 0, 7, 9, "已使用额度", true);
//        w.merge(0, 0, 10, 12, "剩余授信额度", true);
//        w.merge(0, 1, 13, 13, "剩余总授信额度", true);
//        w.merge(0, 1, 14, 14, "管理人", true);
//        w.merge(0, 1, 15, 15, "授信到期日", true);

        for (FundGuaranteeLimitDetailRSP.LimitDetail c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getFinancingCode(),
                            c.getOrganizationName(),
                            Util.toYuan(c.getFinancingAmount()),
                            Util.toYuan(c.getGuaranteeFinancingAmount()),
                            Util.toYuan(c.getCreditFinancingAmount()),
                            Util.toYuan(c.getRemainingAmount()),
                            Util.toYuan(c.getRemainingGuaranteeAmount()),
                            Util.toYuan(c.getRemainingCreditAmount()),
                            c.getBorrowDate(),
                            c.getExpireDate(),
                            FundFinancingStatusEnum.valueOf(c.getFinancingStatus()).display(),
                            c.getCreateByName()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void fundOrg(List resultList) {
        List<FundOrganizationListRSP> list = (List<FundOrganizationListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("资金管理-机构管理.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("机构名称", "机构简称", "机构编号", "机构类型", "联系人", "创建时间", "更新时间", "创建人"));
        for (FundOrganizationListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getOrganizationName(),
                            c.getAbbreviation(),
                            c.getOrganizationCode(),
                            OrganizationType.valueOf(c.getOrganizationType()).display(),
                            c.getContactName(),
                            c.getCreateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                            c.getUpdateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                            c.getCreateByName()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void contract(List resultList) {
        List<ContractBaseInfoListRSP> list = (List<ContractBaseInfoListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("合同列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("合同编号", "项目名称", "业务类型", "合同金额（元）", "业务部门", "项目主办", "项目协办", "客户名称", "合同状态", "合同流状态", "创建时间", "更新时间"));
        for (ContractBaseInfoListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getContractCode(),
                            c.getProjName(),
                            isNull(ProjectBizType.of(c.getBizType())) ? c.getBizType() : ProjectBizType.of(c.getBizType()).display,
                            isNull(c.getApplyCreditAmount()) ? "" : Util.toYuan(c.getApplyCreditAmount()),
                            c.getBizDeptName(),
                            c.getProjSponsorUserName(),
                            isEmpty(c.getProjCosponsorUserNames()) ?
                                    "" : join("，", c.getProjCosponsorUserNames().stream().filter(StrUtil::isNotBlank).collect(Collectors.toList())), c.getClientName(),
                            isNull(ContractStatus.of(c.getContractStatus())) ? c.getContractStatus() : ContractStatus.of(c.getContractStatus()).display,
                            isNull(ContractProcessStatusEnum.of(c.getContractProcessStatus())) ? c.getContractProcessStatus() : ContractProcessStatusEnum.of(c.getContractProcessStatus()).display,
                            c.getCreateTime(),
                            c.getUpdateTime()
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void projPricing(List resultList) {
        List<ProjPricingBaseInfoListRSP> list = (List<ProjPricingBaseInfoListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("定价列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("项目名称", "项目编号", "业务类型", "业务部门", "项目主办", "项目协办", "申报授信金额（万元）", "客户名称", "审批状态", "定价状态",
                "创建时间", "更新时间"));
        for (ProjPricingBaseInfoListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getProjName(),
                            c.getProjCode(),
                            isNull(ProjectBizType.of(c.getBizType())) ? c.getBizType() : ProjectBizType.of(c.getBizType()).display,
                            c.getBizDeptName(),
                            c.getProjSponsorUserName(),
                            isEmpty(c.getProjCosponsorUserNames()) ?
                                    "" : join("，", c.getProjCosponsorUserNames().stream().filter(StrUtil::isNotBlank).collect(Collectors.toList())), isNull(c.getDeclaredAmount()) ? "" : Util.toWanYuan(c.getDeclaredAmount()),
                            c.getClientName(),
                            isNull(ProjProcessState.of(c.getProjPricingProcessStatus())) ? c.getProjPricingProcessStatus() : ProjProcessState.of(c.getProjPricingProcessStatus()).display,
                            isNull(ProjItemStatus.of(c.getProjPricingStatus())) ? c.getProjPricingStatus() : ProjItemStatus.of(c.getProjPricingStatus()).display,
                            c.getCreateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                            c.getUpdateTime().format(ofPattern(NORM_DATETIME_PATTERN))
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void projReview(List resultList) {
        List<ProjReviewBaseInfoListRSP> list = (List<ProjReviewBaseInfoListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("评审列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("项目名称", "项目编号", "业务类型", "业务部门", "项目主办", "项目协办", "申报授信金额（万元）", "客户名称", "审批状态", "项目状态",
                "创建时间", "更新时间"));
        for (ProjReviewBaseInfoListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getProjName(),
                            c.getProjCode(),
                            isNull(ProjectBizType.of(c.getBizType())) ? c.getBizType() : ProjectBizType.of(c.getBizType()).display,
                            c.getBizDeptName(),
                            c.getProjSponsorUserName(),
                            isEmpty(c.getProjCosponsorUserNames()) ?
                                    "" : join("，", c.getProjCosponsorUserNames().stream().filter(StrUtil::isNotBlank).collect(Collectors.toList())), isNull(c.getDeclaredAmount()) ? "" : Util.toWanYuan(c.getDeclaredAmount()),
                            c.getClientName(),
                            isNull(ProjProcessState.of(c.getProjReviewProcessStatus())) ? c.getProjReviewProcessStatus() : ProjProcessState.of(c.getProjReviewProcessStatus()).display,
                            isNull(ProjItemStatus.of(c.getProjReviewStatus())) ? c.getProjReviewStatus() : ProjItemStatus.of(c.getProjReviewStatus()).display,
                            c.getCreateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                            c.getUpdateTime().format(ofPattern(NORM_DATETIME_PATTERN))
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    @SneakyThrows
    private void projEstablish(List resultList) {
        List<ProjEstablishBaseInfoListRSP> list = (List<ProjEstablishBaseInfoListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("立项列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("项目名称", "项目编号", "业务类型", "业务部门", "项目主办", "项目协办", "客户名称", "立项状态", "审批状态",
                "创建时间", "更新时间"));
        for (ProjEstablishBaseInfoListRSP c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getProjName(),
                            c.getProjCode(),
                            isNull(ProjectBizType.of(c.getBizType())) ? c.getBizType() : ProjectBizType.of(c.getBizType()).display,
                            c.getBizDeptName(),
                            c.getProjSponsorUserName(),
                            isEmpty(c.getProjCosponsorUserNames()) ?
                                    "" : join("，", c.getProjCosponsorUserNames().stream().filter(StrUtil::isNotBlank).collect(Collectors.toList())),
                            c.getClientName(),
                            isNull(ProjItemStatus.of(c.getProjEstablishStatus())) ? c.getProjEstablishStatus() : ProjItemStatus.of(c.getProjEstablishStatus()).display,
                            isNull(ProjProcessState.of(c.getProjEstablishProcessStatus())) ? c.getProjEstablishProcessStatus() : ProjProcessState.of(c.getProjEstablishProcessStatus()).display,
                            c.getCreateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                            c.getUpdateTime().format(ofPattern(NORM_DATETIME_PATTERN))
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }


    @SneakyThrows
    private void client(List resultList) {
        List<ClientListRSP> list = (List<ClientListRSP>) resultList;
        String downloadFileName = URLEncoder.encode("客户列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("客户名称", "授信金额（万元）", "剩余本金（万元）", "存量风险敞口（万元）", "所属主办", "所属部门", "客户状态", "审批状态",
                "创建时间", "更新时间", "创建人"));
        for (ClientListRSP c : list) {
            w.writeRow(ListUtil.of(
                    c.getClientName(),
                    isNull(c.getApplyCreditAmount()) ? "" : Util.toYuan(c.getApplyCreditAmount()),
                    isNull(c.getLastPrincipal()) ? "" : Util.toYuan(c.getLastPrincipal()),
                    isNull(c.getStockRiskExposure()) ? "" : Util.toYuan(c.getStockRiskExposure()),
                    c.getBelongSponsorName(),
                    c.getBelongDeptName(),
                    clientStatusDisplay(c),
                    c.getProcessStatusName(),
                    c.getCreateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                    c.getUpdateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                    c.getCreatorName()
            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

    private String clientStatusDisplay(ClientListRSP c) {
        if (Objects.equals(c.getClientStatus(), ClientStatus.NEW.name()) && Objects.equals(c.getIsReleased(), YesOrNoNumberEnum.YES.getCode())) {
            return ClientStatus.RELEASE.display;
        }
        return isNull(ClientStatus.of(c.getClientStatus())) ? c.getClientStatus() : ClientStatus.of(c.getClientStatus()).display;
    }

    @SneakyThrows
    private void myAfterLeaseCheckPlan(List resultList) {
        List<AfterLeaseAssetStrategyRSP> list = (List<AfterLeaseAssetStrategyRSP>) resultList;
        String downloadFileName = URLEncoder.encode("资产管理策略列表.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(ListUtil.of("计划名称", "客户名称", "部门", "风险敞口(元)", "投放日期",
                "五级分类", "项目经理", "检查人员",
                "跟进频率", "上次跟进时间", "上次跟进形式",
                "下次跟进时间", "下次跟进形式", "计划状态", "创建时间", "变更时间"));
        for (AfterLeaseAssetStrategyRSP c : list) {
            w.writeRow(ListUtil.of(
                    c.getPlanName(),
                    c.getClientName(),
                    c.getBizDeptName(),
                    isNull(c.getRiskExposure()) ? "" : Util.toYuan(c.getRiskExposure()),
                    c.getPaymentDate(),
                    Optional.ofNullable(AssetClassifyResultEnum.of(c.getClassifyResult())).map(AssetClassifyResultEnum::display).orElse(null),
                    c.getSponsorName(),
                    c.getRiskManagerName(),
                    Optional.ofNullable(AfterLeaseCheckTermEnum.ofTerm(c.getTerm())).map(AfterLeaseCheckTermEnum::display).orElse(null),
                    c.getLastEndDate(),
                    Optional.ofNullable(AfterLeaseCheckWayEnum.find(c.getLastCheckWay())).map(AfterLeaseCheckWayEnum::display).orElse(null),
                    c.getDeadLine(),
                    Optional.ofNullable(AfterLeaseCheckWayEnum.find(c.getCheckWay())).map(AfterLeaseCheckWayEnum::display).orElse(null),
                    Optional.ofNullable(AfterLeaseCheckPlanProcessStatusEnum.of(c.getApprovalStatus())).map(AfterLeaseCheckPlanProcessStatusEnum::display).orElse(null),
                    c.getCreateTime().format(ofPattern(NORM_DATETIME_PATTERN)),
                    c.getUpdateTime().format(ofPattern(NORM_DATETIME_PATTERN))
            ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }


    @SneakyThrows
    private void fundTransfer(List resultList) {
        List<FundTransferListRSP.AccountBalanceDetail> list = (List<FundTransferListRSP.AccountBalanceDetail>) resultList;
        String downloadFileName = URLEncoder.encode("监管户待转资金.xlsx", "UTF-8");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ExcelWriter w = ExcelUtil.getWriter(true);
        w.writeHeadRow(Arrays.asList("日期","开户银行","银行账号","待转余额"));
        for (FundTransferListRSP.AccountBalanceDetail c : list) {
            w.writeRow(
                    ListUtil.of(
                            c.getDate(),
                            c.getAccountBank(),
                            c.getAccountNumber(),
                            Util.toYuan(c.getPendingBalanceAmount())
                    ));
        }
        w.flush(bos, true);
        ServletOutputStream outputStream = response.getOutputStream();
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        outputStream.write(bos.toByteArray());
    }

}
