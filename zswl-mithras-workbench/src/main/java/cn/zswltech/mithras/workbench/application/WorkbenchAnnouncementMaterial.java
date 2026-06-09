package cn.zswltech.mithras.workbench.application;

public class WorkbenchAnnouncementMaterial {

    private Long id;
    private Long belongId;
    private String ossFilename;
    private String previewUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBelongId() {
        return belongId;
    }

    public void setBelongId(Long belongId) {
        this.belongId = belongId;
    }

    public String getOssFilename() {
        return ossFilename;
    }

    public void setOssFilename(String ossFilename) {
        this.ossFilename = ossFilename;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
