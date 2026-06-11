package cn.zswltech.mithras.application.orchestration.document.materialsfile.batchdownload;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.file.FileBatchDownLoadREQ;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.document.materialsfile.MaterialsListLibService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import lombok.SneakyThrows;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static cn.zswltech.mithras.dto.MaterialsListIdType.VERSIONED;

/**
 * @ClassName AbstractFileBatchDownload
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/2/13 10:53 上午
 * @Version 1.0
 **/
public abstract class AbstractFileBatchDownload {

    @Resource
    private HttpServletResponse httpServletResponse;

    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private MaterialsListLibService materialsListLibService;

    public abstract String getModuleKey();

    @SneakyThrows
    public void batchDownload(FileBatchDownLoadREQ req) {
        //下载
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("资料清单.zip", StandardCharsets.UTF_8.name()));
        List<MaterialsList> materialsLists;
        if (ObjectUtil.equal(VERSIONED, req.getIdType())) {
            materialsLists = new ArrayList<>(materialsListLibService.listByIds(req.getFileId()));
        } else {
            materialsLists = materialsListService.getMaterialsListByIdsAndVersion(req.getFileId(), req.getVersion());
        }

        materialsListService.downloadBatch(httpServletResponse.getOutputStream(), materialsLists, Boolean.TRUE.equals(req.getColZipFlag()));
    }

    public Boolean isCheck(String moduleKey) {
        return ObjectUtil.equals(moduleKey, getModuleKey());
    }

}
