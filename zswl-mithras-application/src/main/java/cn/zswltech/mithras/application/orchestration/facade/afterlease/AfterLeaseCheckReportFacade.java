package cn.zswltech.mithras.application.orchestration.facade.afterlease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.*;
import cn.zswltech.mithras.dto.client.subjectitem.CorpSubjectItemListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListRSP;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.afterlease.application.auth.AfterLeaseCheckReportModifyMainChecker;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportMaterialsEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseCheckReportTypeEnum;
import cn.zswltech.mithras.afterlease.enums.NewAfterLeaseCheckMaterialsEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportFinance;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckReportMeta;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportMetaService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanClientService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportBaseService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportDownloadService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportExtraService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportFinanceService;
import cn.zswltech.mithras.afterlease.application.NewAfterLeaseCheckReportDetailService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/11/16
 * @description
 */
@Slf4j
@Service
public class AfterLeaseCheckReportFacade implements AfterLeaseCheckReportApplicationService {
    @Resource
    private AfterLeaseCheckReportMetaService afterLeaseCheckReportMetaService;
    @Resource
    private AfterLeaseCheckPlanClientService afterLeaseCheckPlanClientService;
    @Resource
    private AfterLeaseCheckReportBaseService afterLeaseCheckReportBaseService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AfterLeaseCheckReportExtraService afterLeaseCheckReportExtraService;
    @Resource
    private AfterLeaseCheckReportDownloadService afterLeaseCheckReportDownloadService;
    @Resource
    private AfterLeaseCheckReportFinanceService afterLeaseCheckReportFinanceService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private NewAfterLeaseCheckReportDetailService afterLeaseCheckReportDetailService;

    @DataAuthCheck(keyFieldName = "checkPlanClientId", checkerClass = AfterLeaseCheckReportModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<Long> saveReportBase(@Valid AfterLeaseCheckReportBaseREQ req) {
        return R.ok(afterLeaseCheckReportBaseService.saveReportBase(req));
    }

    @Override
    public R<AfterLeaseCheckReportBaseRSP> getReportBase(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckReportBaseService.getCheckReportBaseRSP(req.getId(), req.getVersion()));
    }

    @DataAuthCheck(keyFieldName = "checkPlanClientId", checkerClass = AfterLeaseCheckReportModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<Void> saveReportContent(@Valid AfterLeaseCheckReportCSREQ req) {
        boolean success = afterLeaseCheckReportDetailService.saveReportDetail(req, NewAfterLeaseCheckReportDetailService.TYPE_CONTENT);
        if (!success) {
            return R.fail("保存检查内容失败");
        }
        return R.ok();
    }

    @Override
    public R<AfterLeaseCheckReportCSRSP> getReportContent(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckReportDetailService.getReportDetail(req.getId(), req.getVersion(), NewAfterLeaseCheckReportDetailService.TYPE_CONTENT));
    }

    @DataAuthCheck(keyFieldName = "checkPlanClientId", checkerClass = AfterLeaseCheckReportModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<Void> saveReportSummary(@Valid AfterLeaseCheckReportCSREQ req) {
        boolean success = afterLeaseCheckReportDetailService.saveReportDetail(req, NewAfterLeaseCheckReportDetailService.TYPE_SUMMARY);
        if (!success) {
            return R.fail("保存检查总结失败");
        }
        return R.ok();
    }

    @Override
    public R<AfterLeaseCheckReportCSRSP> getReportSummary(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckReportDetailService.getReportDetail(req.getId(), req.getVersion(), NewAfterLeaseCheckReportDetailService.TYPE_SUMMARY));
    }

    @Override
    public R<List<AfterLeaseCheckReportNonPublicExtraRSP>> getNonPublicExtra(@Valid SinglePkREQ req) {
        return R.ok(afterLeaseCheckReportExtraService.listNonPublicExtraRSP(req.getId(), req.getVersion()));
    }

    @DataAuthCheck(keyFieldName = "checkPlanClientId", checkerClass = AfterLeaseCheckReportModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<Void> saveNonPublicExtra(@Valid AfterLeaseCheckReportNonPublicExtraREQ req) {
        afterLeaseCheckReportExtraService.saveNonPublicExtra(req);
        return R.ok();
    }

    //
    //@DataAuthCheck(keyFieldName = "id", checkerClass = AfterLeaseCheckReportModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<Void> uploadNonPublicReportFile(@Valid AfterLeaseCheckReportFileREQ req) {
        try {
            materialsListService.add(
                    req.getFile().getInputStream(),
                    req.getFile().getOriginalFilename(),
                    req.getId(),
                    NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_NON_PUBLIC_ATTACHMENT.name(),
                    req.getFileType(),
                    BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(),
                    YesOrNoNumberEnum.NO
            );
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("上传检查附件发生异常[{}]", JSONUtil.toJsonStr(req), e);
            throw new MithrasException("上传检查附件发生异常");
        }
        return R.ok();
    }

