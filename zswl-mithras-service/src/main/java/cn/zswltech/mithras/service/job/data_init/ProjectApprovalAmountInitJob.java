package cn.zswltech.mithras.service.job.data_init;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.blackgray.enums.BusinessType;
import cn.zswltech.mithras.third.enums.capital.BizTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ProjItemStatus;
import cn.zswltech.mithras.service.job.data_init.dto.ProjectApprovalAmountExcelModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.projectprocess.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @version 1.0
 * @description
 * @since 2025/8/27 08:52
 **/
@Slf4j
@Component
public class ProjectApprovalAmountInitJob {

    @Autowired
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Autowired
    private ProjReviewLeasePriceService leasePriceService;
    @Autowired
    private ProjReviewLeasePriceLibService leasePriceLibService;
    @Autowired
    private ProjReviewAocPriceService aocPriceService;
    @Autowired
    private ProjReviewAocPriceLibService aocPriceLibService;
    @Autowired
    private ProjReviewFactoringPriceService factoringPriceService;
    @Autowired
    private ProjReviewFactoringPriceLibService factoringPriceLibService;

    @XxlJob("projectApprovalAmountInitJob")
    @Transactional(rollbackFor = Throwable.class)
    public void projectApprovalAmountInitJob() {
        log.info("项目批复金额初始化任务开始");
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/doc/project_approval_amount.xlsx");
            if (inputStream == null) {
                log.error("项目批复金额初始化模版不存在");
                return;
            }
            List<ProjectApprovalAmountExcelModel> excelModelList = ExcelUtil.getReader(inputStream).read(0, 0, ProjectApprovalAmountExcelModel.class);
            inputStream.close();

            if (CollUtil.isEmpty(excelModelList)) {
                log.error("项目批复金额初始化模版数据为空");
                return;
            }
            // 查询项目列表
            List<String> collected = excelModelList.stream().map(ProjectApprovalAmountExcelModel::getProjectName).collect(Collectors.toList());
            List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                    .in(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.NEW.name(), RecordStatus.TAKE_EFFECT.name()));
            Map<String, List<ProjReviewBaseInfo>> projReviewBaseInfoMap = projReviewBaseInfos.stream().collect(Collectors.groupingBy(e -> e.getProjName().trim()));
            List<Long> projectIds = projReviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
            List<ProjReviewLeasePrice> projReviewLeasePrices = leasePriceService.listByProjectIds(projectIds);
            Map<Long, ProjReviewLeasePrice> leasePriceMap = new HashMap<>();
            if (CollUtil.isNotEmpty(projReviewLeasePrices)) {
                leasePriceMap = projReviewLeasePrices.stream().collect(Collectors.toMap(ProjReviewLeasePrice::getProjectId, v -> v));
            }
            List<ProjReviewFactoringPrice> projReviewFactoringPrices = factoringPriceService.listByProjectIds(projectIds);
            Map<Long, ProjReviewFactoringPrice> factoringPriceMap = new HashMap<>();
            if (CollUtil.isNotEmpty(projReviewFactoringPrices)) {
                factoringPriceMap = projReviewFactoringPrices.stream().collect(Collectors.toMap(ProjReviewFactoringPrice::getProjectId, v -> v));
            }
            List<ProjReviewAocPrice> projReviewAocPrices = aocPriceService.listByProjectIds(projectIds);
            Map<Long, ProjReviewAocPrice> aocPriceMap = new HashMap<>();
            if (CollUtil.isNotEmpty(projReviewAocPrices)) {
                aocPriceMap = projReviewAocPrices.stream().collect(Collectors.toMap(ProjReviewAocPrice::getProjectId, v -> v));
            }

            // 准备3个容器
            List<ProjReviewLeasePrice> leasePrices = new ArrayList<>();
            List<ProjReviewFactoringPrice> factoringPrices = new ArrayList<>();
            List<ProjReviewAocPrice> aocPrices = new ArrayList<>();
            for (ProjectApprovalAmountExcelModel excelModel : excelModelList) {
                List<ProjReviewBaseInfo> baseInfos = projReviewBaseInfoMap.get(excelModel.getProjectName());
                if (Objects.isNull(baseInfos)) {
                    log.error("项目信息怎么可能是空的呢！！！===>{}", excelModel.getProjectName());
                    continue;
                }
                for (ProjReviewBaseInfo baseInfo : baseInfos) {
                    // 3种业务类型对应的报价方案不一样
                    if (baseInfo == null || CharSequenceUtil.isBlank(baseInfo.getBizType())) {
                        continue;
                    }
                    long projectApprovalAmount = excelModel.getProjectApprovalAmount().multiply(new BigDecimal(100000000)).longValue();
                    // 租赁
                    if (CharSequenceUtil.equalsAny(baseInfo.getBizType(), ProjectBizType.ZL.name(), ProjectBizType.ZZ.name())) {
                        ProjReviewLeasePrice leasePrice = leasePriceMap.get(baseInfo.getId());
                        if (leasePrice == null) {
                            continue;
                        }
                        leasePrice.setProjectApprovalAmount(projectApprovalAmount);
                        leasePrices.add(leasePrice);

                        // 更新版本表
                        leasePriceLibService.lambdaUpdate()
                                .set(ProjReviewLeasePriceLib::getProjectApprovalAmount, projectApprovalAmount)
                                .eq(ProjReviewLeasePriceLib::getProjectId, baseInfo.getId())
                                .eq(ProjReviewLeasePriceLib::getOriginId, leasePrice.getId())
                                .update();
                    }

                    // 保理
                    if (baseInfo.getBizType().equals(ProjectBizType.BL.name())) {
                        ProjReviewFactoringPrice factoringPrice = factoringPriceMap.get(baseInfo.getId());
                        if (factoringPrice == null) {
                            continue;
                        }
                        factoringPrice.setProjectApprovalAmount(projectApprovalAmount);
                        factoringPrices.add(factoringPrice);

                        // 更新版本表
                        factoringPriceLibService.lambdaUpdate()
                                .eq(ProjReviewFactoringPrice::getProjectId, baseInfo.getId())
                                .eq(ProjReviewFactoringPriceLib::getOriginId, factoringPrice.getId())
                                .set(ProjReviewFactoringPriceLib::getProjectApprovalAmount, projectApprovalAmount)
                                .update();
                    }

                    // 债权转让
                    if (CharSequenceUtil.equals(ProjectBizType.ZR.name(), baseInfo.getBizType())) {
                        ProjReviewAocPrice aocPrice = aocPriceMap.get(baseInfo.getId());
                        if (aocPrice == null) {
                            continue;
                        }
                        aocPrice.setProjectApprovalAmount(projectApprovalAmount);
                        aocPrices.add(aocPrice);

                        // 更新版本表
                        aocPriceLibService.lambdaUpdate()
                                .eq(ProjReviewAocPrice::getProjectId, baseInfo.getId())
                                .eq(ProjReviewAocPriceLib::getOriginId, aocPrice.getId())
                                .set(ProjReviewAocPriceLib::getProjectApprovalAmount, projectApprovalAmount)
                                .update();
                    }
                }
            }
            if (CollUtil.isNotEmpty(leasePrices)) {
                leasePriceService.updateBatchById(leasePrices);
            }
            if (CollUtil.isNotEmpty(factoringPrices)) {
                factoringPriceService.updateBatchById(factoringPrices);
            }
            if (CollUtil.isNotEmpty(aocPrices)) {
                aocPriceService.updateBatchById(aocPrices);
            }
        } catch (Exception e) {
            log.error("项目批复金额初始化任务失败", e);
        }
        log.info("项目批复金额初始化任务结束");
    }
}
