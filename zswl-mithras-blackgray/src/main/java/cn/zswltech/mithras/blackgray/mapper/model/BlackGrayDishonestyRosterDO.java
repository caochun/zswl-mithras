package cn.zswltech.mithras.blackgray.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@TableName(value = "black_gray_dishonesty_roster")
public class BlackGrayDishonestyRosterDO {

    @Id
    @TableId(type = IdType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 人员强制执行类型


     */
    @Column(name = "people_enforced_type")
    private String peopleEnforcedType;

    /**
     * 人员强制id
     */
    @Column(name = "people_enforced_id")
    private String peopleEnforcedId;

    /**
     * 被执行企业名称
     */
    @Column(name = "people_enforced")
    private String peopleEnforced;

    /**
     * 性别
     */
    @Column(name = "gender")
    private String gender;

    /**
     * 统一社会编码
     */
    @Column(name = "cert_code")
    private String certCode;

    /**
     * 法人
     */
    @Column(name = "legal_person")
    private String legalPerson;

    /**
     * 法院名称
     */
    @Column(name = "court_name")
    private String courtName;

    /**
     * 状态
     */
    @Column(name = "state")
    private String state;

    /**
     * 执行编号


     */
    @Column(name = "execute_number")
    private String executeNumber;

    /**
     * 归档时间


     */
    @Column(name = "filing_time")
    private String filingTime;

    /**
     * 案例编号


     */
    @Column(name = "case_number")
    private String caseNumber;

    /**
     * 执行者


     */
    @Column(name = "execute_maker")
    private String executeMaker;

    /**
     * 执行状态
     */
    @Column(name = "performance")
    private String performance;

    /**
     * 执行
     */
    @Column(name = "executed")
    private String executed;

    /**
     * 不执行
     */
    @Column(name = "unexecute")
    private String unexecute;

    /**
     * 行为
     */
    @Column(name = "behavior")
    private String behavior;

    /**
     * 信息发布日期(披露时间)
     */
    @Column(name = "info_publ_date")
    private Date infoPublDate;

    /**
     * 创建时间
     */
    @Column(name = "insert_time")
    private Date insertTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "jsid")
    private String jsid;

    /**
     * 执行状态
     */
    @Column(name = "execute_state")
    private String executeState;

    /**
     * 执行目标金额
     */
    @Column(name = "execute_target")
    private String executeTarget;

    @Column(name = "data_json")
    private String dataJson;

    /**
     * 历史标识
     */
    @Column(name = "If_history")
    private String ifHistory;

    /**
     * 名单类型
     */
    @Column(name = "roster_type")
    private String rosterType;

    /**
     * 黑灰标识
     */
    @Column(name = "black_gray_type")
    private String blackGrayType;

    /**
     * 列入原因
     */
    @Column(name = "obligations")
    private String obligations;

    public static final String ID = "id";

    public static final String DB_ID = "ID";

    public static final String PEOPLE_ENFORCED_TYPE = "peopleEnforcedType";

    public static final String DB_PEOPLE_ENFORCED_TYPE = "people_enforced_type";

    public static final String PEOPLE_ENFORCED_ID = "peopleEnforcedId";

    public static final String DB_PEOPLE_ENFORCED_ID = "people_enforced_id";

    public static final String PEOPLE_ENFORCED = "peopleEnforced";

    public static final String DB_PEOPLE_ENFORCED = "people_enforced";

    public static final String GENDER = "gender";

    public static final String DB_GENDER = "gender";

    public static final String CERT_CODE = "certCode";

    public static final String DB_CERT_CODE = "cert_code";

    public static final String LEGAL_PERSON = "legalPerson";

    public static final String DB_LEGAL_PERSON = "legal_person";

    public static final String COURT_NAME = "courtName";

    public static final String DB_COURT_NAME = "court_name";

    public static final String STATE = "state";

    public static final String DB_STATE = "state";

    public static final String EXECUTE_NUMBER = "executeNumber";

    public static final String DB_EXECUTE_NUMBER = "execute_number";

    public static final String FILING_TIME = "filingTime";

    public static final String DB_FILING_TIME = "filing_time";

    public static final String CASE_NUMBER = "caseNumber";

    public static final String DB_CASE_NUMBER = "case_number";

    public static final String EXECUTE_MAKER = "executeMaker";

    public static final String DB_EXECUTE_MAKER = "execute_maker";

    public static final String PERFORMANCE = "performance";

    public static final String DB_PERFORMANCE = "performance";

    public static final String EXECUTED = "executed";

