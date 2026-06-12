package cn.zswltech.mithras.application.orchestration.workflow.process.prepare;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.workflow.process.prepare.ProcessPrepareApplicationService;
import cn.zswltech.mithras.dto.IdPageREQ;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.process.prepare.*;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.job.NextMonthRentNotify;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.workflow.model.CommonProcessPrepare;
import cn.zswltech.mithras.workflow.model.FinancingRepayActualProcessDetail;
import cn.zswltech.mithras.workflow.model.RentCollectionMonthDetail;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.fund.application.financing.model.FundPledgeSupervisedBO;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.workflow.process.prepare.CommonProcessPrepareService;
import cn.zswltech.mithras.workflow.process.prepare.RentCollectionMonthDetailService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.collection.ListUtil.of;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.hutool.core.util.EnumUtil.fromStringQuietly;
import static cn.hutool.core.util.ObjectUtil.notEqual;
import static cn.hutool.extra.spring.SpringUtil.getBean;
import static cn.hutool.json.JSONUtil.toList;
import static cn.zswltech.mithras.foundation.util.Util.toYuan;

/**
 * @author luyi
 */
@Service
public class ProcessPrepareFacade implements ProcessPrepareApplicationService {

    @Resource
    private CommonProcessPrepareService prepareService;
    @Autowired
    private HttpServletResponse response;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private RentCollectionMonthDetailService rentCollectionMonthDetailService;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Autowired
    private NextMonthRentNotify nextMonthRentNotify;

