package cn.zswltech.mithras.service.service.leaseholdproperty.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.common.ResultCode;
import cn.zswltech.mithras.dto.leaseholdproperty.*;
import cn.zswltech.mithras.application.config.OcrConfigProperties;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.LeaseItemVehicleRegistrationTypeEnum;
import cn.zswltech.mithras.leaseholdproperty.domain.enums.LeaseOperateEnum;
import cn.zswltech.mithras.leaseholdproperty.excel.exporter.LeaseItemVehicleRegistrationCertificateExcelExporter;
import cn.zswltech.mithras.leaseholdproperty.excel.model.LeaseItemVehicleRegistrationCertificateExcelModel;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.LeaseItemVehicleRegistrationCertificateMapper;
import cn.zswltech.mithras.leaseholdproperty.infrastructure.persistence.mapper.model.LeaseItemVehicleRegistrationCertificate;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseVehicleRegistrationService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.OcrServiceUtil;
import cn.zswltech.sleipnir.toolkit.OcrUtil;
import cn.zswltech.sleipnir.toolkit.request.OcrFileInfo;
import cn.zswltech.sleipnir.toolkit.request.VehicleRegistrationRequest;
import cn.zswltech.sleipnir.toolkit.response.BasicItem;
import cn.zswltech.sleipnir.toolkit.response.VehicleRegistrationResponse;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
@Slf4j
@Service
public class LeaseVehicleRegistrationServiceImpl extends ServiceImpl<LeaseItemVehicleRegistrationCertificateMapper, LeaseItemVehicleRegistrationCertificate> implements LeaseVehicleRegistrationService {

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private OcrConfigProperties ocrConfigProperties;

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private LeaseItemVehicleRegistrationCertificateExcelExporter leaseVehicleExcelExporter;

    @Resource
    protected FlowTaskApiService flowTaskApiService;

    @Resource
    private RedisDistLock redisDistLock;

    @Resource
    @Qualifier("ocrThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<LeaseVehicleRegistrationUploadRSP> vehicleRegistrationUpload(LeaseVehicleRegistrationUploadREQ req) {
        if (req.getFiles().size() > 50) {
            throw new MithrasException("最多支持同时上传50个文件");
        }
        int success = 0;
        int fail = 0;

        List<Future<Boolean>> futures = new ArrayList<>();
        Long userId = AccountUtil.getLoginInfo().getId();
        for (MultipartFile file : req.getFiles()) {
            //文件为pdf和docx格式要处理一下，拆分成多个文件流，每个文流就是一张车证
            List<OcrFileInfo> ocrFileInfoList = OcrServiceUtil.getOcrFileInfoList(file);
            for (OcrFileInfo fileInfo : ocrFileInfoList) {
                fileInfo.setRandomNumber(getBatchNum());
                //解析车证并保存
                Future<Boolean> future = threadPoolTaskExecutor.submit(() -> identifyAndSave(fileInfo, req, userId));
                futures.add(future);
                // try {
                //     identifyAndSave(fileInfo, req, AccountUtil.getLoginInfo().getId());
                // } catch (Exception e) {
                //     log.warn("车证保存失败：" + e);
                // }
            }
        }
        for (Future<Boolean> future : futures) {
            try {
                Boolean resultStatus = future.get(); // 阻塞等待任务完成，并获取结果
                if (resultStatus) {
                    success++;
                } else {
                    fail++;
                }
            } catch (InterruptedException | ExecutionException e) {
                //发生异常不能影响到后续操作
                log.warn("车证保存失败：" + e);
                fail++;
            }
        }
        LeaseVehicleRegistrationUploadRSP rsp = new LeaseVehicleRegistrationUploadRSP();
        rsp.setFail(fail);
        rsp.setSuccess(success);
        return R.ok(rsp);
    }