    public static final String DB_EXECUTED = "executed";

    public static final String UNEXECUTE = "unexecute";

    public static final String DB_UNEXECUTE = "unexecute";

    public static final String BEHAVIOR = "behavior";

    public static final String DB_BEHAVIOR = "behavior";

    public static final String INFO_PUBL_DATE = "infoPublDate";

    public static final String DB_INFO_PUBL_DATE = "info_publ_date";

    public static final String INSERT_TIME = "insertTime";

    public static final String DB_INSERT_TIME = "insert_time";

    public static final String UPDATE_TIME = "updateTime";

    public static final String DB_UPDATE_TIME = "update_time";

    public static final String JSID = "jsid";

    public static final String DB_JSID = "jsid";

    public static final String EXECUTE_STATE = "executeState";

    public static final String DB_EXECUTE_STATE = "execute_state";

    public static final String EXECUTE_TARGET = "executeTarget";

    public static final String DB_EXECUTE_TARGET = "execute_target";

    public static final String DATA_JSON = "dataJson";

    public static final String DB_DATA_JSON = "data_json";

    public static final String IF_HISTORY = "ifHistory";

    public static final String DB_IF_HISTORY = "If_history";

    public static final String ROSTER_TYPE = "rosterType";

    public static final String DB_ROSTER_TYPE = "roster_type";

    public static final String BLACK_GRAY_TYPE = "blackGrayType";

    public static final String DB_BLACK_GRAY_TYPE = "black_gray_type";

    public static final String OBLIGATIONS = "obligations";

    public static final String DB_OBLIGATIONS = "obligations";

    /**
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取人员强制执行类型


     *
     * @return people_enforced_type - 人员强制执行类型


     */
    public String getPeopleEnforcedType() {
        return peopleEnforcedType;
    }

    /**
     * 设置人员强制执行类型


     *
     * @param peopleEnforcedType 人员强制执行类型


     */
    public void setPeopleEnforcedType(String peopleEnforcedType) {
        this.peopleEnforcedType = peopleEnforcedType;
    }

    /**
     * 获取人员强制id
     *
     * @return people_enforced_id - 人员强制id
     */
    public String getPeopleEnforcedId() {
        return peopleEnforcedId;
    }

    /**
     * 设置人员强制id
     *
     * @param peopleEnforcedId 人员强制id
     */
    public void setPeopleEnforcedId(String peopleEnforcedId) {
        this.peopleEnforcedId = peopleEnforcedId;
    }

    /**
     * 获取被执行企业名称
     *
     * @return people_enforced - 被执行企业名称
     */
    public String getPeopleEnforced() {
        return peopleEnforced;
    }

    /**
     * 设置被执行企业名称
     *
     * @param peopleEnforced 被执行企业名称
     */
    public void setPeopleEnforced(String peopleEnforced) {
        this.peopleEnforced = peopleEnforced;
    }

    /**
     * 获取性别
     *
     * @return gender - 性别
     */
    public String getGender() {
        return gender;
    }

    /**
     * 设置性别
     *
     * @param gender 性别
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * 获取统一社会编码
     *
     * @return cert_code - 统一社会编码
     */
    public String getCertCode() {
        return certCode;
    }

    /**
     * 设置统一社会编码
     *
     * @param certCode 统一社会编码
     */
    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    /**
     * 获取法人
     *
     * @return legal_person - 法人
     */
    public String getLegalPerson() {
        return legalPerson;
    }

