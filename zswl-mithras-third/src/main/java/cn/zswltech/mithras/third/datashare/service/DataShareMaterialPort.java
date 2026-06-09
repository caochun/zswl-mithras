package cn.zswltech.mithras.third.datashare.service;

import cn.zswltech.mithras.dto.file.FileUploadREQ;

import java.io.InputStream;

public interface DataShareMaterialPort {
    InputStream downloadLatestArchive(String filename);

    Long add(FileUploadREQ fileUploadREQ);
}
