package cn.zswltech.mithras.dashboard.application.boss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dashboard.config.GuanYuanConfigProperties;
import cn.zswltech.mithras.dashboard.domain.enums.BossDashboardGuanYuanDataSourceKeyEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dashboard.application.GuanYuanDSInfoService;
import cn.zswltech.mithras.dashboard.application.guanyuandata.GuanYuanColumnPopulate;
import cn.zswltech.sleipnir.toolkit.GuanYuanUtil;
import cn.zswltech.sleipnir.toolkit.request.guanyuan.GuanYuanDSRequest;
import cn.zswltech.sleipnir.toolkit.response.guanyuan.GuanYuanDSResponse;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/5/16/15:17
 * @description 观远数据处理器
 */
@Slf4j
public abstract class GuanYuanBasicService {
    @Resource
    private GuanYuanConfigProperties properties;
    @Resource
    protected GuanYuanDSInfoService guanYuanDsInfoService;

    /**
     * 查询数据
     */
    protected final List<Map<String, String>> getData(String dsId, GuanYuanDSRequest.Body body) {
        GuanYuanDSRequest request = new GuanYuanDSRequest();
        request.setDsId(dsId);
        request.setBody(body);
        request.setDomain(properties.getDomain());
        request.setHost(properties.getHost());
        request.setToken(properties.getToken());
        request.setLoginId(properties.getLoginId());
        request.setPassword(Base64.getEncoder().encodeToString(properties.getPassword().getBytes(StandardCharsets.UTF_8)));
        log.info("观远API请求数据集数据-开始[{}]", JSONUtil.toJsonStr(request));
        try {
            GuanYuanDSResponse response = GuanYuanUtil.getData(request);
            log.info("观远API请求数据集数据-结束[{}]", JSONUtil.toJsonStr(response));
            List<String> columns = response.getResponse().getColumns();
            List<List<String>> data = response.getResponse().getData();
            List<Map<String, String>> result = new ArrayList<>(data.size());
            data.forEach(row -> {
                Map<String, String> map = new HashMap<>(row.size());
                for (int i = 0; i < row.size(); i++) {
                    map.put(columns.get(i), row.get(i));
                }
                result.add(map);
            });
            return result;
        } catch (Exception e) {
            log.error("调用观远API请求数据集数据发生异常", e);
            throw new MithrasException("获取数据失败");
        }
    }

    protected <T extends GuanYuanColumnPopulate> List<T> queryFromGuanYuan(BossDashboardGuanYuanDataSourceKeyEnum guanYuanDataSourceKey, GuanYuanDSRequest.Body requestBody, Class<T> clz) {
        String dsId = guanYuanDsInfoService.getGuanYuanDsId(guanYuanDataSourceKey);
        if (StrUtil.isBlank(dsId)) {
            throw new MithrasException("数据集配置不存在[" + guanYuanDataSourceKey.getDisplay() + "]");
        }
        List<Map<String, String>> sourceList = getData(dsId, requestBody);
        if (CollUtil.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return sourceList.stream().map(item -> {
            T t;
            try {
                t = clz.newInstance();
            } catch (Exception e) {
                log.error("生成对象实例发生异常[{}]", clz.getName(), e);
                throw new MithrasException("内部处理异常");
            }
            t.populate(item);
            return t;
        }).collect(Collectors.toList());
    }
}