    /**
     * 设置法人
     *
     * @param legalPerson 法人
     */
    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }

    /**
     * 获取法院名称
     *
     * @return court_name - 法院名称
     */
    public String getCourtName() {
        return courtName;
    }

    /**
     * 设置法院名称
     *
     * @param courtName 法院名称
     */
    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    /**
     * 获取状态
     *
     * @return state - 状态
     */
    public String getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取执行编号


     *
     * @return execute_number - 执行编号


     */
    public String getExecuteNumber() {
        return executeNumber;
    }

    /**
     * 设置执行编号


     *
     * @param executeNumber 执行编号


     */
    public void setExecuteNumber(String executeNumber) {
        this.executeNumber = executeNumber;
    }

    /**
     * 获取归档时间


     *
     * @return filing_time - 归档时间


     */
    public String getFilingTime() {
        return filingTime;
    }

    /**
     * 设置归档时间


     *
     * @param filingTime 归档时间


     */
    public void setFilingTime(String filingTime) {
        this.filingTime = filingTime;
    }

    /**
     * 获取案例编号


     *
     * @return case_number - 案例编号


     */
    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * 设置案例编号


     *
     * @param caseNumber 案例编号


     */
    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    /**
     * 获取执行者


     *
     * @return execute_maker - 执行者


     */
    public String getExecuteMaker() {
        return executeMaker;
    }

    /**
     * 设置执行者


     *
     * @param executeMaker 执行者


     */
    public void setExecuteMaker(String executeMaker) {
        this.executeMaker = executeMaker;
    }

    /**
     * 获取执行状态
     *
     * @return performance - 执行状态
     */
    public String getPerformance() {
        return performance;
    }

    /**
     * 设置执行状态
     *
     * @param performance 执行状态
     */
    public void setPerformance(String performance) {
        this.performance = performance;
    }

    /**
     * 获取执行
     *
     * @return executed - 执行
     */
    public String getExecuted() {
        return executed;
    }

    /**
     * 设置执行
     *
     * @param executed 执行
     */
    public void setExecuted(String executed) {
        this.executed = executed;
    }

    /**
     * 获取不执行
     *
     * @return unexecute - 不执行
     */
    public String getUnexecute() {
        return unexecute;
    }

    /**
     * 设置不执行
     *
     * @param unexecute 不执行
     */
    public void setUnexecute(String unexecute) {
        this.unexecute = unexecute;
    }

    /**
     * 获取行为
     *
     * @return behavior - 行为
     */
    public String getBehavior() {
        return behavior;
    }

    /**
     * 设置行为
     *
     * @param behavior 行为
     */
    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    /**
     * 获取信息发布日期(披露时间)


     *
     * @return info_publ_date - 信息发布日期(披露时间)


     */
    public Date getInfoPublDate() {
        return infoPublDate;
    }

    /**
     * 设置信息发布日期(披露时间)


     *
     * @param infoPublDate 信息发布日期(披露时间)


     */
    public void setInfoPublDate(Date infoPublDate) {
        this.infoPublDate = infoPublDate;
    }

    /**
     * 获取创建时间
     *
     * @return insert_time - 创建时间
     */
    public Date getInsertTime() {
        return insertTime;
    }

    /**
     * 设置创建时间
     *
     * @param insertTime 创建时间
     */
    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return jsid
     */
    public String getJsid() {
        return jsid;
    }

    /**
     * @param jsid
     */
    public void setJsid(String jsid) {
        this.jsid = jsid;
    }

    /**
     * 获取执行状态
     *
     * @return execute_state - 执行状态
     */
    public String getExecuteState() {
        return executeState;
    }

    /**
     * 设置执行状态
     *
     * @param executeState 执行状态
     */
    public void setExecuteState(String executeState) {
        this.executeState = executeState;
    }

    /**
     * 获取执行目标金额


     *
     * @return execute_target - 执行目标金额


     */
    public String getExecuteTarget() {
        return executeTarget;
    }

    /**
     * 设置执行目标金额


     *
     * @param executeTarget 执行目标金额


     */
    public void setExecuteTarget(String executeTarget) {
        this.executeTarget = executeTarget;
    }

    /**
     * @return data_json
     */
    public String getDataJson() {
        return dataJson;
    }

    /**
     * @param dataJson
     */
    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    /**
     * 获取历史标识
     *
     * @return If_history - 历史标识
     */
    public String getIfHistory() {
        return ifHistory;
    }

    /**
     * 设置历史标识
     *
     * @param ifHistory 历史标识
     */
    public void setIfHistory(String ifHistory) {
        this.ifHistory = ifHistory;
    }

    /**
     * 获取名单类型
     *
     * @return roster_type - 名单类型
     */
    public String getRosterType() {
        return rosterType;
    }

    /**
     * 设置名单类型
     *
     * @param rosterType 名单类型
     */
    public void setRosterType(String rosterType) {
        this.rosterType = rosterType;
    }

    /**
     * 获取名单类型
     *
     * @return black_gray_type - 名单类型
     */
    public String getBlackGrayType() {
        return blackGrayType;
    }

    /**
     * 设置名单类型
     *
     * @param blackGrayType 名单类型
     */
    public void setBlackGrayType(String blackGrayType) {
        this.blackGrayType = blackGrayType;
    }

    /**
     * 获取列入原因
     *
     * @return obligations - 列入原因
     */
    public String getObligations() {
        return obligations;
    }

    /**
     * 设置列入原因
     *
     * @param obligations 列入原因
     */
    public void setObligations(String obligations) {
        this.obligations = obligations;
    }
}