    private boolean identifyAndSave(OcrFileInfo fileInfo, LeaseVehicleRegistrationUploadREQ req, Long userId) {
        //ocr识别车证信息
        VehicleRegistrationRequest vehicleRegistrationRequest = new VehicleRegistrationRequest();
        vehicleRegistrationRequest.setAppId(ocrConfigProperties.getAppId());
        vehicleRegistrationRequest.setSecretCode(ocrConfigProperties.getSecretCode());
        vehicleRegistrationRequest.setFileInfo(fileInfo);
        VehicleRegistrationResponse response = OcrUtil.vehicleRegistration(vehicleRegistrationRequest);

        //上传文件
        Long fileId;
        try {
            if (fileInfo.getInputStream() != null) {
                fileInfo.getInputStream().reset();
            }
            fileId = materialsListService.add(fileInfo.getInputStream(), fileInfo.getOriginalFilename() + ".png", null, "VEHICLE", "OCR");
        } catch (Exception e) {
            log.error("上传车证文件失败" + e.getMessage());
            throw new MithrasException("上传车证文件失败");
        }

        //基本信息
        LeaseItemVehicleRegistrationCertificate certificate = new LeaseItemVehicleRegistrationCertificate(userId);
        certificate.setId(req.getVehicleId());
        certificate.setLeaseItemInfoId(req.getLeaseholdId());
        certificate.setOperation(req.getOperateType());
        certificate.setUpdateBy(userId);
        certificate.setUpdateTime(LocalDateTime.now());
        List<VehicleChangeRecordData> changeRecordDataList = new ArrayList<>();

        //解析ocr识别信息
        if (Objects.nonNull(response)
                && Objects.nonNull(response.getCode())
                && response.getCode() == 200
                && Objects.nonNull(response.getResult())
                && StringUtils.isNotBlank(response.getResult().getType())) {
            //1.车证首页解析
            if (LeaseItemVehicleRegistrationTypeEnum.VEHICLE_REGISTRATION_CERTIFICATE.getFieldName().equals(response.getResult().getType())) {
                certificate.setFileId(fileId);
                certificate.setFileName(fileInfo.getOriginalFilename());
                certificate.setIsPresentHomePage(true);
                if (CollectionUtils.isNotEmpty(response.getResult().getItemList())) {
                    Map<String, String> basicItemMap = response.getResult().getItemList().stream().collect(Collectors.toMap(BasicItem::getKey, BasicItem::getValue));
                    //机动车所有人
                    certificate.setVehicleRegistrationOwner(basicItemMap.get("vehicle_registration_owner"));
                    //机动车登记编号
                    certificate.setVehicleRegistrationNumber(basicItemMap.get("vehicle_registration_number"));
                    //机动车登记证书编号
                    certificate.setRegistrationPageNo(basicItemMap.get("vehicle_registration_page_no"));
                    //车辆识别代号/车架号
                    certificate.setVehicleVin(basicItemMap.get("vehicle_vin"));
                    //制造厂名称
                    certificate.setVehicleManufacturer(basicItemMap.get("vehicle_manufacturer"));
                }
                //识别状态
                if (StringUtils.isNotBlank(certificate.getVehicleManufacturer())
                        && StringUtils.isNotBlank(certificate.getVehicleVin())
                        && StringUtils.isNotBlank(certificate.getVehicleRegistrationNumber())
                        && StringUtils.isNotBlank(certificate.getVehicleRegistrationOwner())) {
                    certificate.setStatus("success");
                } else {
                    certificate.setStatus("failure");
                }
                //保存车证信息
                String lock;
                if (StringUtils.isBlank(certificate.getRegistrationPageNo())) {
                    lock = certificate.getFileName();
                } else {
                    lock = certificate.getRegistrationPageNo();
                }
                // 尝试获取锁，10秒内获取不到则返回false,获取到锁，10秒之后锁自定失效
                boolean isLocked = redisDistLock.tryLock(lock, 10000, 10000);
                if (!isLocked) {
                    log.error("获取锁超时");
                    throw new MithrasException("获取锁超时");
                }
                try {
                    this.certificateCheckAndSave(certificate, userId);
                    return true;
                } catch (Exception e) {
                    log.error("保存失败，未知异常:{}", e);
                    throw new MithrasException("保存失败，未知异常");
                } finally {
                    // 尝试释放锁
                    redisDistLock.unlock(lock);
                }
            }

            //2.变更记录
            if (LeaseItemVehicleRegistrationTypeEnum.VEHICLE_REGIST_PAGE_MORTGAGE.getFieldName().equals(response.getResult().getType())) {
                VehicleChangeRecordData changeRecord = new VehicleChangeRecordData();
                changeRecord.setFileName(fileInfo.getOriginalFilename());
                changeRecord.setFileId(fileId);
                //1.编号
                if (CollectionUtils.isNotEmpty(response.getResult().getItemList())) {
                    Map<String, String> baseItemMap = response.getResult().getItemList().stream().collect(Collectors.toMap(BasicItem::getKey, BasicItem::getValue));
                    certificate.setRegistrationPageNo(baseItemMap.get("vehicle_registration_page_mortgage_no"));
                }
                //2.变更记录，仅需要名称
                if (CollectionUtils.isNotEmpty(response.getResult().getTransferRegisterList())) {
                    List<VehicleChangeRecordData.ChangeRecord> changeRecordList = response.getResult().getTransferRegisterList().stream()
                            .map(item -> {
                                Map<String, String> map = item.stream().collect(Collectors.toMap(BasicItem::getKey, BasicItem::getValue));
                                VehicleChangeRecordData.ChangeRecord record = new VehicleChangeRecordData.ChangeRecord();
                                //姓名/名称
                                record.setName(map.get("vehicle_registration_page_transfer_id_name"));
                                //变更日期
                                String dateStr = map.get("vehicle_registration_page_transfer_date");
                                if (StringUtils.isNotBlank(dateStr)) {
                                    try {
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        LocalDate date = LocalDate.parse(dateStr, formatter);
                                        record.setChangeDate(date);
                                    } catch (DateTimeParseException e) {
                                        log.warn("日期格式错误：" + dateStr + "-" + e.getMessage());
                                    }
                                }
                                return record;
                            }).collect(Collectors.toList());
                    changeRecord.setChangeRecordList(changeRecordList);
                }
                changeRecordDataList.add(changeRecord);
                //3.识别状态
                if (StringUtils.isNotBlank(certificate.getRegistrationPageNo())) {
                    certificate.setStatus("success");
                } else {
                    certificate.setStatus("failure");
                }
                //保存变更记录
                String lock;
                if (StringUtils.isBlank(certificate.getRegistrationPageNo())) {
                    lock = certificate.getFileName();
                } else {
                    lock = certificate.getRegistrationPageNo();
                }
                // 尝试获取锁，10秒内获取不到则返回false,获取到锁，10秒之后锁自定失效
                boolean isLocked = redisDistLock.tryLock(lock, 1000, 1000);
                if (!isLocked) {
                    log.error("获取锁超时");
                    throw new MithrasException("获取锁超时");
                }
                try {
                    this.changeRecordCheckAndSave(certificate, changeRecordDataList, req.getChangeRecordId(), userId);
                    return true;
                } catch (Exception e) {
                    log.error("保存失败，未知异常:{}", e);
                    return false;
                } finally {
                    // 尝试释放锁
                    redisDistLock.unlock(lock);
                }
            }

            //3.其他类型
            certificate.setFileName(fileInfo.getOriginalFilename());
            certificate.setFileId(fileId);
            certificate.setIsPresentHomePage(false);
            this.save(certificate);
            return false;

        } else {
            certificate.setFileName(fileInfo.getOriginalFilename());
            certificate.setFileId(fileId);
            certificate.setIsPresentHomePage(false);
            this.save(certificate);
            return false;
        }
    }

