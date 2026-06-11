package cn.zswltech.mithras.application.orchestration.leaseholdproperty.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.document.config.OcrConfigProperties;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.leaseholdproperty.enums.JudgeEnum;
import cn.zswltech.mithras.leaseholdproperty.enums.*;
import cn.zswltech.mithras.leaseholdproperty.excel.exporter.LeaseVatInvoiceExcelManagerExporter;
import cn.zswltech.mithras.leaseholdproperty.excel.model.LeaseVatInvoiceExcelModel;
import cn.zswltech.mithras.leaseholdproperty.mapper.LeaseItemVatInvoiceMapper;
import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemListRowData;
import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemVatInvoice;
import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemVatInvoiceProduct;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemListRowDataService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemVatInvoiceProductService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemVatInvoiceService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.third.util.OcrServiceUtil;
import cn.zswltech.sleipnir.toolkit.OcrUtil;
import cn.zswltech.sleipnir.toolkit.request.OcrFileInfo;
import cn.zswltech.sleipnir.toolkit.request.VatInvoiceRequest;
import cn.zswltech.sleipnir.toolkit.request.VehicleSalesInvoiceRequest;
import cn.zswltech.sleipnir.toolkit.request.VerifyVatRequest;
import cn.zswltech.sleipnir.toolkit.response.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yupengfei
 * @date 2024/5/9 15:27
 */
@Service
public class LeaseItemVatInvoiceServiceImpl extends ServiceImpl<LeaseItemVatInvoiceMapper, LeaseItemVatInvoice> implements LeaseItemVatInvoiceService {

    @Resource
    private LeaseItemVatInvoiceMapper leaseItemVatInvoiceMapper;

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private LeaseItemVatInvoiceProductService leaseItemVatInvoiceProductService;

    @Resource
    private OcrConfigProperties ocrConfigProperties;

    @Resource
    private LeaseItemListRowDataService leaseItemListRowDataService;

    @Resource
    private LeaseVatInvoiceExcelManagerExporter vatInvoiceExcelManagerExporter;

    @Resource
    protected FlowTaskApiService flowTaskApiService;

    @Resource
    @Qualifier("ocrThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> vatInvoiceUpload(LeaseVatInvoiceUploadREQ req) {
        Long userId = AccountUtil.getLoginInfo().getId();
        if (req.getFiles().size() > 50) {
            throw new MithrasException("最多支持同时上传50个文件");
        }
        List<Future<?>> futures = new ArrayList<>();
        for (MultipartFile file : req.getFiles()) {
            //文件为pdf和docx格式要处理一下，拆分成多个文件流，每个文流就是一张发票
            List<OcrFileInfo> ocrFileInfoList = OcrServiceUtil.getOcrFileInfoList(file);
            for (OcrFileInfo fileInfo : ocrFileInfoList) {
                //解析发票并保存
                Future<?> future = threadPoolTaskExecutor.submit(() -> identifyAndSave(fileInfo, req, userId));
                futures.add(future);
            }
        }
        for (Future<?> future : futures) {
            try {
                future.get(); // 阻塞等待任务完成，并获取结果
            } catch (InterruptedException | ExecutionException e) {
                //发生异常不能影响到后续操作
                log.warn("发票保存失败：" + e.getMessage());
            }
        }
        return R.ok();
    }

    @Override
    public R<Void> anewUpload(LeaseVatInvoiceUploadREQ req) {
        if (Objects.isNull(req.getVatInvoiceId())) {
            throw new MithrasException("发票id不能为空");
        }
        //删除发票信息
        this.update(Wrappers.<LeaseItemVatInvoice>lambdaUpdate()
                .eq(LeaseItemVatInvoice::getId, req.getVatInvoiceId())
                .eq(LeaseItemVatInvoice::getDeleted, "0")
                .set(LeaseItemVatInvoice::getOperation, LeaseOperateEnum.DELETE.name())
                .set(LeaseItemVatInvoice::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVatInvoice::getUpdateTime, LocalDateTime.now())
                .set(LeaseItemVatInvoice::getDeleted, "1"));
        //删除发票产品信息
        leaseItemVatInvoiceProductService.update(Wrappers.<LeaseItemVatInvoiceProduct>lambdaUpdate()
                .eq(LeaseItemVatInvoiceProduct::getInvoiceId, req.getVatInvoiceId())
                .eq(LeaseItemVatInvoiceProduct::getDeleted, "0")
                .set(LeaseItemVatInvoiceProduct::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVatInvoiceProduct::getUpdateTime, LocalDateTime.now())
                .set(LeaseItemVatInvoiceProduct::getDeleted, "1"));

        //文件信息
        MultipartFile multipartFile = req.getFiles().get(0);
        List<OcrFileInfo> ocrFileInfoList = OcrServiceUtil.getOcrFileInfoList(multipartFile);
        //对发票文件进行识别并保存
        identifyAndSave(ocrFileInfoList.get(0), req, AccountUtil.getLoginInfo().getId());

        return R.ok();
    }

    @Override
    public R<PageR<LeaseVatInvoiceListRSP>> queryVatInvoiceList(LeaseVatInvoiceQueryREQ req) {
        //分页
        Page<LeaseItemVatInvoice> page = PageHelper.startPage(req.getPage(), req.getPageSize());
        List<LeaseItemVatInvoice> leaseItemVatInvoices = leaseItemVatInvoiceMapper.queryVatInvoiceList(req);

        //为空返回
        if (CollectionUtils.isEmpty(leaseItemVatInvoices)) {
            return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }

        //封装返回数据
        List<LeaseVatInvoiceListRSP> rspList = leaseItemVatInvoices.stream()
                .map(this::convertToLeaseVatInvoiceListRSP)
                .collect(Collectors.toList());

        //将重复的发票号码筛选出来
        List<String> invoiceNoList = leaseItemVatInvoiceMapper.queryRepeatInvoiceNo(req.getLeaseholdId());

        //不为空，则将重复的发票打上标识
        if (CollectionUtils.isNotEmpty(invoiceNoList)) {
            rspList.forEach(leaseVatInvoiceListRSP -> {
                if (invoiceNoList.contains(leaseVatInvoiceListRSP.getInvoiceNo())) {
                    leaseVatInvoiceListRSP.setIsRepeat(true);
                }
            });
        }

        return R.ok(PageR.of(rspList, page.getTotal(), page.getPageNum(), page.getPageSize()));
    }

