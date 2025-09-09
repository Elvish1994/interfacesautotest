package api;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelInterfaceTest {
    
    public void executeTest(String excelPath) throws IOException {
        FileInputStream fis = new FileInputStream(excelPath);
        Workbook workbook = new XSSFWorkbook(fis);
        
        // 获取各个sheet页
        Sheet testCaseSheet = workbook.getSheetAt(0);  // 测试用例数据
        Sheet requestSheet = workbook.getSheetAt(1);   // 请求参数
        Sheet expectSheet = workbook.getSheetAt(2);    // 预期结果
        
        // 遍历请求参数sheet执行测试
        for (int i = 1; i <= requestSheet.getLastRowNum(); i++) {
            Row requestRow = requestSheet.getRow(i);
            if (requestRow == null) continue;
            
            // 提取请求参数
            String url = getCellValueAsString(requestRow.getCell(1));
            String method = getCellValueAsString(requestRow.getCell(2));
            String headers = getCellValueAsString(requestRow.getCell(3));
            String params = getCellValueAsString(requestRow.getCell(4));
            String body = getCellValueAsString(requestRow.getCell(5));
            
            try {
                // 发送HTTP请求
                String response = sendHttpRequest(url, method, headers, params, body);
                
                // 获取预期结果
                Row expectRow = expectSheet.getRow(i);
                String expectedResult = expectRow != null ? 
                    getCellValueAsString(expectRow.getCell(1)) : "";
                
                // 比对结果
                boolean isMatch = compareResult(response, expectedResult);
                
                // 标记结果
                Cell resultCell = requestRow.createCell(6);
                resultCell.setCellValue(response);
                
                Cell statusCell = requestRow.createCell(7);
                statusCell.setCellValue(isMatch ? "PASS" : "FAIL");
                
                // 如果不一致，标红
                if (!isMatch) {
                    CellStyle redStyle = createRedStyle(workbook);
                    resultCell.setCellStyle(redStyle);
                    statusCell.setCellStyle(redStyle);
                }
                
            } catch (Exception e) {
                // 异常处理
                Cell errorCell = requestRow.createCell(7);
                errorCell.setCellValue("ERROR: " + e.getMessage());
                errorCell.setCellStyle(createRedStyle(workbook));
            }
        }
        
        // 保存结果
        FileOutputStream fos = new FileOutputStream(excelPath.replace(".xlsx", "_result.xlsx"));
        workbook.write(fos);
        
        fis.close();
        fos.close();
        workbook.close();
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
    
    private CellStyle createRedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private String sendHttpRequest(String url, String method, String headers, 
                                  String params, String body) {
        // 实现HTTP请求逻辑
        // 这里需要根据实际需求实现具体的HTTP请求
        return "mock response"; // 示例返回值
    }
    
    private boolean compareResult(String actual, String expected) {
        // 实现结果比对逻辑
        return actual.equals(expected);
    }
}
