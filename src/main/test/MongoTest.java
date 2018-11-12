import com.war4.enums.TaskListType;
import com.war4.pojo.TaskList;
import com.war4.repository.BaseRepository;
import com.war4.service.impl.FilmServiceImpl;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by hh on 2017.8.22 0022.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class MongoTest {
    static {
        try {
            Log4jConfigurer.initLogging("classpath:log4j.properties");
        } catch (FileNotFoundException ex) {
            System.err.println("Cannot Initialize log4j");
        }
    }

    @Autowired
    private YinghezhongService service;
    @Autowired
    private UserInfoBaseService userInfoBaseService;
    @Autowired
    private UserRatioService userRatioService;
    @Autowired
    private MomentService momentService;
    @Autowired
    private BaseOrderService baseOrderService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private BaseRepository repository;
    @Autowired
    private VideoChatService chatService;
    @Autowired
    private EvaluateSystemService evaluateSystemService;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private RongYunService rongYunService;
    @Autowired
    private FileService fileService;
    @Autowired
    private KouDianYingService kouDianYingService;
    @Autowired
    private AdPageService adPageService;
    @Autowired
    private FilmServiceImpl filmService;
    @Autowired
    private NearbyDatingService nearbyDatingService;

    @Autowired
    private SystemDictionaryService systemDictionaryService;
    @Autowired
    private HomePageService homePageService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private TaskListService taskListService;
    //影院编码
    public static final String clientNo = "98179869";

    @Test
    public void testdgdfgd() throws Exception {
        List<TaskList> taskLists = taskListService.queryTaskListByDate(null, TaskListType.FILM_ORDER_REPORT,
                DateUtils.parseDate("2018-02-02","yyyy-MM-dd"), DateUtils.parseDate("2018-02-05","yyyy-MM-dd"));
    }


}
