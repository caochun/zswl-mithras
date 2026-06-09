package cn.zswltech.mithras.service.factory.file.impl;

import cn.hutool.core.lang.Pair;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.factory.file.AbstractFileListProvider;
import cn.zswltech.mithras.service.factory.file.bo.FileListExtQuery;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 项目评审
 *
 * @author wangchuanhao
 * @date 2023/2/6 1:41 PM
 */
@Component
public class ProjPricingFileListProvider extends AbstractFileListProvider {

    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.PROJ_PRICING;
    }

    @Override
    public List<Pair<String, List<FileListRSP>>> listGroup(FileListREQ req) {
        FileListExtQuery extQuery = new FileListExtQuery();
        extQuery.setMaterialsTypes(ProjPricingMaterialsEnum.listAll());
        return listGroup(req, extQuery);
    }

    @Override
    protected int getGroupFileSort(FileListRSP rsp) {
        return Optional.ofNullable(ProjReviewMaterialsEnum.getByName(rsp.getMaterialsType())).map(ProjReviewMaterialsEnum::getSort).orElse(Integer.MAX_VALUE);
    }

}
