package com.klee.module.blog.service;

import com.klee.module.blog.model.form.BlogSiteConfigForm;
import com.klee.module.blog.model.vo.BlogSiteConfigVO;

public interface BlogSiteConfigService {

    BlogSiteConfigVO getSiteConfig();

    boolean updateSiteConfig(BlogSiteConfigForm form);
}