package cn.zswltech.mithras.application.orchestration.facade.projreview;

import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewReportApplicationService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projreview.report.*;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/5
 * @description
 */
@Slf4j
@Service
public class ProjReviewReportFacade implements ProjReviewReportApplicationService {
    @Resource
    private ProjReviewReportService projReviewReportService;
    @Resource
    private UserService userService;
    @Resource
    private HttpServletResponse httpServletResponse;
    @Resource
    private MaterialsListService materialsListService;

    @Override
    public R< List<Pair<String,List<ProjReviewReportListRSP>>>> list(ProjReviewReportListREQ projReviewReportListREQ) {
        List<MaterialsList> materialsList = projReviewReportService.list(projReviewReportListREQ);
        if (CollectionUtils.isEmpty(materialsList)) {
            return R.ok();
        }
        List<ProjReviewReportListRSP> rspList = materialsList.stream().map(item -> {
            ProjReviewReportListRSP rsp = new ProjReviewReportListRSP();
            rsp.setId(item.getId());
            ProjReviewMaterialsEnum type = ProjReviewMaterialsEnum.getByName(item.getMaterialsType());
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
            rsp.setCreateTimestamp(Optional.ofNullable(item.getCreateTime()).map(LocalDateTimeUtil::toEpochMilli).orElse(0L));
            rsp.setCreateTime(LocalDateTimeUtil.format(item.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            return rsp;
        }).sorted(Comparator.comparingInt(ProjReviewReportListRSP::getSort)).collect(Collectors.toList());// 排序

        Map<String, List<ProjReviewReportListRSP>> mapRes = new HashMap<>();
        for (ProjReviewReportListRSP rspDatum : rspList) {
            List<ProjReviewReportListRSP> orDefault = mapRes.getOrDefault(rspDatum.getMaterialsType(), new ArrayList<>());
            orDefault.add(rspDatum);
            mapRes.put(rspDatum.getMaterialsType(), orDefault);
        }
        List<ProjReviewMaterialsEnum> types = rspList.stream()
                .map(ProjReviewReportListRSP::getMaterialsType)
                .map(ProjReviewMaterialsEnum::valueOf)
                .distinct()
                .sorted(Comparator.comparingInt(ProjReviewMaterialsEnum::getSort))
                .collect(Collectors.toList());
        List<Pair<String,List<ProjReviewReportListRSP>>> sortedRes = new ArrayList<>();
        for (ProjReviewMaterialsEnum type : types) {
            List<ProjReviewReportListRSP> reportList = mapRes.get(type.name());
            reportList.sort(new CommonFileSortComparator());
            sortedRes.add(new Pair<>(type.name(), reportList));
        }
        return R.ok(sortedRes);
    }

    @Override
    public R<Long> generate(ProjReviewReportGenerateREQ projReviewReportGenerateREQ) throws Exception {
        return R.ok(projReviewReportService.generate(projReviewReportGenerateREQ));
    }

    @Override
    public R<FileListRSP> download(ProjReviewReportDownloadREQ projReviewReportDownloadREQ) throws IOException {
        MaterialsList materialsList = materialsListService.getById(projReviewReportDownloadREQ.getRecordId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应的报告文件");
        }
        return R.ok(materialsListService.download(projReviewReportDownloadREQ.getRecordId()));
    }

    @Override
    public R<Void> upload(MultipartFile file, ProjReviewReportUploadREQ projReviewReportUploadREQ) {
        projReviewReportService.upload(file, projReviewReportUploadREQ);
        return R.ok();
    }

    @Override
    public R<Void> remove(ProjReviewReportRemoveREQ projReviewReportRemoveREQ) {
        projReviewReportService.remove(projReviewReportRemoveREQ.getId());
        return R.ok();
    }

    @Override
    public R<Long> generateEarningsRate(@Valid ProjReviewReportGenerateREQ projReviewReportGenerateREQ) {
        return R.ok(projReviewReportService.generateEarningsRate(projReviewReportGenerateREQ));
    }
}
