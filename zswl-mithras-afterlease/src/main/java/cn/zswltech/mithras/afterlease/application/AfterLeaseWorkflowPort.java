package cn.zswltech.mithras.afterlease.application;

import java.util.List;
import java.util.Map;

public interface AfterLeaseWorkflowPort {

    String startExternalQueryApproval(ExternalQueryApprovalStartContext context);

    String startPenaltyReductionApproval(PenaltyReductionApprovalStartContext context);

    boolean canModifyAtCurrentProcessNode(Long businessKey, List<String> modelKeys);

    List<ApprovalReminderTask> listReportApprovalReminderTasks();

    class ExternalQueryApprovalStartContext {
        private Long queryId;
        private String clientName;
        private Long clientId;
        private Long deptId;
        private Map<String, Object> variables;

        public Long getQueryId() {
            return queryId;
        }

        public void setQueryId(Long queryId) {
            this.queryId = queryId;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public Long getClientId() {
            return clientId;
        }

        public void setClientId(Long clientId) {
            this.clientId = clientId;
        }

        public Long getDeptId() {
            return deptId;
        }

        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        public Map<String, Object> getVariables() {
            return variables;
        }

        public void setVariables(Map<String, Object> variables) {
            this.variables = variables;
        }
    }

    class PenaltyReductionApprovalStartContext {
        private Long reductionId;
        private Long clientId;
        private Long bizDeptId;
        private Long bizDeptLeaderId;
        private Long bizDivisionLeaderId;
        private String bizType;
        private String projName;
        private String projCosponsorUserIds;
        private Long applyCreditAmount;

        public Long getReductionId() {
            return reductionId;
        }

        public void setReductionId(Long reductionId) {
            this.reductionId = reductionId;
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

        public Long getBizDivisionLeaderId() {
            return bizDivisionLeaderId;
        }

        public void setBizDivisionLeaderId(Long bizDivisionLeaderId) {
            this.bizDivisionLeaderId = bizDivisionLeaderId;
        }

        public String getBizType() {
            return bizType;
        }

        public void setBizType(String bizType) {
            this.bizType = bizType;
        }

        public String getProjName() {
            return projName;
        }

        public void setProjName(String projName) {
            this.projName = projName;
        }

        public String getProjCosponsorUserIds() {
            return projCosponsorUserIds;
        }

        public void setProjCosponsorUserIds(String projCosponsorUserIds) {
            this.projCosponsorUserIds = projCosponsorUserIds;
        }

        public Long getApplyCreditAmount() {
            return applyCreditAmount;
        }

        public void setApplyCreditAmount(Long applyCreditAmount) {
            this.applyCreditAmount = applyCreditAmount;
        }
    }

    class ApprovalReminderTask {
        private Long assignee;
        private String taskId;
        private String businessKey;
        private String subModule;
        private String clientName;
        private String modelName;
        private String processInstanceId;
        private java.time.LocalDateTime taskCreateTime;

        public Long getAssignee() {
            return assignee;
        }

        public void setAssignee(Long assignee) {
            this.assignee = assignee;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getBusinessKey() {
            return businessKey;
        }

        public void setBusinessKey(String businessKey) {
            this.businessKey = businessKey;
        }

        public String getSubModule() {
            return subModule;
        }

        public void setSubModule(String subModule) {
            this.subModule = subModule;
        }

        public String getClientName() {
            return clientName;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getProcessInstanceId() {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
        }

        public java.time.LocalDateTime getTaskCreateTime() {
            return taskCreateTime;
        }

        public void setTaskCreateTime(java.time.LocalDateTime taskCreateTime) {
            this.taskCreateTime = taskCreateTime;
        }
    }
}
