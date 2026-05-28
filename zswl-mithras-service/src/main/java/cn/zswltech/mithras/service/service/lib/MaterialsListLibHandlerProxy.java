package cn.zswltech.mithras.service.service.lib;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 文件处理代理 仅限handler调用
 *
 * @author wangchuanhao
 * @date 2022/12/14 7:44 PM
 */
@Component
public class MaterialsListLibHandlerProxy {

    @Resource
    private MaterialsListMapper draftMapper;
    @Resource
    private MaterialsListLibMapper mapper;

    public MaterialsListLib entity2Lib(MaterialsList f) {
        if (Objects.isNull(f)) {
            return null;
        }
        MaterialsListLib materialsListLib = BeanUtil.copyProperties(f, MaterialsListLib.class);
        return materialsListLib;
    }

    public MaterialsList lib2Entity(MaterialsListLib t) {
        if (Objects.isNull(t)) {
            return null;
        }
        MaterialsList materialsList = BeanUtil.copyProperties(t, MaterialsList.class);
        return materialsList;
    }

    public ListBaseRSP lib2Rsp(MaterialsListLib f) {
        ListBaseRSP listBaseRSP = new ListBaseRSP();
        listBaseRSP.setId(f.getId());
        return listBaseRSP;
    }

    /**
     * 过滤出需要处理的编辑区数据
     * @param mainId
     * @return
     */
    public List<MaterialsList> listNeedHandleEntity(Long mainId, BusinessModuleEnum businessModuleEnum) {
        List<MaterialsList> draftDataList = draftMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, mainId)
                .eq(MaterialsList::getBusinessType, businessModuleEnum.name())
        );
        return draftDataList;
    }

    /**
     * 过滤出需要处理的版本区数据
     *
     * @param mainId
     * @return
     */
    public List<MaterialsListLib> listNeedHandleLib(Long mainId, String version, BusinessModuleEnum businessModuleEnum) {
        List<MaterialsListLib> versionList = mapper.selectList(Wrappers.<MaterialsListLib>lambdaQuery()
                .eq(MaterialsListLib::getVersion, version)
                .eq(MaterialsListLib::getBelongId, mainId)
                .eq(MaterialsListLib::getBusinessType, businessModuleEnum.name())
        );
        return versionList;
    }

}
