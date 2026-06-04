package cn.zswltech.mithras.others.hand.extract;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.query.UserQuery;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.system.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据转换
 *
 * @author wangchuanhao
 * @date 2022/9/25 9:58 AM
 */
@Component
public class ImportCommonHelper {


    @Autowired
    @Qualifier("userServiceAPI")
    private UserService userServiceAPI;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private OrgDOMapper orgDOMapper;

    /**
     * 查所有用户id
     * @return
     */
    public Map<String, Long> queryUserIdMap() {
        UserQuery userQuery = new UserQuery();
        userQuery.setPageSize(Integer.MAX_VALUE);
        return userServiceAPI.queryUserSys(userQuery).getContents().stream().collect(Collectors.toMap(UserVO::getUserName, UserVO::getId));
    }

    /**
     * 查用户部门
     * @param userIdList
     * @return
     */
    public Map<Long, Long> queryUserOrgMap(Collection<Long> userIdList) {
        return userIdList.stream().collect(Collectors.toMap(id -> id, id -> sysUserService.getSpecificUserDeptList(id).get(0).getId()));
    }

    /**
     * 查所有部门
     * @return
     */
    public Map<String, Long> queryOrgMap() {
        return orgDOMapper.selectAll().stream().collect(Collectors.toMap(OrgDO::getName, OrgDO::getId));
    }

    public static BigDecimal extractNumber(Object data) {
        return extractNumber(data, null, null);
    }

    /**
     * 数字提取
     * @return
     */
    public static BigDecimal extractNumber(Object data, Function<String, String> stringHandler, Function<Double, Double> doubleHandler) {
        if (Objects.isNull(data) || "null".equals(data) || " - ".equals(data)) {
            return null;
        }
        if (data instanceof String) {
            if (StringUtils.isBlank((String)data)) {
                return null;
            }
            if (Objects.isNull(stringHandler)) {
                return new BigDecimal(((String)data)).setScale(2, RoundingMode.HALF_UP);
            } else {
                return new BigDecimal((stringHandler.apply((String)data))).setScale(2, RoundingMode.HALF_UP);
            }
        } else if (data instanceof Double) {
            if (Objects.isNull(doubleHandler)) {
                return new BigDecimal(((Double)data)).setScale(2, RoundingMode.HALF_UP);
            } else {
                return new BigDecimal((doubleHandler.apply((Double) data))).setScale(2, RoundingMode.HALF_UP);
            }
        } else if (data instanceof Float) {
            return new BigDecimal((Float)data).setScale(2, RoundingMode.HALF_UP);
        } else if (data instanceof Integer) {
            return new BigDecimal((Integer)data).setScale(2, RoundingMode.HALF_UP);
        } else if (data instanceof Long) {
            return new BigDecimal((Long)data).setScale(2, RoundingMode.HALF_UP);
        }
        return null;
    }

    public static Long extractAmount(String data) {
        return Optional.ofNullable(data).map(BigDecimal::new).map(b -> b.multiply(new BigDecimal(10000)).longValue()).orElse(0L);
    }

    public static LocalDate extractDate(String data) {
        return Optional.ofNullable(data).map(d -> LocalDateTimeUtil.parse(d, "yyyy-MM-dd HH:mm:ss")).map(LocalDateTime::toLocalDate).orElse(null);
    }

}
