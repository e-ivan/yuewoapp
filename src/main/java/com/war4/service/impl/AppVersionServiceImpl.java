package com.war4.service.impl;

import com.war4.base.BaseService;
import com.war4.enums.FilePurpose;
import com.war4.enums.FileTypeAndPath;
import com.war4.pojo.VersionUpgrade;
import com.war4.service.AppVersionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


/**
 * Created by E_Iva on 2018.1.17.0017.
 */
@Service
public class AppVersionServiceImpl extends BaseService implements AppVersionService {
    @Override
    @Transactional
    public void saveVersion(MultipartFile file, VersionUpgrade version) {
        baseRepository.saveObj(version);
        try {
            List<String> files = fileService.saveFile(FileTypeAndPath.APP_TYPE_PATH, FilePurpose.APP_APK, version.getId().toString(), file);
            version.setApkUrl(files.get(0));
            baseRepository.updateObj(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public VersionUpgrade selectLatestVersion(byte appType,int versionCode) {
        String hql = "from VersionUpgrade where appType = :appType order by created desc";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("appType",appType);
        VersionUpgrade versionUpgrade = baseRepository.executeHql(hql, paramMap);
        if (versionUpgrade != null && !versionUpgrade.isMust()) {
            versionUpgrade.setMust(this.queryBeforeVersionMustUpgrade(appType,versionCode) > 0);
        }
        return versionUpgrade;
    }

    private long queryBeforeVersionMustUpgrade(byte appType,int versionCode){
        String hql = "select count(id) from VersionUpgrade where appType = :appType and versionCode > :versionCode and must = 1";
        Map<String, Object> paramMap = baseRepository.paramMap();
        paramMap.put("appType",appType);
        paramMap.put("versionCode",versionCode);
        return baseRepository.executeHql(hql,paramMap);
    }
}
