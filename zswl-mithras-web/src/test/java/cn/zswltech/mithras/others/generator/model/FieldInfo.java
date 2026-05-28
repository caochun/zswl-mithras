package cn.zswltech.mithras.others.generator.model;

/**
 * field info
 *
 * @author xuxueli 2018-05-02 20:11:05
 */
public class FieldInfo {

    private String columnName;
    private String fieldName;
    private String ucHeadFieldName;
    private String fieldClass;
    private String swaggerClass;
    private String fieldComment;
    //暂时只考虑，BIGINT、TINYINT、INTEGER、VARCHAR、TIMESTAMP、DECIMAL
    private String jdbcType;

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
        this.ucHeadFieldName = (new StringBuilder())
                .append(Character.toUpperCase(fieldName.charAt(0)))
                .append(fieldName.substring(1))
                .toString();
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getUcHeadFieldName() {
        return ucHeadFieldName;
    }

    public void setUcHeadFieldName(String ucHeadFieldName) {
        this.ucHeadFieldName = ucHeadFieldName;
    }

    public String getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(String fieldClass) {
        this.fieldClass = fieldClass;
    }

    public String getSwaggerClass() {
        return swaggerClass;
    }

    public void setSwaggerClass(String swaggerClass) {
        this.swaggerClass = swaggerClass;
    }

    public String getFieldComment() {
        return fieldComment;
    }

    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }
}
