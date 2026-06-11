package cn.zswltech.mithras.application.orchestration.facade.afterlease;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.afterlease.application.AfterLeaseReportApplicationService;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseReportUploadREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportDownloadREQ;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportRemoveREQ;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.foundation.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.application.orchestration.auth.checker.common.CommonAddSubAuthCheckerNew;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.afterlease.enums.AfterLeaseAdjustMaterialsEnum;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseReportService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AfterLeaseReportController
 * @Description
 * @Author jackerhe
 * @Date 2022/11/11 11:35 上午
 * @Version 1.0
 **/
@Service
@Slf4j
public class AfterLeaseReportFacade implements AfterLeaseReportApplicationService {

    @Resource
    private AfterLeaseReportService afterLeaseReportService;
    @Resource
    private UserService userService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public R<List<Pair<String, List<ProjReviewReportListRSP>>>> list(@Valid AfterLeaseReportListREQ req) {
        List<MaterialsList> materialsList = afterLeaseReportService.list(req);
        if (CollectionUtils.isEmpty(materialsList)) {
            return R.ok();
        }
        List<ProjReviewReportListRSP> rspList = materialsList.stream().map(item -> {
            ProjReviewReportListRSP rsp = new ProjReviewReportListRSP();
            rsp.setId(item.getId());
            AfterLeaseAdjustMaterialsEnum type = AfterLeaseAdjustMaterialsEnum.getByName(item.getMaterialsType());
            if (Objects.nonNull(type)) {
                rsp.setTypeName(type.getDisplay());
                rsp.setSort(type.getSort());
            }
            rsp.setFileName(item.getFilename());
            rsp.setMaterialsType(item.getMaterialsType());
            Response<UserVO> response = userService.getUserInfoById(item.getCreateBy());
            if (response != null && response.isSuccess() && response.getData() != null) {
                rsp.setCreator(response.getData().getUserName());
            } else {
                log.warn("没有找到创建人信息[id: {}, response: {}]", item.getCreateBy(), JSONUtil.toJsonStr(response));
            }
            rsp.setCreateTime(LocalDateTimeUtil.format(item.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            rsp.setCreateTimestamp(Optional.ofNullable(item.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
            return rsp;
        }).sorted(Comparator.comparingInt(ProjReviewReportListRSP::getSort)).collect(Collectors.toList());// 排序

        Map<String, List<ProjReviewReportListRSP>> mapRes = new HashMap<>();
        for (ProjReviewReportListRSP rspDatum : rspList) {
            List<ProjReviewReportListRSP> orDefault = mapRes.getOrDefault(rspDatum.getMaterialsType(), new ArrayList<>());
            orDefault.add(rspDatum);
            mapRes.put(rspDatum.getMaterialsType(), orDefault);
        }
        List<AfterLeaseAdjustMaterialsEnum> types = rspList.stream()
                .map(ProjReviewReportListRSP::getMaterialsType)
                .map(AfterLeaseAdjustMaterialsEnum::valueOf)
                .distinct()
                .sorted(Comparator.comparingInt(AfterLeaseAdjustMaterialsEnum::getSort))
                .collect(Collectors.toList());
        List<Pair<String,List<ProjReviewReportListRSP>>> sortedRes = new ArrayList<>();
        for (AfterLeaseAdjustMaterialsEnum type : types) {
            List<ProjReviewReportListRSP> list = mapRes.get(type.name());
            list.sort(new CommonFileSortComparator());
            sortedRes.add(new Pair<>(type.display(), list));
        }
        return R.ok(sortedRes);
    }

    @Override
    @Deprecated
    public R<FileListRSP> download(@Valid ProjReviewReportDownloadREQ req) {
        return R.ok(materialsListService.download(req.getRecordId()));
    }

    @Override
    @DataAuthCheck(keyFieldName = "adjustId", paramIndex = 1, checkerClass = CommonAddSubAuthCheckerNew.class, businessModule = "ADJUST")
    public R<Void> upload(MultipartFile file, AfterLeaseReportUploadREQ req) {
        afterLeaseReportService.upload(file, req);
        return R.ok();
    }

    @Override
    public R<Void> remove(@Valid ProjReviewReportRemoveREQ projReviewReportRemoveREQ) {
        afterLeaseReportService.remove(projReviewReportRemoveREQ.getId());
        return R.ok();
    }

}
