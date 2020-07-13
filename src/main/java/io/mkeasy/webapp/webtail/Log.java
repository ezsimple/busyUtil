package io.mkeasy.webapp.webtail;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 작 성 자 : Lee MinHo 
 * 작 성 일 : 2013. 2. 5.
 * 수정이력 : 2013. 2. 5. Lee MinHo 최초작성
 * </pre>
 */
@Slf4j
public final class Log extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final JspFactory jspFactory = JspFactory.getDefaultFactory();
	private final String title = "로그파일 보기";
	private final String PASSWD = "system";
	private Map<String, String> LOG_LOCATIONS_FOR_COMMERCIAL;
	private Map<String, String> LOG_LOCATIONS_FOR_TESTBED;
	
	public Log() {
		LOG_LOCATIONS_FOR_COMMERCIAL = new HashMap<String, String>();
		LOG_LOCATIONS_FOR_COMMERCIAL.put("CSELLW1", "/data/wls11g/domains/cstore_domain/servers/cstore/logs");
		LOG_LOCATIONS_FOR_COMMERCIAL.put("CSELLW2", "/data/wls11g/domains/cstore_domain/servers/cstore/logs");
		LOG_LOCATIONS_FOR_COMMERCIAL.put("CSELLW3", "/data/wls11g/domains/cstore_domain/servers/cstore/logs");

		LOG_LOCATIONS_FOR_TESTBED = new HashMap<String, String>();
		LOG_LOCATIONS_FOR_TESTBED.put("sellertb2.ollehmarket.com", "/data/wls11g/domains/cstore_domain/servers/cstore2/logs");
		LOG_LOCATIONS_FOR_TESTBED.put("mediat2.ollehmarket.com",   "/data/wls11g/domains/cstore_domain/servers/mediat2/logs");
	}
	
// serena를 통한 클래스 등록시 서브클래스 등록 문제가 있어, inner class사용을 없앰.
//	private final Map<String, String> LOG_LOCATIONS_FOR_COMMERCIAL = 
//			Collections.unmodifiableMap(new HashMap<String, String>() {
//				private static final long serialVersionUID = 1L;
//				{ 
//					put("CSELLW1", "/data/wls11g/domains/cstore_domain/servers/cstore/logs");
//					put("CSELLW2", "/data/wls11g/domains/cstore_domain/servers/cstore/logs");
//					put("CSELLW3", "/data/wls11g/domains/cstore_domain/servers/cstore/logs");
//				}});
	
