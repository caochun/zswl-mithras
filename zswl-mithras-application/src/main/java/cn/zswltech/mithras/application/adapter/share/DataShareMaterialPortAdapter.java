package cn.zswltech.mithras.application.adapter.share;

import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.basic.Constant;
import cn.zswltech.mithras.third.datashare.service.DataShareMaterialPort;
import cn.zswltech.mithras.dto.file.FileUploadREQ;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;

@Component
public class DataShareMaterialPortAdapter implements DataShareMaterialPort {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private OssClient ossClient;

    @Override
    @SneakyThrows
    public InputStream downloadLatestArchive(String filename) {
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getMaterialsType, Constant.ARCHIVES);
        query.in(MaterialsList::getBusinessType, Constant.ARCHIVE_STAFF, Constant.ARCHIVE_TRAVEL);
        query.eq(MaterialsList::getFilename, filename);
        query.orderByDesc(MaterialsList::getUpdateTime);
        query.last(StringUtil.mysqlLimitOne());
        MaterialsList material = materialsListService.getOne(query);
        return ossClient.downLoad(material.getOssFilename());
    }

    @Override
    @SneakyThrows
    public Long add(FileUploadREQ fileUploadREQ) {
        return materialsListService.add(
                fileUploadREQ.getFile().getInputStream(),
                fileUploadREQ.getFile().getOriginalFilename(),
                fileUploadREQ.getMainId(),
                fileUploadREQ.getMaterialsType(),
                fileUploadREQ.getMaterialsSubType(),
                fileUploadREQ.getModuleType(),
                YesOrNoNumberEnum.NO,
                fileUploadREQ.getSourceBusinessKey(),
                fileUploadREQ.getLocation(),
                fileUploadREQ.getCreatedBy()
        );
    }
}
