package io.mkeasy.webapp.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import io.mkeasy.resolver.CommandMap;
import io.mkeasy.utils.MapUtil;
import io.mkeasy.utils.StringUtil;
import io.mkeasy.utils.excel.ExcelRead;
import io.mkeasy.utils.excel.ExcelReadOption;
import io.mkeasy.utils.excel.ExcelWrite;
import io.mkeasy.utils.excel.ExcelWriteOption;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExcelFactory {
	
    @Autowired
    FileFactory fileFactory;
	final String[] cols = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" , "M", "N" , "O", "P", "Q", "S", "T", "U", "V", "W", "X", "Y", "Z",
				"AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL" , "AM", "AN" , "AO", "AP", "AQ", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ",
				"BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL" , "BM", "BN" , "BO", "BP", "BQ", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ",
				"CA", "CC", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL" , "CM", "CN" , "CO", "CP", "CQ", "CS", "CT", "CU", "CV", "CW", "CX", "CY", "CZ",
				"DA", "DD", "DD", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL" , "DM", "DN" , "DO", "DP", "DQ", "DS", "DT", "DU", "DV", "DW", "DX", "DY", "DZ",
				"EA", "EE", "ED", "ED", "EE", "EF", "EG", "EH", "EI", "EJ", "EK", "EL" , "EM", "EN" , "EO", "EP", "EQ", "ES", "ET", "EU", "EV", "EW", "EX", "EY", "EZ",
				"FA", "FF", "FC", "FD", "FF", "FF", "FG", "FH", "FI", "FJ", "FK", "FL" , "FM", "FN" , "FO", "FP", "FQ", "FS", "FT", "FU", "FV", "FW", "FX", "FY", "FZ",
				"GA", "GG", "GC", "GD", "GF", "GF", "GG", "GH", "GI", "GJ", "GK", "GL" , "GM", "GN" , "GO", "GP", "GQ", "GS", "GT", "GU", "GV", "GW", "GX", "GY", "GZ",
				"HA", "HH", "HC", "HD", "HE", "HF", "HH", "HH", "HI", "HJ", "HK", "HL" , "HM", "HN" , "HO", "HP", "HQ", "HS", "HT", "HU", "HV", "HW", "HX", "HY", "HZ",
				"IA", "II", "IC", "ID", "IE", "IF", "IH", "IH", "II", "IJ", "IK", "IL" , "IM", "IN" , "IO", "IP", "IQ", "IS", "IT", "IU", "IV", "IW", "IX", "IY", "IZ" 
				};

	public Map<String, String> getHeader(String filePath) throws Exception {

		if(filePath == null)
			throw new Exception(filePath +"가 존재하지 않습니다.");

		File file = new File(filePath);
		String ext = FilenameUtils.getExtension(file.getName());
		File tempFile = File.createTempFile("temp-","."+ext); 
		tempFile.deleteOnExit();

		FileUtils.copyFile(file, tempFile);
		
		ExcelReadOption ro = new ExcelReadOption();
		ro.setFilePath(tempFile.getAbsolutePath());
		
		ro.setOutputColumns(cols);
		ro.setStartRow(1); // skip first rows (skip titles)

		List<Map<String, String>> result = ExcelRead.read(ro);

		FileUtils.deleteQuietly(tempFile);
		
		return result!=null?result.get(0):MapUtil.EMPTY;
	}

	public List<Map<String, String>> upload(String filePath, ModelMap model, CommandMap commandMap) throws Exception {

		if(filePath == null)
			throw new Exception(filePath +"가 존재하지 않습니다.");

		File file = new File(filePath);
		String ext = FilenameUtils.getExtension(file.getName());
		File tempFile = File.createTempFile("temp-","."+ext); 
		tempFile.deleteOnExit();

		FileUtils.copyFile(file, tempFile);
		
		ExcelReadOption ro = new ExcelReadOption();
		ro.setFilePath(tempFile.getAbsolutePath());
		ro.setOutputColumns(cols);
		ro.setStartRow(2); // skip first rows (skip titles)

		List<Map<String, String>> result = ExcelRead.read(ro);
		log.debug("excel result : {}", result);

		FileUtils.deleteQuietly(tempFile);
		
		return result;
	}

	public void download(HttpServletRequest request, HttpServletResponse response
			,String dnFileName // 다운로드 엑셀파일명
			,List<String> headerNames // 엑셀파일 컬럼명
			,List<String> fieldNames  // 쿼리결과 필드명
			,List<Map<String, Object>> result) throws Exception {

    	String ext = FilenameUtils.getExtension(dnFileName);
    	if(!(StringUtils.equals(ext, "xls") 
    			|| StringUtils.equals(ext, "xlsx"))) {
    		log.error("{}", "download filename is not Excel");
    		return;
    	}
    	
    	File tempExcel = File.createTempFile("temp-", "."+ext); 
    	tempExcel.deleteOnExit();

    	String path = tempExcel.getAbsolutePath();
		String fileName = tempExcel.getName();
		String filePath = path+fileName;
		String sheetName = "Sheet1";

		File xlsFile = new File(filePath);
		if(xlsFile.exists())
			FileUtils.forceDelete(new File(filePath));

		ExcelWriteOption wo = new ExcelWriteOption();

		wo.setFileName(fileName);
		wo.setFilePath(path);
		wo.setSheetName(sheetName);
		wo.setTitles(headerNames);
		
		List<String[]> excelRows = new ArrayList<String[]>();
		for(Map<String, Object> item : result) {
            List<String> tmpRow = new ArrayList<String>();
			for(int i = 0; i<fieldNames.size(); i++) {
				String field = fieldNames.get(i);
                String value = StringUtil.trimToEmpty(item.get(field));
                tmpRow.add(i, value);
			}
			String[] row = tmpRow.toArray(new String[tmpRow.size()]);
			excelRows.add(row);
		}
		wo.setContents(excelRows);
		
		File excelFile = ExcelWrite.write(wo);	
		
		fileFactory.download(request, response, excelFile, dnFileName);

		if(xlsFile.exists())
			FileUtils.forceDelete(new File(filePath));
	}
	
}