    private void changeRecordCheckAndSave(LeaseItemVehicleRegistrationCertificate certificate, List<VehicleChangeRecordData> changeRecordDataList, Long changeRecordFileId, Long userId) {
        LeaseItemVehicleRegistrationCertificate certificateOld = getCertificate(certificate.getLeaseItemInfoId(), certificate.getId(), certificate.getRegistrationPageNo());
        //检查是否存在旧数据，存在则修改，不存在则新增
        if (Objects.nonNull(certificateOld)) {
            certificate.setId(certificateOld.getId());
            if (StringUtils.isNotBlank(certificateOld.getChangeRecord())) {
                List<VehicleChangeRecordData> changeRecordList = JSON.parseArray(certificateOld.getChangeRecord(), VehicleChangeRecordData.class);
                if (CollectionUtils.isNotEmpty(changeRecordList) && Objects.nonNull(changeRecordFileId)) {
                    changeRecordList.removeIf(item -> item.getFileId().equals(changeRecordFileId));
                }
                changeRecordDataList.addAll(changeRecordList);
            }
            certificate.setChangeRecord(JSON.toJSONString(changeRecordDataList));
//            if (certificate.getIsPresentHomePage()) {
//                certificate.setIsPresentHomePage(certificate.getIsPresentHomePage());
//            }
            certificate.setPictureCount(changeRecordDataList.size() + (Objects.nonNull(certificateOld.getFileId()) ? 1 : 0));
            this.updateById(certificate);
        } else {
            certificate.setChangeRecord(JSON.toJSONString(changeRecordDataList));
            certificate.setCreateBy(userId);
            certificate.setPictureCount(changeRecordDataList.size() + (Objects.nonNull(certificate.getFileId()) ? 1 : 0));
            this.save(certificate);
        }
    }

    private void certificateCheckAndSave(LeaseItemVehicleRegistrationCertificate certificate, Long userId) {
        LeaseItemVehicleRegistrationCertificate certificateOld = getCertificate(certificate.getLeaseItemInfoId(), certificate.getId(), certificate.getRegistrationPageNo());
        if (Objects.nonNull(certificateOld)) {
            certificate.setChangeRecord(certificateOld.getChangeRecord());
            certificate.setCreateTime(certificateOld.getCreateTime());
            certificate.setCreateBy(certificateOld.getCreateBy());
            if (Objects.isNull(certificate.getId())) {
                certificate.setId(certificateOld.getId());
            }
            int changeRecordCount = 0;
            if (StrUtil.isNotBlank(certificateOld.getChangeRecord())) {
                JSONArray jsonArray = JSONUtil.parseArray(certificateOld.getChangeRecord());
                changeRecordCount = jsonArray.size();
            }
            certificate.setPictureCount(changeRecordCount + (Objects.nonNull(certificate.getFileId()) ? 1 : 0));
            this.updateById(certificate);
        } else {
            certificate.setPictureCount(1);
            certificate.setCreateBy(userId);
            certificate.setPictureCount(Objects.nonNull(certificate.getFileId()) ? 1 : 0);
            this.save(certificate);
        }
    }

