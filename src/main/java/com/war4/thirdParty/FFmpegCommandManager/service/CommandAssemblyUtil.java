package com.war4.thirdParty.FFmpegCommandManager.service;


import java.util.Map;
/**
 * 命令组装器实现
 * @author eguid
 * @since jdk1.7
 * @version 2016年10月29日
 */
public class CommandAssemblyUtil implements CommandAssembly {
	/**
	 * 
	 * @param paramMap
	 *            -要组装的map
	 * @return
	 */
	public String assembly(Map<String, String> paramMap) {
		try {
			if (paramMap.containsKey("ffmpegPath")) {
				String ffmpegPath = (String) paramMap.get("ffmpegPath");
				// -i：输入流地址或者文件绝对地址
				StringBuilder comm = new StringBuilder(ffmpegPath + " -re -i ");
				// 是否有必输项：输入地址，输出地址，应用名，twoPart：0-推一个元码流；1-推一个自定义推流；2-推两个流（一个是自定义，一个是元码）
				if (paramMap.containsKey("input") && paramMap.containsKey("output") && paramMap.containsKey("appName")
						) {
					String input = (String) paramMap.get("input");
					String output = (String) paramMap.get("output");
					String appName = (String) paramMap.get("appName");
//					String twoPart = (String) paramMap.get("twoPart");
//					String codec = (String) paramMap.get("codec");
					// 默认h264解码
//					codec = (codec == null ? "h264" : (String) paramMap.get("codec"));
					// 输入地址
					comm.append(input);
					comm.append(" -c copy -f flv "+output + appName);
					// 当twoPart为0时，只推一个元码流
//					if ("0".equals(twoPart)) {
//					} else {
//						// -f ：转换格式，默认flv
//						if (paramMap.containsKey("fmt")) {
//							String fmt = (String) paramMap.get("fmt");
//							comm.append(" -f " + fmt);
//						}

//						// 输出地址+发布的应用名
//						comm.append(" -an " + output + appName);
//						// 当twoPart为2时推两个流，一个自定义流，一个元码流
//						if ("2".equals(twoPart)) {
//							// 一个视频源，可以有多个输出，第二个输出为拷贝源视频输出，不改变视频的各项参数并且命名为应用名+HD
//							comm.append(" -vcodec copy  -f flv -an ").append(output + appName + "HD");
//						}
//					}
					return comm.toString();
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
