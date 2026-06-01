package cn.zswltech.mithras.blackgray.service.external;

import cn.hutool.core.map.MapUtil;
import cn.zswltech.mithras.blackgray.service.external.remote.JKBlackGrayCollisionLibraryREQ;
import cn.zswltech.mithras.blackgray.service.external.remote.JKBlackGrayCollisionLibraryRSP;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName PaymentHandle
 * @Description 苍穹应付单
 * @Author jackerhe
 * @Date 2022/10/26 2:43 下午
 * @Version 1.0
 **/
@Component
public class JKBlackGrayCollisionLibraryHandle extends JKBlackGrayApiHandler<JKBlackGrayCollisionLibraryREQ, JKBlackGrayCollisionLibraryRSP> {

    @Override
    public PlatformApiEnum platformApi() {
        return PlatformApiEnum.JK_BLACK_GRAY_COLLISION_LIBRARY;
    }

    @Override
    public String getUrl() {
        return "/gungnirApi/public/collision/library";
    }

    @Override
    public JKBlackGrayCollisionLibraryRSP analyResponseResult(String response) {
        return JSONObject.parseObject(response, JKBlackGrayCollisionLibraryRSP.class);
    }

    @Override
    public JKBlackGrayCollisionLibraryRSP execute(JKBlackGrayCollisionLibraryREQ reqData) {
        return super.execute(reqData);
    }

    @Override
    public Map<String, String> getHttpHeadParam() {
        return MapUtil.empty();
    }

    @Override
    public boolean isFailureSave() {
        return false;
    }


}
