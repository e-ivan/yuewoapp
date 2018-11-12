package com.war4.thirdParty.FFmpegCommandManager;

import com.war4.thirdParty.FFmpegCommandManager.dao.TaskDao;
import com.war4.thirdParty.FFmpegCommandManager.dao.TaskDaoImpl;
import com.war4.thirdParty.FFmpegCommandManager.entity.TaskEntity;
import com.war4.thirdParty.FFmpegCommandManager.service.CommandAssembly;
import com.war4.thirdParty.FFmpegCommandManager.service.CommandAssemblyUtil;
import com.war4.thirdParty.FFmpegCommandManager.service.TaskHandler;
import com.war4.thirdParty.FFmpegCommandManager.service.TaskHandlerImpl;
import com.war4.thirdParty.FFmpegCommandManager.util.LoadConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * FFmpeg命令操作管理器
 * 
 * @author eguid
 * @since jdk1.7
 * @version 2016年10月29日
 */
public class FFmpegManagerImpl implements FFmpegManager {
	private TaskDao taskDao = null;
	private TaskHandler taskHandler = null;
	private CommandAssembly commandAssembly = null;
	private LoadConfig loadConf = null;


    private static class LazyHolder {
        private static final FFmpegManagerImpl INSTANCE = new FFmpegManagerImpl(20);
    }


    public static FFmpegManagerImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    public FFmpegManagerImpl(boolean init) {
		if (loadConf == null) {
			loadConf = new LoadConfig();
		}
		if (init) {
			init(10);
		}
	}

	public FFmpegManagerImpl(int size) {
		if (loadConf == null) {
			loadConf = new LoadConfig();
		}
		init(size);
	}

	/**
	 * 初始化
	 * 
	 * @param size
	 */
	public void init(int size) {
		this.taskDao = new TaskDaoImpl(size);
		this.taskHandler = new TaskHandlerImpl();
		this.commandAssembly = new CommandAssemblyUtil();
	}

	public void setTaskDao(TaskDao taskDao) {
		this.taskDao = taskDao;
	}

	public void setTaskHandler(TaskHandler taskHandler) {
		this.taskHandler = taskHandler;
	}

	public void setCommandAssembly(CommandAssembly commandAssembly) {
		this.commandAssembly = commandAssembly;
	}

	@Override
	public String start(String id, String command) {
		if (id != null && command != null) {
			TaskEntity tasker = taskHandler.process(id, command);
			if (tasker != null) {
				int ret = taskDao.add(tasker);
				if (ret > 0) {
					return tasker.getId();
				} else {
					// 持久化信息失败，停止处理
					taskHandler.stop(tasker.getProcess(), tasker.getThread());
					System.err.println("持久化失败，停止任务！");
				}
			}
		}
		return null;
	}

	@Override
	public String start(Map assembly) {
		// ffmpeg环境是否配置正确
		if (!loadConf.isHave()) {
			System.err.println("ffmpeg环境未配置，无法执行");
			return null;
		}
		// 参数是否符合要求
		if (assembly == null || assembly.isEmpty() || !assembly.containsKey("appName")) {
			System.err.println("参数不正确，无法执行");
			return null;
		}
		String appName = (String) assembly.get("appName");
		if (appName != null && "".equals(appName.trim())) {
			System.err.println("appName不能为空");
			return null;
		}
		assembly.put("ffmpegPath", loadConf.getPath());
		String command = commandAssembly.assembly(assembly);
		if (command != null) {
			return start(appName, command);
		}

		return null;
	}

	@Override
	public boolean stop(String id) {
		if (id != null && taskDao.isHave(id)) {
			System.out.println("正在停止任务：" + id);
			TaskEntity tasker = taskDao.get(id);
			if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
				taskDao.remove(id);
				return true;
			}
		}
		System.err.println("停止任务失败！");
		return false;
	}

	@Override
	public int stopAll() {
		Collection<TaskEntity> list = taskDao.getAll();
		Iterator<TaskEntity> iter = list.iterator();
		TaskEntity tasker = null;
		int index = 0;
		while (iter.hasNext()) {
			tasker = iter.next();
			if (taskHandler.stop(tasker.getProcess(), tasker.getThread())) {
				taskDao.remove(tasker.getId());
				index++;
			}
		}
		System.out.println("停止了" + index + "个任务！");
		return index;
	}

	@Override
	public TaskEntity query(String id) {
		return taskDao.get(id);
	}

	@Override
	public Collection<TaskEntity> queryAll() {
		return taskDao.getAll();
	}
//	rtmp://pdlb958ef09.live.126.net/live/b7796110a9c441ebae1e4d881013bde5?wsSecret=e2705ffdd50563d032cc382bf0462dbd&wsTime=1482999083
	public static void main(String[] args) throws Exception{
		FFmpegManager manager = new FFmpegManagerImpl(10);
		Map map = new HashMap();
		map.put("appName", "test123");
		map.put("input",  "D:\\JavaProjectFile\\qnyyFile"+"\\video\\filmVideo\\842b96d6-5eac-49bb-9971-1ad8a8754347.flv");
		map.put("output", "rtmp://pdlb958ef09.live.126.net/live/62fae6a8e04e48a9b5762dfcca430d8b?wsSecret=8e3107569a339255d77fee1d61ac799a&wsTime=1483083604");

		// 执行任务，id就是appName，如果执行失败返回为null
		String id = manager.start(map);
		System.out.println(id);
		// 通过id查询
		TaskEntity info = manager.query(id);
		System.out.println(info);
		// 查询全部
		Collection<TaskEntity> infoList = manager.queryAll();
		System.out.println(infoList);
//		// 停止id对应的任务
//		manager.stop(id);
//
//		manager.start("test1", "这里放原生的ffmpeg命令");
//		// 停止全部任务
//		manager.stopAll();
//		Thread.sleep(1000);
		//ffmpeg -re -i localFile.mp4 -c copy -f flv rtmp://server/live/streamName


	}
}
