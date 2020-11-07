package io.mkeasy.utils.excel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import lombok.extern.slf4j.Slf4j;

/**
 * 엑셀 파일을 읽어 온다.
 * <pre>
 * 사용 예제
        ExcelReadOption ro = new ExcelReadOption();
        ro.setFilePath("/Users/mcjang/ktest/uploadedFile/practiceTest.xlsx");
        ro.setOutputColumns("C", "D", "E", "F", "G", "H", "I");
        ro.setStartRow(3);

        List<Map<String, String>> result = ExcelRead.read(ro);

        for(Map<String, String> map : result) {
                System.out.println(map.get("E"));
        }
</pre>
 * @author Minchang Jang (mc.jang@hucloud.co.kr)
 */
@Slf4j
public class ExcelRead {

	/**
	 * 엑셀 파일을 읽어옴
	 * @param readOption
	 * @return
	 * @throws Exception 
	 */
	public static List<Map<String, String>> read(ExcelReadOption readOption) throws Exception {

		Workbook wb = ReadFileType.getWorkbook(readOption.getFilePath());
		Sheet sheet = wb.getSheetAt(0);

		int numOfRows = sheet.getPhysicalNumberOfRows();
		int numOfCells = 0;

		Row row = null;
		Cell cell = null;

		String cellName = "";

		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        List<String> outputColumns = readOption.getOutputColumns();

		for(int rowIndex = readOption.getStartRow() - 1; rowIndex < numOfRows; rowIndex++) {

			row = sheet.getRow(rowIndex);
			if(row != null) {
				// numOfCells = row.getPhysicalNumberOfCells(); // bug
				numOfCells = row.getLastCellNum();

                Map<String, String> map = new HashMap<String, String>();
				for(int cellIndex = 0; cellIndex < numOfCells; cellIndex++) {
					cell = row.getCell(cellIndex);
					cellName = CellRef.getName(cell, cellIndex);
					if(!outputColumns.contains(cellName)) continue;
					map.put(cellName, CellRef.getValue(cell));
				}
				result.add(map);
			}

		}

		return result;

	}

}

