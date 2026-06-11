package cn.zswltech.mithras.application.orchestration.workflow.flow.file.focusfileselector;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.application.orchestration.workflow.flow.file.FlowFocusFileSelector;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.document.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.projectprocess.application.bo.FileBO;
import cn.zswltech.mithras.document.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
@Component
public abstract class AbstractFlowFocusFileSelector implements FlowFocusFileSelector {
    @Resource
    protected MaterialsListService materialsListService;
    @Resource
    protected MaterialsListLibService materialsListLibService;

    @Override
    public List<FileBO> listBizImportantFile(String businessKey, String version) {
        Long mainId = Long.parseLong(businessKey);
        if (StrUtil.isBlank(version)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, this.businessModule().name());
            query.eq(MaterialsList::getBelongId, mainId);
            if (CollectionUtil.isNotEmpty(this.bizImportantFileTypes())) {
                query.in(MaterialsList::getMaterialsType, this.bizImportantFileTypes());
            }
            List<MaterialsList> list = materialsListService.list(query);
            if (CollectionUtil.isEmpty(list)) {
                return Collections.emptyList();
            } else {
                return list.stream().map(FileBO::copyFromMaterial).collect(Collectors.toList());
            }
        } else {
            LambdaQueryWrapper<MaterialsListLib> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, this.businessModule().name());
            query.eq(MaterialsListLib::getVersion, version);
            query.eq(MaterialsList::getBelongId, mainId);
            if (CollectionUtil.isNotEmpty(this.bizImportantFileTypes())) {
                query.in(MaterialsList::getMaterialsType, this.bizImportantFileTypes());
            }
            List<MaterialsListLib> libList = materialsListLibService.list(query);
            if (CollectionUtil.isEmpty(libList)) {
                return Collections.emptyList();
            } else {
                return libList.stream().map(FileBO::copyFromMaterialLib).collect(Collectors.toList());
            }
        }
    }

    @Override
    public List<FileBO> listMeetingDecisionFile(String businessKey, String version) {
        Long mainId = Long.parseLong(businessKey);
        if (StrUtil.isBlank(version)) {
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, this.businessModule().name());
            query.eq(MaterialsList::getBelongId, mainId);
            if (CollectionUtil.isNotEmpty(this.meetingDecisionFileTypes())) {
                query.in(MaterialsList::getMaterialsType, this.meetingDecisionFileTypes());
            }
            List<MaterialsList> list = materialsListService.list(query);
            if (CollectionUtil.isEmpty(list)) {
                return Collections.emptyList();
            } else {
                return list.stream().map(FileBO::copyFromMaterial).collect(Collectors.toList());
            }
        } else {
            LambdaQueryWrapper<MaterialsListLib> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBusinessType, this.businessModule().name());
            query.eq(MaterialsListLib::getVersion, version);
            query.eq(MaterialsList::getBelongId, mainId);
            if (CollectionUtil.isNotEmpty(this.meetingDecisionFileTypes())) {
                query.in(MaterialsList::getMaterialsType, this.meetingDecisionFileTypes());
            }
            List<MaterialsListLib> libList = materialsListLibService.list(query);
            if (CollectionUtil.isEmpty(libList)) {
                return Collections.emptyList();
            } else {
                return libList.stream().map(FileBO::copyFromMaterialLib).collect(Collectors.toList());
            }
        }
    }

    protected List<String> meetingDecisionFileTypes() {
        return Arrays.asList(
                ProjReviewMaterialsEnum.MEETING_REVIEW_REPORT.name(),
                ProjReviewMaterialsEnum.DIRECTOR_MEETING_REPORT.name()
        );
    }

    protected abstract List<String> bizImportantFileTypes();

    protected abstract BusinessModuleEnum businessModule();
}
