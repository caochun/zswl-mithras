package cn.zswltech.mithras.service.controller.filingmaterials;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.filingmaterials.AfterFilingMaterialsApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.filingmaterials.FilingBaseREQ;
import cn.zswltech.mithras.dto.filingmaterials.FilingBasicRemoveREQ;
import cn.zswltech.mithras.dto.filingmaterials.FundFilingMaterialsBatchDownloadREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model.FilingMaterials;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.filingmaterials.AfterFilingMaterialsService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.FileUriUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

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

import static cn.hutool.core.text.CharSequenceUtil.join;

/**
 * 租后检查资料归档
 * @author lllin
 * @date 2026-01-07
 */
@RestController
@Slf4j
public class AfterFilingMaterialsController implements AfterFilingMaterialsApi {
    @Resource
    private AfterFilingMaterialsService afterFilingMaterialsService;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private HttpServletResponse response;
    @Resource
    private OssClient ossClient;
    @Resource
    private MaterialsListMapper materialsListMapper;

    @Override
    public void batchDownload(FundFilingMaterialsBatchDownloadREQ req) throws IOException {
        StopWatch st = new StopWatch("租后归档资料批量下载");
        st.start("数据查询");
        List<MaterialsList> materialsListList = materialsListMapper.selectList(Wrappers.<MaterialsList>lambdaQuery()
                .eq(MaterialsList::getBelongId, req.getId())
                .eq(MaterialsList::getBusinessType, req.getModuleCode())
                .in(CollUtil.isNotEmpty(req.getFileIds()), MaterialsList::getId, req.getFileIds()));
        Assert.notEmpty(materialsListList, () -> MithrasException.newException("没有找到任何文件记录"));
        FilingMaterials filingMaterials = afterFilingMaterialsService.getById(req.getId());
        Assert.notNull(filingMaterials, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        st.stop();
        String rootPath = "归档资料";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(rootPath + ".zip", StandardCharsets.UTF_8.name()));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        st.start("文件压缩下载");
        Map<String, List<MaterialsList>> listMap = materialsListList.stream().collect(Collectors.groupingBy(MaterialsList::getMaterialsType));
        Map<String, String> dirMap = afterFilingMaterialsService.getDirMap(filingMaterials.getFilingType());
        String clientPath ;
        try (OutputStream out = response.getOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            Set<String> pathSet = Collections.synchronizedSet(new LinkedHashSet<>());
            List<String> pathList = new CopyOnWriteArrayList<>();
            //根据一级文件目录分组
            for (Map.Entry<String, List<MaterialsList>> entry : listMap.entrySet()) {
                String materialsTypeName = Optional.ofNullable(dirMap.get(entry.getKey())).orElse("");
                clientPath = rootPath + File.separator + materialsTypeName;
                download(zipOut, entry.getValue(), clientPath, pathList, pathSet);
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

    private void download(ZipOutputStream zipOut, List<MaterialsList> clientMaterials, String clientPath, List<String> pathList, Set<String> pathSet){
        /*重复文件名替换*/
        afterFilingMaterialsService.repeatFileNameReplace(clientMaterials);
        for (MaterialsList material : clientMaterials) {
            // 拼接文件路径
            String filePath = clientPath + File.separator +  material.getFilename();
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
        return R.ok(afterFilingMaterialsService.getOperationsDirDict(req.getId()));
    }

    @Override
    public R<Void> remove(@Valid FilingBasicRemoveREQ filingBasicRemoveREQ) {
        afterFilingMaterialsService.fileRemove(filingBasicRemoveREQ);
        return R.ok();
    }



    @Override
    public void test() {
        afterFilingMaterialsService.initFilingMaterials(3254L,null);
    }
}
