package cn.zswltech.mithras.application.orchestration.facade.filingmaterials;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.filingmaterials.application.FundFilingMaterialsApplicationService;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingFileDownloadREQ;
import cn.zswltech.mithras.dto.filingmaterials.FundFilingMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsFilingTypeEnum;
import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsInitiationMethodEnum;
import cn.zswltech.mithras.document.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.filingmaterials.mapper.model.FilingMaterials;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.filingmaterials.FundFilingMaterialsService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.document.util.FileUriUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.hutool.core.text.CharSequenceUtil.join;
import static cn.hutool.extra.spring.SpringUtil.getBean;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 资金资料归档
 * @author lllin
 * @date 2026-01-07
 */
@Service
@Slf4j
public class FundFilingMaterialsFacade implements FundFilingMaterialsApplicationService {
    @Resource
    private FundFilingMaterialsService fundFilingMaterialsService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private OssClient ossClient;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private FilingMaterialsService filingMaterialsService;

    @Override
    public void batchDownload(FundFilingMaterialsBatchDownloadREQ req) throws IOException {
        StopWatch st = new StopWatch("资金归档资料批量下载");
        st.start("数据查询");
        List<MaterialsList> materialsListList = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, req.getId())
                .eq(MaterialsList::getBusinessType, req.getModuleCode())
                .in(CollUtil.isNotEmpty(req.getFileIds()), MaterialsList::getId, req.getFileIds()));
        Assert.notEmpty(materialsListList, () -> MithrasException.newException("没有找到任何文件记录"));
        FilingMaterials filingMaterials = filingMaterialsService.getById(req.getId());
        Assert.notNull(filingMaterials, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        st.stop();
        String rootPath = "归档资料";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(rootPath + ".zip", StandardCharsets.UTF_8.name()));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        st.start("文件压缩下载");
        Map<String, List<MaterialsList>> listMap = materialsListList.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        Map<String, String> dirMap = fundFilingMaterialsService.getDirMap(filingMaterials.getFilingType());
        String clientPath ;
        String overrideFileNamePrefix = fundFilingMaterialsService.getOverrideFileName(filingMaterials) ;
        try (OutputStream out = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            Set<String> pathSet = Collections.synchronizedSet(new LinkedHashSet<>());
            List<String> pathList = new CopyOnWriteArrayList<>();
            //根据一级文件目录分组
            for (Map.Entry<String, List<MaterialsList>> entry : listMap.entrySet()) {
                String materialsTypeName = Optional.ofNullable(dirMap.get(entry.getKey())).orElse("");
                clientPath = rootPath + File.separator + dirMap.get(entry.getKey());
                download(zipOut, entry.getValue(), clientPath,materialsTypeName,overrideFileNamePrefix, pathList, pathSet);
            }
            zipOut.finish();
            zipOut.flush();
            out.flush();
            response.flushBuffer();
        } catch (IOException e) {
            log.error("批量下载失败", e);
            throw MithrasException.newException("文件下载失败：" + e.getMessage());
        } finally {
            st.stop();
            log.info("MaterialsListController batchDownload {}", st.prettyPrint(TimeUnit.MILLISECONDS));
        }
    }

    private void download(ZipOutputStream zipOut, List<MaterialsList> clientMaterials, String clientPath,
                          String materialsTypeName,String overrideFileNamePrefix,List<String> pathList, Set<String> pathSet){
        /*重复文件名替换*/
        fundFilingMaterialsService.repeatFileNameReplace(clientMaterials);
        for (MaterialsList material : clientMaterials) {
            //文件名重写
            String fileName = overrideFileNamePrefix + "-" + materialsTypeName +"-" + material.getFilename();
            // 拼接文件路径
            String filePath = clientPath + File.separator + fileName;
            // 获取文件流
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ossClient.downLoad(byteArrayOutputStream, join("/", material.getOssFilename()));
                ZipEntry zEntry = new ZipEntry(filePath);
                zipOut.putNextEntry(zEntry);
                byteArrayOutputStream.writeTo(zipOut);
                zipOut.closeEntry();
                log.info("文件写入zip成功:"+filePath);
            } catch (Exception e){
                log.error("文件写入zip失败:{},写入失败原因:{}"+filePath + e.getMessage());
            }
        }
    }

    @Override
    public R<Map<String, List<SelectRSP>>> getOperationsDirDict(@Valid FilingBaseREQ req) {
        return R.ok(fundFilingMaterialsService.getOperationsDirDict(req.getId()));
    }

    @Override
    public void download(@Valid FilingFileDownloadREQ filingFileDownloadREQ) throws IOException {
        MaterialsList materialsList = materialsListService.getById(filingFileDownloadREQ.getFileId());
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("没有找到对应的文件");
        }
        FilingMaterials filingMaterials = fundFilingMaterialsService.getById(materialsList.getBelongId());
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        String overrideFileName = fundFilingMaterialsService.getOverrideFileName(filingMaterials);
        String materialsTypeName = Optional.ofNullable(fundFilingMaterialsService.getDirMap(filingMaterials.getFilingType()).get(materialsList.getMaterialsType())).orElse("");
        String fileName = overrideFileName + "-" + materialsTypeName + "-" + materialsList.getFilename();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        try (OutputStream out = response.getOutputStream()) {
            ossClient.downLoad(out, join("/", materialsList.getOssFilename()));
            log.info("资金归档资料单个文件下载成功:"+fileName);
        } catch (Exception e){
            log.error("资金归档资料单个文件下载成功:{},下载失败原因:{}"+fileName + e.getMessage());
            throw MithrasException.newException("文件下载失败：" + e.getMessage());
        }
    }


    @Override
    public R<Boolean> checkFile(FilingBaseREQ req){
        FilingMaterials filingMaterials = fundFilingMaterialsService.getById(req.getId());
        if (filingMaterials == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        fundFilingMaterialsService.checkFile(filingMaterials);
        return R.ok(true);
    }

    @Override
    public void test() {
        fundFilingMaterialsService.initFundCommonProcessPrepare(29L,65L,"DK202304140031-07", FilingMaterialsFilingTypeEnum.FUND_FINANCING.name(), FilingMaterialsInitiationMethodEnum.SYSTEM.name());
    }


}
