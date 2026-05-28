package cn.zswltech.mithras.dto.filingmaterials;

import lombok.Data;

import java.io.File;


public class TempFileInfo {
    String zipFilePath;   // ZIP内的相对路径
    File tempFile;        // 本地临时文件
    boolean downloadSuccess; // 下载是否成功
    String errorMsg;      // 失败原因

    public TempFileInfo(String zipFilePath, File tempFile, boolean downloadSuccess, String errorMsg) {
        this.zipFilePath = zipFilePath;
        this.tempFile = tempFile;
        this.downloadSuccess = downloadSuccess;
        this.errorMsg = errorMsg;
    }

    public String getZipFilePath() {
        return zipFilePath;
    }

    public void setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
    }

    public File getTempFile() {
        return tempFile;
    }

    public void setTempFile(File tempFile) {
        this.tempFile = tempFile;
    }

    public boolean isDownloadSuccess() {
        return downloadSuccess;
    }

    public void setDownloadSuccess(boolean downloadSuccess) {
        this.downloadSuccess = downloadSuccess;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
