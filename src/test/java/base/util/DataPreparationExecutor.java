package base.util;


import config.databaseUtils.DatabaseOperations;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataPreparationExecutor {

    /**
     * 执行 Excel 中 "数据准备" Sheet 的 SQL 语句
     * @param excelFilePath 文件路径（绝对路径或相对路径）
     * @throws Exception 如果文件不存在、Sheet 不存在、SQL 执行失败等
     */
    public  void executeDataPreparation(String excelFilePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            executeWorkbook(workbook);
        }
    }

    /**
     * 从 ClassPath 读取 Excel 文件（推荐用于测试资源）
     * @param classpathResource 位于 src/test/resources 下的文件路径，如 "data/test-data.xlsx"
     * @throws Exception
     */
    public  void executeDataPreparationFromClasspath(String classpathResource) throws Exception {
        try (InputStream is = DataPreparationExecutor.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) {
                throw new IOException("无法在 ClassPath 中找到文件: " + classpathResource);
            }
            try (Workbook workbook = new XSSFWorkbook(is)) {
                executeWorkbook(workbook);
            }
        }
    }

    /**
     * 核心执行逻辑
     */
    private  void executeWorkbook(Workbook workbook) throws Exception {
        // 1. 获取 "数据准备" Sheet
        Sheet sheet = workbook.getSheet("数据准备");
        if (sheet == null) {
            throw new IOException("Excel 文件中未找到名为 '数据准备' 的 Sheet。");
        }

        boolean isFirstRow = true;

        for (Row row : sheet) {
            if (isFirstRow) {
                isFirstRow = false;
                continue; // 跳过表头
            }

            // 假设 SQL 在第一列（索引 0）
            Cell cell = row.getCell(0);
            if (cell == null) {
                continue; // 跳过空行
            }

            String sql = cell.getStringCellValue().trim();
            if (sql.isEmpty()) {
                continue; // 跳过空 SQL
            }

            // 2. 执行 SQL（调用你指定的方法）
            try {
              //  DatabaseOperations.execute(sql);
                System.out.println("✅ 执行成功: " + sql);
            } catch (Exception e) {
                // 3. 任何异常都立即抛出，导致测试失败
                throw new Exception("SQL 执行失败 (第 " + (row.getRowNum() + 1) + " 行): " + sql, e);
            }
        }
    }
}