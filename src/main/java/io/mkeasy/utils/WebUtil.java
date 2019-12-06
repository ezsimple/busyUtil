package io.mkeasy.utils;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class WebUtil {

	/* Resource 는 static을 사용할 수 없습니다. */
	@Resource 
	AbstractApplicationContext ctx;

	private String WEB_ROOT = null;
	
	public String getWebRoot() throws IOException {

		if (WEB_ROOT!=null)
			return this.WEB_ROOT;

		org.springframework.core.io.Resource resource = ctx.getResource("/");
		File f = resource.getFile();
		WEB_ROOT = f.getPath();

		return WEB_ROOT;
	}
	
}
