package cn.zswltech.mithras.filingmaterials.service;

import cn.zswltech.mithras.filingmaterials.mapper.ArchivedMaterialsDownloadRecordMapper;
import cn.zswltech.mithras.filingmaterials.model.ArchivedMaterialsDownloadRecord;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ArchivedMaterialsDownloadRecordService extends ServiceImpl<ArchivedMaterialsDownloadRecordMapper, ArchivedMaterialsDownloadRecord> {

    public ArchivedMaterialsDownloadRecord create(String fileName, String downloadStatus) {
        ArchivedMaterialsDownloadRecord downloadRecord = new ArchivedMaterialsDownloadRecord();
        downloadRecord.setFileName(fileName);
        downloadRecord.setDownloadStatus(downloadStatus);
        this.save(downloadRecord);
        return downloadRecord;
    }

    public Page<ArchivedMaterialsDownloadRecord> pageOrderByCreateTimeDesc(long page, long pageSize) {
        return this.page(new Page<>(page, pageSize),
                Wrappers.<ArchivedMaterialsDownloadRecord>lambdaQuery().orderByDesc(ArchivedMaterialsDownloadRecord::getCreateTime));
    }

    public void updateResult(Long id, String downloadStatus, String filePath) {
        ArchivedMaterialsDownloadRecord downloadRecord = new ArchivedMaterialsDownloadRecord();
        downloadRecord.setId(id);
        downloadRecord.setDownloadStatus(downloadStatus);
        downloadRecord.setFilePath(filePath);
        this.updateById(downloadRecord);
    }
}
