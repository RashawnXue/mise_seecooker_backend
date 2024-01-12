package com.seecooker.recipe.service;

import com.alibaba.cloud.commons.io.FileUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.seecooker.recipe.service.dao.IngredientDao;
import com.seecooker.recipe.service.pojo.po.IngredientPO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
class RecipeServiceApplicationTests {
    @Autowired
    IngredientDao ingredientDao;

    @Test
    void contextLoads() {
    }

//    @Test
//    void init() throws IOException {
//        String path = "/Users/rashawn/Desktop/ingredients.json";
//        File f = new File(path);
//
//        log.info(f.getName());
//        JSONObject jsonObject = JSON.parseObject(FileUtils.readFileToString(f));
//        List<JSONObject> list = JSON.parseObject(jsonObject.getJSONArray("categories").toJSONString(), new TypeReference<>(){});
//
//        for (int i = 0 ; i < list.size() ; ++i) {
//            JSONObject ob = list.get(i);
//            String category = ob.getString("name");
//            log.info(category);
//            List<Map<String, String>> names = JSON.parseObject(ob.getJSONArray("ingredients").toJSONString(), new TypeReference<>(){});
//            for (Map<String, String> o : names) {
//                String name = o.get("name");
//                IngredientPO ingredientPO = IngredientPO.builder()
//                        .category(category)
//                        .name(name)
//                        .createTime(LocalDateTime.now())
//                        .updateTime(LocalDateTime.now())
//                        .build();
//                log.info(name);
//                ingredientDao.save(ingredientPO);
//            }
//        }
//    }

}
