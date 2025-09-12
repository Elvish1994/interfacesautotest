package tests;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        Workbook workbook = WorkbookFactory.create(new File("src/test/resources/data/模板.xlsx"));
        Sheet sheet1 = workbook.getSheet("数据准备");

        if (sheet1 != null) {
            // 遍历数据，插入数据库 or 缓存中用于后续测试准备
            for (Row row : sheet1) {
                if (row.getRowNum() == 0) continue; // 跳过标题行
                // 处理每一行数据，例如写入 DB 或 mock 数据服务
            }
            System.out.println("✅ Sheet1 数据准备完成");
        } else {
            System.out.println("⚠️ Sheet1 不存在，跳过数据准备");
        }
    }

}
