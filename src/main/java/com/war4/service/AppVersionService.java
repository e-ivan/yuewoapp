package com.war4.service;

import com.war4.pojo.VersionUpgrade;
import org.springframework.web.multipart.MultipartFile;

/**
 * app版本服务
 * Created by E_Iva on 2018.1.17.0017.
 */
public interface AppVersionService {
    void saveVersion(MultipartFile file, VersionUpgrade version);

    VersionUpgrade selectLatestVersion(byte appType, int versionCode);
}
