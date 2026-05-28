package cn.zswltech.mithras.service.flow.file.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.service.enums.rating.RatingClientMaterialsEnum;
import cn.zswltech.mithras.service.flow.file.IFileHandler;
import cn.zswltech.mithras.service.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Component
public class RatingClientFileHandlerImpl implements IFileHandler {

    @Resource
    private MaterialsListMapper materialsListMapper;
    
    @Override
    public void uploadCheck(ProcessResp processResp, String materialsType, String taskId) {

    }

    @Override
    public void removeCheck(ProcessResp processResp, MaterialsList materialsList, String taskId) {
        if(!Objects.equals(AccountUtil.getLoginInfo().getId(),materialsList.getCreateBy())){
            throw new MithrasException("非文件创建者，不可执行该操作");
        }

    }

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        return materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery().eq(MaterialsList::getBusinessType, BusinessModuleEnum.RATING_CLIENT.name())
                        .in(ObjectUtil.isNotEmpty(materialsTypeList), MaterialsList::getMaterialsType, materialsTypeList)
                        .eq(MaterialsList::getBelongId, processResp.getBusinessKey())
                        .eq(MaterialsList::getCreateBy, AccountUtil.getLoginInfo().getId())
                        .orderByDesc(MaterialsList::getUpdateTime));
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.RATING_CLIENT;
    }


    @Override
    public String convertMaterialsType(String materialsType) {
        return Optional.ofNullable(RatingClientMaterialsEnum.getByName(materialsType)).map(RatingClientMaterialsEnum::getDisplay).orElse(null);
    }

}
