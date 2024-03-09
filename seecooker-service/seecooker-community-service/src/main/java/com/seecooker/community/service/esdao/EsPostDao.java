package com.seecooker.community.service.esdao;

import com.seecooker.community.service.pojo.po.EsPostPO;
import com.seecooker.community.service.pojo.po.PostPO;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsPostDao extends ElasticsearchRepository<EsPostPO, Long> {
    @Highlight(fields = {
            @HighlightField(name = "content"),
            @HighlightField(name = "title")
    })
//    @Query("{\"bool\":{\"should\":[{\"multi_match\": {\"query\": \"?0\", \"fields\":[\"title\", \"content\"]}}]}}")
    @Query("{\"multi_match\":{\"query\":\"叔叔\", \"fields\":[\"title\", \"content\"]}}")
    SearchHits<EsPostPO> find(String keyword);
}
