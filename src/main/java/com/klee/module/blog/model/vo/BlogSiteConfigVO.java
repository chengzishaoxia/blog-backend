package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "博客站点配置")
public class BlogSiteConfigVO {

    @Schema(description = "首页页首配置")
    private HeroSection homeHero = new HeroSection();

    @Schema(description = "标签页首配置")
    private HeroSection tagsHero = new HeroSection();

    @Schema(description = "归档页首配置")
    private HeroSection archivesHero = new HeroSection();

    @Schema(description = "友链页首配置")
    private HeroSection friendLinksHero = new HeroSection();

    @Schema(description = "聊天室页首配置")
    private HeroSection chatHero = new HeroSection();

    @Schema(description = "留言板配置")
    private MessageBoardSection messageBoard = new MessageBoardSection();

    @Schema(description = "侧栏关于配置")
    private AboutSection about = new AboutSection();

    @Schema(description = "站点作者卡配置")
    private SidebarProfileSection sidebarProfile = new SidebarProfileSection();

    @Schema(description = "侧栏公告配置")
    private SidebarNoticeSection sidebarNotice = new SidebarNoticeSection();

    @Schema(description = "侧栏联系方式配置")
    private ContactSection contact = new ContactSection();

    @Schema(description = "页脚配置")
    private FooterSection footer = new FooterSection();

    @Data
    @Schema(description = "单个页面页首配置")
    public static class HeroSection {
        @Schema(description = "眉标题")
        private String eyebrow;

        @Schema(description = "主标题")
        private String title;

        @Schema(description = "副标题")
        private String subtitle;

        @Schema(description = "背景图片文件ID")
        private Long imageFileId;

        @Schema(description = "背景图片地址")
        private String imageUrl;
    }

    @Data
    @Schema(description = "留言板配置")
    public static class MessageBoardSection {
        @Schema(description = "背景图片文件ID")
        private Long backgroundFileId;

        @Schema(description = "背景图片地址")
        private String backgroundUrl;
    }

    @Data
    @Schema(description = "关于卡片配置")
    public static class AboutSection {
        @Schema(description = "模块标题")
        private String title;

        @Schema(description = "仓库标签文案")
        private String repoLabel;

        @Schema(description = "仓库名称")
        private String repoName;

        @Schema(description = "仓库链接")
        private String repoUrl;
    }

    @Data
    @Schema(description = "站点作者卡配置")
    public static class SidebarProfileSection {
        @Schema(description = "头像文件ID")
        private Long avatarFileId;

        @Schema(description = "头像地址")
        private String avatarUrl;

        @Schema(description = "展示名称")
        private String name;

        @Schema(description = "简介")
        private String intro;
    }

    @Data
    @Schema(description = "侧栏公告配置")
    public static class SidebarNoticeSection {
        @Schema(description = "标题")
        private String title;

        @Schema(description = "内容")
        private String content;
    }

    @Data
    @Schema(description = "侧栏联系方式配置")
    public static class ContactSection {
        @Schema(description = "QQ")
        private String qq;

        @Schema(description = "GitHub")
        private String github;

        @Schema(description = "Gitee")
        private String gitee;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "微信")
        private String wechat;

        @Schema(description = "CSDN")
        private String csdn;

        @Schema(description = "QQ群")
        private String qqGroup;
    }

    @Data
    @Schema(description = "页脚配置")
    public static class FooterSection {
        @Schema(description = "版权文案")
        private String copyrightText;

        @Schema(description = "主文案")
        private String primaryText;

        @Schema(description = "建站时间")
        private String siteStartTime;
    }
}