package cn.zswltech.mithras.application.orchestration.workflow.flow.file.focusfileselector;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.projectprocess.application.bo.FileBO;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/2/5
 * @description
 */
@Component
public abstract class AbstractContractFlowFocusFileSelector extends AbstractFlowFocusFileSelector {
    @Resource
    protected ContractBaseInfoService contractBaseInfoService;

    @Override
    public List<FileBO> listBizImportantFile(String businessKey, String version) {
        List<FileBO> list = super.listBizImportantFile(businessKey, version);
        // 根据名称过滤，如果是合同类型的只取带"合同"的文件
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().filter(e -> {
            if (StrUtil.isBlank(e.getFileName())) {
                return false;
            }
            if (this.isContractTextFile(e.getMaterialType())) {
                return e.getFileName().replace(" ", "").contains("合同");
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    public List<FileBO> listMeetingDecisionFile(String businessKey, String version) {
        // 合同需要找到对应项目评审id
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(Long.parseLong(businessKey));
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同信息不存在");
        }
        Long projReviewId = contractBaseInfo.getProjReviewId();
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.PROJ_REVIEW.name());
        if (CollectionUtil.isNotEmpty(this.meetingDecisionFileTypes())) {
            query.in(MaterialsList::getMaterialsType, this.meetingDecisionFileTypes());
        }
        query.eq(MaterialsList::getBelongId, projReviewId);
        List<MaterialsList> list = materialsListService.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        } else {
            return list.stream().map(FileBO::copyFromMaterial).collect(Collectors.toList());
        }
    }

    @Override
    protected BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.CONTRACT;
    }

    private boolean isContractTextFile(String type) {
        return Objects.nonNull(ContractTypeEnum.getByName(type));
    }
}
