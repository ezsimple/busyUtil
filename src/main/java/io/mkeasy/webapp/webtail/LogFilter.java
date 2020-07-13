package io.mkeasy.webapp.webtail;

import java.io.File;
import java.io.FilenameFilter;

public class LogFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.contains(".out");
	}

}
