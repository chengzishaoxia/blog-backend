package com.klee.module.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.klee.common.enums.DeleteStatusEnum;
import com.klee.core.security.util.SecurityUtils;
import com.klee.module.blog.model.form.BlogSiteConfigForm;
import com.klee.module.blog.model.vo.BlogSiteConfigVO;
import com.klee.module.blog.service.BlogSiteConfigService;
import com.klee.shared.file.mapper.FileInfoMapper;
import com.klee.shared.file.model.FileInfo;
import com.klee.system.model.entity.Config;
import com.klee.system.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogSiteConfigServiceImpl implements BlogSiteConfigService {

    private static final String HOME_HERO_EYEBROW = "blog.site.home.hero.eyebrow";
    private static final String HOME_HERO_TITLE = "blog.site.home.hero.title";
    private static final String HOME_HERO_SUBTITLE = "blog.site.home.hero.subtitle";

    private static final String TAGS_HERO_EYEBROW = "blog.site.tags.hero.eyebrow";
    private static final String TAGS_HERO_TITLE = "blog.site.tags.hero.title";
    private static final String TAGS_HERO_SUBTITLE = "blog.site.tags.hero.subtitle";
    private static final String TAGS_HERO_IMAGE_FILE_ID = "blog.site.tags.hero.imageFileId";

    private static final String ARCHIVES_HERO_EYEBROW = "blog.site.archives.hero.eyebrow";
    private static final String ARCHIVES_HERO_TITLE = "blog.site.archives.hero.title";
    private static final String ARCHIVES_HERO_SUBTITLE = "blog.site.archives.hero.subtitle";
    private static final String ARCHIVES_HERO_IMAGE_FILE_ID = "blog.site.archives.hero.imageFileId";

    private static final String FRIEND_LINKS_HERO_EYEBROW = "blog.site.friend-links.hero.eyebrow";
    private static final String FRIEND_LINKS_HERO_TITLE = "blog.site.friend-links.hero.title";
    private static final String FRIEND_LINKS_HERO_SUBTITLE = "blog.site.friend-links.hero.subtitle";
    private static final String FRIEND_LINKS_HERO_IMAGE_FILE_ID = "blog.site.friend-links.hero.imageFileId";

    private static final String CHAT_HERO_EYEBROW = "blog.site.chat.hero.eyebrow";
    private static final String CHAT_HERO_TITLE = "blog.site.chat.hero.title";
    private static final String CHAT_HERO_SUBTITLE = "blog.site.chat.hero.subtitle";
    private static final String CHAT_HERO_IMAGE_FILE_ID = "blog.site.chat.hero.imageFileId";

    private static final String MESSAGE_BOARD_BACKGROUND_FILE_ID = "blog.site.message-board.backgroundFileId";

    private static final String ABOUT_TITLE = "blog.site.about.title";
    private static final String ABOUT_REPO_LABEL = "blog.site.about.repoLabel";
    private static final String ABOUT_REPO_NAME = "blog.site.about.repoName";
    private static final String ABOUT_REPO_URL = "blog.site.about.repoUrl";

    private static final String SIDEBAR_PROFILE_AVATAR_FILE_ID = "blog.site.sidebar.profile.avatarFileId";
    private static final String SIDEBAR_PROFILE_NAME = "blog.site.sidebar.profile.name";
    private static final String SIDEBAR_PROFILE_INTRO = "blog.site.sidebar.profile.intro";

    private static final String SIDEBAR_NOTICE_TITLE = "blog.site.sidebar.notice.title";
    private static final String SIDEBAR_NOTICE_CONTENT = "blog.site.sidebar.notice.content";

    private static final String CONTACT_QQ = "blog.site.contact.qq";
    private static final String CONTACT_GITHUB = "blog.site.contact.github";
    private static final String CONTACT_GITEE = "blog.site.contact.gitee";
    private static final String CONTACT_EMAIL = "blog.site.contact.email";
    private static final String CONTACT_WECHAT = "blog.site.contact.wechat";
    private static final String CONTACT_CSDN = "blog.site.contact.csdn";
    private static final String CONTACT_QQ_GROUP = "blog.site.contact.qqGroup";

    private static final String LEGACY_FOOTER_RECORD_TEXT = "川公网安备51118102000279号 蜀ICP备2024067985号-1";
    private static final String DEFAULT_FOOTER_PRIMARY_TEXT = "本站内容仅作学习交流与个人记录使用";

    private static final String FOOTER_PRIMARY_TEXT = "blog.site.footer.primaryText";
    private static final String FOOTER_COPYRIGHT_TEXT = "blog.site.footer.copyrightText";
    private static final String FOOTER_SITE_START_TIME = "blog.site.footer.siteStartTime";

    private static final Map<String, ConfigMeta> CONFIG_META_MAP = buildConfigMetaMap();

    private final ConfigService configService;
    private final FileInfoMapper fileInfoMapper;

    @Override
    public BlogSiteConfigVO getSiteConfig() {
        BlogSiteConfigVO config = createDefaultConfig();

        BlogSiteConfigVO.HeroSection homeHero = config.getHomeHero();
        homeHero.setEyebrow(getStringConfig(HOME_HERO_EYEBROW, homeHero.getEyebrow()));
        homeHero.setTitle(getStringConfig(HOME_HERO_TITLE, homeHero.getTitle()));
        homeHero.setSubtitle(getStringConfig(HOME_HERO_SUBTITLE, homeHero.getSubtitle()));

        BlogSiteConfigVO.HeroSection tagsHero = config.getTagsHero();
        tagsHero.setEyebrow(getStringConfig(TAGS_HERO_EYEBROW, tagsHero.getEyebrow()));
        tagsHero.setTitle(getStringConfig(TAGS_HERO_TITLE, tagsHero.getTitle()));
        tagsHero.setSubtitle(getStringConfig(TAGS_HERO_SUBTITLE, tagsHero.getSubtitle()));
        tagsHero.setImageFileId(getLongConfig(TAGS_HERO_IMAGE_FILE_ID));

        BlogSiteConfigVO.HeroSection archivesHero = config.getArchivesHero();
        archivesHero.setEyebrow(getStringConfig(ARCHIVES_HERO_EYEBROW, archivesHero.getEyebrow()));
        archivesHero.setTitle(getStringConfig(ARCHIVES_HERO_TITLE, archivesHero.getTitle()));
        archivesHero.setSubtitle(getStringConfig(ARCHIVES_HERO_SUBTITLE, archivesHero.getSubtitle()));
        archivesHero.setImageFileId(getLongConfig(ARCHIVES_HERO_IMAGE_FILE_ID));

        BlogSiteConfigVO.HeroSection friendLinksHero = config.getFriendLinksHero();
        friendLinksHero.setEyebrow(getStringConfig(FRIEND_LINKS_HERO_EYEBROW, friendLinksHero.getEyebrow()));
        friendLinksHero.setTitle(getStringConfig(FRIEND_LINKS_HERO_TITLE, friendLinksHero.getTitle()));
        friendLinksHero.setSubtitle(getStringConfig(FRIEND_LINKS_HERO_SUBTITLE, friendLinksHero.getSubtitle()));
        friendLinksHero.setImageFileId(getLongConfig(FRIEND_LINKS_HERO_IMAGE_FILE_ID));

        BlogSiteConfigVO.HeroSection chatHero = config.getChatHero();
        chatHero.setEyebrow(getStringConfig(CHAT_HERO_EYEBROW, chatHero.getEyebrow()));
        chatHero.setTitle(getStringConfig(CHAT_HERO_TITLE, chatHero.getTitle()));
        chatHero.setSubtitle(getStringConfig(CHAT_HERO_SUBTITLE, chatHero.getSubtitle()));
        chatHero.setImageFileId(getLongConfig(CHAT_HERO_IMAGE_FILE_ID));

        BlogSiteConfigVO.MessageBoardSection messageBoard = config.getMessageBoard();
        messageBoard.setBackgroundFileId(getLongConfig(MESSAGE_BOARD_BACKGROUND_FILE_ID));

        BlogSiteConfigVO.AboutSection about = config.getAbout();
        about.setTitle(getStringConfig(ABOUT_TITLE, about.getTitle()));
        about.setRepoLabel(getStringConfig(ABOUT_REPO_LABEL, about.getRepoLabel()));
        about.setRepoName(getStringConfig(ABOUT_REPO_NAME, about.getRepoName()));
        about.setRepoUrl(getStringConfig(ABOUT_REPO_URL, about.getRepoUrl()));

        BlogSiteConfigVO.SidebarProfileSection sidebarProfile = config.getSidebarProfile();
        sidebarProfile.setAvatarFileId(getLongConfig(SIDEBAR_PROFILE_AVATAR_FILE_ID));
        sidebarProfile.setName(getStringConfig(SIDEBAR_PROFILE_NAME, sidebarProfile.getName()));
        sidebarProfile.setIntro(getStringConfig(SIDEBAR_PROFILE_INTRO, sidebarProfile.getIntro()));

        BlogSiteConfigVO.SidebarNoticeSection sidebarNotice = config.getSidebarNotice();
        sidebarNotice.setTitle(getStringConfig(SIDEBAR_NOTICE_TITLE, sidebarNotice.getTitle()));
        sidebarNotice.setContent(getStringConfig(SIDEBAR_NOTICE_CONTENT, sidebarNotice.getContent()));

        BlogSiteConfigVO.ContactSection contact = config.getContact();
        contact.setQq(getStringConfig(CONTACT_QQ, contact.getQq()));
        contact.setGithub(getStringConfig(CONTACT_GITHUB, contact.getGithub()));
        contact.setGitee(getStringConfig(CONTACT_GITEE, contact.getGitee()));
        contact.setEmail(getStringConfig(CONTACT_EMAIL, contact.getEmail()));
        contact.setWechat(getStringConfig(CONTACT_WECHAT, contact.getWechat()));
        contact.setCsdn(getStringConfig(CONTACT_CSDN, contact.getCsdn()));
        contact.setQqGroup(getStringConfig(CONTACT_QQ_GROUP, contact.getQqGroup()));

        BlogSiteConfigVO.FooterSection footer = config.getFooter();
        footer.setCopyrightText(getStringConfig(FOOTER_COPYRIGHT_TEXT, footer.getCopyrightText()));
        footer.setPrimaryText(normalizeFooterPrimaryText(getStringConfig(FOOTER_PRIMARY_TEXT, footer.getPrimaryText())));
        footer.setSiteStartTime(getStringConfig(FOOTER_SITE_START_TIME, footer.getSiteStartTime()));

        enrichHeroImageUrls(config);
        return config;
    }

    @Override
    public boolean updateSiteConfig(BlogSiteConfigForm form) {
        BlogSiteConfigForm normalizedForm = form == null ? new BlogSiteConfigForm() : form;
        Map<String, String> valueMap = new LinkedHashMap<>();

        putHeroValues(valueMap, normalizedForm.getHomeHero(), HOME_HERO_EYEBROW, HOME_HERO_TITLE, HOME_HERO_SUBTITLE, null);
        putHeroValues(valueMap, normalizedForm.getTagsHero(), TAGS_HERO_EYEBROW, TAGS_HERO_TITLE, TAGS_HERO_SUBTITLE, TAGS_HERO_IMAGE_FILE_ID);
        putHeroValues(valueMap, normalizedForm.getArchivesHero(), ARCHIVES_HERO_EYEBROW, ARCHIVES_HERO_TITLE, ARCHIVES_HERO_SUBTITLE, ARCHIVES_HERO_IMAGE_FILE_ID);
        putHeroValues(valueMap, normalizedForm.getFriendLinksHero(), FRIEND_LINKS_HERO_EYEBROW, FRIEND_LINKS_HERO_TITLE, FRIEND_LINKS_HERO_SUBTITLE, FRIEND_LINKS_HERO_IMAGE_FILE_ID);
        putHeroValues(valueMap, normalizedForm.getChatHero(), CHAT_HERO_EYEBROW, CHAT_HERO_TITLE, CHAT_HERO_SUBTITLE, CHAT_HERO_IMAGE_FILE_ID);

        BlogSiteConfigForm.MessageBoardSection messageBoard = normalizedForm.getMessageBoard() == null ? new BlogSiteConfigForm.MessageBoardSection() : normalizedForm.getMessageBoard();
        valueMap.put(MESSAGE_BOARD_BACKGROUND_FILE_ID, messageBoard.getBackgroundFileId() == null ? "" : String.valueOf(messageBoard.getBackgroundFileId()));

        BlogSiteConfigForm.AboutSection about = normalizedForm.getAbout() == null ? new BlogSiteConfigForm.AboutSection() : normalizedForm.getAbout();
        valueMap.put(ABOUT_TITLE, defaultString(about.getTitle()));
        valueMap.put(ABOUT_REPO_LABEL, defaultString(about.getRepoLabel()));
        valueMap.put(ABOUT_REPO_NAME, defaultString(about.getRepoName()));
        valueMap.put(ABOUT_REPO_URL, defaultString(about.getRepoUrl()));

        BlogSiteConfigForm.SidebarProfileSection sidebarProfile = normalizedForm.getSidebarProfile() == null ? new BlogSiteConfigForm.SidebarProfileSection() : normalizedForm.getSidebarProfile();
        valueMap.put(SIDEBAR_PROFILE_AVATAR_FILE_ID, sidebarProfile.getAvatarFileId() == null ? "" : String.valueOf(sidebarProfile.getAvatarFileId()));
        valueMap.put(SIDEBAR_PROFILE_NAME, defaultString(sidebarProfile.getName()));
        valueMap.put(SIDEBAR_PROFILE_INTRO, defaultString(sidebarProfile.getIntro()));

        BlogSiteConfigForm.SidebarNoticeSection sidebarNotice = normalizedForm.getSidebarNotice() == null ? new BlogSiteConfigForm.SidebarNoticeSection() : normalizedForm.getSidebarNotice();
        valueMap.put(SIDEBAR_NOTICE_TITLE, defaultString(sidebarNotice.getTitle()));
        valueMap.put(SIDEBAR_NOTICE_CONTENT, defaultString(sidebarNotice.getContent()));

        BlogSiteConfigForm.ContactSection contact = normalizedForm.getContact() == null ? new BlogSiteConfigForm.ContactSection() : normalizedForm.getContact();
        valueMap.put(CONTACT_QQ, defaultString(contact.getQq()));
        valueMap.put(CONTACT_GITHUB, defaultString(contact.getGithub()));
        valueMap.put(CONTACT_GITEE, defaultString(contact.getGitee()));
        valueMap.put(CONTACT_EMAIL, defaultString(contact.getEmail()));
        valueMap.put(CONTACT_WECHAT, defaultString(contact.getWechat()));
        valueMap.put(CONTACT_CSDN, defaultString(contact.getCsdn()));
        valueMap.put(CONTACT_QQ_GROUP, defaultString(contact.getQqGroup()));

        BlogSiteConfigForm.FooterSection footer = normalizedForm.getFooter() == null ? new BlogSiteConfigForm.FooterSection() : normalizedForm.getFooter();
        valueMap.put(FOOTER_COPYRIGHT_TEXT, defaultString(footer.getCopyrightText()));
        valueMap.put(FOOTER_PRIMARY_TEXT, defaultString(footer.getPrimaryText()));
        valueMap.put(FOOTER_SITE_START_TIME, defaultString(footer.getSiteStartTime()));

        List<Config> existingList = configService.list(new LambdaQueryWrapper<Config>()
                .eq(Config::getIsDeleted, DeleteStatusEnum.NOT_DELETED.getValue())
                .in(Config::getConfigKey, valueMap.keySet()));
        Map<String, Config> existingMap = existingList.stream().collect(Collectors.toMap(Config::getConfigKey, item -> item));

        Long userId = SecurityUtils.getUserId();
        for (Map.Entry<String, String> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            Config existing = existingMap.get(key);
            if (existing == null) {
                Config config = new Config();
                config.setConfigKey(key);
                config.setConfigValue(value);
                ConfigMeta meta = CONFIG_META_MAP.get(key);
                config.setConfigName(meta == null ? key : meta.getName());
                config.setRemark(meta == null ? "博客站点配置" : meta.getRemark());
                config.setCreateBy(userId);
                config.setUpdateBy(userId);
                config.setIsDeleted(DeleteStatusEnum.NOT_DELETED.getValue());
                configService.save(config);
            } else {
                existing.setConfigValue(value);
                existing.setUpdateBy(userId);
                configService.updateById(existing);
            }
        }
        return configService.refreshCache();
    }

    private static Map<String, ConfigMeta> buildConfigMetaMap() {
        Map<String, ConfigMeta> map = new LinkedHashMap<>();
        map.put(HOME_HERO_EYEBROW, new ConfigMeta("首页页首眉标题", "博客站点配置-首页页首"));
        map.put(HOME_HERO_TITLE, new ConfigMeta("首页页首标题", "博客站点配置-首页页首"));
        map.put(HOME_HERO_SUBTITLE, new ConfigMeta("首页页首副标题", "博客站点配置-首页页首"));
        map.put(TAGS_HERO_EYEBROW, new ConfigMeta("标签页首眉标题", "博客站点配置-标签页首"));
        map.put(TAGS_HERO_TITLE, new ConfigMeta("标签页首标题", "博客站点配置-标签页首"));
        map.put(TAGS_HERO_SUBTITLE, new ConfigMeta("标签页首副标题", "博客站点配置-标签页首"));
        map.put(TAGS_HERO_IMAGE_FILE_ID, new ConfigMeta("标签页首图片", "博客站点配置-标签页首图片文件ID"));

        map.put(ARCHIVES_HERO_EYEBROW, new ConfigMeta("归档页首眉标题", "博客站点配置-归档页首"));
        map.put(ARCHIVES_HERO_TITLE, new ConfigMeta("归档页首标题", "博客站点配置-归档页首"));
        map.put(ARCHIVES_HERO_SUBTITLE, new ConfigMeta("归档页首副标题", "博客站点配置-归档页首"));
        map.put(ARCHIVES_HERO_IMAGE_FILE_ID, new ConfigMeta("归档页首图片", "博客站点配置-归档页首图片文件ID"));

        map.put(FRIEND_LINKS_HERO_EYEBROW, new ConfigMeta("友链页首眉标题", "博客站点配置-友链页首"));
        map.put(FRIEND_LINKS_HERO_TITLE, new ConfigMeta("友链页首标题", "博客站点配置-友链页首"));
        map.put(FRIEND_LINKS_HERO_SUBTITLE, new ConfigMeta("友链页首副标题", "博客站点配置-友链页首"));
        map.put(FRIEND_LINKS_HERO_IMAGE_FILE_ID, new ConfigMeta("友链页首图片", "博客站点配置-友链页首图片文件ID"));

        map.put(CHAT_HERO_EYEBROW, new ConfigMeta("聊天室页首眉标题", "博客站点配置-聊天室页首"));
        map.put(CHAT_HERO_TITLE, new ConfigMeta("聊天室页首标题", "博客站点配置-聊天室页首"));
        map.put(CHAT_HERO_SUBTITLE, new ConfigMeta("聊天室页首副标题", "博客站点配置-聊天室页首"));
        map.put(CHAT_HERO_IMAGE_FILE_ID, new ConfigMeta("聊天室页首图片", "博客站点配置-聊天室页首图片文件ID"));

        map.put(MESSAGE_BOARD_BACKGROUND_FILE_ID, new ConfigMeta("留言板背景图", "博客站点配置-留言板背景图文件ID"));

        map.put(ABOUT_TITLE, new ConfigMeta("关于模块标题", "博客站点配置-关于模块"));
        map.put(ABOUT_REPO_LABEL, new ConfigMeta("关于模块仓库标签", "博客站点配置-关于模块"));
        map.put(ABOUT_REPO_NAME, new ConfigMeta("关于模块仓库名称", "博客站点配置-关于模块"));
        map.put(ABOUT_REPO_URL, new ConfigMeta("关于模块仓库链接", "博客站点配置-关于模块"));
        map.put(SIDEBAR_PROFILE_AVATAR_FILE_ID, new ConfigMeta("站点作者头像", "博客站点配置-侧栏作者卡"));
        map.put(SIDEBAR_PROFILE_NAME, new ConfigMeta("站点作者名称", "博客站点配置-侧栏作者卡"));
        map.put(SIDEBAR_PROFILE_INTRO, new ConfigMeta("站点作者简介", "博客站点配置-侧栏作者卡"));
        map.put(SIDEBAR_NOTICE_TITLE, new ConfigMeta("侧栏公告标题", "博客站点配置-侧栏公告"));
        map.put(SIDEBAR_NOTICE_CONTENT, new ConfigMeta("侧栏公告内容", "博客站点配置-侧栏公告"));
        map.put(CONTACT_QQ, new ConfigMeta("侧栏联系QQ", "博客站点配置-侧栏联系信息"));
        map.put(CONTACT_GITHUB, new ConfigMeta("侧栏GitHub", "博客站点配置-侧栏联系信息"));
        map.put(CONTACT_GITEE, new ConfigMeta("侧栏Gitee", "博客站点配置-侧栏联系信息"));
        map.put(CONTACT_EMAIL, new ConfigMeta("侧栏邮箱", "博客站点配置-侧栏联系信息"));
        map.put(CONTACT_WECHAT, new ConfigMeta("侧栏微信", "博客站点配置-侧栏联系信息"));
        map.put(CONTACT_CSDN, new ConfigMeta("侧栏CSDN", "博客站点配置-侧栏联系信息"));
        map.put(CONTACT_QQ_GROUP, new ConfigMeta("侧栏QQ群", "博客站点配置-侧栏联系信息"));
        map.put(FOOTER_COPYRIGHT_TEXT, new ConfigMeta("页脚版权文案", "博客站点配置-页脚"));
        map.put(FOOTER_PRIMARY_TEXT, new ConfigMeta("页脚主文案", "博客站点配置-页脚"));
        map.put(FOOTER_SITE_START_TIME, new ConfigMeta("页脚建站时间", "博客站点配置-页脚"));
        return map;
    }

    private void putHeroValues(Map<String, String> target, BlogSiteConfigForm.HeroSection hero, String eyebrowKey, String titleKey, String subtitleKey, String imageKey) {
        BlogSiteConfigForm.HeroSection value = hero == null ? new BlogSiteConfigForm.HeroSection() : hero;
        target.put(eyebrowKey, defaultString(value.getEyebrow()));
        target.put(titleKey, defaultString(value.getTitle()));
        target.put(subtitleKey, defaultString(value.getSubtitle()));
        if (imageKey != null) {
            target.put(imageKey, value.getImageFileId() == null ? "" : String.valueOf(value.getImageFileId()));
        }
    }

    private String getStringConfig(String key, String defaultValue) {
        Object value = configService.getSystemConfig(key);
        String text = value == null ? null : String.valueOf(value);
        return StringUtils.isBlank(text) ? defaultValue : text;
    }

    private Long getLongConfig(String key) {
        Object value = configService.getSystemConfig(key);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        if (StringUtils.isBlank(text)) {
            return null;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void enrichHeroImageUrls(BlogSiteConfigVO config) {
        List<BlogSiteConfigVO.HeroSection> heroes = List.of(
                config.getHomeHero(),
                config.getTagsHero(),
                config.getArchivesHero(),
                config.getFriendLinksHero(),
                config.getChatHero()
        );
        Set<Long> fileIds = heroes.stream()
                .map(BlogSiteConfigVO.HeroSection::getImageFileId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (config.getMessageBoard() != null && config.getMessageBoard().getBackgroundFileId() != null) {
            fileIds.add(config.getMessageBoard().getBackgroundFileId());
        }
        if (config.getSidebarProfile() != null && config.getSidebarProfile().getAvatarFileId() != null) {
            fileIds.add(config.getSidebarProfile().getAvatarFileId());
        }
        if (fileIds.isEmpty()) {
            return;
        }
        List<FileInfo> files = fileInfoMapper.selectList(new LambdaQueryWrapper<FileInfo>().in(FileInfo::getId, fileIds));
        Map<Long, String> fileUrlMap = files.stream().collect(Collectors.toMap(FileInfo::getId, FileInfo::getUrl));
        heroes.forEach(hero -> hero.setImageUrl(fileUrlMap.get(hero.getImageFileId())));
        if (config.getMessageBoard() != null) {
            config.getMessageBoard().setBackgroundUrl(fileUrlMap.get(config.getMessageBoard().getBackgroundFileId()));
        }
        if (config.getSidebarProfile() != null) {
            config.getSidebarProfile().setAvatarUrl(fileUrlMap.get(config.getSidebarProfile().getAvatarFileId()));
        }
    }

    private BlogSiteConfigVO createDefaultConfig() {
        BlogSiteConfigVO config = new BlogSiteConfigVO();

        BlogSiteConfigVO.HeroSection homeHero = new BlogSiteConfigVO.HeroSection();
        homeHero.setEyebrow("HOME");
        homeHero.setTitle("KLEE BLOG");
        homeHero.setSubtitle("就算一次也好，我想在这颗星球上尽情奔跑。");
        config.setHomeHero(homeHero);

        BlogSiteConfigVO.HeroSection tagsHero = new BlogSiteConfigVO.HeroSection();
        tagsHero.setEyebrow("TAGS");
        tagsHero.setTitle("标签");
        tagsHero.setSubtitle("把站内主题拆成更易浏览的入口，保留一些轻量的标签墙氛围。");
        config.setTagsHero(tagsHero);

        BlogSiteConfigVO.HeroSection archivesHero = new BlogSiteConfigVO.HeroSection();
        archivesHero.setEyebrow("ARCHIVES");
        archivesHero.setTitle("归档");
        archivesHero.setSubtitle("把最近三年的文章按照年份和月份整理在一起，保留一点旧博客的仪式感。");
        config.setArchivesHero(archivesHero);

        BlogSiteConfigVO.HeroSection friendLinksHero = new BlogSiteConfigVO.HeroSection();
        friendLinksHero.setEyebrow("LINKS");
        friendLinksHero.setTitle("友链");
        friendLinksHero.setSubtitle("把常逛的站点独立整理成一页，作为博客外延阅读的入口。");
        config.setFriendLinksHero(friendLinksHero);

        BlogSiteConfigVO.HeroSection chatHero = new BlogSiteConfigVO.HeroSection();
        chatHero.setEyebrow("CHAT");
        chatHero.setTitle("聊天室");
        chatHero.setSubtitle("保留站点统一的波浪页首，下面进入实时聊天和在线状态。");
        config.setChatHero(chatHero);

        config.setMessageBoard(new BlogSiteConfigVO.MessageBoardSection());

        BlogSiteConfigVO.AboutSection about = new BlogSiteConfigVO.AboutSection();
        about.setTitle("关于");
        about.setRepoLabel("项目开源地址：");
        about.setRepoName("KLEE - BLOG");
        about.setRepoUrl("https://gitee.com/variable-wing-core/klee-blog");
        config.setAbout(about);

        BlogSiteConfigVO.SidebarProfileSection sidebarProfile = new BlogSiteConfigVO.SidebarProfileSection();
        sidebarProfile.setName("橙子的小屋");
        sidebarProfile.setIntro("欢迎来到橙子的小屋。");
        config.setSidebarProfile(sidebarProfile);

        BlogSiteConfigVO.SidebarNoticeSection sidebarNotice = new BlogSiteConfigVO.SidebarNoticeSection();
        sidebarNotice.setTitle("公告");
        sidebarNotice.setContent("暂无！！");
        config.setSidebarNotice(sidebarNotice);

        BlogSiteConfigVO.ContactSection contact = new BlogSiteConfigVO.ContactSection();
        contact.setGithub("https://github.com/variable-wing-core/klee-blog");
        contact.setGitee("https://gitee.com/variable-wing-core/klee-blog");
        contact.setEmail("admin@example.com");
        config.setContact(contact);

        BlogSiteConfigVO.FooterSection footer = new BlogSiteConfigVO.FooterSection();
        footer.setCopyrightText("© 橙子的小屋 2026");
        footer.setPrimaryText(DEFAULT_FOOTER_PRIMARY_TEXT);
        footer.setSiteStartTime("2026-01-01T00:00:00");
        config.setFooter(footer);
        return config;
    }

    private String normalizeFooterPrimaryText(String value) {
        if (StringUtils.isBlank(value) || StringUtils.equals(value, LEGACY_FOOTER_RECORD_TEXT)) {
            return DEFAULT_FOOTER_PRIMARY_TEXT;
        }
        return value;
    }

    private String defaultString(String value) {
        return value == null ? "" : value.trim();
    }

    private static class ConfigMeta {
        private final String name;
        private final String remark;

        private ConfigMeta(String name, String remark) {
            this.name = name;
            this.remark = remark;
        }

        public String getName() {
            return name;
        }

        public String getRemark() {
            return remark;
        }
    }
}