    @Override
    public R<Void> invoiceRemove(LeaseVatInvoiceRemoveREQ req) {
        //REPLACE 表示是替换发票，要全部删除之前的发票数据
        if (StringUtils.isNotBlank(req.getOperateType())
                && LeaseOperateEnum.REPLACE.name().equals(req.getOperateType())
                && Objects.nonNull(req.getLeaseholdId())) {
            deleteAll(req.getLeaseholdId());
            return R.ok();
        }

        //逻辑删除发票
        Wrapper<LeaseItemVatInvoice> vatInvoiceUpdateWrapper = Wrappers.<LeaseItemVatInvoice>lambdaUpdate()
                .in(LeaseItemVatInvoice::getId, req.getVatInvoiceIds())
                .set(LeaseItemVatInvoice::getDeleted, "1")
                .set(LeaseItemVatInvoice::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVatInvoice::getUpdateTime, LocalDateTime.now())
                .set(LeaseItemVatInvoice::getOperation, LeaseOperateEnum.DELETE.name());
        this.update(vatInvoiceUpdateWrapper);

        //逻辑删除发票产品
        Wrapper<LeaseItemVatInvoiceProduct> vatInvoiceProductUpdateWrapper = Wrappers.<LeaseItemVatInvoiceProduct>lambdaUpdate()
                .in(LeaseItemVatInvoiceProduct::getInvoiceId, req.getVatInvoiceIds())
                .set(LeaseItemVatInvoiceProduct::getDeleted, "1")
                .set(LeaseItemVatInvoiceProduct::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVatInvoiceProduct::getUpdateTime, LocalDateTime.now());
        leaseItemVatInvoiceProductService.update(vatInvoiceProductUpdateWrapper);

        return R.ok();
    }

    @Override
    public R<Void> invoiceUpdate(LeaseVatInvoiceUpdateREQ req) {
        //动态批量修改发票信息
        LocalDateTime updateTime = LocalDateTime.now();
        Wrapper<LeaseItemVatInvoice> vatInvoiceUpdateWrapper = Wrappers.<LeaseItemVatInvoice>lambdaUpdate()
                .set(StringUtils.isNotBlank(req.getInvoiceNo()), LeaseItemVatInvoice::getInvoiceNo, req.getInvoiceNo())
                .set(StringUtils.isNotBlank(req.getInvoicePayerName()), LeaseItemVatInvoice::getInvoicePayerName, req.getInvoicePayerName())
                .set(StringUtils.isNotBlank(req.getInvoiceSellerName()), LeaseItemVatInvoice::getInvoiceSellerName, req.getInvoiceSellerName())
                .set(Objects.nonNull(req.getInvoiceIssueDate()), LeaseItemVatInvoice::getInvoiceIssueDate, req.getInvoiceIssueDate())
                .set(LeaseItemVatInvoice::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVatInvoice::getUpdateTime, updateTime)
                .set(LeaseItemVatInvoice::getOperation, LeaseOperateEnum.UPDATE.name())
                .eq(LeaseItemVatInvoice::getDeleted, "0")
                .in(LeaseItemVatInvoice::getId, req.getVatInvoiceIds());
        this.update(vatInvoiceUpdateWrapper);

        //动态批量修改发票产品信息
        Wrapper<LeaseItemVatInvoiceProduct> vatInvoiceProductUpdateWrapper = Wrappers.<LeaseItemVatInvoiceProduct>lambdaUpdate()
                .set(StringUtils.isNotBlank(req.getInvoiceGoods()), LeaseItemVatInvoiceProduct::getInvoiceGoods, req.getInvoiceGoods())
                .set(StringUtils.isNotBlank(req.getInvoicePlateSpecific()), LeaseItemVatInvoiceProduct::getInvoicePlateSpecific, req.getInvoicePlateSpecific())
                .set(StringUtils.isNotBlank(req.getInvoiceElectransUnit()), LeaseItemVatInvoiceProduct::getInvoiceElectransUnit, req.getInvoiceElectransUnit())
                .set(LeaseItemVatInvoiceProduct::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVatInvoiceProduct::getUpdateTime, updateTime)
                .eq(LeaseItemVatInvoiceProduct::getDeleted, "0")
                .in(LeaseItemVatInvoiceProduct::getInvoiceId, req.getVatInvoiceIds());
        leaseItemVatInvoiceProductService.update(vatInvoiceProductUpdateWrapper);

        return R.ok();
    }

    @Override
    public R<LeaseVatInvoiceCountRSP> invoiceCount(LeaseItemIdREQ req) {
        //根据租赁物id获取发票全部的信息
        List<LeaseItemVatInvoice> vatInvoiceList = getLeaseItemVatInvoices(req.getLeaseholdId());
        //为空返回
        if (CollectionUtils.isEmpty(vatInvoiceList)) {
            return R.ok(new LeaseVatInvoiceCountRSP());
        }
        //统计发票各个状态的值
        Map<String, Long> invoiceCountMap = vatInvoiceList.stream()
                .map(LeaseItemVatInvoice::getVerifyResult)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        //本次识别总数量
        Long totalNum = (long) vatInvoiceList.size();
        //识别失败数量
        Long failNum = Optional.ofNullable(invoiceCountMap.get(LeaseFileOCRStatusEnum.IDENTIFY_FAILED.name())).orElse(0L);
        //识别成功数量
        Long succeedNum = totalNum - failNum;
        //验真成功数量
        Long passNum = Optional.ofNullable(invoiceCountMap.get(LeaseFileOCRStatusEnum.VERIFICATION_PASSED.name())).orElse(0L);
        //验真不通过
        Long noPassNum = Optional.ofNullable(invoiceCountMap.get(LeaseFileOCRStatusEnum.VERIFICATION_NOT_PASSED.name())).orElse(0L);
        //封装数据
        LeaseVatInvoiceCountRSP rsp = new LeaseVatInvoiceCountRSP()
                .setFail(failNum).setPass(passNum).setNoPass(noPassNum).setSucceed(succeedNum).setTotal(totalNum);

        //是否支持锁定
        BusinessModuleEnum businessModule = BusinessModuleEnum.LEASE_TEXT;
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(String.valueOf(req.getLeaseholdId()));
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setModelKeyList(businessModule.getModelKeyList());
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(processPageReq).getContents()
                .stream().findFirst().orElse(null);
        rsp.setCanLock(false);
        if (!Objects.isNull(processResp)) {
            String[] curTaskActivityIds = processResp.getCurTaskActivityIds().split(",");
            if (curTaskActivityIds.length > 0
                    && ("operationManagement").equalsIgnoreCase(curTaskActivityIds[0])) {
                rsp.setCanLock(true);
            }
        }

        return R.ok(rsp);
    }

