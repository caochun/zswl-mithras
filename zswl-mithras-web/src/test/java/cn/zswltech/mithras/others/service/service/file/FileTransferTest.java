package cn.zswltech.mithras.others.service.service.file;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName FileTransferTest
 * @Description 处理转化历史文件
 * @Author jackerhe
 * @Date 2023/2/1 2:05 下午
 * @Version 1.0
 **/
@Slf4j
public class FileTransferTest extends ApplicationTest {

    @Resource
    private MaterialsListService materialsListService;

    @Resource
    private MaterialsListLibService materialsListLibService;

    @Resource
    private OssClient ossClient;

    @Test
    public void transferFile() {
        List<Long> ids = Arrays.asList();
        DateTime startTime = DateUtil.parse("2000-03-01 00:00:00", DatePattern.NORM_DATETIME_PATTERN);
        //转化文件
        List<MaterialsList> list = materialsListService.list(Wrappers.<MaterialsList>lambdaQuery()
                .gt(MaterialsList::getCreateTime, startTime)
                .in(ObjectUtil.isNotEmpty(ids), MaterialsList::getBelongId, ids));
        List<MaterialsList> updateList = new ArrayList<>();
        List<Long> errorIds = new ArrayList<>();
        List<Long> errorLibIds = new ArrayList<>();
        String businessType;
        String materialsType;
        String materialsSubType;
        String name;
        String ossFileName;
        String tempFileName;
        String ossPath;
        for (MaterialsList material : list) {
            try {
                StringBuilder builder = new StringBuilder();
                businessType = material.getBusinessType();
                materialsType = material.getMaterialsType();
                materialsSubType = material.getMaterialSubType();
                name = material.getFilename();
                builder.append(material.getBelongId());
                if (StrUtil.isNotEmpty(businessType)) {
                    builder.append("/").append(businessType);
                    if (StrUtil.isNotEmpty(materialsType)) {
                        builder.append("/").append(materialsType);
                    }
                    if (StrUtil.isNotEmpty(materialsSubType)) {
                        builder.append("/").append(materialsSubType);
                    }
                }
                ossPath = builder.toString();
                tempFileName = ossPath + "/" + name;
                if (ossClient.doesObjectExist("zswl", ossClient.getBasePath() + "/" + tempFileName)) {
                    ossPath += "/" + System.currentTimeMillis();
                }
                ossFileName = ossPath + "/" + name;
                ossPath = "/mithras" + "/" + ossPath;
                ossClient.copy("/mithras" + "/" + material.getOssFilename(), "/mithras" + "/" + ossFileName);
                material.setOssFilename(ossFileName);
                material.setFilePath(ossPath);
                updateList.add(material);
                if (ObjectUtil.equals(updateList.size(), 500)) {
                    materialsListService.updateBatchById(updateList);
                    updateList.clear();
                    Thread.sleep(1000);
                }
            } catch (Exception e){
                errorIds.add(material.getId());
                log.info("文件失败ID {}", material.getId(), e);
            }
        }
        if (!ObjectUtil.equals(updateList.size(), 500) && ObjectUtil.isNotEmpty(updateList)) {
            materialsListService.updateBatchById(updateList);
            updateList.clear();
        }
        //转化版本文件
        List<Long> libIds = Arrays.asList(1346L,1347L,1348L,1901L,1902L,4142L,4143L,4144L,4145L,4150L,4151L,4152L,4153L,4154L,4155L,4156L,4157L,
                4158L,4159L,4160L,9220L,9794L,9796L,9801L,9808L,9809L,9810L,9811L,10881L,10882L,10883L,10884L,10885L,10886L,
                10887L,10888L,10889L,10890L,10891L,10892L,10893L,10894L,10895L,10896L,10897L,10898L,10899L,
                10900L,10901L,10902L,10903L,10904L,10905L,10906L,10907L,10911L,11121L,11123L,11131L,11132L,11398L,11399L,11400L,11401L,11402L,
                11403L,11404L,11405L,11406L,11407L,11408L,11676L,11678L,11680L,11681L,11683L,12687L,12688L,12689L,13137L,13140L,13142L,13190L,13361L,13376L,13969L,14472L,
                14473L,14474L,15118L,15119L,15142L,15169L,15170L,15337L,15519L,15520L,15569L,15774L,15775L,15776L,15777L,15779L,15780L,16071L,16089L,
                16154L,16161L,16318L);
        if(ObjectUtil.isNotEmpty(libIds)){
            List<MaterialsListLib> updateLib = new ArrayList<>();
            List<MaterialsListLib> materialsListLibs = materialsListLibService.listByIds(libIds);
            for(MaterialsListLib listLib : materialsListLibs){
                try {
                    StringBuilder builder = new StringBuilder();
                    businessType = listLib.getBusinessType();
                    materialsType = listLib.getMaterialsType();
                    materialsSubType = listLib.getMaterialSubType();
                    name = listLib.getFilename();
                    builder.append(listLib.getBelongId());
                    if (StrUtil.isNotEmpty(businessType)) {
                        builder.append("/").append(businessType);
                        if (StrUtil.isNotEmpty(materialsType)) {
                            builder.append("/").append(materialsType);
                        }
                        if (StrUtil.isNotEmpty(materialsSubType)) {
                            builder.append("/").append(materialsSubType);
                        }
                    }
                    ossPath = builder.toString();
                    tempFileName = ossPath + "/" + name;
                    if (ossClient.doesObjectExist("zswl", ossClient.getBasePath() + "/" + tempFileName)) {
                        ossPath += "/" + System.currentTimeMillis();
                    }
                    ossFileName = ossPath + "/" + name;
                    ossPath = "/mithras" + "/" + ossPath;
                    ossClient.copy("/mithras" + "/" + listLib.getOssFilename(), "/mithras" + "/" + ossFileName);
                    listLib.setOssFilename(ossFileName);
                    listLib.setFilePath(ossPath);
                    updateLib.add(listLib);
                    if (ObjectUtil.equals(updateList.size(), 500)) {
                        materialsListLibService.updateBatchById(updateLib);
                        updateLib.clear();
                    }
                } catch (Exception e){
                    errorLibIds.add(listLib.getId());
                    log.info("文件失败ID {}", listLib.getId(), e);
                }
            }
            if (!ObjectUtil.equals(updateLib.size(), 500) && ObjectUtil.isNotEmpty(updateLib)) {
                materialsListLibService.updateBatchById(updateLib);
            }
        }
        log.info("文件转换失败ids = {}", errorIds);
        log.info("文件转换失败errorLibIds = {}", errorLibIds);
    }


}
