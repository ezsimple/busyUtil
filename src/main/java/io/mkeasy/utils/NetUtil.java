package io.mkeasy.utils;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetUtil {

	public static String getMAC() throws UnknownHostException, SocketException {
		InetAddress ip = InetAddress.getLocalHost();
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : "").toUpperCase());
		}
		String macAddress = sb.toString();
		log.debug("IP : {}, MAC : {}",ip.getHostAddress(), macAddress);
		return macAddress;
	}

	// http://localhost:8100 => return "localhost"
	public static String getDomain() {
		HttpServletRequest request = getHttpServletRequest();
		String host = request.getServerName();
		return host;
	}

	public static String getHost(ServletRequest request) {
		String host = request.getServerName();
		int port = request.getServerPort();
		String scheme = request.getScheme();
		ServletContext ctx = request.getServletContext();
		String _ctx = ctx.getContextPath();
		String hostname = scheme+"://"+host+":"+port+"/"+_ctx;
		return hostname;
	}

	public static String getHost() throws Exception {
		final HttpServletRequest request = getHttpServletRequest();
		if(request!=null) {
			final String host = request.getServerName();
			final int port = request.getServerPort();
			final String scheme = request.getScheme();
			final String ctx = request.getContextPath();
			final String hostname = scheme+"://"+host+":"+port+"/"+ctx;
			return hostname;
		}

		final String hostname = getTomcatServerInfo();
		return hostname;
	}

	public static String getPagename() {
		HttpServletRequest req = getHttpServletRequest();
		String uri = req.getRequestURI();
		log.debug("uri : {}", uri);
		if(StringUtils.isEmpty(uri)) {
			log.warn("uri is empty");
			return "";
		}
		String pageName = String.valueOf(uri.subSequence(uri.lastIndexOf("/")+1, uri.lastIndexOf('.')));
		if(StringUtils.isEmpty(pageName)) {
			log.warn("pageName is empty");
			return "";
		}
		return pageName;

	}

	public static String getClientIP() {
		HttpServletRequest req = getHttpServletRequest();
		String ip = req.getHeader("X-FORWARDED-FOR");
		log.info("TEST : X-FORWARDED-FOR : "+ip);
		if (ip == null) {
			ip = req.getHeader("Proxy-Client-IP");
			log.info("TEST : Proxy-Client-IP : "+ip);
		}
		if (ip == null) {
			ip = req.getHeader("WL-Proxy-Client-IP");
			log.info("TEST : WL-Proxy-Client-IP : "+ip);
		}
		if (ip == null) {
			ip = req.getHeader("HTTP_CLIENT_IP");
			log.info("TEST : HTTP_CLIENT_IP : "+ip);
		}
		if (ip == null) {
			ip = req.getHeader("HTTP_X_FORWARDED_FOR");
			log.info("TEST : HTTP_X_FORWARDED_FOR : "+ip);
		}
		if (ip == null) {
			ip = req.getRemoteAddr();
			log.info("TEST : getRemoteAddr : "+ip);
		}
		return ip;
	}

	public static HttpServletRequest getHttpServletRequest() {
		RequestAttributes request = RequestContextHolder.getRequestAttributes();
		if(request==null) return null;
		return ((ServletRequestAttributes)request).getRequest();
	}

	// Thread Safe 
	public static JSONObject get(String url) throws Exception {
		String response = HttpClientUtil.get(url);
		return new JSONObject(response); // caution : dealloc memory
	}

	// Thread Safe 
	public static JSONObject post(final String url, JSONObject params) throws Exception {
		String response = HttpClientUtil.post(url, params);
		// cmd_type=S의 경우 response가 별도 없습니다.
		JSONObject ret = null;
		if(StringUtils.isEmpty(response))
			return null;

		try {
			ret = new JSONObject(response);
		} catch (Exception e) {
			log.debug("url : {}", url);
			log.debug("req : {}", params);
			log.error("res : {}", response);
			throw e;
		}
		return ret; // caution : dealloc memory
	}



	public static String getTomcatServerInfo() throws Exception {

		MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectName> objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
				Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
		String host = InetAddress.getLocalHost().getHostAddress();
		String port = objectNames.iterator().next().getKeyProperty("port");

		// context 정보는 가져오지 못했습니다.
		return ("http://"+host+":"+port);

	}

	@Cacheable(value="IotCache", keyGenerator="keyGenerator")
	public static String getHostname() {

		InetAddress ip;
		String hostname = "no_name_host";
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
		} catch (Exception e) { }

		return hostname;
	}

	@Test
	public void Test() throws UnknownHostException, SocketException {
		getMAC();
	}

}