    @Override
    public R<String> amountCheckout(LeaseItemIdREQ req) {
        //计算租赁物清单账面原值（元）合计值
        List<LeaseItemListRowData> leaseItemListRowData = leaseItemListRowDataService.list(Wrappers.<LeaseItemListRowData>lambdaQuery()
                .eq(LeaseItemListRowData::getLeaseItemInfoId, req.getLeaseholdId()));
        double totalOriginalValues = 0D;
        if (CollectionUtils.isNotEmpty(leaseItemListRowData)) {
            for (LeaseItemListRowData rowData : leaseItemListRowData) {
                totalOriginalValues += extractedTotalOriginalValues(rowData.getRowData());
            }
        }

        //计算租赁物发票全部含税金额
        List<LeaseItemVatInvoice> leaseItemVatInvoices = getLeaseItemVatInvoices(req.getLeaseholdId());
        double invoiceTotalCoverTaxDigits = 0D;
        if (CollectionUtils.isNotEmpty(leaseItemVatInvoices)) {
            for (LeaseItemVatInvoice itemVatInvoice : leaseItemVatInvoices) {
                if (StringUtils.isNotBlank(itemVatInvoice.getInvoiceTotalCoverTaxDigits())) {
                    try {
                        double number = Double.parseDouble(itemVatInvoice.getInvoiceTotalCoverTaxDigits());
                        invoiceTotalCoverTaxDigits += number;
                    } catch (NumberFormatException e) {
                        log.warn("发票为：" + JSONUtil.toJsonStr(itemVatInvoice) + "数值转换失败:" + e.getMessage());
                    }
                }
            }
        }

        //比较金额，返回结果
        if (invoiceTotalCoverTaxDigits < totalOriginalValues) {
            return R.ok("大于");
        }
        if (invoiceTotalCoverTaxDigits > totalOriginalValues) {
            return R.ok("小于");
        }
        return R.ok("等于");
    }

    @Override
    public R<Void> retest(LeaseVatInvoiceRetestREQ req) {
        LeaseItemVatInvoice vatInvoice = new LeaseItemVatInvoice();
        vatInvoice.setId(req.getInvoiceId());
        vatInvoice.setInvoiceNo(req.getInvoiceNo());
        vatInvoice.setInvoiceDaima(req.getInvoiceCode());
        vatInvoice.setInvoiceIssueDate(req.getInvoiceDate());
        vatInvoice.setInvoiceCorrectCode(req.getVerifyCode());
        vatInvoice.setInvoiceType(req.getInvoiceType());
        BigDecimal invoiceSum = NumberUtil.div(req.getInvoiceSum(), GlobalConstants.MONEY_MULTIPLE, 2, RoundingMode.HALF_UP);
        //根据不同的发票类型传入不同的金额
        if (LeaseVatInvoiceTypeEnum.VAT_SPECIAL_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.VAT_ELECTRONIC_SPECIAL_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.MOTOR_VEHICLE_SALE_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())) {
            //不含税金额
            vatInvoice.setInvoiceTaxTotal(String.valueOf(invoiceSum));
        }
        if (LeaseVatInvoiceTypeEnum.VAT_ELECTRONIC_SPECIAL_INVOICE_NEW.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.VAT_ELECTRONIC_INVOICE_NEW.getFieldName().equals(vatInvoice.getInvoiceType())) {
            //含税金额
            vatInvoice.setInvoiceTotalCoverTaxDigits(String.valueOf(invoiceSum));
        }