    private LeaseItemVehicleRegistrationCertificate getCertificate(Long leaseholdId, Long vehicleId, String registrationPageNo) {
        LambdaQueryWrapper<LeaseItemVehicleRegistrationCertificate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, leaseholdId);
        queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0);
        if (Objects.nonNull(vehicleId)) {
            queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getId, vehicleId);
        } else if (StringUtils.isNotEmpty(registrationPageNo)) {
            queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getRegistrationPageNo, registrationPageNo);
        } else {
            return null;
        }
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<PageR<LeaseVehicleRegistrationListRSP>> queryVehicleRegistrationList(LeaseVehicleRegistrationQueryREQ req) {
        if (Objects.isNull(req.getLeaseItemInfoId())) {
            return R.fail(ResultCode.PARAM_MISS);
        }

        //1.查询车证首页信息；根据条件查询
        Page<Object> page = PageHelper.startPage(req.getPage(), req.getPageSize());
        List<LeaseItemVehicleRegistrationCertificate> certificateList = baseMapper.selectList(Wrappers.<LeaseItemVehicleRegistrationCertificate>lambdaQuery()
                .eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getLeaseItemInfoId())
                .like(CharSequenceUtil.isNotEmpty(req.getRegistrationPageNo()), LeaseItemVehicleRegistrationCertificate::getRegistrationPageNo, req.getRegistrationPageNo())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleRegistrationOwner()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationOwner, req.getVehicleRegistrationOwner())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleVin()), LeaseItemVehicleRegistrationCertificate::getVehicleVin, req.getVehicleVin())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleRegistrationNumber()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationNumber, req.getVehicleRegistrationNumber())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleManufacturer()), LeaseItemVehicleRegistrationCertificate::getVehicleManufacturer, req.getVehicleManufacturer())
                .eq(CharSequenceUtil.isNotEmpty(req.getStatus()), LeaseItemVehicleRegistrationCertificate::getStatus, req.getStatus())
                .eq(Objects.nonNull(req.getPictureCount()), LeaseItemVehicleRegistrationCertificate::getPictureCount, req.getPictureCount())
                .eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0)
                .orderByDesc(LeaseItemVehicleRegistrationCertificate::getCreateTime));

        if (CollectionUtils.isEmpty(certificateList)) {
            return R.ok(PageR.empty(req.getPage(), req.getPageSize()));
        }

        //封装返回数据
        List<LeaseVehicleRegistrationListRSP> rsps = certificateList.stream().map(this::convertFromDBResult).collect(Collectors.toList());

        return R.ok(PageR.of(rsps, page.getTotal(), req.getPage(), req.getPageSize()));
    }

    private List<LeaseVehicleRegistrationListRSP> getVehicleRegistrationList(LeaseVehicleRegistrationQueryREQ req) {
        if (Objects.isNull(req.getLeaseItemInfoId())) {
            return new ArrayList<>();
        }
        List<LeaseItemVehicleRegistrationCertificate> certificateList = baseMapper.selectList(Wrappers.<LeaseItemVehicleRegistrationCertificate>lambdaQuery()
                .eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getLeaseItemInfoId())
                .like(CharSequenceUtil.isNotEmpty(req.getRegistrationPageNo()), LeaseItemVehicleRegistrationCertificate::getRegistrationPageNo, req.getRegistrationPageNo())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleRegistrationOwner()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationOwner, req.getVehicleRegistrationOwner())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleVin()), LeaseItemVehicleRegistrationCertificate::getVehicleVin, req.getVehicleVin())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleRegistrationNumber()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationNumber, req.getVehicleRegistrationNumber())
                .like(CharSequenceUtil.isNotEmpty(req.getVehicleManufacturer()), LeaseItemVehicleRegistrationCertificate::getVehicleManufacturer, req.getVehicleManufacturer())
                .eq(CharSequenceUtil.isNotEmpty(req.getStatus()), LeaseItemVehicleRegistrationCertificate::getStatus, req.getStatus())
                .eq(Objects.nonNull(req.getPictureCount()), LeaseItemVehicleRegistrationCertificate::getPictureCount, req.getPictureCount())
                .eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0)
                .in(ObjectUtils.isNotEmpty(req.getVehicleIds()), LeaseItemVehicleRegistrationCertificate::getId, req.getVehicleIds())
                .orderByDesc(LeaseItemVehicleRegistrationCertificate::getCreateTime));

        if (CollectionUtils.isEmpty(certificateList)) {
            return new ArrayList<>();
        }

        //封装返回数据
        return certificateList.stream().map(this::convertFromDBResult).collect(Collectors.toList());
    }

    private LeaseVehicleRegistrationListRSP convertFromDBResult(LeaseItemVehicleRegistrationCertificate certificate) {
        LeaseVehicleRegistrationListRSP rsp = new LeaseVehicleRegistrationListRSP();
        BeanUtils.copyProperties(certificate, rsp);
        List<String> fileNameList = new LinkedList<>();
        if (StrUtil.isNotBlank(certificate.getFileName())) {
            fileNameList.add(certificate.getFileName());
        }
        String changeRecord = certificate.getChangeRecord();
        if (StringUtils.isNotEmpty(changeRecord)) {
            List<VehicleChangeRecordData> changeRecordList = JSON.parseArray(changeRecord, VehicleChangeRecordData.class);
            //变更记录根据时间排序
            changeRecordList.forEach(item -> {
                if (StrUtil.isNotBlank(item.getFileName())) {
                    fileNameList.add(item.getFileName());
                }
                List<VehicleChangeRecordData.ChangeRecord> list = item.getChangeRecordList();
                if (CollectionUtils.isNotEmpty(list)) {
//                        List<VehicleChangeRecordData.ChangeRecord> sortedList = list.stream()
//                                .filter(f -> Objects.nonNull(f.getChangeDate()))
//                                .sorted(Comparator.comparing(VehicleChangeRecordData.ChangeRecord::getChangeDate))
//                                .collect(Collectors.toList());
                    list.sort((data1, data2) -> {
                        LocalDate d1 = Objects.nonNull(data1.getChangeDate()) ? data1.getChangeDate() : LocalDate.of(2099, 1, 1);
                        LocalDate d2 = Objects.nonNull(data2.getChangeDate()) ? data2.getChangeDate() : LocalDate.of(2099, 1, 1);
                        return d1.compareTo(d2);
                    });
                    item.setChangeRecordList(list);
                }
            });
            rsp.setChangeRecordRspList(changeRecordList);
        }
        if (CollectionUtil.isNotEmpty(fileNameList)) {
            rsp.setFileName(CharSequenceUtil.join("、", fileNameList));
        }
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> remove(LeaseVehicleRegistrationRemoveREQ req) {
        //操作类型没有不做处理
        if (StringUtils.isBlank(req.getOperateType())) {
            return R.ok();
        }

        LambdaUpdateWrapper<LeaseItemVehicleRegistrationCertificate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0);
        updateWrapper.eq(LeaseItemVehicleRegistrationCertificate::getLocked, 0);
        updateWrapper.eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getLeaseItemInfoId());
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getUpdateBy, AccountUtil.getLoginInfo().getId());
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getUpdateTime, LocalDateTime.now());

        //1.删除变更记录
        if (LeaseOperateEnum.DELETE_CHANGE_RECORD.name().equals(req.getOperateType())) {
            if (CollectionUtils.isEmpty(req.getChangeRecordIds())) {
                return R.ok();
            }
            //删除变更记录时，车证id，变更记录文件id只有一个
            if (CollectionUtils.isEmpty(req.getVehicleIds()) || req.getVehicleIds().size() != 1 || req.getChangeRecordIds().size() != 1) {
                return R.fail(ResultCode.PARAM_VALID_ERROR);
            }
            LeaseItemVehicleRegistrationCertificate certificate = this.getById(req.getVehicleIds().get(0));
            if (Objects.isNull(certificate)) {
                return R.ok();
            }
            String changeRecord = certificate.getChangeRecord();
            if (StringUtils.isBlank(changeRecord)) {
                return R.ok();
            }
            List<VehicleChangeRecordData> changeRecordData = JSON.parseArray(changeRecord, VehicleChangeRecordData.class);
            if (CollectionUtils.isEmpty(changeRecordData)) {
                return R.ok();
            }
            changeRecordData.removeIf(item -> item.getFileId().equals(req.getChangeRecordIds().get(0)));
            //都为空删除整条数据
            if (CollectionUtils.isEmpty(changeRecordData) && !certificate.getIsPresentHomePage()) {
                updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
            }
            int pictureCount = changeRecordData.size() + (Objects.nonNull(certificate.getFileId()) ? 1 : 0);
            updateWrapper.eq(LeaseItemVehicleRegistrationCertificate::getId, req.getVehicleIds().get(0));
            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getChangeRecord, JSON.toJSONString(changeRecordData));
            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getPictureCount, pictureCount);
            this.update(updateWrapper);
            return R.ok();
        }

        //2.删除首页信息
        if (LeaseOperateEnum.DELETE_HOME_PAGE.name().equals(req.getOperateType())) {
            //删除变更记录时，车证id只有一个
            if (CollectionUtils.isEmpty(req.getVehicleIds()) || req.getVehicleIds().size() != 1) {
                return R.fail(ResultCode.PARAM_MISS);
            }
            //删除指定首页时，车证id只有一个
            LeaseItemVehicleRegistrationCertificate certificate = this.getById(req.getVehicleIds().get(0));
            if (Objects.isNull(certificate)) {
                return R.ok();
            }
            updateWrapper.eq(LeaseItemVehicleRegistrationCertificate::getId, req.getVehicleIds().get(0));
            this.setNullUpdateWrapper(updateWrapper);
            List<VehicleChangeRecordData> vehicleChangeRecordData = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
            if (CollectionUtils.isEmpty(vehicleChangeRecordData)) {
                updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
            }
            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getPictureCount, certificate.getPictureCount() - 1);
            this.update(updateWrapper);
            return R.ok();
        }

        //3.非替换操作根据id删除
        if (!LeaseOperateEnum.REPLACE.name().equals(req.getOperateType())) {
            updateWrapper.in(LeaseItemVehicleRegistrationCertificate::getId, req.getVehicleIds());
        }
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getOperation, LeaseOperateEnum.DELETE.name());
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
        this.update(updateWrapper);
        return R.ok();
    }

    private void setNullUpdateWrapper(LambdaUpdateWrapper<LeaseItemVehicleRegistrationCertificate> updateWrapper) {
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getVehicleManufacturer, null);
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getFileId, null);
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getFileName, null);
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getVehicleVin, null);
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationNumber, null);
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationOwner, null);
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getIsPresentHomePage, false);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> update(LeaseVehicleRegistrationUpdateREQ req) {
        LambdaUpdateWrapper<LeaseItemVehicleRegistrationCertificate> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseItemVehicleRegistrationCertificate::getLocked, 0);
        updateWrapper.in(LeaseItemVehicleRegistrationCertificate::getId, req.getIds());
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getUpdateBy, AccountUtil.getLoginInfo().getId());
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getUpdateTime, LocalDateTime.now());
        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getStatus, "success");

        //批量修改
        if (req.getIds().size() > 1) {
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleManufacturer()), LeaseItemVehicleRegistrationCertificate::getVehicleManufacturer, req.getVehicleManufacturer());
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleVin()), LeaseItemVehicleRegistrationCertificate::getVehicleVin, req.getVehicleVin());
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleRegistrationNumber()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationNumber, req.getVehicleRegistrationNumber());
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleRegistrationOwner()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationOwner, req.getVehicleRegistrationOwner());
            this.update(updateWrapper);
            return R.ok();
        }

        LeaseItemVehicleRegistrationCertificate certificate = getCertificate(req.getLeaseholdId(), req.getIds().get(0), null);
        if (Objects.isNull(certificate)) {
            throw new MithrasException(ResultCode.RECORD_NOT_FOUND_ERROR.getMsg());
        }
        //1.是否变更：isChang为true表示变更，为false表示未变更
        if (Objects.nonNull(req.getIsChange()) && req.getIsChange()) {
            //一、首页变附页
            List<VehicleChangeRecordData> changeRecordList = new ArrayList<>();
            if (Objects.nonNull(req.getChangeRecordData())) {
                //1.若编号不为空，则查询是否存在相同编号的数据，若存在则挂在其下面,并删除原有编号的变更记录
                if (!req.getRegistrationPageNo().equals(certificate.getRegistrationPageNo())) {
                    LeaseItemVehicleRegistrationCertificate certificate2 = getCertificate(req.getLeaseholdId(), null, req.getRegistrationPageNo());
                    if (Objects.nonNull(certificate2)) {
                        String changeRecord = certificate2.getChangeRecord();
                        if (StringUtils.isNotBlank(changeRecord)) {
                            List<VehicleChangeRecordData> vehicleChangeRecordData = JSON.parseArray(changeRecord, VehicleChangeRecordData.class);
                            changeRecordList.addAll(vehicleChangeRecordData);
                        }
                        changeRecordList.add(req.getChangeRecordData());
                        certificate2.setChangeRecord(JSON.toJSONString(changeRecordList));
                        certificate2.setCreateBy(AccountUtil.getLoginInfo().getId());
                        certificate2.setUpdateTime(LocalDateTime.now());
                        this.updateById(certificate2);
                    } else {
                        //不为空，则新增
                        LeaseItemVehicleRegistrationCertificate certificateNew = new LeaseItemVehicleRegistrationCertificate();
                        certificateNew.setLeaseItemInfoId(req.getLeaseholdId());
                        certificateNew.setLocked(false);
                        certificateNew.setStatus("success");
                        certificateNew.setRegistrationPageNo(req.getRegistrationPageNo());
                        certificateNew.setIsPresentHomePage(false);
                        certificateNew.setCreateBy(AccountUtil.getLoginInfo().getId());
                        certificateNew.setUpdateBy(AccountUtil.getLoginInfo().getId());
                        List<VehicleChangeRecordData> list = ListUtil.toList(req.getChangeRecordData());
                        certificateNew.setChangeRecord(JSON.toJSONString(list));
                        this.save(certificateNew);
                    }
                    //删除原编号的变更记录
                    if (StringUtils.isBlank(certificate.getChangeRecord())) {
                        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
                    } else {
                        List<VehicleChangeRecordData> vehicleChangeRecordData = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
                        vehicleChangeRecordData.removeIf(item -> Objects.equals(item.getFileId(), req.getChangeRecordData().getFileId()));
                        updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getChangeRecord, JSON.toJSONString(vehicleChangeRecordData));
                        if (CollectionUtils.isEmpty(vehicleChangeRecordData) && certificate.getIsPresentHomePage()) {
                            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
                        }
                    }
                    this.update(updateWrapper);
                    return R.ok();
                }

                //2.编号相同，则再原数据的基础上进行修改
                changeRecordList = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
                changeRecordList.removeIf(item -> Objects.equals(item.getFileId(), req.getChangeRecordData().getFileId()));
                changeRecordList.add(req.getChangeRecordData());
                updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getChangeRecord, JSON.toJSONString(changeRecordList));
                this.setNullUpdateWrapper(updateWrapper);
                this.update(updateWrapper);
                return R.ok();
            }

            //二、附页变首页
            if (StringUtils.isNotBlank(req.getRegistrationPageNo())) {
                if (req.getRegistrationPageNo().equals(certificate.getRegistrationPageNo())) {
                    if (certificate.getIsPresentHomePage()) {
                        throw new MithrasException("首页已存在");
                    }
                    //若其没有首页，则对其赋值并保存,并删除相应的变更记录
                    certificate.setUpdateBy(AccountUtil.getLoginInfo().getId());
                    certificate.setIsPresentHomePage(true);
                    certificate.setFileId(req.getFileId());
                    certificate.setFileName(req.getFileName());
                    certificate.setVehicleVin(req.getVehicleVin());
                    certificate.setVehicleRegistrationOwner(req.getVehicleRegistrationOwner());
                    certificate.setVehicleRegistrationNumber(req.getVehicleRegistrationNumber());
                    certificate.setVehicleManufacturer(req.getVehicleManufacturer());
                    certificate.setStatus("success");
                    certificate.setUpdateTime(LocalDateTime.now());
                    List<VehicleChangeRecordData> changeRecords = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
                    changeRecords.removeIf(item -> Objects.equals(item.getFileId(), req.getFileId()));
                    certificate.setChangeRecord(JSON.toJSONString(changeRecords));
                    this.updateById(certificate);
                    return R.ok();
                }
                LeaseItemVehicleRegistrationCertificate certificate2 = getCertificate(req.getLeaseholdId(), null, req.getRegistrationPageNo());
                if (Objects.nonNull(certificate2)) {
                    if (certificate2.getIsPresentHomePage()) {
                        throw new MithrasException("首页已存在");
                    } else {
                        //若其没有首页，则对其赋值并保存
                        certificate2.setUpdateBy(AccountUtil.getLoginInfo().getId());
                        certificate2.setIsPresentHomePage(true);
                        certificate2.setFileId(req.getFileId());
                        certificate2.setFileName(req.getFileName());
                        certificate2.setVehicleVin(req.getVehicleVin());
                        certificate2.setVehicleRegistrationOwner(req.getVehicleRegistrationOwner());
                        certificate2.setVehicleRegistrationNumber(req.getVehicleRegistrationNumber());
                        certificate2.setVehicleManufacturer(req.getVehicleManufacturer());
                        certificate2.setStatus("success");
                        certificate2.setUpdateTime(LocalDateTime.now());
                        this.updateById(certificate2);
                        //删除原编号的首页信息
                        this.setNullUpdateWrapper(updateWrapper);
                        if (StringUtils.isNotBlank(certificate.getChangeRecord())) {
                            List<VehicleChangeRecordData> list = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
                            if (CollectionUtils.isEmpty(list)) {
                                updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
                            }
                        } else {
                            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
                        }
                        this.update(updateWrapper);
                        return R.ok();
                    }
                } else {
                    //为空，则新增
                    LeaseItemVehicleRegistrationCertificate certificateNew = new LeaseItemVehicleRegistrationCertificate();
                    BeanUtils.copyProperties(req, certificateNew);
                    certificateNew.setLeaseItemInfoId(req.getLeaseholdId());
                    certificateNew.setLocked(false);
                    certificateNew.setStatus("success");
                    certificateNew.setRegistrationPageNo(req.getRegistrationPageNo());
                    certificateNew.setIsPresentHomePage(true);
                    certificateNew.setCreateBy(AccountUtil.getLoginInfo().getId());
                    certificateNew.setUpdateBy(AccountUtil.getLoginInfo().getId());
                    this.save(certificateNew);
                }
            }
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleManufacturer()), LeaseItemVehicleRegistrationCertificate::getVehicleManufacturer, req.getVehicleManufacturer());
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleVin()), LeaseItemVehicleRegistrationCertificate::getVehicleVin, req.getVehicleVin());
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleRegistrationNumber()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationNumber, req.getVehicleRegistrationNumber());
            updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleRegistrationOwner()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationOwner, req.getVehicleRegistrationOwner());
            updateWrapper.set(ObjectUtils.isNotNull(req.getFileId()), LeaseItemVehicleRegistrationCertificate::getFileId, req.getFileId());
            updateWrapper.set(StringUtils.isNotEmpty(req.getFileName()), LeaseItemVehicleRegistrationCertificate::getFileName, req.getFileName());
            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getIsPresentHomePage, true);
            changeRecordList = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
            assert changeRecordList != null;
            changeRecordList.removeIf(item -> Objects.equals(item.getFileId(), req.getFileId()));
            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getChangeRecord, JSON.toJSONString(changeRecordList));
            this.update(updateWrapper);
            return R.ok();
        }

        //2。未变更的情况
        //修改变更记录
        if (Objects.nonNull(req.getChangeRecordData())) {
            if (Objects.nonNull(req.getRegistrationPageNo()) && !req.getRegistrationPageNo().equals(certificate.getRegistrationPageNo())) {
                LeaseItemVehicleRegistrationCertificate certificate2 = getCertificate(req.getLeaseholdId(), null, req.getRegistrationPageNo());
                if (Objects.nonNull(certificate2)) {
                    String changeRecord = certificate2.getChangeRecord();
                    List<VehicleChangeRecordData> vehicleChangeRecordData = new ArrayList<>();
                    if (StringUtils.isNotBlank(changeRecord)) {
                        vehicleChangeRecordData = JSON.parseArray(changeRecord, VehicleChangeRecordData.class);
                    }
                    vehicleChangeRecordData.add(req.getChangeRecordData());
                    certificate2.setChangeRecord(JSON.toJSONString(vehicleChangeRecordData));
                    certificate2.setCreateBy(AccountUtil.getLoginInfo().getId());
                    certificate2.setUpdateTime(LocalDateTime.now());
                    this.updateById(certificate2);
                } else {
                    LeaseItemVehicleRegistrationCertificate certificate1 = new LeaseItemVehicleRegistrationCertificate();
                    certificate1.setUpdateBy(AccountUtil.getLoginInfo().getId());
                    certificate1.setCreateBy(AccountUtil.getLoginInfo().getId());
                    certificate1.setStatus("success");
                    certificate1.setIsPresentHomePage(false);
                    certificate1.setRegistrationPageNo(req.getRegistrationPageNo());
                    certificate1.setLeaseItemInfoId(req.getLeaseholdId());
                    certificate1.setChangeRecord(JSON.toJSONString(Collections.singletonList(req.getChangeRecordData())));
                    this.save(certificate1);
                }
                //删除原编号的变更记录
                List<VehicleChangeRecordData> vehicleChangeRecordData = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
                vehicleChangeRecordData.removeIf(item -> Objects.equals(item.getFileId(), req.getChangeRecordData().getFileId()));
                if (CollectionUtils.isEmpty(vehicleChangeRecordData) && !certificate.getIsPresentHomePage()) {
                    updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
                } else {
                    updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getChangeRecord, JSON.toJSONString(vehicleChangeRecordData));
                }
                this.update(updateWrapper);
                return R.ok();
            }
            //只修改变更记录，其他不变走这里
            List<VehicleChangeRecordData> vehicleChangeRecordData = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
            assert vehicleChangeRecordData != null;
            vehicleChangeRecordData.removeIf(item -> Objects.equals(item.getFileId(), req.getChangeRecordData().getFileId()));
            vehicleChangeRecordData.add(req.getChangeRecordData());
            updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getChangeRecord, JSON.toJSONString(vehicleChangeRecordData));
            this.update(updateWrapper);
            return R.ok();
        }

        //修改主页
        if (Objects.nonNull(req.getRegistrationPageNo()) && !req.getRegistrationPageNo().equals(certificate.getRegistrationPageNo())) {
            this.setNullUpdateWrapper(updateWrapper);
            if (StringUtils.isBlank(certificate.getChangeRecord())) {
                updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
            } else {
                List<VehicleChangeRecordData> list = JSON.parseArray(certificate.getChangeRecord(), VehicleChangeRecordData.class);
                if (CollectionUtils.isEmpty(list)) {
                    updateWrapper.set(LeaseItemVehicleRegistrationCertificate::getDeleted, 1);
                }
            }
            this.update(updateWrapper);
            LeaseItemVehicleRegistrationCertificate certificate2 = getCertificate(req.getLeaseholdId(), null, req.getRegistrationPageNo());
            if (Objects.isNull(certificate2)) {
                LeaseItemVehicleRegistrationCertificate certificate1 = new LeaseItemVehicleRegistrationCertificate();
                BeanUtils.copyProperties(req, certificate1);
                certificate1.setLeaseItemInfoId(req.getLeaseholdId());
                certificate1.setFileId(certificate.getFileId());
                certificate1.setIsPresentHomePage(true);
                //识别状态
                if (StringUtils.isNotBlank(certificate.getVehicleManufacturer())
                        && StringUtils.isNotBlank(certificate.getVehicleVin())
                        && StringUtils.isNotBlank(certificate.getVehicleRegistrationNumber())
                        && StringUtils.isNotBlank(certificate.getVehicleRegistrationOwner())) {
                    certificate1.setStatus("success");
                } else {
                    certificate1.setStatus("failure");
                }
                certificate1.setFileName(certificate.getFileName());
                certificate1.setUpdateBy(AccountUtil.getLoginInfo().getId());
                certificate1.setCreateBy(AccountUtil.getLoginInfo().getId());
                this.save(certificate1);
                return R.ok();
            }
            if (certificate2.getIsPresentHomePage()) {
                throw new MithrasException("首页已存在");
            }
            BeanUtils.copyProperties(req, certificate2);
            certificate2.setStatus("success");
            certificate2.setLeaseItemInfoId(req.getLeaseholdId());
            this.updateById(certificate2);
            return R.ok();
        }
        updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleManufacturer()), LeaseItemVehicleRegistrationCertificate::getVehicleManufacturer, req.getVehicleManufacturer());
        updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleVin()), LeaseItemVehicleRegistrationCertificate::getVehicleVin, req.getVehicleVin());
        updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleRegistrationNumber()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationNumber, req.getVehicleRegistrationNumber());
        updateWrapper.set(StringUtils.isNotEmpty(req.getVehicleRegistrationOwner()), LeaseItemVehicleRegistrationCertificate::getVehicleRegistrationOwner, req.getVehicleRegistrationOwner());
        this.update(updateWrapper);
        return R.ok();
    }

    @Override
    public R<Void> lock(LeaseVehicleRegistrationLockREQ req) {
        LambdaUpdateWrapper<LeaseItemVehicleRegistrationCertificate> vehicleWrapper = Wrappers.<LeaseItemVehicleRegistrationCertificate>lambdaUpdate()
                .in(LeaseItemVehicleRegistrationCertificate::getId, req.getVehicleIds())
                .eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getLeaseItemInfoId())
                .set(LeaseItemVehicleRegistrationCertificate::getLocked, 1)
                .set(LeaseItemVehicleRegistrationCertificate::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVehicleRegistrationCertificate::getUpdateTime, LocalDateTime.now());
        this.update(vehicleWrapper);
        return R.ok();
    }

    @Override
    public R<Void> unlock(LeaseVehicleRegistrationUnlockREQ req) {
        LambdaUpdateWrapper<LeaseItemVehicleRegistrationCertificate> vehicleWrapper = Wrappers.<LeaseItemVehicleRegistrationCertificate>lambdaUpdate()
                .in(LeaseItemVehicleRegistrationCertificate::getId, req.getVehicleIds())
                .eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getLeaseItemInfoId())
                .set(LeaseItemVehicleRegistrationCertificate::getLocked, 0)
                .set(LeaseItemVehicleRegistrationCertificate::getUpdateBy, AccountUtil.getLoginInfo().getId())
                .set(LeaseItemVehicleRegistrationCertificate::getUpdateTime, LocalDateTime.now());
        this.update(vehicleWrapper);
        return R.ok();
    }

    @Override
    public R<LeaseVehicleRegistrationCountRSP> count(LeaseItemIdREQ req) {
        LambdaQueryWrapper<LeaseItemVehicleRegistrationCertificate> query = Wrappers.lambdaQuery();
        query.eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, req.getLeaseholdId())
                .eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0L);
        List<LeaseItemVehicleRegistrationCertificate> vehicleList = this.list(query);
        LeaseVehicleRegistrationCountRSP response = new LeaseVehicleRegistrationCountRSP();
        int succeed = 0;
        int failed = 0;
        int total = 0;
        if (CollectionUtil.isNotEmpty(vehicleList)) {
            total = vehicleList.size();
            for (LeaseItemVehicleRegistrationCertificate vehicle : vehicleList) {
                if ("failure".equals(vehicle.getStatus())) {
                    failed++;
                } else {
                    succeed++;
                }
            }
        }
        BusinessModuleEnum businessModule = BusinessModuleEnum.LEASE_TEXT;
        ProcessPageReq processPageReq = new ProcessPageReq();
        processPageReq.setBusinessKey(String.valueOf(req.getLeaseholdId()));
        processPageReq.setPageIndex(1);
        processPageReq.setPageSize(1);
        processPageReq.setModelKeyList(businessModule.getModelKeyList());
        processPageReq.setProcessStatusList(Collections.singletonList(ProcessBusinessStatusEnum.RUNNING.getType()));
        ProcessResp processResp = flowTaskApiService.queryProcess(processPageReq).getContents()
                .stream().findFirst().orElse(null);
        response.setCanLock(false);
        if (!Objects.isNull(processResp)) {
            String[] curTaskActivityIds = processResp.getCurTaskActivityIds().split(",");
            if (curTaskActivityIds.length > 0
                    && ("operationManagement").equalsIgnoreCase(curTaskActivityIds[0])) {
                response.setCanLock(true);
            }
        }
        response.setTotal(total);
        response.setSucceed(succeed);
        response.setFail(failed);
        return R.ok(response);
    }

    @Override
    public R<Void> exportExcel(LeaseVehicleRegistrationQueryREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("车证识别" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            download(httpServletResponse.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出车证列表发生未知异常", e);
            throw new MithrasException("导出车证列表发生未知异常");
        }
        return R.ok();
    }

    private void download(ServletOutputStream outputStream, LeaseVehicleRegistrationQueryREQ req) {
        //查询车证信息
        List<LeaseVehicleRegistrationListRSP> vehicleList = this.getVehicleRegistrationList(req);
        if (CollUtil.isEmpty(vehicleList)) {
            throw new MithrasException("不存在数据，导出失败");
        }

        List<LeaseItemVehicleRegistrationCertificateExcelModel> excelModels = new ArrayList<>();
        for (LeaseVehicleRegistrationListRSP vehicle : vehicleList) {
            LeaseItemVehicleRegistrationCertificateExcelModel model = LeaseItemVehicleRegistrationCertificateExcelModel.builder()
                    .vehicleRegistrationNumber(vehicle.getVehicleRegistrationNumber())
                    .vehicleManufacturer(vehicle.getVehicleManufacturer())
                    .vehicleVin(vehicle.getVehicleVin())
                    .vehicleRegistrationOwner(vehicle.getVehicleRegistrationOwner())
                    .registrationPageNo(vehicle.getRegistrationPageNo())
                    .pictureCount(vehicle.getPictureCount())
                    .fileName(vehicle.getFileName())
                    .build();
            if (CollectionUtil.isNotEmpty(vehicle.getChangeRecordRspList())) {
                List<String> list = new LinkedList<>();
                for (VehicleChangeRecordData vehicleChangeRecordData : vehicle.getChangeRecordRspList()) {
                    if (CollectionUtil.isNotEmpty(vehicleChangeRecordData.getChangeRecordList())) {
                        for (VehicleChangeRecordData.ChangeRecord changeRecord : vehicleChangeRecordData.getChangeRecordList()) {
                            if (StrUtil.isNotBlank(changeRecord.getName())) {
                                list.add(changeRecord.getName());
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(list)) {
                    model.setChangeRecordStr(CharSequenceUtil.join("、", list));
                }
            }
            excelModels.add(model);
        }
        leaseVehicleExcelExporter.exportExcel(excelModels, outputStream);
    }

    //唯一的编号
    private static synchronized String getBatchNum() {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String LocalDate = LocalDateTime.now().format(ofPattern);
        //3位随机数
        String randomNumeric = RandomStringUtils.randomNumeric(3);
        return LocalDate + randomNumeric;
    }

    @Override
    public List<String> getLockedPrefixFileName(Long leaseholdId, String operateType) {
        LambdaQueryWrapper<LeaseItemVehicleRegistrationCertificate> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getLeaseItemInfoId, leaseholdId);
        queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getDeleted, 0);
        if (LeaseOperateEnum.REPLACE.name().equalsIgnoreCase(operateType)) {
            queryWrapper.eq(LeaseItemVehicleRegistrationCertificate::getLocked, "1");
        }
        List<LeaseItemVehicleRegistrationCertificate> certificateList = this.list(queryWrapper);

        //获取变更记录文件名集合
        List<String> fileNameList = certificateList.stream().map(item -> JSON.parseArray(item.getChangeRecord(), VehicleChangeRecordData.class))
                .filter(CollectionUtils::isNotEmpty)
                .flatMap(Collection::stream)
                .map(VehicleChangeRecordData::getFileName)
                .collect(Collectors.toList());

        //获取正本文件名
        List<String> list = certificateList.stream().map(LeaseItemVehicleRegistrationCertificate::getFileName).collect(Collectors.toList());
        fileNameList.addAll(list);

        return fileNameList;
    }
}
