package com.masker.excelMasker.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

	private Workbook workbook = null;
	private Sheet sheet = null;
	private Map<String, Integer> columnHeaderIndex = new HashMap<>();
	
	public String readExcel (MultipartFile multipartFile, boolean isHeaderPresent) {

		try {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
			sheet = workbook.getSheetAt(0);
			sheet.getRow(0).forEach(cell -> columnHeaderIndex.put(cell.getRichStringCellValue().getString(), 
																  cell.getColumnIndex()));
		} catch (IOException e) {
			System.out.println(e);
		}
		return columnHeaderIndex.keySet().toString();
	}

	public ByteArrayInputStream maskSelectedColumns (MultipartFile file, boolean isHeaderPresent, List<String> selectedColumnHeaders) throws IOException{
		
		if (workbook == null) { readExcel(file, isHeaderPresent); }
		
		if (!selectedColumnHeaders.isEmpty()) { columnHeaderIndex.keySet().retainAll(selectedColumnHeaders); }
		sheet.forEach(row -> {
				columnHeaderIndex.forEach((name, index) -> {
					//row.getCell(index).setCellValue((double) maskvalue(row.getCell(index)));//call maskString, maskNumeric
					Cell cell = row.getCell(index);
					if(cell != null) maskvalue(cell);//call maskString, maskNumeric
				}); 
			});
		
		try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			workbook.write(outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		} 
	}
	
	private Cell maskvalue (Cell cell) {
		
        switch (cell.getCellType()) {
        case STRING:
    		String string = cell.getRichStringCellValue().getString() + "Dinessh";
    		cell.setCellValue(string);
            break;
        case NUMERIC:
            break;
        case BOOLEAN:
            break;
        case FORMULA:
            break;
        default:
        }

        return cell;
	}
	
}