    @Override
    public R<PageR<ProcessPrepareListRSP>> list(ProcessPrepareListREQ req) {
        Page<CommonProcessPrepare> pageData = prepareService.list(req);
        List<ProcessPrepareListRSP> rspList = BeanUtil.copyToList(pageData.getRecords(), ProcessPrepareListRSP.class);
        Set<Long> userIdList = new HashSet<>();
        rspList.forEach(e -> {
            if (isNotBlank(e.getCurrentAssignee())) {
                userIdList.addAll(toList(e.getCurrentAssignee(), Long.class));
            }
        });
        Map<Long, String> idNameMap = getBean(UserService.class).getUserInfoByIds(new ArrayList<>(userIdList)).stream()
                .collect(Collectors.toMap(UserVO::getId, UserVO::getUserName, (v1, v2) -> v1));
        //

        List<Long> paymentIds = rspList.stream().filter(e -> ObjectUtil.equals(e.getProcessTypeName(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name())).map(ProcessPrepareListRSP::getBusinessId).map(Long::valueOf).collect(Collectors.toList());
        Map<Long, Long> paymentBaseInfoMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(paymentIds)) {
            paymentBaseInfoMap.putAll(paymentBaseInfoService.listByIds(paymentIds).stream().collect(Collectors.toMap(PaymentBaseInfo::getId, PaymentBaseInfo::getContractId, (a, b) -> a)));
        }
        for (ProcessPrepareListRSP rsp : rspList) {
            String currentAssignee = rsp.getCurrentAssignee();
            if (isNotBlank(currentAssignee)) {
                List<Long> idList = toList(currentAssignee, Long.class);
                String currentAssigneeNames = idList.stream()
                        .map(idNameMap::get).collect(Collectors.joining("，"));
                rsp.setCurrentAssigneeNames(currentAssigneeNames);
            }
            //
            ProcessModelTypeEnum modelTypeEnum = fromStringQuietly(ProcessModelTypeEnum.class, rsp.getProcessType());
            if (null != modelTypeEnum) {
                rsp.setProcessTypeName(modelTypeEnum.getDisplay());
            }
            //这里业务id不一定是前端需要的，在这里做转换
            if (ObjectUtil.equals(rsp.getProcessType(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name())) {
                rsp.setBusinessId(String.valueOf(paymentBaseInfoMap.get(Long.valueOf(rsp.getBusinessId()))));
            } else {
                if (ObjectUtil.isEmpty(rsp.getBusinessId())) {
                    rsp.setBusinessId(String.valueOf(rsp.getId()));
                }
            }
        }

        /*判断 客户评级更新的待发起是否超期15天  是则需要给超时标记*/
        rspList.forEach(rsp -> rsp.setOvertimeFlag(("RatingClientUpdateFlow".equals(rsp.getProcessType()) && LocalDateTimeUtil.between(rsp.getApplyTime(), LocalDateTime.now(), ChronoUnit.DAYS) > 15) ? 1 : 0));
        //
        return R.ok(
                PageR.of(rspList, pageData.getTotal())
        );
    }

    @Override
    public R<ProcessPrepareDetailRSP> detail(@Valid IdREQ req) {
        CommonProcessPrepare prepare = Optional.ofNullable(prepareService.getById(req.getId())).orElseThrow(() -> new MithrasException(ResultMsg.RECORD_NOT_EXIST));
        ProcessPrepareDetailRSP processPrepareDetailRSP = copyProperties(prepare, ProcessPrepareDetailRSP.class);
        //这里业务id不一定是前端需要的，在这里做转换
        if (ObjectUtil.equals(processPrepareDetailRSP.getProcessType(), ProcessModelTypeEnum.ContractStartRentAutoFlow.name())) {
            PaymentDetailRsp detail = paymentBaseInfoService.detail(Long.valueOf(processPrepareDetailRSP.getBusinessId()));
            if (ObjectUtil.isNotEmpty(detail)) {
                processPrepareDetailRSP.setBusinessId(String.valueOf(detail.getContractId()));
            }
        } else {
            if (ObjectUtil.isEmpty(processPrepareDetailRSP.getBusinessId())) {
                processPrepareDetailRSP.setBusinessId(String.valueOf(processPrepareDetailRSP.getId()));
            }
        }
        return R.ok(processPrepareDetailRSP);
    }

    @Override
    public R<Void> commitPrepare(IdREQ req) {
        prepareService.commit(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> discardPrepare(IdREQ req) {
        prepareService.discard(req.getId());
        return R.ok();
    }

    @Override
    public R<PageR<RentCollectionMonthDetailListRSP>> detailList(IdPageREQ req) {
        Page<RentCollectionMonthDetail> pageData = prepareService.detailList(req);
        //
        List<Long> clientIdList = new ArrayList<>(pageData.getRecords().size());
        List<Long> userIdList = new ArrayList<>(pageData.getRecords().size());
        List<Long> deptIdList = new ArrayList<>(pageData.getRecords().size());
        for (RentCollectionMonthDetail record : pageData.getRecords()) {
            clientIdList.add(record.getClientId());
            userIdList.add(record.getSponsorId());
            deptIdList.add(record.getDeptId());
        }
        Map<Long, String> clientIdNameMap = getBean(Id2NameService.class).clientId2Name(clientIdList);
        Map<Long, String> userIdNameMap = getBean(Id2NameService.class).sysUserId2Name(userIdList);
        Map<Long, String> deptIdNameMap = getBean(Id2NameService.class).deptId2Name(deptIdList);
        //
        List<RentCollectionMonthDetailListRSP> rspList = new ArrayList<>();
        for (RentCollectionMonthDetail record : pageData.getRecords()) {
            RentCollectionMonthDetailListRSP rsp = copyProperties(record, RentCollectionMonthDetailListRSP.class,
                    "repayDate", "rent", "principal", "interest", "bankAccountName", "bankAccountNumber", "bankName");
            FundPledgeSupervisedBO fundPledgeSupervisedBO = fundFinancingPledgeInfoService.getSuperviseAccountNum(rsp.getContractCode());
            String accountNumber = null;
            String accountName = null;
            String accountBank = null;
            //不符合业务需求，先去除
            /*if (fundPledgeSupervisedBO != null) {
                accountBank = fundPledgeSupervisedBO.getAccountBank();
                accountNumber = fundPledgeSupervisedBO.getAccountNumber();
                accountName = fundPledgeSupervisedBO.getAccountName();
            }*/
            if (StringUtils.isBlank(accountNumber)) {
                accountNumber = record.getBankAccountNumber();
            }
            if (StringUtils.isBlank(accountBank)) {
                accountBank = record.getBankName();
            }
            if (StringUtils.isBlank(accountName)) {
                accountName = record.getBankAccountName();
            }
            //返回有变动的值
            String initialJson = record.getInitialData();
            RentCollectionMonthDetail initRecord = JSONUtil.toBean(initialJson, RentCollectionMonthDetail.class);
            //repayDate
            DiffValue diffRepayDate = new DiffValue();
            diffRepayDate.setIsChange(notEqual(record.getRepayDate(), initRecord.getRepayDate()));
            diffRepayDate.setValue(record.getRepayDate());
            rsp.setRepayDate(diffRepayDate);
            //rent
            DiffValue diffRent = new DiffValue();
            diffRent.setIsChange(notEqual(record.getRent(), initRecord.getRent()));
            diffRent.setValue(record.getRent());
            rsp.setRent(diffRent);
            //principal
            DiffValue diffPrincipal = new DiffValue();
            diffPrincipal.setIsChange(notEqual(record.getPrincipal(), initRecord.getPrincipal()));
            diffPrincipal.setValue(record.getPrincipal());
            rsp.setPrincipal(diffPrincipal);
            //interest
            DiffValue diffInterest = new DiffValue();
            diffInterest.setIsChange(notEqual(record.getInterest(), initRecord.getInterest()));
            diffInterest.setValue(record.getInterest());
            rsp.setInterest(diffInterest);
            //bank name
            DiffValue diffBankName = new DiffValue();
            diffBankName.setIsChange(notEqual(accountBank, initRecord.getBankName()));
            diffBankName.setValue(accountBank);
            rsp.setBankName(diffBankName);
            //account name
            DiffValue diffAccountName = new DiffValue();
            diffAccountName.setIsChange(notEqual(accountName, initRecord.getBankAccountName()));
            diffAccountName.setValue(accountName);
            rsp.setBankAccountName(diffAccountName);
            //account number
            DiffValue diffAccountNumber = new DiffValue();
            diffAccountNumber.setIsChange(notEqual(accountNumber, initRecord.getBankAccountNumber()));
            diffAccountNumber.setValue(accountNumber);
            rsp.setBankAccountNumber(diffAccountNumber);
            //

            rsp.setClientName(clientIdNameMap.get(record.getClientId()));
            rsp.setSponsorName(userIdNameMap.get(record.getSponsorId()));
            rsp.setDeptName(deptIdNameMap.get(record.getDeptId()));
            rspList.add(rsp);
        }
        return R.ok(PageR.of(rspList, pageData.getTotal()));
    }

    @Override
    public R<RentCollectionMonthKeyListRSP> keyList(IdPageREQ req) {
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        Page<RentCollectionMonthDetail> pageData = prepareService.detailList(req);
        List<Long> clientIdList = new ArrayList<>(pageData.getRecords().size());
        List<Long> userIdList = new ArrayList<>(pageData.getRecords().size());
        List<Long> deptIdList = new ArrayList<>(pageData.getRecords().size());
        for (RentCollectionMonthDetail record : pageData.getRecords()) {
            clientIdList.add(record.getClientId());
            userIdList.add(record.getSponsorId());
            deptIdList.add(record.getDeptId());
        }
        Map<Long, String> deptIdNameMap = getBean(Id2NameService.class).deptId2Name(deptIdList);
        RentCollectionMonthKeyListRSP res = new RentCollectionMonthKeyListRSP();
        res.setClientCount(clientIdList.size());
        res.setDeptId(deptIdList.get(0));
        res.setDeptName(deptIdNameMap.get(deptIdList.get(0)));
        Long totalRent = 0L;
        for (RentCollectionMonthDetail record : pageData.getRecords()) {
            totalRent += record.getRent();
        }
        res.setTotalRent(totalRent);
        return R.ok(res);
    }

    @Override
    @SneakyThrows
    public void downloadList(IdREQ req) {
        IdPageREQ idPageREQ = new IdPageREQ();
        idPageREQ.setId(req.getId());
        idPageREQ.setPageSize(1000);
        List<RentCollectionMonthDetailListRSP> rspList = detailList(idPageREQ).getData().getList();
        if (!rspList.isEmpty()) {
            String deptName = rspList.get(0).getDeptName();
            Integer month = rspList.get(0).getMonth();
            String downloadFileName = URLEncoder.encode(String.format("%s-%s月-租金支付-全量清单.xlsx", deptName, month), "UTF-8");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ExcelWriter w = ExcelUtil.getWriter(true);
            w.writeHeadRow(of("客户名称", "主办", "合同编号", "期数", "租金支付日", "租金", "租赁成本", "租赁利息", "户名", "开户行", "账号"));
            for (RentCollectionMonthDetailListRSP rsp : rspList) {
                w.writeRow(of(
                        rsp.getClientName(),
                        rsp.getSponsorName(),
                        rsp.getContractCode(),
                        rsp.getPhase(),
                        rsp.getRepayDate().getValue(),
                        toYuan((Long) rsp.getRent().getValue()),
                        toYuan((Long) rsp.getPrincipal().getValue()),
                        toYuan((Long) rsp.getInterest().getValue()),
                        rsp.getBankAccountName().getValue(),
                        rsp.getBankName().getValue(),
                        rsp.getBankAccountNumber().getValue()
                ));
            }
            w.flush(bos, true);
            ServletOutputStream outputStream = response.getOutputStream();
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
            outputStream.write(bos.toByteArray());
        }
    }

    @Override
    public byte[] previewNotify(IdREQ req) {
        return prepareService.previewNotify(req.getId());
    }

    @SneakyThrows
    @Override
    public void downloadPreview(IdREQ req) {
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租金支付通知书.docx", StandardCharsets.UTF_8.name()));
        prepareService.downloadPreview(response.getOutputStream(), req.getId());

    }

    @SneakyThrows
    @Override
    public void downloadBatch(IdREQ req) {
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" +
                URLEncoder.encode("租金支付通知书.zip", StandardCharsets.UTF_8.name()));
        prepareService.downloadBatch(response.getOutputStream(), req.getId());

    }

    @Override
    public R<Void> refreshRentInfo(IdREQ req) {
        prepareService.refreshRentInfo(req.getId());
        return R.ok();
    }

    @Override
    public R<Void> generateNextMonthRentNotify() {
        nextMonthRentNotify.runJob(null, null, null);
        return R.ok();
    }

    @Override
    public R<Void> modifyBankAccountInfo(@Valid RentCollectionMonthModifyAccountREQ req) {
        rentCollectionMonthDetailService.modifyBankInfo(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinancingRepayActualProcessDetailListRSP>> repayDetailList(IdPageREQ req) {
        Page<FinancingRepayActualProcessDetail> pageData = prepareService.repayDetailList(req);
        List<FinancingRepayActualProcessDetailListRSP> rspList = new ArrayList<>();
        for (FinancingRepayActualProcessDetail record : pageData.getRecords()) {
            FinancingRepayActualProcessDetailListRSP processDetail = BeanUtil.copyProperties(record, FinancingRepayActualProcessDetailListRSP.class);
            rspList.add(processDetail);
        }
        return R.ok(PageR.of(rspList, pageData.getTotal()));
    }
}
