package api;

import org.testng.annotations.DataProvider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * 数据驱动得示例
 */
public class DataDrivenUtil {


    /**
     * 从外部文件读取数据（如 Excel、CSV）
     * @return
     * @throws IOException
     */
    @DataProvider(name = "loginFromCSV")
    public Object[][] provideDataFromCSV() throws IOException {
        List<Object[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/login-test-data.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                data.add(new Object[]{
                        fields[0],           // username
                        fields[1],           // password
                        Integer.parseInt(fields[2]), // expectedCode
                        fields[3]            // expectedMsg
                });
            }
        }
        return data.toArray(Object[][]::new);
    }

    /**
     * 数据提供者返回 Iterator<Object[]>（大数据量推荐）
     * @return
     */
    @DataProvider(name = "largeData")
    public Iterator<Object[]> provideLargeData() {
        List<Object[]> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add(new Object[]{"user" + i, "123456", 0, "success"});
        }
        return data.iterator();
    }
}
