package cn.zswltech.mithras.archives.application.port;

public interface ArchivesWorkflowPort {

    void startDownloadApproval(String batch);

    boolean isArchivesApprovalRunning(Long archivesId);

    void startArchivesApproval(ArchivesApprovalStartContext context);

    class ArchivesApprovalStartContext {
        private Long archivesId;
        private String projName;
        private Long clientId;
        private Long bizDeptId;
        private Long bizDeptLeaderId;

        public Long getArchivesId() {
            return archivesId;
        }

        public void setArchivesId(Long archivesId) {
            this.archivesId = archivesId;
        }

        public String getProjName() {
            return projName;
        }

        public void setProjName(String projName) {
            this.projName = projName;
        }

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public Long getBizDeptId() {
            return bizDeptId;
        }

        public void setBizDeptId(Long bizDeptId) {
            this.bizDeptId = bizDeptId;
        }

        public Long getBizDeptLeaderId() {
            return bizDeptLeaderId;
        }

        public void setBizDeptLeaderId(Long bizDeptLeaderId) {
            this.bizDeptLeaderId = bizDeptLeaderId;
        }
    }
}