//	private final Map<String, String> LOG_LOCATIONS_FOR_TESTBED = 
//			Collections.unmodifiableMap(new HashMap<String, String>() {
//				private static final long serialVersionUID = 1L;
//				{ 
//					put("sellertb2.ollehmarket.com", "/data/wls11g/domains/cstore_domain/servers/cstore2/logs");
//					put("mediat2.ollehmarket.com",   "/data/wls11g/domains/cstore_domain/servers/mediat2/logs");
//				}});

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		PageContext pageContext = jspFactory.getPageContext(this, request, response, null, true, 8192, true);
		HttpSession session = request.getSession( true );
		ServletConfig config = null;
		JspWriter out = null;
		JspWriter jspOut = null;
		PageContext jspPageContext = null;
		long start = System.currentTimeMillis();

		try {
			response.setContentType("text/html; charset=UTF-8");
			jspPageContext = pageContext;
			config = pageContext.getServletConfig();
			
			session = pageContext.getSession();
			String pw = (String) session.getAttribute("pw");
			if(pw==null || !PASSWD.equals(pw)) {
				pw=request.getParameter("pw")==null?"":request.getParameter("pw");
				session.setAttribute("pw", pw);
			}
			if(!PASSWD.equals(pw)) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			
//			if(!NetUtils.isLocal()) {
//				final String cookie = StringUtils.trim(CookieUtils.getCookie(request, "dev"));
//				final String userId = StringUtils.trim(CookieUtils.getCookie(request, "PSSO_UserID"));
//				if(StringUtils.isEmpty(cookie) || StringUtils.isEmpty(userId)){
//					response.sendError(HttpServletResponse.SC_NOT_FOUND);
//					return;
//				}
//			}

			// System.out.println("cookie : "+cookie);
			// System.out.println("userId : "+userId);
			
			out = pageContext.getOut();
			jspOut = out;

			out.write("\r\n");
			out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
			out.write("<html>\n");
			out.write("<head>\n");
			out.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
			out.write("<meta http-equiv=\"expires\" content=\"0\">\n");
			out.write("<meta http-equiv=\"cache-control\" content=\"no-cache\">\n");
			out.write("<meta http-equiv=\"pragma\" content=\"no-cache\">\n");
			out.write("\n");
			out.write("<link rel=\"stylesheet/less\" type=\"text/css\" href=\"/css/style.less\">\n");
			out.write("<link rel=\"shortcut icon\" href=\"/seller_res/images/favicon.ico\">\n");
			out.write("<script src=\"/jquery/js/jquery.js\" language=\"javascript\" type=\"text/javascript\"></script>\n");
			out.write("<script src=\"/jquery/js/scroll.js\" language=\"javascript\" type=\"text/javascript\"></script>\n");
			out.write("<script src=\"/jquery/js/less.js\" type=\"text/javascript\"></script>\n");
			out.write("<title>"+title+"</title>\n");
			out.write("</head>\n");
			out.write("<body onload=\"javascript:pageload();\">\n");

			final String servername = request.getServerName();
			String hostname = "";
			try {hostname += InetAddress.getLocalHost().getHostName();} catch(Exception e) {};
			
			String strLineSep = System.getProperty("line.separator");
			if (strLineSep != null) {
				if(strLineSep.equals("\r\n")) {
					strLineSep = "\n";
				}
			}
			String strFileSep = System.getProperty("file.separator");
			String strDirName = "logs";
			

			String strCtxParam = config.getServletContext().getInitParameter("logdir");
			if (strCtxParam != null) {
				if(!strCtxParam.isEmpty()) {
//					if(NetUtils.isCommercial())
//						strDirName = LOG_LOCATIONS_FOR_COMMERCIAL.get(hostname);
//					else if(NetUtils.isTB()) 
//						strDirName = LOG_LOCATIONS_FOR_TESTBED.get(servername);
//					else // unknown or localhost 
//						strDirName = strCtxParam;
				}
			}
			hostname = "서버명 : " + hostname;

			String strLogName = "";
			String strReqLogName = request.getParameter("log");
			File dir = new File(strDirName);
			LogFilter filter = new LogFilter();
			String[] strLogFiles = dir.list(filter);
			java.util.Arrays.sort(strLogFiles);
			int iLines = 200;
			String strReqLines = request.getParameter("lines");
			if (strReqLines != null) {
				if(strReqLines != "") {
					iLines = Integer.parseInt(strReqLines);   
				}
			}

			String strChecked = "";
			String strCheckbox = request.getParameter("gotobottom");
			if (strCheckbox != null) {
				strChecked = "checked";   
			} else {
				if (request.getParameter("prevScroll") == null) {
					strChecked = "checked"; 
				}
			}

			String strPrevScroll = request.getParameter("currScroll");
			if (strPrevScroll == null) {
				strPrevScroll = "0";
			}

			String strSeconds = request.getParameter("seconds");
			if (strSeconds == null) {
				strSeconds = "5";
			}
			// ---------------------------------------------------------------------------
			// 강제 세션 타임아웃 처리
			// ---------------------------------------------------------------------------
			// if ((int)Double.parseDouble(strSeconds)<0) strSeconds = "0";
			// session.setMaxInactiveInterval((int)Double.parseDouble(strSeconds));
			// long idletime = System.currentTimeMillis() - session.getLastAccessedTime();
			// request.setAttribute("idleTime", idletime); 
			// ---------------------------------------------------------------------------

			if (strReqLogName != null) {
				strLogName = strReqLogName;
			} else if (strLogFiles.length >= 1){
				strLogName = strLogFiles[0];
			}

			
			
			out.write("\n");
			out.write("<div class=\"headerform\">\n");
			out.write("<form id=\"headerform\" action=\"#\" method=\"post\" name=\"taillog\">\n");
			out.write("<input type=text name=\"hostname\" id=\"hostname\" style=\"width:130px;\" value=\"");
			out.print(hostname);
			out.write("\" readonly />\n");
			
			out.write("<select name=\"log\" onchange=\"javascript:logfileChanged();\">\n");
			for(String strLogFile : strLogFiles)  {
				if(strLogFile.equals(strLogName)) {
					out.write("\n");
					out.write("\t\t<option selected>");
					out.print(strLogFile );
					out.write("</option>\n");
					out.write("\t");
				} else {
					out.write("\n");
					out.write("\t\t<option>");
					out.print(strLogFile );
					out.write("</option>\n");
				}
			}

			out.write("</select> \n");
			out.write(" 출력 라인수: <input type=\"text\" name=\"lines\" value=\"");
			out.print(iLines );
			out.write("\" style=\"width:50px;\" onchange=\"this.form.submit();\" />\n");
			out.write(" 항상아래: <input type=\"checkbox\" id=\"gotobottom\" name=\"gotobottom\" value=\"gotobottom\" ");
			out.print(strChecked );
			out.write(" onchange=\"javascript:toBottom()\" />\n");
			out.write(" <input type=\"hidden\" id=\"currScroll\" name=\"currScroll\" value=\"\" />\n");
			out.write(" <input type=\"hidden\" id=\"prevScroll\" name=\"prevScroll\" value=\"");
			out.print(strPrevScroll );
			out.write("\" />\n");
			out.write("<input type=\"hidden\" id=\"maxScroll\" name=\"maxScroll\" value=\"\" />\n");
			out.write("<a href=\"javascript:pagerefresh()\">새로고침</a>\n");
			out.write(" 주기: <input type=\"text\" id=\"seconds\" name=\"seconds\" value=\"");
			out.print(strSeconds );
			out.write("\" size=\"2\" onchange=\"this.form.submit();\" /> 초 \n");
			out.write("<a href=\"");
			out.print("DownloadZipFile?logfile=" + strLogName );
			out.write("\">다운로드(Zip)</a>\n");
			out.write("\n");
			out.write("</form>\n");
			out.write("<hr>\n");
			out.write("</div>\n");
			out.write("<p class=\"content\" id=\"content\">\r\n");
			out.write("<pre>\n");
			out.write("\t");
			
			File f = new File(strDirName+strFileSep+strLogName);
			LogFile logFile = new LogFile(f);
			Tail t = new Tail(logFile);
			t.tailLog(out,iLines);
			long end = System.currentTimeMillis();
			String dur = Long.toString(end - start);
			out.write("</pre>\n");
			out.print("<br>로그 처리 시간 : "+dur+"ms, 사용법 : ESC 멈춤, F5 재시작");
			
			out.write("\n");
			out.write("</p>\n");
			out.write("</body>\n");
			out.write("</html>");
		} catch (Throwable t) {
			if (!(t instanceof SkipPageException)){
				out = jspOut;
				if (out != null && out.getBufferSize() != 0)
					try { out.clearBuffer(); } catch (IOException e) {}
				if (jspPageContext != null) jspPageContext.handlePageException(t);
			}
		} finally {
			jspFactory.releasePageContext(jspPageContext);
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}
}