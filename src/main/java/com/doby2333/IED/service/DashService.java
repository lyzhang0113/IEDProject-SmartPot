package com.doby2333.IED.service;

import com.doby2333.IED.dto.PotDto;
import com.doby2333.IED.dto.SettingDto;
import com.doby2333.IED.entity.Plant;
import com.doby2333.IED.mapper.PlantMapper;
import com.doby2333.IED.mapper.SettingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashService {

    @Autowired
    private PlantMapper plantMapper;

    @Autowired
    private SettingMapper settingMapper;

    public List<Plant> getPlants() {
        return plantMapper.getPlants();
    }

//    public Map<Long, String> getNames() {return plantMapper.getNames();}
//
//    public Map<Long, String> getSciNames() {return plantMapper.getSciNames();}
//
//    public Map<Long, Integer> getLightFreq() {return plantMapper.getLightFreq();}
//
//    public Map<Long, Integer> getLightIntense() {return plantMapper.getLightIntense();}
//
//    public Map<Long, String> getLightDetail() {return plantMapper.getLightDetail();}
//
//    public Map<Long, Integer> getWaterFreq() {return plantMapper.getWaterFreq();}
//
//    public Map<Long, String> getWaterFreqDetail() {return plantMapper.getWaterFreqDetail();}
//
//    public Map<Long, Integer> getWaterIntense() {return plantMapper.getWaterIntense();}
//
//    public Map<Long, String> getWaterIntenseDetail() {return plantMapper.getWaterIntenseDetail();}
//
//    public Map<Long, String> getFertGuide() {return plantMapper.getFertGuide();}
//
//    public Map<Long, String> getFertDetail() {return plantMapper.getFertDetail();}

    public int countPots(Long uid) {
        return settingMapper.countPots(uid);
    }

    public String getPlantName(Long pid) {
        return settingMapper.getPlantName(pid);
    }

    public Long getUserPot(Long uid) {
        Long r = settingMapper.getUserPot(uid);
        return r == null ? Long.valueOf(-1) : r;
    }

    public PotDto getPots(Long uid) {
        PotDto potDto = new PotDto(settingMapper.getPots(uid));
        return potDto;
    }

    public boolean registerPot(Long uid, Long pid) {
        if (settingMapper.validatePID(pid) != 0) return false;
        settingMapper.saveSetting(uid, pid, Long.valueOf(0), 0, 0, 0, 0);
        return true;
    }

    public Boolean saveSetting(Long uid, Long pid, Long plant_id, Integer lightFreq, Integer lightIntense, Integer waterFreq, Integer waterIntense) {
        return settingMapper.saveSetting(uid, pid, plant_id, lightFreq, lightIntense, waterFreq, waterIntense);
    }

    public Boolean saveSetting(Long uid, Long pid, Long plant_id) {
        int lightFreq = plantMapper.getLightFreqByID(plant_id);
        int lightIntense = plantMapper.getLightIntenseByID(plant_id);
        int waterFreq = plantMapper.getWaterFreqByID(plant_id);
        int waterIntense = plantMapper.getWaterIntenseByID(plant_id);
        return saveSetting(uid, pid, plant_id, lightFreq, lightIntense, waterFreq, waterIntense);
    }

    public Map<Date, Integer> getRecentSetting(Long uid, Long pid, String setting) {
        Map<Date, Integer> result = new LinkedHashMap<>();
        List<SettingDto> settings = new LinkedList<>();
        switch (setting) {
            case "light_freq":
                settings = settingMapper.find5LightFreq(uid, pid);
                break;
            case "light_intense":
                settings = settingMapper.find5LightIntense(uid, pid);
                break;
            case "water_freq":
                settings = settingMapper.find5WaterFreq(uid, pid);
                break;
            case "water_intense":
                settings = settingMapper.find5WaterIntense(uid, pid);
                break;
        }
        for (SettingDto s : settings) {
            result.put(s.getTime(), s.getValue());
        }
        return result;
    }

    public Date getRecentSettingDate(Long uid, Long pid) {
        return settingMapper.findRecentSettingDate(uid, pid);
    }
}
