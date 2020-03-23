package com.spike.myshop.service.search.provider.test;

import com.spike.myshop.service.search.domain.TbItemResult;
import com.spike.myshop.service.search.provider.MyShopServiceProviderApplication;
import com.spike.myshop.service.search.provider.mapper.TbItemResultMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyShopServiceProviderApplication.class)
public class SearchServiceTest {

    @Autowired
    private SolrClient solrClient;

    @Autowired
    private TbItemResultMapper tbItemResultMapper;

    @Test
    public void testInitSolr() {
        List<TbItemResult> tbItemResults = tbItemResultMapper.selectAll();

        for (TbItemResult tbItemResult : tbItemResults) {
            System.out.println(tbItemResult.getId());
        }
    }

    @Test
    public void addDocument() {
        SolrInputDocument solrInputDocument = new SolrInputDocument();
        solrInputDocument.addField("id", 562379);

        try {
            solrClient.add(solrInputDocument);
            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteDocument() {
        try {
            solrClient.deleteByQuery("*:*");
            solrClient.commit();
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void initSolr() {
        List<TbItemResult> tbItemResults = tbItemResultMapper.selectAll();
        SolrInputDocument solrInputDocument;
        for (TbItemResult tbItemResult : tbItemResults) {
            solrInputDocument = new SolrInputDocument();
            solrInputDocument.addField("id", tbItemResult.getId());
            solrInputDocument.addField("tb_item_cid", tbItemResult.getTbItemCid());
            solrInputDocument.addField("tb_item_cname", tbItemResult.getTbItemCname());
            solrInputDocument.addField("tb_item_title", tbItemResult.getTbItemTitle());
            solrInputDocument.addField("tb_item_sell_point", tbItemResult.getTbItemSellPoint());
            solrInputDocument.addField("tb_item_desc", tbItemResult.getTbItemDesc());

            try {
                solrClient.add(solrInputDocument);
                solrClient.commit();
            } catch (SolrServerException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void queryDocument() {
        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery("手机");

        solrQuery.setStart(0);
        solrQuery.setRows(10);

        solrQuery.set("df", "tb_item_keywords");

        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<span style='color:red;'>");
        solrQuery.setHighlightSimplePost("</span>");

        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);
            SolrDocumentList results = queryResponse.getResults();
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            Set<String> ids = highlighting.keySet();
            for (String id : ids) {
                Map<String, List<String>> listMap = highlighting.get("id");
                Set<String> keys = listMap.keySet();
                for (String key : keys) {
                    List<String> highlightResults = listMap.get(key);
                    for (String highlightResult : highlightResults) {
                        System.out.println(String.format("id:%s, key:%s value:%s", id, key, highlightResult));
                    }

                }
            }
            for (SolrDocument result : results) {
                System.out.println(result.get("id"));
            }


        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }
    }
}
