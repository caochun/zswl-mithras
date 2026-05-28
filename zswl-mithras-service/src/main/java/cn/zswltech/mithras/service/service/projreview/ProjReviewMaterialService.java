package cn.zswltech.mithras.service.service.projreview;



import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.dto.materialsfile.MaterialsListListRSP;
import cn.zswltech.mithras.dto.materialsfile.ProjMaterialsListListRSP;
import cn.zswltech.mithras.service.enums.projreview.ReviewRelationDataType;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewMaterial;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewMaterialMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 评审材料审核
 * @Author: heng
 * @Date: 2026/1/7 08:37
 */
@Slf4j
@Service
public class ProjReviewMaterialService extends ServiceImpl<ProjReviewMaterialMapper, ProjReviewMaterial> {
    @Resource
    private ProjReviewMaterialMapper projReviewMaterialMapper;

    @Transactional(rollbackFor = Exception.class)
    public void fillReviewMaterial(ProjReviewBaseInfo reviewBaseInfo, List<ProjMaterialsListListRSP> materialsList){
        List<ProjReviewMaterial> reviewMaterials = this.getReviewMaterialsByReviewId(reviewBaseInfo.getId());
        List<String> exist = reviewMaterials.stream().map(reviewMaterial -> reviewMaterial.getClientId() + reviewMaterial.getClientTypeName()).collect(Collectors.toList());
        List<String> now = materialsList.stream().filter(item -> !ReviewRelationDataType.PROJ_ESTABLISH.name().equals(item.getBusinessType())).map(reviewMaterial -> reviewMaterial.getClientId() + reviewMaterial.getClientTypeName()).collect(Collectors.toList());
        boolean eq = new HashSet<>(exist).containsAll(now) && new HashSet<>(now).containsAll(exist);
        if(!eq){
            // 若表中数据与现在结果不一致，说明发生了变更，直接初始化
            reviewMaterials = this.initReviewMaterial(reviewBaseInfo, materialsList);
        }

        // 上个版本数据
        List<ProjReviewMaterial> lastVersionReviewMaterialsByReviewId = getLastVersionReviewMaterialsByReviewId(reviewBaseInfo.getId());
        Map<String, ProjReviewMaterial> lastMap = lastVersionReviewMaterialsByReviewId.stream().collect(Collectors.toMap(item -> item.getClientId() + item.getClientTypeName(), identity -> identity));

        Map<Long, ProjReviewMaterial> materialMap = reviewMaterials.stream().collect(Collectors.toMap(ProjReviewMaterial::getClientId, identity -> identity));
        for (ProjMaterialsListListRSP materials : materialsList) {
            if(ReviewRelationDataType.PROJ_ESTABLISH.name().equals(materials.getBusinessType()))
                continue;
            // 补充评审数据
            ProjReviewMaterial projReviewMaterial = materialMap.get(materials.getClientId());
            materials.setProjReviewMaterialId(projReviewMaterial.getId());
            materials.setReviewComments(projReviewMaterial.getReviewComments());
            materials.setReviewInstructions(projReviewMaterial.getReviewInstructions());

            if(Objects.nonNull(materials.getBusinessMaterialList())) {
                // 补充文件修改标识
                if (lastMap.containsKey(materials.getClientId() + materials.getClientTypeName())) {
                    ProjReviewMaterial lastProjReviewMaterial = lastMap.get(materials.getClientId() + materials.getClientTypeName());
                    List<String> ids = Arrays.stream(lastProjReviewMaterial.getRecordId().split(",")).collect(Collectors.toList());
                    materials.getBusinessMaterialList().stream().flatMap(item -> item.getMaterialList().stream()).forEach(uploadItem -> {
                        uploadItem.setIsChange(!ids.contains(uploadItem.getRecordId().toString()));
                    });
                }

                // 没有旧版
                if (CollectionUtil.isEmpty(lastVersionReviewMaterialsByReviewId)) {
                    materials.getBusinessMaterialList().stream().flatMap(item -> item.getMaterialList().stream()).forEach(uploadItem -> {
                        uploadItem.setIsChange(false);
                    });
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<ProjReviewMaterial> initReviewMaterial(ProjReviewBaseInfo reviewBaseInfo, List<ProjMaterialsListListRSP> materialsList){
        ArrayList<ProjReviewMaterial> saveList = new ArrayList<>();
        for (ProjMaterialsListListRSP materials : materialsList) {
            if(ReviewRelationDataType.PROJ_ESTABLISH.name().equals(materials.getBusinessType()))
            {
                continue;
            }
            ProjReviewMaterial reviewMaterial = new ProjReviewMaterial();
            BeanUtil.copyProperties(materials, reviewMaterial,"id");
            reviewMaterial.setProjReviewId(reviewBaseInfo.getId());
            reviewMaterial.setProjCode(reviewBaseInfo.getProjCode());
            reviewMaterial.setLastVersionFlag(0);
            // 保存文件id
            if(Objects.nonNull(materials.getBusinessMaterialList())) {
                List<Long> ids = materials.getBusinessMaterialList().stream().flatMap(item -> item.getMaterialList().stream()).map(MaterialsListListRSP.MaterialGroup.UploadItem::getRecordId).collect(Collectors.toList());
                reviewMaterial.setRecordId(ids.stream().map(Object::toString).collect(Collectors.joining(",")));
            }
            saveList.add(reviewMaterial);
        }
        this.saveBatch(saveList);
        return saveList;
    }

    public List<ProjReviewMaterial> getReviewMaterialsByReviewId(Long projReviewId){
        return this.list(Wrappers.<ProjReviewMaterial>lambdaQuery().eq(ProjReviewMaterial::getProjReviewId, projReviewId).eq(ProjReviewMaterial::getLastVersionFlag,0));
    }

    public List<ProjReviewMaterial> getLastVersionReviewMaterialsByReviewId(Long projReviewId){
        return this.list(Wrappers.<ProjReviewMaterial>lambdaQuery().eq(ProjReviewMaterial::getProjReviewId, projReviewId).eq(ProjReviewMaterial::getLastVersionFlag,1));
    }

    public boolean updateAnnotationIncludeNullById(ProjReviewMaterial projReviewMaterial){
        return projReviewMaterialMapper.updateAnnotationIncludeNullById(projReviewMaterial) == 1;
    }

    public void updateVersionByReviewId(Long reviewId){
        // 先把上个版本干掉
        this.remove(Wrappers.<ProjReviewMaterial>lambdaQuery().eq(ProjReviewMaterial::getProjReviewId,reviewId).eq(ProjReviewMaterial::getLastVersionFlag,1));
        // 再把表里数据更新成上版
        this.update(Wrappers.<ProjReviewMaterial>lambdaUpdate().set(ProjReviewMaterial::getLastVersionFlag,1).eq(ProjReviewMaterial::getProjReviewId,reviewId));
    }
}
