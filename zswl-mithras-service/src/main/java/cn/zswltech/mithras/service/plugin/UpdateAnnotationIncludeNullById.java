package cn.zswltech.mithras.service.plugin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2022/10/19
 * @description
 */
public class UpdateAnnotationIncludeNullById extends AbstractMethod {
    private static final String SQL_TEMPLATE = "<script> update %s set %s where %s </script>";

    private static final List<String> IGNORE_COLUMNS = Arrays.asList("create_by", "create_time", "update_time");

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String tableName = tableInfo.getTableName();
        String sqlWhere = tableInfo.getKeyColumn() + " = " + "#{" + tableInfo.getKeyProperty() + "}";
        String setPropSql = this.sqlSet(modelClass, tableInfo);
        String sql = String.format(SQL_TEMPLATE, tableName, setPropSql, sqlWhere);
        if (StrUtil.isBlank(sql)) {
            throw new RuntimeException("table " + tableInfo.getTableName() + " has no update property");
        }
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass, modelClass, "updateAnnotationIncludeNullById", sqlSource);
    }

    private String sqlSet(Class<?> modelClass, TableInfo tableInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        List<TableFieldInfo> tableFieldInfoList = tableInfo.getFieldList();
        if (CollectionUtil.isEmpty(tableFieldInfoList)) {
            throw new RuntimeException("table " + tableInfo.getTableName() + " model class no property");
        }
        for (TableFieldInfo tableFieldInfo : tableFieldInfoList) {
            if (IGNORE_COLUMNS.contains(tableFieldInfo.getColumn())) {
                continue;
            }
            Field field = ReflectUtil.getField(modelClass, tableFieldInfo.getProperty());
            IncludeNull includeNull = field.getAnnotation(IncludeNull.class);
            if (Objects.isNull(includeNull)) {
                // 说明字段不需要支持更新空值，sql中需要拼接判空条件
                stringBuilder.append(String.format("<if test=\"%s != null\">", tableFieldInfo.getProperty()));
            } else {
                stringBuilder.append("<if test=\"1 == 1\">");
            }
            stringBuilder.append(String.format("%s = #{%s}, ", tableFieldInfo.getColumn(), tableFieldInfo.getProperty()));
            stringBuilder.append("</if>");
        }
        stringBuilder.append("update_time = now()");
        return stringBuilder.toString();
    }
}
