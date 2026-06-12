package cn.zswltech.mithras.document.onlyoffice;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.dto.onlyoffice.DocDetailRSP;
import cn.zswltech.mithras.dto.onlyoffice.GetDocDetailREQ;
import cn.zswltech.mithras.document.enums.FileTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListLibMapper;
import cn.zswltech.mithras.document.persistence.mapper.MaterialsListMapper;
import cn.zswltech.mithras.document.persistence.mapper.OnlyofficeKeyStoreMapper;
import cn.zswltech.mithras.document.persistence.model.MaterialsList;
import cn.zswltech.mithras.document.persistence.model.MaterialsListLib;
import cn.zswltech.mithras.document.persistence.model.OnlyofficeKeyStore;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserResolver;
import cn.zswltech.mithras.foundation.port.UserNameResolver;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.Objects;

import static cn.zswltech.mithras.dto.MaterialsListIdType.EDIT_AREA;
import static cn.zswltech.mithras.dto.MaterialsListIdType.VERSIONED;


/**
 * onlyoffice在线文档编辑服务
 *
 * @author wangchuanhao
 * @date 2022/7/8 11:35 AM
 */
@Service
@Slf4j
public class OnlyOfficeService {

    @Value("${onlyoffice.callbackIp}")
    private String callbackIp;
    @Value("${onlyoffice.indexHost}")
    private String indexHost;
    @Value("${onlyoffice.logoUrl}")
    private String logoUrl;

    @Autowired
    private HttpServletResponse response;
    @Resource
    private OnlyofficeKeyStoreMapper onlyofficeKeyStoreMapper;
    @Resource
    private MaterialsListMapper materialsListMapper;
    @Resource
    private MaterialsListLibMapper materialsListLibMapper;
    @Resource
    private OssClient ossClient;
    @Resource
    private OoBizHandlerFactory ooBizHandlerFactory;
    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private UserNameResolver userNameResolver;

