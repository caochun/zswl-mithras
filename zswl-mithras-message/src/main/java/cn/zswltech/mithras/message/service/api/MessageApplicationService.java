package cn.zswltech.mithras.message.service.api;

import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.mithras.api.MessageApi;
import cn.zswltech.mithras.dto.message.OAAuthREQ;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface MessageApplicationService extends MessageApi {

    Response<Map<String, Object>> authOA(OAAuthREQ req, HttpServletRequest request, HttpServletResponse response);
}
