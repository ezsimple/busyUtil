package io.mkeasy.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class WebUtil implements ApplicationContextAware {

	private static ApplicationContext ctx;

	private static String WEB_ROOT = null;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
	}
	
	// WEB ROOT 의 절대경로를 가져온다.
	public static String getWebRoot() throws IOException {

		if (ctx==null) return null;

		Resource resource = ctx.getResource("/");

		if(resource==null || !resource.exists())
			return null;
		
		if (!StringUtils.isEmpty(WEB_ROOT))
			return WEB_ROOT;

		File f = resource.getFile();
		WEB_ROOT = f.getPath();

		return WEB_ROOT;
	}
	
}