    public DocDetailRSP docDetail(GetDocDetailREQ req) {
        MaterialsList materialsList;
        int fileIdType;
        Long fileId;
        if (ObjectUtil.equal(VERSIONED, req.getIdType())) {
            MaterialsListLib materialsListLib = materialsListLibMapper.selectById(req.getId());
            fileIdType = VERSIONED;
            fileId = materialsListLib.getId();
            materialsList = actualLib2Entity(materialsListLib);
        } else if (StringUtils.isBlank(req.getVersion())) {
            materialsList = materialsListMapper.selectById(req.getId());
            fileIdType = EDIT_AREA;
            fileId = materialsList.getId();
        } else {
            MaterialsListLib materialsListLib = materialsListLibMapper.selectOne(Wrappers.<MaterialsListLib>lambdaQuery()
                    .eq(MaterialsListLib::getVersion, req.getVersion())
                    .eq(MaterialsListLib::getOriginId, req.getId())
                    .last("LIMIT 1")
            );
            fileIdType = VERSIONED;
            fileId = materialsListLib.getId();
            materialsList = actualLib2Entity(materialsListLib);
        }
        if (Objects.isNull(materialsList)) {
            throw new MithrasException("文件不存在");
        }
//        // 根据文件模块找到key
//        OnlyofficeKeyStore keyStore = onlyofficeKeyStoreMapper.selectOne(Wrappers.<OnlyofficeKeyStore>lambdaQuery()
//                .eq(OnlyofficeKeyStore::getFileOssPathMd5, SecureUtil.md5(materialsList.getOssFilename()))
//                .last("LIMIT 1"));
//
//        if (Objects.isNull(keyStore)) {
//            // 如果没有则新增
//            keyStore = new OnlyofficeKeyStore();
//            keyStore.setFileKey(generateKey());
//            keyStore.setFileOssPath(materialsList.getOssFilename());
//            keyStore.setFileOssPathMd5(SecureUtil.md5(materialsList.getOssFilename()));
//            onlyofficeKeyStoreMapper.insert(keyStore);
//        }
        // 为了解决多人用同一个key可能引发的读取到老的文件缓存的问题，暂时暴力解决，只要预览文件就重新生成key
        // 后续看是否需要另外新增一个定时任务每天清理文件key，防止数据量无限增长
        OnlyofficeKeyStore keyStore = new OnlyofficeKeyStore();
        keyStore.setFileId(fileId);
        keyStore.setFileIdType(fileIdType);
        keyStore.setFileKey(generateKey());
        keyStore.setFileOssPath(materialsList.getOssFilename());
        keyStore.setFileOssPathMd5(SecureUtil.md5(materialsList.getOssFilename()));
        onlyofficeKeyStoreMapper.insert(keyStore);
        Long currentUserId = currentUserResolver.currentUserId();
        DocDetailRSP docDetailRSP = DocDetailRSP.builder()
                .type(Objects.equals(2, req.getWindowType()) ? "mobile" : "desktop")
                .documentType(FileTypeEnum.getDocumentType(FileNameUtil.extName(materialsList.getFilename())))
                .document(DocDetailRSP.Document.builder()
                        .fileType(FileNameUtil.extName(materialsList.getFilename()))
                        .key(keyStore.getFileKey())
                        .url(String.format("%s%s?key=%s", callbackIp, "/onlyoffice/download", keyStore.getFileKey()))
                        .title(FileNameUtil.mainName(materialsList.getFilename()))
                        .permissions(DocDetailRSP.Permissions.builder()
                                .edit(Objects.equals(2, req.getOperate()))
                                .download(true)
                                .print(true)
                                .copy(true)
                                .chat(false)
                                .comment(false)
                                .review(true)
                                .build())
                        .build())
                .editorConfig(DocDetailRSP.EditorConfig.builder()
                        .lang("zh-CN")
                        .callbackUrl(callbackIp + "/onlyoffice/callback")
                        .region("zh-CN")
                        .mode(Objects.equals(2, req.getOperate()) ? "edit" : "view")
                        .user(DocDetailRSP.User.builder()
                                .id(String.valueOf(currentUserId))
                                .name(userNameResolver.sysUserId2NameSingle(currentUserId))
                                .build())
                        .customization(DocDetailRSP.Customization.builder()
                                // 选为不允许自动保存
                                .autosave(false)
                                .forcesave(true)
                                .help(false)
                                .comments(false)
                                .plugins(false)
                                .logo(DocDetailRSP.Logo.builder()
                                        .image(logoUrl)
                                        .imageDark(logoUrl)
                                        .url(indexHost)
                                        .build())
                                .review(DocDetailRSP.Review.builder()
                                        .hoverMode(false)
                                        .reviewDisplay("markup")
                                        .showReviewChanges(false)
                                        .trackChanges(true)
                                        .build())
                                .build())
                        .coEditing(DocDetailRSP.CoEditing.builder()
                                // 选为严格 不允许修改 不让用户触发自动保存 必须让用户在界面上手动保存，然后才可触发保存后回调
                                .mode("strict")
                                .change(false)
                                .build())
                        .build())
                .build();
        // 业务模块增强
        OoBizHandler ooBizHandler = ooBizHandlerFactory.getHandler(materialsList.getBusinessType());
        if (Objects.nonNull(ooBizHandler)) {
            ooBizHandler.handle(docDetailRSP, materialsList);
        }
        return docDetailRSP;
    }


