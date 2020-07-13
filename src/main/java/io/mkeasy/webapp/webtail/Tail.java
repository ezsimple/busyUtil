package io.mkeasy.webapp.webtail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 작 성 자 : Lee MinHo 
 * 작 성 일 : 2013. 2. 5.
 * 수정이력 : 2013. 2. 5. Lee MinHo 최초작성
 * </pre>
 */
@Slf4j
public class Tail {

	private LogFile file;
	
	public Tail(LogFile file) {
		this.file = file;
	}
	
	public void tailLog(JspWriter out, int lines) {
		try {
			File f = file.getFile();
			tail(f,out,lines);
		} catch (FileNotFoundException e) {
			log.error("{}", "파일이 존재하지 않습니다.");
		} catch (IOException e) {
			log.error("{}","IO 오류가 발생하였습니다.");
		}
	}
	
	public String getEncodeType(File f) {
		String encoding = null; // 1. Set Default Type
		
		if(f==null) return encoding;
		
		try {
			byte[] buf = new byte[4096];
			@SuppressWarnings("resource")
			FileInputStream fis = new FileInputStream(f);

			// 2. Construct an instance of org.mozilla.universalchardet.UniversalDetector.
			UniversalDetector detector = new UniversalDetector(null);

			// 3. Feed some data (typically several thousands bytes) to the detector 
			//    by calling UniversalDetector.handleData().
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}

			// 4. Notify the detector of the end of data by calling UniversalDetector.dataEnd().
			detector.dataEnd();

			// 5. Get the detected encoding name 
			//    by calling UniversalDetector.getDetectedCharset().
			//    encoding is UTF-8/EUC-KR/.../NULL
			encoding = detector.getDetectedCharset();

			// 6. Don't forget to call UniversalDetector.reset() 
			//    before you reuse the detector instance.
			detector.reset();
		} catch (Exception e) {
		} finally {
			if(encoding == null) encoding = "UTF-8";
			if(encoding.startsWith("WINDOW")) encoding = "EUC-KR";
		}
		return encoding;
	}
	
	// Direct out.print() call 
	// for Performance & for prevent - java.lang.OutOfMemoryError: Java heap space using StringBuffer or StringBuilder
	// for prevent stuck thread because of new String() buffer for lines[Idx];
	public void tail(File src, JspWriter out, int maxLines) throws IOException {
		String encoding = getEncodeType(src);
		FileInputStream fis = new FileInputStream(src);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(fis,encoding));
	    String[] lines = new String[maxLines];
	    int lastIdx = 0;
	    for (String line=reader.readLine(); line != null; line=reader.readLine()) {
	        if (lastIdx == lines.length) lastIdx = 0;
	        if(line != null) 
	        	lines[lastIdx++] = line;
	    }
	    
	    // String out = new String(); - occure stuck thread problem
	    // StringBuilder out = new StringBuilder(); - occured java.lang.OutOfMemoryError: Java heap space problem
	    for (int Idx=lastIdx; Idx != lastIdx-1; Idx++) {
	        if (Idx == lines.length) Idx = 0;
	        if(lines[Idx] != null ) {
		        out.print(StringEscapeUtils.escapeHtml(lines[Idx]));
		        out.print("<br>");
	        }
	    }
	    reader.close();
	    fis.close();
	}
	
	// for Debugging
	public void tail(File src, OutputStream out, int maxLines) throws FileNotFoundException, IOException {
		String encoding = getEncodeType(src);
		FileInputStream fis = new FileInputStream(src);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(fis,encoding));
	    String[] lines = new String[maxLines];
	    int lastIdx = 0;
	    for (String line=reader.readLine(); line != null; line=reader.readLine()) {
	        if (lastIdx == lines.length) lastIdx = 0;
	        if(line != null) 
	        	lines[lastIdx++] = line;
	    }
	    
	    OutputStreamWriter writer = new OutputStreamWriter(out);
	    for (int Idx=lastIdx; Idx != lastIdx-1; Idx++) {
	        if (Idx == lines.length) Idx = 0;
	        if(lines[Idx] != null ) {
		        writer.write(StringEscapeUtils.escapeHtml(lines[Idx]));
		        writer.write("\n");
	        }
	    }
	    reader.close();
	    fis.close();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void test(String[] args) throws FileNotFoundException, IOException {
		String filePath = "C:\\Temp\\eggplan_catarina.out";
		File f = new File(filePath);
		OutputStream out = System.out;
		int lines = 50;
		tail(f,out,lines);
	}

}
