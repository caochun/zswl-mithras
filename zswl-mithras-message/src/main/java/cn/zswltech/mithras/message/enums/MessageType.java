package cn.zswltech.mithras.message.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
	MESSAGE("message"),//系统存储
	NOTICE("notice"),  //通知
	POPUP("pup-up"),//弹窗
	TODO("todo"); //代办


	private final String type;

	private MessageType(String type) {
		this.type = type;
	}

	@JsonValue
	public String getType() {
		return type;
	}
}