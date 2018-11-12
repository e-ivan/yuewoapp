package com.war4.vo;

import com.war4.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 朋友圈文件
 */
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
public class MomentFileVO extends BaseVO{

    private String path;

    private Integer sizeH;

    private Integer sizeW;

    private String type;

    private String mime;
}