package cn.zswltech.mithras.application.orchestration.facade.projpricing;

import cn.zswltech.mithras.projectprocess.application.projpricing.ProjPricingReportApplicationService;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.dto.projpricing.report.*;
import cn.zswltech.mithras.dto.projreview.report.*;
import cn.zswltech.mithras.foundation.util.CommonFileSortComparator;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProjPricingReportFacade implements ProjPricingReportApplicationService {
    @Resource
    private ProjPricingReportService projPricingReportService;
    @Resource
    private UserService userService;
    @Resource
    private MaterialsListService materialsListService;
    @Override
    public R<List<Pair<String, List<ProjPricingReportListRSP>>>> list(ProjPricingReportListREQ projPricingReportListREQ) {
        List<MaterialsList> materialsList = projPricingReportService.list(projPricingReportListREQ);
        if (CollectionUtils.isEmpty(materialsList)) {
            return R.ok();
        }
        List<ProjPricingReportListRSP> rspList = materialsList.stream().map(item -> {
            ProjPricingReportListRSP rsp = new ProjPricingReportListRSP();
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
        }).sorted(Comparator.comparingInt(ProjPricingReportListRSP::getSort)).collect(Collectors.toList());// 排序

        Map<String, List<ProjPricingReportListRSP>> mapRes = new HashMap<>();
        for (ProjPricingReportListRSP rspDatum : rspList) {
            List<ProjPricingReportListRSP> orDefault = mapRes.getOrDefault(rspDatum.getMaterialsType(), new ArrayList<>());
            orDefault.add(rspDatum);
            mapRes.put(rspDatum.getMaterialsType(), orDefault);
        }
        List<ProjPricingMaterialsEnum> types = rspList.stream()
                .map(ProjPricingReportListRSP::getMaterialsType)
                .map(ProjPricingMaterialsEnum::valueOf)
                .distinct()
                .sorted(Comparator.comparingInt(ProjPricingMaterialsEnum::getSort))
                .collect(Collectors.toList());
        List<Pair<String,List<ProjPricingReportListRSP>>> sortedRes = new ArrayList<>();
        for (ProjPricingMaterialsEnum type : types) {
            List<ProjPricingReportListRSP> reportList = mapRes.get(type.name());
            reportList.sort(new CommonFileSortComparator());
            sortedRes.add(new Pair<>(type.name(), reportList));
        }
        return R.ok(sortedRes);
    }

    @Override
    public R<Long> generate(ProjPricingReportGenerateREQ projPricingReportGenerateREQ) throws Exception {
        return R.ok(projPricingReportService.generate(projPricingReportGenerateREQ));
    }

    @Override
    public R<FileListRSP> download(ProjPricingReportDownloadREQ projPricingReportDownloadREQ) throws IOException {
        MaterialsList materialsList = materialsListService.getById(projPricingReportDownloadREQ.getRecordId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应的报告文件");
        }
        return R.ok(materialsListService.download(projPricingReportDownloadREQ.getRecordId()));
    }

    @Override
    public R<Void> upload(MultipartFile file, ProjPricingReportUploadREQ projPricingReportUploadREQ) {
        projPricingReportService.upload(file, projPricingReportUploadREQ);
        return R.ok();
    }

    @Override
    public R<Void> remove(ProjPricingReportRemoveREQ projPricingReportRemoveREQ) {
        projPricingReportService.remove(projPricingReportRemoveREQ.getId());
        return R.ok();
    }
}
