package cn.zswltech.mithras.archives.application.port;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArchivesSupportPort {
    String BUSINESS_TYPE_ARCHIVES = "ARCHIVES";

    Map<String, ProjEstablishVagueListRSP> vagueQuery(ProjEstablishVagueListREQ req);

    ProjectInfo getProjectInfo(Long projectId);

    Map<Long, ProjectInfo> getProjectInfoMap(Collection<Long> projectIds);

    List<MaterialInfo> listMaterialsByBelongId(String businessType, Long belongId);

    List<MaterialInfo> getMaterialsByIds(Collection<Long> recordIds);

    List<Long> canViewDeptIds();

    Map<Long, String> clientId2Name(Collection<Long> clientIds);

    Map<Long, String> sysUserId2Name(Collection<Long> userIds);

    Map<Long, String> deptId2Name(Collection<Long> deptIds);

    class MaterialInfo {
        private Long id;
        private String materialsType;
        private String filename;
        private Long belongId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getMaterialsType() {
            return materialsType;
        }

        public void setMaterialsType(String materialsType) {
            this.materialsType = materialsType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public Long getBelongId() {
            return belongId;
        }

        public void setBelongId(Long belongId) {
            this.belongId = belongId;
        }
    }

    class ProjectInfo {
        private Long id;
        private String projName;
        private String bizType;
        private Long clientId;
        private Long bizDeptId;
        private Long bizDeptLeaderId;
        private Long projSponsorUserId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getProjName() {
            return projName;
        }

        public void setProjName(String projName) {
            this.projName = projName;
        }

        public String getBizType() {
            return bizType;
        }

        public void setBizType(String bizType) {
            this.bizType = bizType;
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

        public Long getProjSponsorUserId() {
            return projSponsorUserId;
        }

        public void setProjSponsorUserId(Long projSponsorUserId) {
            this.projSponsorUserId = projSponsorUserId;
        }
    }
}
