package cn.zswltech.mithras.archives.application;

import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.document.model.MaterialsList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ArchivesSupportPort {
    String BUSINESS_TYPE_ARCHIVES = "ARCHIVES";

    Map<String, ProjEstablishVagueListRSP> vagueQuery(ProjEstablishVagueListREQ req);

    ProjectInfo getProjectInfo(Long projectId);

    Map<Long, ProjectInfo> getProjectInfoMap(Collection<Long> projectIds);

    List<MaterialsList> listMaterialsByBelongId(String businessType, Long belongId);

    List<MaterialsList> getMaterialsByIds(Collection<Long> recordIds);

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