    @SneakyThrows
    public void download(String key) {
        log.info("onlyoffice服务发起文件下载请求[{}]", key);
        OnlyofficeKeyStore keyStore = onlyofficeKeyStoreMapper.selectOne(Wrappers.<OnlyofficeKeyStore>lambdaQuery()
                .eq(OnlyofficeKeyStore::getFileKey, key)
                .last("LIMIT 1"));
        if (Objects.isNull(keyStore)) {
            throw new MithrasException("文件不存在");
        }
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(FileNameUtil.getName(keyStore.getFileOssPath())));
        ossClient.downLoad(response.getOutputStream(), keyStore.getFileOssPath());
    }

    @SneakyThrows
    public void callback(String callbackData) {
        /**
         * {
         *     "key": "123.docx",
         *     "status": 2,
         *     "url": "http://172.16.200.21:9080/cache/files/123.docx_704/output.docx/output.docx?md5=wyNj0Vg4lvtL2OXlIYzdZg&expires=1656903541&filename=output.docx",
         *     "changesurl": "http://172.16.200.21:9080/cache/files/123.docx_704/changes.zip/changes.zip?md5=ijz0LN7L6NrP0cM64DG-cA&expires=1656903541&filename=changes.zip",
         *     "history": {
         *         "serverVersion": "7.1.1",
         *         "changes": [
         *             {
         *                 "created": "2022-07-04 02:42:00",
         *                 "user": {
         *                     "id": "uid-1656901793875",
         *                     "name": "Anonymous"
         *                 }
         *             }
         *         ]
         *     },
         *     "users": [
         *         "uid-1656901793875"
         *     ],
         *     "actions": [
         *         {
         *             "type": 0,
         *             "userid": "uid-1656901793875"
         *         }
         *     ],
         *     "lastsave": "2022-07-04T02:43:52.000Z",
         *     "notmodified": false,
         *     "filetype": "docx"
         * }
         */
        log.info("回调数据:{}", callbackData);
        JSONObject cbObj = JSONObject.parseObject(callbackData);
        String fileKey = cbObj.getString("key");
        Integer status = cbObj.getInteger("status");
        boolean flag = Objects.equals(2, status) || Objects.equals(6, status);
        if (flag || (cbObj.containsKey("c") && Objects.equals("forcesave", cbObj.getString("c")))) {
            OnlyofficeKeyStore keyStore = onlyofficeKeyStoreMapper.selectOne(Wrappers.<OnlyofficeKeyStore>lambdaQuery()
                    .eq(OnlyofficeKeyStore::getFileKey, fileKey)
                    .last("LIMIT 1"));
            if (Objects.isNull(keyStore)) {
                log.error("通过文件key没有找到对应缓存数据[{}]", fileKey);
                return;
            }
            if (flag) {
                // 保存
                URL url = new URL(cbObj.getString("url"));
                URLConnection conn = url.openConnection();
//            byte[] bs = IoUtil.readBytes(conn.getInputStream());
                ossClient.upLoadUnknowSize(conn.getInputStream(), keyStore.getFileOssPath(), true, 5 * 1024 * 1024);
                // 重新生成key（强制保存（status = 6）的时候不重新生成）
                if (Objects.equals(status, 2)) {
                    String newFileKey = generateKey();
                    keyStore.setFileKey(newFileKey);
                    keyStore.setUpdateTime(LocalDateTime.now());
                    onlyofficeKeyStoreMapper.updateById(keyStore);
                    log.info("更新onlyoffice文件key成功[原key:{}, 新key:{}]", fileKey, newFileKey);
                }
            }
            // 如果materials_list中的数据是自动生成的，则更新标识位为非自动生成，用以解决重新自动生成覆盖了已修改文件的问题
            // 最新逻辑：materials_list添加is_edit字段，后续自动生成的文件编辑后不再调整自动生成标识 改为将is_edit更新为1（非自动生成的文件也维护了该字段）
            try {
                this.tryResetSystemGenerateFlag(keyStore.getFileOssPath());
            } catch (Exception e) {
                log.error("尝试修改文件是否被编辑标识位发生异常，不影响当前业务流程，请查看日志确认问题[keyStore:{}]", JSONUtil.toJsonStr(keyStore), e);
            }
        }

    }

    private static String generateKey() {
        return RandomUtil.randomString(32);
    }

    private MaterialsList actualLib2Entity(MaterialsListLib materialsListLib) {
        if (Objects.isNull(materialsListLib)) {
            return null;
        }
        MaterialsList materialsList = BeanUtil.copyProperties(materialsListLib, MaterialsList.class);
        materialsList.setId(materialsListLib.getOriginId());
        materialsList.setCreateBy(materialsListLib.getDataCreateBy());
        materialsList.setUpdateBy(materialsListLib.getDataUpdateBy());
        materialsList.setCreateTime(materialsListLib.getDataCreateTime());
        materialsList.setUpdateTime(materialsListLib.getDataUpdateTime());
        return materialsList;
    }

    private void tryResetSystemGenerateFlag(String ossFileName) {
        Assert.notBlank(ossFileName, () -> MithrasException.newException("OSS文件路径为空"));
//        String[] array = ossFileName.split("/");
//        Assert.isTrue(array.length >= 2, () -> MithrasException.newException("不符合预定规则的OSS文件路径"));
//        String s1 = array[0];
//        Long belongId = null;
//        if (NumberUtil.isNumber(s1)) {
//            belongId = Long.parseLong(s1);
//        }
//        Assert.notNull(belongId, () -> MithrasException.newException("没有成功从OSS文件路径中获取到业务数据id"));
//        String businessType = array[1];
//        Assert.notBlank(businessType, () -> MithrasException.newException("没有成功从OSS文件路径中获取到业务类型"));
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
//        query.eq(MaterialsList::getBelongId, belongId);
//        query.eq(MaterialsList::getBusinessType, businessType);
        query.eq(MaterialsList::getOssFilename, ossFileName);
        query.eq(MaterialsList::getIsEdit, YesOrNoNumberEnum.NO.getCode());
        query.last(StringUtil.mysqlLimitOne());
        MaterialsList materialsList = materialsListMapper.selectOne(query);
        if (Objects.nonNull(materialsList)) {
//            materialsList.setSystemGenerate(YesOrNoNumberEnum.NO.getCode());
            materialsList.setIsEdit(YesOrNoNumberEnum.YES.getCode());
            materialsListMapper.updateById(materialsList);
        }
    }
}
