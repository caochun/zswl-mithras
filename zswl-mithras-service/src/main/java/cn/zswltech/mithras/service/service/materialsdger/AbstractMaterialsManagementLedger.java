package cn.zswltech.mithras.service.service.materialsdger;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public abstract class AbstractMaterialsManagementLedger<T, R> implements InitializingBean {

    /**
     * 部门 id2名称
     */
    protected static final Map<Long, String> bizDeptId2NameMap = new ConcurrentHashMap<>();
    /**
     * 客户 id2名称
     */
    protected static final Map<Long, String> clientId2NameMap = new ConcurrentHashMap<>();
    /**
     * 用户 id2名称
     */
    protected static final Map<Long, String> userId2NameMap = new ConcurrentHashMap<>();

    /**
     * 融资机构 id2名称
     */
    protected static final Map<Long, String> orgId2NameMap = new ConcurrentHashMap<>();

    public abstract PageR<R> queryMaterialsLedger(T t);

    protected abstract void init();

    /**
     * id 转名称
     */
    protected String id2Name(Map<Long, String> id2Name, Long id, Function<Long, String> dealWithNull){
        if (Objects.isNull(id)){
            return Strings.EMPTY;
        }
        String name = id2Name.get(id);
        if (StringUtils.isEmpty(name)){
            name = dealWithNull.apply(id);
            id2Name.put(id, name);
        }
        return name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }
}