    @Override
    public R<List<Pair<String, List<ProjReviewReportListRSP>>>> listNonPublicReportFile(@Valid SinglePkREQ singlePkREQ) {
        List<MaterialsList> materialsListList = materialsListService.list(
                BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(),
                Collections.singletonList(NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_NON_PUBLIC_ATTACHMENT.name()),
                Collections.singletonList(singlePkREQ.getId())
        );
        Map<String, List<MaterialsList>> materialsMap = materialsListList.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialSubType));
        List<Long> creatorIds = materialsListList.stream().map(BaseModel::getCreateBy).collect(Collectors.toList());
        Map<Long, String> userNameMap;
        if (CollectionUtil.isEmpty(creatorIds)) {
            userNameMap = Collections.emptyMap();
        } else {
            userNameMap = id2NameService.sysUserId2Name(creatorIds);
        }
        List<Pair<String, List<ProjReviewReportListRSP>>> result = new LinkedList<>();
        for (AfterLeaseCheckReportMaterialsEnum checkReportMaterialsEnum : AfterLeaseCheckReportMaterialsEnum.values()) {
            List<MaterialsList> list = materialsMap.get(checkReportMaterialsEnum.name());
            Pair<String, List<ProjReviewReportListRSP>> pair;
            if (CollectionUtil.isEmpty(list)) {
                pair = new Pair<>(checkReportMaterialsEnum.name(), Collections.emptyList());
            } else {
                List<ProjReviewReportListRSP> pairList = list.stream().map(item -> {
                    ProjReviewReportListRSP projReviewReportListRSP = new ProjReviewReportListRSP();
                    projReviewReportListRSP.setId(item.getId());
                    projReviewReportListRSP.setTypeName(checkReportMaterialsEnum.display());
                    projReviewReportListRSP.setMaterialsType(item.getMaterialsType());
                    projReviewReportListRSP.setMaterialsSubType(item.getMaterialSubType());
                    projReviewReportListRSP.setFileName(item.getFilename());
                    projReviewReportListRSP.setCreator(userNameMap.get(item.getCreateBy()));
                    projReviewReportListRSP.setCreateTime(LocalDateTimeUtil.format(item.getCreateTime(), DatePattern.NORM_DATE_PATTERN));
                    return projReviewReportListRSP;
                }).collect(Collectors.toList());
                pair = new Pair<>(checkReportMaterialsEnum.name(), pairList);
            }
            result.add(pair);
        }
        return R.ok(result);
    }

    @Override
    public void downloadSingleReport(@Valid SinglePkREQ req) {
        try {
            NewAfterLeaseCheckPlanClient checkPlanClient = afterLeaseCheckPlanClientService.getById(req.getId());
            Assert.notNull(checkPlanClient, () -> MithrasException.newException("检查客户记录不存在"));
            NewAfterLeaseCheckReportMeta checkReportMeta = afterLeaseCheckReportMetaService.getByCheckPlanClientId(req.getId());
            Assert.notNull(checkReportMeta, () -> MithrasException.newException("检查报告元数据不存在"));
            String suffix;
            if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.PUBLIC.name())) {
                suffix = GlobalConstants.COMPRESSED_FILE_SUFFIX;
            } else if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.NON_PUBLIC.name())) {
                suffix = GlobalConstants.COMPRESSED_FILE_SUFFIX;
            } else if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.LOW_RISK.name())) {
                suffix = GlobalConstants.OFFICE_WORD_SUFFIX;
            } else if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.BUS.name())) {
                suffix = GlobalConstants.OFFICE_WORD_SUFFIX;
            } else if (Objects.equals(checkReportMeta.getReportType(), AfterLeaseCheckReportTypeEnum.STATE_OWNED_ASSET.name())) {
                suffix = GlobalConstants.OFFICE_WORD_SUFFIX;
            } else {
                throw new MithrasException("非法的检查报告类型");
            }
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租后检查报告" + suffix, StandardCharsets.UTF_8.name()));
            afterLeaseCheckReportDownloadService.downloadSingleClientReport(httpServletResponse.getOutputStream(), checkPlanClient);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载租后检查报告生未知异常[checkPlanProjectId: {}]", req.getId(),  e);
            throw new MithrasException("下载租后检查报告生未知异常");
        }
    }

    @Override
    public void downloadBatchReport(@Valid MultiplePkREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租后检查报告" + GlobalConstants.COMPRESSED_FILE_SUFFIX, StandardCharsets.UTF_8.name()));
            afterLeaseCheckReportDownloadService.downloadBatchClientReport(httpServletResponse.getOutputStream(), req.getIds());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载租后检查报告生未知异常[checkPlanProjectIds: {}]", JSONUtil.toJsonStr(req.getIds()),  e);
            throw new MithrasException("下载租后检查报告生未知异常");
        }
    }

    @Override
    public void downloadDeptReport(@Valid AfterLeaseCheckReportDownloadREQ req) {
        try {
            httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("租后检查报告" + GlobalConstants.COMPRESSED_FILE_SUFFIX, StandardCharsets.UTF_8.name()));
            afterLeaseCheckReportDownloadService.downloadBizDeptClientReport(httpServletResponse.getOutputStream(), req.getPlanId(), req.getDeptId());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("下载租后检查报告生未知异常[req: {}]", JSONUtil.toJsonStr(req),  e);
            throw new MithrasException("下载租后检查报告生未知异常");
        }
    }

    @Override
    public R<AfterLeaseCheckReportFinanceRSP> getReportFinanceSnapshot(@Valid AfterLeaseCheckReportFinanceREQ req) {
        NewAfterLeaseCheckReportFinance reportFinance = afterLeaseCheckReportFinanceService.getSpecificSnapshot(req);
        if (Objects.isNull(reportFinance)) {
            return R.ok();
        }
        AfterLeaseCheckReportFinanceRSP rsp = new AfterLeaseCheckReportFinanceRSP();
        if (StrUtil.isNotBlank(reportFinance.getQueryJson())) {
            rsp.setQueryData(JSONUtil.toBean(reportFinance.getQueryJson(), AfterLeaseCheckReportFinanceRSP.QueryData.class));
        }
        if (StrUtil.isNotBlank(reportFinance.getDataJson())) {
            rsp.setResultData(JSONUtil.toList(reportFinance.getDataJson(), CorpSubjectItemListRSP.class));
        }
        return R.ok(rsp);
    }

    @DataAuthCheck(keyFieldName = "checkPlanClientId", checkerClass = AfterLeaseCheckReportModifyMainChecker.class, paramType = DataAuthCheck.ParamType.OBJECT, businessModule = "NEW_AFTER_LEASE_CHECK_REPORT")
    @Override
    public R<Void> saveReportFinanceSnapshot(@Valid AfterLeaseCheckReportFinanceSaveREQ req) {
        afterLeaseCheckReportFinanceService.saveSnapshot(req);
        return R.ok();
    }

    @Override
    public R<List<Pair<String, List<ProjReviewReportListRSP>>>> listFinanceDataFile(@Valid SinglePkREQ req) {
        List<MaterialsList> materialsListList = materialsListService.list(
                BusinessModuleEnum.NEW_AFTER_LEASE_CHECK_REPORT.name(),
                Collections.singletonList(NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_PUBLIC_FINANCE.name()),
                Collections.singletonList(req.getId())
        );
        if (CollectionUtil.isEmpty(materialsListList)) {
            return R.ok(Collections.emptyList());
        }
        Set<Long> userIds = materialsListList.stream().map(BaseModel::getCreateBy).collect(Collectors.toSet());
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIds);
        List<ProjReviewReportListRSP> list = materialsListList.stream().map(item -> {
            ProjReviewReportListRSP rsp = new ProjReviewReportListRSP();
            rsp.setId(item.getId());
            rsp.setMaterialsType(item.getMaterialsType());
            rsp.setMaterialsSubType(item.getMaterialSubType());
            rsp.setFileName(item.getFilename());
            rsp.setCreator(userNameMap.get(item.getCreateBy()));
            rsp.setCreateTime(LocalDateTimeUtil.format(item.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            return rsp;
        }).collect(Collectors.toList());
        return R.ok(Collections.singletonList(new Pair<>(NewAfterLeaseCheckMaterialsEnum.CHECK_REPORT_PUBLIC_FINANCE.name(), list)));
    }
}
