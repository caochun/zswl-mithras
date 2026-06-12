package cn.zswltech.mithras.others.service.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.document.persistence.model.MaterialsListLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 修复文件版本数据 以现在的文件数据修复到版本表里去
 * @author wangchuanhao
 * @date 2022/12/19 2:59 PM
 */
public class MaterialsLibDataFix extends ApplicationTest {

    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private MaterialsListLibMapper materialsListLibMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Test
    public void fix() {
        List<CommonVersion> commonVersionList = commonVersionMapper.selectList(Wrappers.<CommonVersion>lambdaQuery().in(CommonVersion::getModule,
                ListUtil.toList(BusinessModuleEnum.AFTER_LEASE_CHECK_PROJECT.name(),
                        BusinessModuleEnum.FTP_MONTHLY_GUIDANCE.name(),
                        BusinessModuleEnum.FTP_QUARTERLY_GUIDANCE.name(),
                        BusinessModuleEnum.ASSET_CLASSIFY_REVIEW.name()
                )));
        for (CommonVersion commonVersion : commonVersionList) {
            List<MaterialsList> draftMaterialsList = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                    .eq(MaterialsList::getBusinessType, commonVersion.getModule())
                    .eq(MaterialsList::getBelongId, commonVersion.getMainId())
            );
            if (CollectionUtils.isEmpty(draftMaterialsList)) {
                continue;
            }
            // 查下如果这个版本文件>0,说明修过或自己录入了，就不修了，虽然说应该不可能
            if (materialsListLibMapper.selectCount(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getBusinessType, commonVersion.getModule())
                    .eq(MaterialsListLib::getBelongId, commonVersion.getMainId())) > 0) {
                continue;
            }
            transactionTemplate.execute(c -> {
                try {
                    for (MaterialsList entity : draftMaterialsList) {
                        MaterialsListLib lib = entity2Lib(entity, commonVersion.getVersion(), commonVersion.getVersionType());
                        materialsListLibMapper.insert(lib);
                    }
                    return true;
                } catch (Exception e) {
                    log.error("导入历史文件版本数据出现问题", e);
                    throw new MithrasException("导入历史文件版本数据出现问题");
                }
            });
        }
    }

    private MaterialsListLib entity2Lib(MaterialsList entity, String version, Integer versionType) {
        MaterialsListLib lib = BeanUtil.copyProperties(entity, MaterialsListLib.class);
        lib.setId(null);
        lib.setOriginId(entity.getId());
        lib.setVersion(version);
        // 记录版本数据的操作人及审计数据
        lib.setUpdateBy(entity.getUpdateBy());
        lib.setCreateBy(entity.getUpdateBy());
        lib.setCreateTime(LocalDateTime.now());
        lib.setUpdateTime(LocalDateTime.now());
        // 记录原数据的操作人及审计数据
        lib.setDataCreateBy(entity.getCreateBy());
        lib.setDataCreateTime(entity.getCreateTime());
        lib.setDataUpdateBy(entity.getUpdateBy());
        lib.setDataUpdateTime(entity.getUpdateTime());
        // 版本类型
        lib.setVersionType(versionType);
        return lib;
    }

}
