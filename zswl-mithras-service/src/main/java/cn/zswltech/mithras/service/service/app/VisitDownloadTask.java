package cn.zswltech.mithras.service.service.app;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswl.oss.core.OssClient;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.app.VisitDownloadTaskStatusEnum;
import cn.zswltech.mithras.service.mapper.MaterialsListMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitDownloadTaskRecordMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app.VisitRecordMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitDownloadTaskRecord;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.FileUriUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static cn.hutool.core.text.CharSequenceUtil.join;

/**
 * @author dingqi
 * @date 2025/9/18
 * @description
 */
@AllArgsConstructor
@Slf4j
public class VisitDownloadTask implements Runnable {
    private Long taskId;

    @Override
    public void run() {
        VisitDownloadTaskRecord taskRecord = SpringUtil.getBean(VisitDownloadTaskRecordMapper.class).selectById(taskId);
        if (Objects.isNull(taskRecord)) {
            throw new MithrasException("下载任务记录不存在");
        }
        List<Long> visitRecordIds = JSONUtil.toList(taskRecord.getReqParams(), Long.class);
        // 查询拜访记录
        List<VisitRecord> visitRecordList = SpringUtil.getBean(VisitRecordMapper.class).selectBatchIds(visitRecordIds);
        Map<String, List<VisitRecord>> visitRecordMap = visitRecordList.stream().collect(Collectors.groupingBy(VisitRecord::getClientName));
        // 查询文件信息
        List<MaterialsList> fileList = SpringUtil.getBean(MaterialsListMapper.class).selectList(
                Wrappers.<MaterialsList>lambdaQuery()
                        .eq(MaterialsList::getBusinessType, BusinessModuleEnum.VISIT_RECORD.name())
                        .in(MaterialsList::getBelongId, visitRecordIds)
        );
        Map<Long, List<MaterialsList>> fileMap = fileList.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
        String localFilePath = "/tmp/" + taskRecord.getFileName();
        OutputStream outputStream = FileUtil.getOutputStream(localFilePath);
        try {
            // 目录结构：拜访照片/客户名称/拜访时间
            String rootPath = "拜访照片";
            Set<String> pathSet = new HashSet<>();
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            for (Map.Entry<String, List<VisitRecord>> entry : visitRecordMap.entrySet()) {
                entry.getValue().sort(Comparator.comparing(VisitRecord::getCheckInDate));
                for (VisitRecord visitRecord : entry.getValue()) {
                    List<MaterialsList> mList = fileMap.get(visitRecord.getId());
                    if (CollectionUtil.isEmpty(mList)) {
                        continue;
                    }
                    String date = LocalDateTimeUtil.format(visitRecord.getCheckInDate(), DatePattern.PURE_DATETIME_PATTERN);
                    for (MaterialsList m : mList) {
                        try {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            SpringUtil.getBean(OssClient.class).downLoad(byteArrayOutputStream, join("/", m.getOssFilename()));
                            byte[] buffer = byteArrayOutputStream.toByteArray();
                            String originPath = rootPath + "/" + entry.getKey() + "/" + date + "/" + m.getFilename();
                            String finalPath = FileUriUtil.fileNameDeduplication(pathSet, originPath);
                            ZipEntry zipEntry = new ZipEntry(finalPath);
                            zipOutputStream.putNextEntry(zipEntry);
                            zipOutputStream.write(buffer);
                            zipOutputStream.closeEntry();
                            zipOutputStream.flush();
                        } catch (Exception e) {
                            log.error("批量导出客户拜访照片，下载单个照片发生异常，跳过该照片不处理[{}]", JSONUtil.toJsonStr(m), e);
                        }
                    }
                }
            }
            zipOutputStream.flush();
            zipOutputStream.close();
            // 上传至Minio
            SpringUtil.getBean(OssClient.class).upLoad(FileUtil.getInputStream(localFilePath), taskRecord.getFilePath());
        } catch (Exception e) {
            log.error("客户拜访文件批量下载任务处理异常[{}]", JSONUtil.toJsonStr(taskRecord), e);
            throw new MithrasException("");
        } finally {
            // 删除本地文件
            FileUtil.del(localFilePath);
        }
    }
}
