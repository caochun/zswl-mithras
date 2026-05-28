package cn.zswltech.mithras.api.dto.onlyoffice;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * onlyoffice文档配置
 * https://api.onlyoffice.com/editors/config/
 *
 * @author wangchuanhao
 * @date 2022/7/8 10:25 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("获取文档配置-返回体（就是onlyoffice配置）")
public class DocDetailRSP {

    @ApiModelProperty("文档类型")
    private String documentType;

    @ApiModelProperty("界面类型（desktop、embedded、mobile）")
    private String type;

//    @ApiModelProperty("高度（100%）")
//    private String height;
//
//    @ApiModelProperty("宽度（100%）")
//    private String width;

    @ApiModelProperty("文档配置")
    private Document document;

    @ApiModelProperty("编辑器配置")
    private EditorConfig editorConfig;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-文档配置")
    public static class Document {

        @ApiModelProperty("文件类型")
        private String fileType;

        @ApiModelProperty("文件key")
        private String key;

        @ApiModelProperty("文件标题")
        private String title;

        @ApiModelProperty("文件下载地址")
        private String url;

        @ApiModelProperty("权限控制")
        private Permissions permissions;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-权限")
    public static class Permissions {

        @ApiModelProperty("聊天权限")
        private Boolean chat;

        @ApiModelProperty("评论权限")
        private Boolean comment;

        @ApiModelProperty("复制权限")
        private Boolean copy;

        @ApiModelProperty("下载权限")
        private Boolean download;

        @ApiModelProperty("编辑权限")
        private Boolean edit;

        @ApiModelProperty("打印权限")
        private Boolean print;

        @ApiModelProperty("修订模式")
        private Boolean review;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-编辑配置")
    public static class EditorConfig {

        @ApiModelProperty("编辑回调地址")
        private String callbackUrl;

        @ApiModelProperty("语言（zh-CN）")
        private String lang;

        @ApiModelProperty("区域（zh-CN）")
        private String region;

        @ApiModelProperty("当前登陆用户")
        private User user;

        @ApiModelProperty("界面定制化")
        private Customization customization;

        @ApiModelProperty("模式（view、edit）")
        private String mode;

        @ApiModelProperty("协作-共同编辑")
        private CoEditing coEditing;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-协作-共同编辑")
    public static class CoEditing {

        /**
         * 快速 改了别人就能看到 并会自动保存
         * 严格 需要保存别人才能看到
         */
        @ApiModelProperty("协作模式（fast、strict）")
        private String mode;

        @ApiModelProperty("是否允许在界面上自己修改")
        private Boolean change;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-用户")
    public static class User {

        @ApiModelProperty("当前登陆用户id")
        private String id;

        @ApiModelProperty("当前登陆用户名称")
        private String name;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-界面定制化")
    public static class Customization {

        @ApiModelProperty("自动保存按钮是否显示")
        private Boolean autosave;

        @ApiModelProperty("评论栏是否显示")
        private Boolean comments;
//
//        @ApiModelProperty("顶部工具栏是否与logo同级")
//        private Boolean compactToolbar;

//        @ApiModelProperty("公司信息")
//        private Customer customer;

        @ApiModelProperty("是否显示帮助按钮")
        private Boolean help;

//        @ApiModelProperty("首次加载时隐藏右边菜单")
//        private Boolean hideRightMenu;

//        @ApiModelProperty("是否运行宏")
//        private Boolean macros;
//
//        @ApiModelProperty("运行宏的模式")
//        private String macrosMode;
//
//        @ApiModelProperty("界面样式模版")
//        private String uiTheme;

        @ApiModelProperty("修订相关参数")
        private Review review;

        @ApiModelProperty("插件")
        private Boolean plugins;

        @ApiModelProperty("左上角logo")
        private Logo logo;

        @ApiModelProperty("强制保存 用户点击界面上的保存后触发回调程序")
        private Boolean forcesave;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-公司信息")
    public static class Customer {

        @ApiModelProperty("公司地址")
        private String address;

        @ApiModelProperty("详情")
        private String info;

        @ApiModelProperty("logo图片（432x70）")
        private String logo;

        @ApiModelProperty("暗色调logo图片（432x70）")
        private String logoDark;

        @ApiModelProperty("公司邮箱")
        private String mail;

        @ApiModelProperty("公司名称")
        private String name;

        @ApiModelProperty("公司网址")
        private String www;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-左上角logo")
    public static class Logo {

        @ApiModelProperty("logo url")
        private String image;

        @ApiModelProperty("暗色模式下的logo url")
        private String imageDark;

        @ApiModelProperty("logo点击后跳转的网址")
        private String url;

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ApiModel("onlyoffice-修订")
    public static class Review {

        private Boolean showReviewChanges = true;

        private String reviewDisplay = "markup";

        private Boolean hoverMode = true;

        private Boolean trackChanges = true;

    }

}
