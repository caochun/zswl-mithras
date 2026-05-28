package cn.zswltech.mithras.metric.emit.model.req.regime;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegimeInfoReqBody {
    private Byte addType;// byte 必须 操作类型（0 新增，1修订）若修订，则需关联制度
    private String appendix;// string 必须 附件JsonArray  (文件id数组）长度限制:200
    private String bizType;	//string	非必须	制度类型	长度限制:50
    private String createBy;//	string	必须	创建用户	长度限制:50
    private LocalDateTime gmtCreate;//	date	必须	创建时间
    private LocalDateTime implTime;	//date	必须	实施日期
    private String issueCompany;//	string	必须	印发公司--机构Code	长度限制:40
    private String issueNumber;	//string	必须	印发文号	长度限制:40
    private LocalDateTime issueTime;	//date	必须	发文日期
    private String issuedDept;	//string	必须	印发部门-机构Code	长度限制:40
    private LocalDateTime maturityTime;//	date	非必须	到期日期
    private String relation;	//string	必须	关联制度json数组	长度限制:200
    private Byte status;	//byte	必须	状态(0启用，1作废)	format: byte
    private String title;	//string	必须	标题名称	长度限制:100
    private Byte isTry;	//byte	必须	是否试行（0 是，1否）	format: byte
    private LocalDateTime tryEndTime;	//date	非必须	制度-试行结束时间（若试行必填）
    private String orgId;	//String	必须	机构code	长度限制：50
}
