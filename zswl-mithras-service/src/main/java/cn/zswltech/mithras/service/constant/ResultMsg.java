package cn.zswltech.mithras.service.constant;

/**
 * @author junke
 */
public interface ResultMsg {
    String CLIENT_EXIST = "该客户已存在";
    String TYC_EXCEPTION = "调用天眼查失败";
    String ID_CARD_ERROR = "身份证号输入不合法，请重新输入!";
    String ZZ_CODE_DUPLICATE = "中征码重复";
    String ORG_CODE_DUPLICATE = "组织机构代码重复";
    String CLIENT_COMMERCE_INFO_EXIST = "客户工商信息已经存在";
    String RECORD_NOT_EXIST = "记录不存在";
    String RECORD_ALREADY_EXIST = "记录已存在";
    String CLIENT_RELATED_PROJECTS = "客户已关联到项目";
    String SUBJECT_EXCEL_TOO_MANY = "excel行列过多";
    String SUBJECT_REPORT_TYPE_ERROR = "财务报表类型错误";
    String SUBJECT_QUARTER_TYPE_ERROR = "财务报表季度错误";
    String SUBJECT_YEAR_ERROR = "财务报表年份错误";
    String SUBJECT_ITEM_BALANCE_ERROR = "财务报表平衡检测错误";
    String SUBJECT_ITEM_CALC_ERROR = "财务报表计算业务指标错误";
    String USER_NOT_LOGIN = "用户未登陆";
    String COMMERCE_INFO_NULL = "请先维护工商信息";
    String ONLY_BIZ_DEPT_DO = "只有业务部门才能创建此数据";
    String ONLY_CREATOR_MODIFY = "只有创建人可以维护数据";
    String PROJ_CLOSED = "已关闭";
    String CONCURRENT_OPERATION = "该业务数据变动处理中，请稍后重试";
    String UNSUPPORT_TYPE = "暂不支持此类型";
    String FILE_NOT_EXIST = "文件不存在";
    String PROJECT_CLASSIFY_NULL="行业分类不能为空";
    String LIQUIDITY_MANAGE_LOCK="保存中，请稍后重试";
    String REPORT_INSTANCE_ID_NULL=" 报表实例唯一标识不能为空";
    String KPI_PROVISION_DETAIL_LOCK="计算中，请稍后重试";
    String LEGAL_MANAGER_NULL="法务经理为null";
}
