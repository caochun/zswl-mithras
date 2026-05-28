package cn.zswltech.mithras.service.auth.checker;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限处理helper
 *
 * @author wangchuanhao
 * @date 2022/11/17 10:01 PM
 */
@Component
@Slf4j
public class AuthHelper {

    /**
     * 找到有主办和业务部门的对象
     * 该模块主表自己没有主办字段 要从别的模块取 获取实际要取主办值的模块对象
     *
     * @param businessModule
     * @param mainObject
     * @return
     */
    public Object getAuthObj(BusinessModuleEnum businessModule, Object mainObject) {
        if (Objects.isNull(mainObject)) {
            return mainObject;
        }
        BusinessModuleEnum relateBusinessModule = BusinessModuleEnum.of(businessModule.getSponsorModule());
        BaseMapper relateMainTableMapper = SpringContextHolder.getBean(relateBusinessModule.getMainMapperClass());
        Long relateMainId = (Long) ReflectUtil.getFieldValue(mainObject, businessModule.getSponsorModuleIdFieldName());
        Object relateMainObject = relateMainTableMapper.selectById(relateMainId);
        if (Objects.isNull(relateMainObject)) {
            // 虽然理论上不可能出现关联模块找不到数据的问题
            log.error("权限校验失败，从关联模块找数据不存在，数据:{}", JSON.toJSONString(mainObject));
            throw new AuthCheckException("主表数据不存在");
        }
        if (StringUtils.isNotBlank(relateBusinessModule.getSponsorModule())) {
            // 不止一级 递归寻找
            return getAuthObj(relateBusinessModule, relateMainObject);
        }
        return relateMainObject;
    }

    public List<Object> getAuthObjList(BusinessModuleEnum businessModule, List<Object> mainObjectList) {
        if (CollectionUtils.isEmpty(mainObjectList)) {
            return mainObjectList;
        }
        BusinessModuleEnum relateBusinessModule = BusinessModuleEnum.of(businessModule.getSponsorModule());
        BaseMapper relateMainTableMapper = SpringContextHolder.getBean(relateBusinessModule.getMainMapperClass());
        Set<Long> relateMainIdSet = mainObjectList.stream().map(o -> (Long) ReflectUtil.getFieldValue(o, businessModule.getSponsorModuleIdFieldName())).collect(Collectors.toSet());
        List<Object> relateMainObjectList = relateMainTableMapper.selectBatchIds(relateMainIdSet);
        Set<Long> queryRelateMainIdSet = relateMainObjectList.stream().map(o -> (Long) ReflectUtil.getFieldValue(o, "id")).collect(Collectors.toSet());

        Sets.SetView<Long> errorRelateMainIdSet = Sets.difference(relateMainIdSet, queryRelateMainIdSet);
        if (CollectionUtils.isNotEmpty(errorRelateMainIdSet)) {
            // 虽然理论上不可能出现关联模块找不到数据的问题
            log.error("权限校验失败，从关联模块找数据不存在，缺失id列表:{}，源数据:{}", JSON.toJSONString(errorRelateMainIdSet), JSON.toJSONString(mainObjectList));
            throw new AuthCheckException("主表数据不存在");
        }
        if (StringUtils.isNotBlank(relateBusinessModule.getSponsorModule())) {
            // 不止一级 递归寻找
            return getAuthObjList(relateBusinessModule, relateMainObjectList);
        }
        return relateMainObjectList;
    }

    /**
     * 从子表拿到主表的id
     *
     * @param businessModule
     * @param helperMapperClass
     * @param keyId
     * @return
     */
    public Long getMainIdFromSubTable(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Long keyId) {
        BaseMapper subTableMapper = SpringContextHolder.getBean(helperMapperClass);
        Object subData = subTableMapper.selectById(keyId);
        if (Objects.isNull(subData)) {
            throw new AuthCheckException("子表数据不存在，无法进行操作");
        }
        Long mainId = (Long) ReflectUtil.getFieldValue(subData, businessModule.getSubTableMainIdFieldName());
        return mainId;
    }

    /**
     * 从子表拿到主表的id
     *
     * @param businessModule
     * @param helperMapperClass
     * @param keyIdList
     * @return
     */
    public List<Long> listMainIdFromSubTable(BusinessModuleEnum businessModule, Class<? extends BaseMapper> helperMapperClass, Collection<Long> keyIdList) {
        BaseMapper subTableMapper = SpringContextHolder.getBean(helperMapperClass);
        List<Object> subDataList = subTableMapper.selectBatchIds(keyIdList);
        if (CollectionUtils.isEmpty(subDataList)) {
            throw new AuthCheckException("子表数据不存在，无法进行操作");
        }
        List<Long> mainIdList = subDataList.stream().map(subData -> (Long) ReflectUtil.getFieldValue(subData, businessModule.getSubTableMainIdFieldName())).distinct().collect(Collectors.toList());
        return mainIdList;
    }

    /**
     * 从对象中根据表达式取值
     * 比如 直取xxx字段
     * 或 取xxx字段中的xxx字段
     *
     * @param paramObject
     * @param expression
     * @return
     */
    public Object extractObjectByExpression(Object paramObject, String expression) {
        if (Objects.isNull(paramObject) || StringUtils.isBlank(expression)) {
            return null;
        }
        Object resultObject = null;
        String[] expressArray = expression.split(",");
        for (String express : expressArray) {
            Object obj = paramObject;
            String[] array = express.split("\\.");
            for (String fieldName : array) {
                obj = ReflectUtil.getFieldValue(obj, fieldName);
                if (Objects.isNull(obj)) {
                    break;
                }
            }
            if (!Objects.isNull(obj)) {
                resultObject = obj;
                break;
            }
        }
        return resultObject;
    }

}
