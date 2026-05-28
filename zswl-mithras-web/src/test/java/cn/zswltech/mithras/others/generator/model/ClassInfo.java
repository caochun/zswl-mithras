package cn.zswltech.mithras.others.generator.model;

import java.util.List;

/**
 * class info
 *
 * @author xuxueli 2018-05-02 20:02:34
 */
public class ClassInfo {

    private String tableName;
    private String originTableName;
    private String className;
    private String lcHeadClassName;
    private String classComment;
    private List<FieldInfo> fieldList;
    private String urlPrefix;

    public void setClassName(String className) {
        this.className = className;
        this.lcHeadClassName = (new StringBuilder())
                .append(Character.toLowerCase(className.charAt(0)))
                .append(className.substring(1))
                .toString();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOriginTableName() {
        return originTableName;
    }

    public void setOriginTableName(String originTableName) {
        this.originTableName = originTableName;
    }

    public String getClassName() {
        return className;
    }

    public String getLcHeadClassName() {
        return lcHeadClassName;
    }

    public void setLcHeadClassName(String lcHeadClassName) {
        this.lcHeadClassName = lcHeadClassName;
    }

    public String getClassComment() {
        return classComment;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }
}
