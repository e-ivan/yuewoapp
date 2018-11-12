import com.alibaba.fastjson.JSONObject;
import com.war4.util.RequestUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by hh on 2017.8.16 0016.
 */
@Log4j
public class AppTest extends Object{
    @Test
    public void testSS() throws Exception {
        Pattern pattern = Pattern.compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://|[fF][tT][pP]://).*");
        System.out.println(pattern.matcher("https://fdgdfg ").matches());

    }


    @Test
    public void testdfdf() throws Exception {
        Pattern pattern = Pattern.compile(".*_.*mall\\..*");
        System.out.println(pattern.matcher("https://www.baidu_xsmall.html").matches());
        String s = StringUtils.replacePattern("https://www.baidu.html", "_.*mall", "");
        System.out.println("https://www.baidu_small/hioe_mm.html".replaceFirst("(_.*\\.)", "."));
//        System.out.println(s);
    }
    @Test
    public void testDDGD() throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("address","北京市");
//        map.put("batch","true");
        JSONObject jsonObject = RequestUtil.baseAmapRequest(map, "geocode/geo");
        System.out.println(jsonObject);
    }
    @Test
    public void testDD() throws Exception {
        Pattern pattern = Pattern.compile("\\d{2}[0]{4}$");
        System.out.println(pattern.matcher("100000").matches());
    }
    @Test
    public void testDGDG() throws Exception {
        System.out.println(Long.MAX_VALUE);
    }

}