        //对发票进行验真
        VerifyVatResponse verifyVatResponse = getVerifyVatRequest(vatInvoice);
        //发票状态
        LeaseVatInvoiceStatusEnum statusEnum = LeaseVatInvoiceStatusEnum.ofByFieldName(verifyVatResponse.getInvalidMark());
        if (statusEnum != null) {
            vatInvoice.setStatus(statusEnum.name());
        }
        //验真结果
        if (LeaseVatInvoiceStatusEnum.EFFECTIVE.getFieldName().equals(verifyVatResponse.getInvalidMark())) {
            vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.VERIFICATION_PASSED.name());
        } else {
            vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.VERIFICATION_NOT_PASSED.name());
        }

        this.updateById(vatInvoice);

        return R.ok();
    }

    @Override
    public R<Void> exportExcel(ServletOutputStream outputStream, LeaseVatInvoiceQueryREQ req) {
        //查询条件
        Wrapper<LeaseItemVatInvoice> vatInvoiceQueryWrapper = Wrappers.<LeaseItemVatInvoice>lambdaQuery()
                .eq(LeaseItemVatInvoice::getLeaseItemInfoId, req.getLeaseholdId())
                .eq(StringUtils.isNotBlank(req.getInvoiceSellerName()), LeaseItemVatInvoice::getInvoiceSellerName, req.getInvoiceSellerName())
                .eq(StringUtils.isNotBlank(req.getInvoicePayerName()), LeaseItemVatInvoice::getInvoicePayerName, req.getInvoicePayerName())
                .between((Objects.nonNull(req.getInvoiceIssueDateFrom()) && Objects.nonNull(req.getInvoiceIssueDateTo())), LeaseItemVatInvoice::getInvoiceIssueDate, req.getInvoiceIssueDateFrom(), req.getInvoiceIssueDateTo())
                .eq(StringUtils.isNotBlank(req.getVerifyResult()), LeaseItemVatInvoice::getVerifyResult, req.getVerifyResult())
                .eq(StringUtils.isNotBlank(req.getStatus()), LeaseItemVatInvoice::getStatus, req.getStatus())
                .in(CollectionUtils.isNotEmpty(req.getVatInvoiceIds()), LeaseItemVatInvoice::getId, req.getVatInvoiceIds())
                .eq(LeaseItemVatInvoice::getDeleted, "0");

        List<LeaseItemVatInvoice> vatInvoiceList = this.list(vatInvoiceQueryWrapper);
        if (CollUtil.isEmpty(vatInvoiceList)) {
            throw new MithrasException("不存在数据，导出失败");
        }

        List<LeaseVatInvoiceExcelModel> invoiceExcelModelList = new ArrayList<>();

        //发票信息集合 key - id ,value - 发票信息
        Map<Long, LeaseItemVatInvoice> vatInvoiceMap = vatInvoiceList.stream().collect(Collectors.toMap(LeaseItemVatInvoice::getId, Function.identity()));

        List<LeaseItemVatInvoiceProduct> vatInvoiceProductList = leaseItemVatInvoiceProductService.list(Wrappers.<LeaseItemVatInvoiceProduct>lambdaQuery()
                .in(LeaseItemVatInvoiceProduct::getInvoiceId, vatInvoiceMap.keySet()).eq(LeaseItemVatInvoiceProduct::getDeleted, "0"));
        if (CollectionUtils.isEmpty(vatInvoiceProductList)) {
            invoiceExcelModelList = vatInvoiceMap.values().stream().map(vatInvoice -> getVatInvoiceExcelModel(vatInvoice, null)).collect(Collectors.toList());
        } else {
            //发票货物信息 key - 发票id , value - 对应的发票信息集合
            Map<Long, List<LeaseItemVatInvoiceProduct>> vatInvoiceProductMap = vatInvoiceProductList.stream().collect(Collectors.groupingBy(LeaseItemVatInvoiceProduct::getInvoiceId));

            for (Long vatInvoiceId : vatInvoiceMap.keySet()) {
                List<LeaseItemVatInvoiceProduct> invoiceProductList = vatInvoiceProductMap.get(vatInvoiceId);
                if (CollectionUtils.isEmpty(invoiceProductList)) {
                    LeaseVatInvoiceExcelModel invoiceExcelModel = getVatInvoiceExcelModel(vatInvoiceMap.get(vatInvoiceId), null);
                    invoiceExcelModelList.add(invoiceExcelModel);
                } else {
                    for (LeaseItemVatInvoiceProduct invoiceProduct : invoiceProductList) {
                        if (Objects.nonNull(invoiceProduct)) {
                            LeaseVatInvoiceExcelModel invoiceExcelModel = getVatInvoiceExcelModel(vatInvoiceMap.get(vatInvoiceId), invoiceProduct);
                            invoiceExcelModelList.add(invoiceExcelModel);
                        }
                    }
                }
            }
        }

        vatInvoiceExcelManagerExporter.exportExcel(invoiceExcelModelList, outputStream);
        return R.ok();
    }

    @Override
    public R<Void> locked(LeaseVatInvoiceLockREQ req) {
        this.update(Wrappers.<LeaseItemVatInvoice>lambdaUpdate()
                .eq(LeaseItemVatInvoice::getDeleted, "0")
                .in(CollectionUtils.isNotEmpty(req.getVatInvoiceIds()), LeaseItemVatInvoice::getId, req.getVatInvoiceIds())
                .set(LeaseItemVatInvoice::getLocked, req.getIsLocked()));

        return R.ok();
    }

    @Override
    public List<String> getLockedFileName(Long leaseholdId, String operateType) {
        LambdaQueryWrapper<LeaseItemVatInvoice> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(LeaseItemVatInvoice::getLeaseItemInfoId, leaseholdId)
                .eq(LeaseItemVatInvoice::getDeleted, "0");
        if (LeaseOperateEnum.REPLACE.name().equalsIgnoreCase(operateType)) {
            queryWrapper.eq(LeaseItemVatInvoice::getLocked, "1");
        }
        List<LeaseItemVatInvoice> itemVatInvoices = this.list(queryWrapper);
        return itemVatInvoices.stream().map(LeaseItemVatInvoice::getFileName).collect(Collectors.toList());
    }

    /**
     * 解析ocr识别发票返回数据
     *
     * @param ocrFileInfo 发票信息
     * @param req         请求参数
     * @param userId      操作人id
     */
    private void identifyAndSave(OcrFileInfo ocrFileInfo, LeaseVatInvoiceUploadREQ req, Long userId) {
        LeaseItemVatInvoice vatInvoice = new LeaseItemVatInvoice();

        //1.为机动车类型的发票识别
        if (LeaseVatInvoiceTypeEnum.MOTOR_VEHICLE_SALE_INVOICE.name().equals(req.getInvoiceType())) {
            VehicleSalesInvoiceResponse salesInvoiceResponse = recognizeVehicleSalesInvoice(ocrFileInfo);
            log.warn("机动车购车发票识别返回状态码：" + salesInvoiceResponse.getCode());
            Map<String, String> basicItemMap = new HashMap<>();
            if (Objects.nonNull(salesInvoiceResponse.getCode())
                    && salesInvoiceResponse.getCode() == 200
                    && Objects.nonNull(salesInvoiceResponse.getVehicleSalesInvoiceResult())
                    && CollectionUtils.isNotEmpty(salesInvoiceResponse.getVehicleSalesInvoiceResult().getItemList())) {
                //解析机动车类型的发票
                //key - 字段名称 ，value - 值
                basicItemMap = salesInvoiceResponse.getVehicleSalesInvoiceResult().getItemList().stream().collect(Collectors.toMap(BasicItem::getKey, BasicItem::getValue));
                this.getSalesInvoice(basicItemMap, salesInvoiceResponse.getVehicleSalesInvoiceResult().getType(), vatInvoice);
                //对发票进行验真
                this.verifyInvoice(vatInvoice);
            } else {
                vatInvoice.setNote("识别失败，请重新上传发票或修改");
                vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.IDENTIFY_FAILED.name());
            }
            saveInvoice(ocrFileInfo, req, vatInvoice, userId);

            if (CollectionUtils.isNotEmpty(basicItemMap)) {
                //保存发票产品信息
                LeaseItemVatInvoiceProduct vatInvoiceProduct = new LeaseItemVatInvoiceProduct();
                vatInvoiceProduct.setCreateBy(userId);
                vatInvoiceProduct.setUpdateBy(userId);
                vatInvoiceProduct.setInvoiceId(vatInvoice.getId());
                //车架号码
                vatInvoiceProduct.setVehicleInvoiceCarVin(basicItemMap.get("vehicle_invoice_car_vin"));
                //货物或服务名称
                vatInvoiceProduct.setInvoiceGoods(basicItemMap.get("vehicle_invoice_vehicle_type"));
                vatInvoiceProduct.setInvoiceElectransUnit("辆");
                vatInvoiceProduct.setInvoiceElectransQuantity("1");
                vatInvoiceProduct.setInvoiceTaxRate(basicItemMap.get("vehicle_invoice_tax_rate"));
                if (StringUtils.isNotBlank(basicItemMap.get("vehicle_invoice_tax_amount")) && StringUtils.isNotBlank(basicItemMap.get("vehicle_invoice_total_price_digits"))) {
                    //税额
                    vatInvoiceProduct.setInvoiceTax(basicItemMap.get("vehicle_invoice_tax_amount"));
                    //金额（含税）
                    vatInvoiceProduct.setInvoicePrice(basicItemMap.get("vehicle_invoice_total_price_digits"));
                    //金额（不含税）= 金额（含税）- 税额
                    double vehicleInvoiceTotalPriceDigits = Double.parseDouble(basicItemMap.get("vehicle_invoice_total_price_digits"));
                    double vehicleInvoiceTaxAmount = Double.parseDouble(basicItemMap.get("vehicle_invoice_tax_amount"));
                    vatInvoiceProduct.setTaxNotIncluded(String.valueOf(vehicleInvoiceTotalPriceDigits - vehicleInvoiceTaxAmount));
                    leaseItemVatInvoiceProductService.save(vatInvoiceProduct);
                }
            }
            return;
        }

        //2.增值税类型的发票识别
        VatInvoiceResponse vatInvoiceResponse = recognizeVatInvoice(ocrFileInfo);
        log.warn("增值税发票识别返回状态码：" + vatInvoiceResponse.getCode());
        if (Objects.nonNull(vatInvoiceResponse.getCode())
                && vatInvoiceResponse.getCode() == 200
                && Objects.nonNull(vatInvoiceResponse.getInvoiceResult())
                && CollectionUtils.isNotEmpty(vatInvoiceResponse.getInvoiceResult().getItemList())) {
            //解析发票信息
            this.getVatInvoice(vatInvoiceResponse.getInvoiceResult(), vatInvoice);
            this.verifyInvoice(vatInvoice);
        } else {
            vatInvoice.setNote("识别失败，请重新上传发票或修改");
            vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.IDENTIFY_FAILED.name());
        }
        saveInvoice(ocrFileInfo, req, vatInvoice, userId);
        //增值税发票可能存在多条发票产品信息
        if (CollectionUtils.isNotEmpty(vatInvoiceResponse.getInvoiceResult().getProductList())) {
            //解析发票产品信息
            List<LeaseItemVatInvoiceProduct> vatInvoiceProductList = getVatInvoiceProduct(vatInvoiceResponse.getInvoiceResult().getProductList(), vatInvoice.getId(), userId);
            //发票产品入库
            leaseItemVatInvoiceProductService.saveBatch(vatInvoiceProductList);
        }
    }

    /**
     * ocr识别机动车购车发票
     *
     * @param ocrFileInfo 发票文件
     * @return ocr识别返回数据
     */
    private VehicleSalesInvoiceResponse recognizeVehicleSalesInvoice(OcrFileInfo ocrFileInfo) {
        final VehicleSalesInvoiceRequest salesInvoiceRequest = new VehicleSalesInvoiceRequest();
        salesInvoiceRequest.setAppId(ocrConfigProperties.getAppId());
        salesInvoiceRequest.setSecretCode(ocrConfigProperties.getSecretCode());
        salesInvoiceRequest.setFileInfo(ocrFileInfo);
        return OcrUtil.vehicleSalesInvoice(salesInvoiceRequest);
    }

    /**
     * ocr识别增值税发票
     *
     * @param ocrFileInfo 发票文件
     * @return ocr识别返回数据
     */
    private VatInvoiceResponse recognizeVatInvoice(OcrFileInfo ocrFileInfo) {
        VatInvoiceRequest vatInvoiceRequest = new VatInvoiceRequest();
        vatInvoiceRequest.setFileInfo(ocrFileInfo);
        vatInvoiceRequest.setAppId(ocrConfigProperties.getAppId());
        vatInvoiceRequest.setSecretCode(ocrConfigProperties.getSecretCode());
        return OcrUtil.vatInvoice(vatInvoiceRequest);
    }

    /**
     * 对机动车购车发票解析
     *
     * @param basicItemMap ocr识别出的发票信息
     * @param vatInvoice   发票实体类
     */
    private void getSalesInvoice(Map<String, String> basicItemMap, String type, LeaseItemVatInvoice vatInvoice) {
        //发票号码 若是发票识别号码为空，则取发票机打号码（通发票号码一致）
        String haoma = StringUtils.isBlank(basicItemMap.get("vehicle_invoice_haoma")) ? basicItemMap.get("vehicle_invoice_jida_haoma") : basicItemMap.get("vehicle_invoice_haoma");
        vatInvoice.setInvoiceNo(haoma);
        //发票代码 若是发票识别代码为空，则取发票机打代码（通发票代码一致）
        String daima = StringUtils.isBlank(basicItemMap.get("vehicle_invoice_daima")) ? basicItemMap.get("vehicle_invoice_jida_daima") : basicItemMap.get("vehicle_invoice_daima");
        vatInvoice.setInvoiceDaima(daima);
        //发票类型
        vatInvoice.setInvoiceType(type);
        //不含税金额
        vatInvoice.setInvoiceTaxTotal(basicItemMap.get("vehicle_invoice_price_without_tax"));
        //含税金额(小写)
        String invoiceTotalCoverTaxDigits = basicItemMap.get("vehicle_invoice_total_price_digits");
        //金额大写
        String totalCoverTax = basicItemMap.get("vehicle_invoice_total_price");
        vatInvoice.setInvoiceTotalCoverTaxDigits(invoiceTotalCoverTaxDigits);
        //比较大写金额和小写金额是否一致
        if (StringUtils.isNotBlank(invoiceTotalCoverTaxDigits) && StringUtils.isNotBlank(totalCoverTax)) {
            String totalCoverTaxFormat = NumberChineseFormatter.format(Double.parseDouble(invoiceTotalCoverTaxDigits), true, true);
            //因不同的发票可能存在不同的字样，将“整”，“元”,“圆”,"零"忽略进行比较
            String str1 = totalCoverTax.replaceAll("[整元圆零]", "");
            String str2 = totalCoverTaxFormat.replaceAll("[整元圆零]", "");
            if (!str1.equals(str2)) {
                vatInvoice.setNote("发票大小写金额识别结果不一致，请重新上传发票或修改");
            }
        }
        //开票日期  需要转换时间类型
        String dateStr = basicItemMap.get("vehicle_invoice_issue_date");
        if (StringUtils.isNotBlank(dateStr)) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(dateStr, formatter);
                vatInvoice.setInvoiceIssueDate(date);
            } catch (DateTimeParseException e) {
                log.warn("日期格式错误：" + dateStr + "-" + e.getMessage());
            }
        }
        //购买方
        vatInvoice.setInvoicePayerName(basicItemMap.get("vehicle_invoice_buyer"));
        //销售方
        vatInvoice.setInvoiceSellerName(basicItemMap.get("vehicle_invoice_dealer"));
        //是否盖章
        String existStample = basicItemMap.get("exist_invoice_seal");
        if ("True".equalsIgnoreCase(existStample)) {
            vatInvoice.setExistStample(JudgeEnum.TRUE.name());
        }
        if ("False".equalsIgnoreCase(existStample)) {
            vatInvoice.setExistStample(JudgeEnum.FALSE.name());
        }
    }

    /**
     * 对发票信息进行解析
     *
     * @param invoiceResult       ocr识别出的发票信息
     * @param leaseItemVatInvoice 发票实体类
     */
    private void getVatInvoice(InvoiceResult invoiceResult, LeaseItemVatInvoice leaseItemVatInvoice) {
        //key - 字段名称 ，value - 值
        Map<String, String> basicItemMap = invoiceResult.getItemList().stream().collect(Collectors.toMap(BasicItem::getKey, BasicItem::getValue));
        //发票号码 取值地方有三个：发票识别号码、发票机打号码、发票机打号码（右侧）
        String invoiceHaoma1 = StringUtils.isBlank(basicItemMap.get("vat_invoice_haoma")) ? basicItemMap.get("vat_invoice_haoma_large_size") : basicItemMap.get("vat_invoice_haoma");
        String invoiceHaoma2 = StringUtils.isBlank(invoiceHaoma1) ? basicItemMap.get("vat_invoice_haoma_right_side") : invoiceHaoma1;
        leaseItemVatInvoice.setInvoiceNo(invoiceHaoma2);
        //发票代码 取值地方有三个：发票识别号码、发票机打号码、发票机打号码（右侧）
        String invoiceDaima1 = StringUtils.isBlank(basicItemMap.get("vat_invoice_daima")) ? basicItemMap.get("vat_invoice_daima_print") : basicItemMap.get("vat_invoice_daima");
        String invoiceDaima2 = StringUtils.isBlank(invoiceDaima1) ? basicItemMap.get("vat_invoice_daima_right_side") : invoiceDaima1;
        leaseItemVatInvoice.setInvoiceDaima(invoiceDaima2);
        //发票验证码 若是发票识别验证码为空，则取发票机打验证码（通发票验证码一致）
        String invoiceCorrectCode = StringUtils.isBlank(basicItemMap.get("vat_invoice_correct_code")) ? basicItemMap.get("vat_invoice_corrent_code_print") : basicItemMap.get("vat_invoice_correct_code");
        leaseItemVatInvoice.setInvoiceCorrectCode(invoiceCorrectCode);
        //发票类型
        leaseItemVatInvoice.setInvoiceType(invoiceResult.getType());
        //不含税金额
        leaseItemVatInvoice.setInvoiceTaxTotal(basicItemMap.get("vat_invoice_total"));
        //含税金额(小写)
        String invoiceTotalCoverTaxDigits = basicItemMap.get("vat_invoice_total_cover_tax_digits");
        //金额大写
        String totalCoverTax = basicItemMap.get("vat_invoice_total_cover_tax");
        leaseItemVatInvoice.setInvoiceTotalCoverTaxDigits(invoiceTotalCoverTaxDigits);
        //比较大写金额和小写金额是否一致
        if (StringUtils.isNotBlank(invoiceTotalCoverTaxDigits) && StringUtils.isNotBlank(totalCoverTax)) {
            String totalCoverTaxFormat = NumberChineseFormatter.format(Double.parseDouble(invoiceTotalCoverTaxDigits), true, true);
            //因不同的发票可能存在不同的字样，将“整”，“元”,“圆”,"零"忽略进行比较
            String str1 = totalCoverTax.replaceAll("[整元圆零]", "");
            String str2 = totalCoverTaxFormat.replaceAll("[整元圆零]", "");
            if (!str1.equals(str2)) {
                leaseItemVatInvoice.setNote("发票大小写金额识别结果不一致，请重新上传发票或修改");
            }
        }

        //开票日期  需要转换时间类型
        String dateStr = StringUtils.isBlank(basicItemMap.get("vat_invoice_issue_date")) ? basicItemMap.get("vat_invoice_issue_date_print") : basicItemMap.get("vat_invoice_issue_date");
        if (StringUtils.isNotBlank(dateStr)) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
                LocalDate date = LocalDate.parse(dateStr, formatter);
                leaseItemVatInvoice.setInvoiceIssueDate(date);
            } catch (DateTimeParseException e) {
                log.warn("日期格式错误：" + dateStr + "-" + e.getMessage());
            }
        }
        //购买方
        leaseItemVatInvoice.setInvoicePayerName(basicItemMap.get("vat_invoice_payer_name"));
        //销售方
        leaseItemVatInvoice.setInvoiceSellerName(basicItemMap.get("vat_invoice_seller_name"));
        //是否盖章
        String existStample = basicItemMap.get("exist_stample");
        if ("True".equalsIgnoreCase(existStample)) {
            leaseItemVatInvoice.setExistStample(JudgeEnum.TRUE.name());
        }
        if ("False".equalsIgnoreCase(existStample)) {
            leaseItemVatInvoice.setExistStample(JudgeEnum.FALSE.name());
        }
    }

    /**
     * 对发票进行验真
     *
     * @param vatInvoice 发票实体类
     */
    private void verifyInvoice(LeaseItemVatInvoice vatInvoice) {
        //对发票进行验真
        VerifyVatResponse verifyVatResponse = getVerifyVatRequest(vatInvoice);
        log.warn("发票验真返回状态码：" + verifyVatResponse.getInvoiceCode());

        //10017 表示为此发票已经超过五年，发票超过五年不能验真。详情见官方 https://www.textin.com/document/verify_vat
        if ("10017".equals(verifyVatResponse.getResultCode())) {
            vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.VERIFICATION_FAILED.name());
            return;
        }
        //验真结果
        if (StringUtils.isNotBlank(verifyVatResponse.getInvalidMark())) {
            if (LeaseVatInvoiceStatusEnum.EFFECTIVE.getFieldName().equals(verifyVatResponse.getInvalidMark())) {
                vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.VERIFICATION_PASSED.name());
            } else {
                vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.VERIFICATION_NOT_PASSED.name());
            }
            //发票状态
            LeaseVatInvoiceStatusEnum statusEnum = LeaseVatInvoiceStatusEnum.ofByFieldName(verifyVatResponse.getInvalidMark());
            if (statusEnum != null) vatInvoice.setStatus(statusEnum.name());
        } else {
            vatInvoice.setVerifyResult(LeaseFileOCRStatusEnum.CHECK_FAILURE.name());
        }
    }

    /**
     * 对发票进行验真
     *
     * @param vatInvoice 发票信息
     * @return 发票验真返回体
     */
    private VerifyVatResponse getVerifyVatRequest(LeaseItemVatInvoice vatInvoice) {
        VerifyVatRequest verifyVatRequest = new VerifyVatRequest();
        verifyVatRequest.setAppId(ocrConfigProperties.getAppId());
        verifyVatRequest.setSecretCode(ocrConfigProperties.getSecretCode());
        verifyVatRequest.setInvoiceNo(vatInvoice.getInvoiceNo());
        verifyVatRequest.setInvoiceCode(vatInvoice.getInvoiceDaima());
        //增值税发票校验码取后6位
        if (StringUtils.isNotBlank(vatInvoice.getInvoiceCorrectCode()) && vatInvoice.getInvoiceCorrectCode().length() > 6) {
            verifyVatRequest.setVerifyCode(vatInvoice.getInvoiceCorrectCode().substring(vatInvoice.getInvoiceCorrectCode().length() - 6));
        }
        if (vatInvoice.getInvoiceIssueDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate invoiceIssueDate = vatInvoice.getInvoiceIssueDate();
            String invoiceDate = invoiceIssueDate.format(formatter);
            verifyVatRequest.setInvoiceDate(invoiceDate);
        }
        //根据不同的发票类型传入不同的金额
        if (LeaseVatInvoiceTypeEnum.VAT_SPECIAL_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.VAT_ELECTRONIC_SPECIAL_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.MOTOR_VEHICLE_SALE_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())) {
            //不含税金额
            verifyVatRequest.setInvoiceSum(vatInvoice.getInvoiceTaxTotal());
        }
        if (LeaseVatInvoiceTypeEnum.VAT_ELECTRONIC_SPECIAL_INVOICE_NEW.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.VAT_ELECTRONIC_INVOICE_NEW.getFieldName().equals(vatInvoice.getInvoiceType())
                || LeaseVatInvoiceTypeEnum.VAT_COMMON_INVOICE.getFieldName().equals(vatInvoice.getInvoiceType())) {
            //含税金额
            verifyVatRequest.setInvoiceSum(vatInvoice.getInvoiceTotalCoverTaxDigits());
        }
        return OcrUtil.verifyVat(verifyVatRequest);
    }

    /**
     * 保存发票并上传到mino
     *
     * @param ocrFileInfo 发票信息
     * @param req         请求体
     * @param vatInvoice  发票实体类
     */
    private void saveInvoice(OcrFileInfo ocrFileInfo, LeaseVatInvoiceUploadREQ req, LeaseItemVatInvoice vatInvoice, Long userId) {
        //操作类型
        if (StringUtils.isNotBlank(req.getOperateType()) && LeaseOperateEnum.RE_UPLOAD.name().equals(req.getOperateType())) {
            vatInvoice.setOperation(LeaseOperateEnum.RE_UPLOAD.name());
        }
        //租赁物id
        vatInvoice.setLeaseItemInfoId(req.getLeaseholdId());
        vatInvoice.setFileName(ocrFileInfo.getOriginalFilename());
        vatInvoice.setCreateBy(userId);
        vatInvoice.setUpdateBy(userId);
        //发票信息入库
        this.save(vatInvoice);

        try {
            if (ocrFileInfo.getInputStream() != null) {
                ocrFileInfo.getInputStream().reset();
            }
            //上传发票文件
            Long fileId = materialsListService.add(ocrFileInfo.getInputStream(), ocrFileInfo.getOriginalFilename() + ".png",
                    vatInvoice.getId(), LeaseFileTypeEnums.LEASE_VAT_INVOICE.name(), "OCR");
            vatInvoice.setFileId(fileId);
        } catch (Exception e) {
            log.error("上传发票文件失败" + e.getMessage());
            throw new MithrasException("上传发票文件失败");
        }
        //将文件id添加到数据库
        this.updateById(vatInvoice);
    }

    /**
     * 解析增值税发票产品信息
     *
     * @param productList 将要解析的数据
     * @param invoiceId   发票id
     * @return 发票产品实体集合
     */
    private List<LeaseItemVatInvoiceProduct> getVatInvoiceProduct(List<List<BasicItem>> productList, Long invoiceId, Long userId) {
        List<LeaseItemVatInvoiceProduct> vatInvoiceProductList = new ArrayList<>();

        if (CollectionUtils.isEmpty(productList)) {
            return vatInvoiceProductList;
        }

        for (List<BasicItem> basicItems : productList) {
            if (CollectionUtils.isEmpty(basicItems)) {
                continue;
            }
            Map<String, String> basicItemMap = basicItems.stream().collect(Collectors.toMap(BasicItem::getKey, BasicItem::getValue));
            LeaseItemVatInvoiceProduct vatInvoiceProduct = new LeaseItemVatInvoiceProduct();
            //发票id
            vatInvoiceProduct.setInvoiceId(invoiceId);
            //货物或服务名称
            vatInvoiceProduct.setInvoiceGoods(basicItemMap.get("vat_invoice_goods"));
            //规格型号
            vatInvoiceProduct.setInvoicePlateSpecific(basicItemMap.get("vat_invoice_plate_specific"));
            //单位
            vatInvoiceProduct.setInvoiceElectransUnit(basicItemMap.get("vat_invoice_electrans_unit"));
            //数量
            vatInvoiceProduct.setInvoiceElectransQuantity(basicItemMap.get("vat_invoice_electrans_quantity"));
            //税率
            vatInvoiceProduct.setInvoiceTaxRate(basicItemMap.get("vat_invoice_tax_rate"));
            //税额小写
            vatInvoiceProduct.setInvoiceTax(basicItemMap.get("vat_invoice_tax"));
            //金额（不含税）
            String taxNotIncluded = basicItemMap.get("vat_invoice_price");
            vatInvoiceProduct.setTaxNotIncluded(taxNotIncluded);
            vatInvoiceProduct.setCreateBy(userId);
            vatInvoiceProduct.setUpdateBy(userId);
            //金额（含税）
            if (StringUtils.isBlank(taxNotIncluded)) {
                taxNotIncluded = "0";
            }
            String invoiceTax = basicItemMap.get("vat_invoice_tax");
            if (StringUtils.isBlank(invoiceTax)) {
                invoiceTax = "0";
            }
            BigDecimal invoicePrice = new BigDecimal(invoiceTax).add(new BigDecimal(taxNotIncluded));
            if (!"0".equals(String.valueOf(invoicePrice))) {
                //含税金额
                vatInvoiceProduct.setInvoicePrice(String.valueOf(invoicePrice));
            }
            vatInvoiceProductList.add(vatInvoiceProduct);
        }
        return vatInvoiceProductList;
    }

    /**
     * 发票实体转为发票RSP
     *
     * @param leaseItemVatInvoice 发票信息
     * @return 发票RSP
     */
    private LeaseVatInvoiceListRSP convertToLeaseVatInvoiceListRSP(LeaseItemVatInvoice leaseItemVatInvoice) {
        LeaseVatInvoiceListRSP rsp = new LeaseVatInvoiceListRSP();
        BeanUtils.copyProperties(leaseItemVatInvoice, rsp);
        //数据库存储为枚举类型，这里进行转换一下
        JudgeEnum judgeEnum1 = JudgeEnum.ofByName(leaseItemVatInvoice.getExistStample());
        if (judgeEnum1 != null) {
            rsp.setExistStample(Boolean.valueOf(judgeEnum1.name()));
        }
        JudgeEnum judgeEnum2 = JudgeEnum.of(leaseItemVatInvoice.getLocked());
        if (judgeEnum2 != null) {
            rsp.setLocked(Boolean.valueOf(judgeEnum2.name()));
        }

        //查找发票产品信息
        List<LeaseItemVatInvoiceProduct> invoiceProductList = leaseItemVatInvoiceProductService.list(Wrappers.<LeaseItemVatInvoiceProduct>lambdaQuery()
                .eq(LeaseItemVatInvoiceProduct::getDeleted, "0").eq(LeaseItemVatInvoiceProduct::getInvoiceId, leaseItemVatInvoice.getId()));
        if (CollectionUtils.isNotEmpty(invoiceProductList)) {
            List<LeaseVatInvoiceProductRSP> productRSPList = invoiceProductList.stream()
                    .map(this::convertToLeaseVatInvoiceProductRSP)
                    .collect(Collectors.toList());
            rsp.setInvoiceProductList(productRSPList);
        }

        return rsp;
    }

    /**
     * 发票产品实体转为发票产品PSP集合
     *
     * @param invoiceProduct 发票产品信息
     * @return 发票产品PSP集合
     */
    private LeaseVatInvoiceProductRSP convertToLeaseVatInvoiceProductRSP(LeaseItemVatInvoiceProduct invoiceProduct) {
        LeaseVatInvoiceProductRSP productRSP = new LeaseVatInvoiceProductRSP();
        BeanUtils.copyProperties(invoiceProduct, productRSP);
        return productRSP;
    }

    /**
     * 删除全部发票信息
     *
     * @param leaseholdId 租赁物id
     */
    private void deleteAll(Long leaseholdId) {
        Wrapper<LeaseItemVatInvoice> queryWrapper = Wrappers.<LeaseItemVatInvoice>lambdaQuery()
                .eq(LeaseItemVatInvoice::getLeaseItemInfoId, leaseholdId)
                .eq(LeaseItemVatInvoice::getDeleted, "0");

        List<LeaseItemVatInvoice> vatInvoiceList = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(vatInvoiceList)) {
            List<Long> invoiceIds = vatInvoiceList.stream().map(LeaseItemVatInvoice::getId).collect(Collectors.toList());
            Wrapper<LeaseItemVatInvoiceProduct> invoiceProductUpdateWrapper = Wrappers.<LeaseItemVatInvoiceProduct>lambdaUpdate()
                    .in(LeaseItemVatInvoiceProduct::getInvoiceId, invoiceIds)
                    .eq(LeaseItemVatInvoiceProduct::getDeleted, "0")
                    .set(LeaseItemVatInvoiceProduct::getDeleted, "1");
            leaseItemVatInvoiceProductService.update(invoiceProductUpdateWrapper);

            Wrapper<LeaseItemVatInvoice> invoiceUpdateWrapper = Wrappers.<LeaseItemVatInvoice>lambdaUpdate()
                    .in(LeaseItemVatInvoice::getId, invoiceIds)
                    .eq(LeaseItemVatInvoice::getDeleted, "0")
                    .eq(LeaseItemVatInvoice::getLocked, "0")
                    .set(LeaseItemVatInvoice::getDeleted, "1");
            this.update(invoiceUpdateWrapper);
        }
    }

    /**
     * 获取租赁物清单账面原值（元）合计值
     *
     * @param rowData 清单账面数据
     * @return 合计值
     */
    private double extractedTotalOriginalValues(String rowData) {
        if (StringUtils.isNotBlank(rowData)) {
            Map<String, Object> dataMap = JSONUtil.toBean(rowData, Map.class);
            Object obj = dataMap.get("账面原值（元）");
            if (obj instanceof String) {
                try {
                    return Double.parseDouble((String) obj);
                } catch (NumberFormatException e) {
                    return 0D;
                }
            }
        }
        // 如果rowData为空或"账面原值（元）"不存在，返回0L
        return 0L;
    }

    /**
     * 根据租赁物id获取发票全部的信息
     *
     * @param leaseholdId 租赁物id
     * @return 发票集合
     */
    private List<LeaseItemVatInvoice> getLeaseItemVatInvoices(Long leaseholdId) {
        Wrapper<LeaseItemVatInvoice> queryWrapper = Wrappers.<LeaseItemVatInvoice>lambdaQuery()
                .eq(LeaseItemVatInvoice::getLeaseItemInfoId, leaseholdId)
                .eq(LeaseItemVatInvoice::getDeleted, "0");
        return this.list(queryWrapper);
    }

    /**
     * Excel表单条数据
     *
     * @param vatInvoice     发票信息
     * @param invoiceProduct 发票货物信息
     * @return 插入Excel表时对象
     */
    private LeaseVatInvoiceExcelModel getVatInvoiceExcelModel(LeaseItemVatInvoice vatInvoice, LeaseItemVatInvoiceProduct invoiceProduct) {
        LeaseVatInvoiceExcelModel vatInvoiceExcelModel = new LeaseVatInvoiceExcelModel();
        if (Objects.nonNull(vatInvoice)) {
            vatInvoiceExcelModel.setFileName(vatInvoice.getFileName());
            vatInvoiceExcelModel.setInvoiceNo(vatInvoice.getInvoiceNo());

            //日期转为String
            if (Objects.nonNull(vatInvoice.getInvoiceIssueDate())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                vatInvoiceExcelModel.setInvoiceIssueDate(vatInvoice.getInvoiceIssueDate().format(formatter));
            }
            vatInvoiceExcelModel.setInvoicePayerName(vatInvoice.getInvoicePayerName());
            vatInvoiceExcelModel.setInvoiceSellerName(vatInvoice.getInvoiceSellerName());
            vatInvoiceExcelModel.setNote(vatInvoice.getNote());

            LeaseFileOCRStatusEnum ocrStatusEnum = LeaseFileOCRStatusEnum.of(vatInvoice.getVerifyResult());
            if (ocrStatusEnum != null) {
                vatInvoiceExcelModel.setVerifyResult(ocrStatusEnum.display());
            }
            LeaseVatInvoiceStatusEnum statusEnum = LeaseVatInvoiceStatusEnum.ofByName(vatInvoice.getStatus());
            if (statusEnum != null) {
                vatInvoiceExcelModel.setStatus(statusEnum.display());
            }
            if (JudgeEnum.FALSE.name().equals(vatInvoice.getExistStample())) {
                vatInvoiceExcelModel.setExistStample("否");
            }
            if (JudgeEnum.TRUE.name().equals(vatInvoice.getExistStample())) {
                vatInvoiceExcelModel.setExistStample("是");
            }
        }

        if (Objects.nonNull(invoiceProduct)) {
            vatInvoiceExcelModel.setInvoiceGoods(invoiceProduct.getInvoiceGoods());
            vatInvoiceExcelModel.setInvoicePlateSpecific(invoiceProduct.getInvoicePlateSpecific());
            vatInvoiceExcelModel.setInvoiceElectransUnit(invoiceProduct.getInvoiceElectransUnit());
            vatInvoiceExcelModel.setInvoiceElectransQuantity(invoiceProduct.getInvoiceElectransQuantity());
            vatInvoiceExcelModel.setInvoiceTaxRate(invoiceProduct.getInvoiceTaxRate());
            vatInvoiceExcelModel.setInvoiceTax(invoiceProduct.getInvoiceTax());
            vatInvoiceExcelModel.setTaxNotIncluded(invoiceProduct.getTaxNotIncluded());
            vatInvoiceExcelModel.setInvoicePrice(invoiceProduct.getInvoicePrice());
            vatInvoiceExcelModel.setVehicleInvoiceCarVin(invoiceProduct.getVehicleInvoiceCarVin());
        }

        return vatInvoiceExcelModel;
    }
}

