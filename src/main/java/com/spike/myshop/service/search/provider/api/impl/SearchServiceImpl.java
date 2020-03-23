package com.spike.myshop.service.search.provider.api.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.google.common.collect.Lists;
import com.spike.myshop.service.search.api.SearchService;
import com.spike.myshop.service.search.domain.TbItemResult;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service(version = "${services.versions.search.v1}")
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SolrClient solrClient;

    @Override
    public List<TbItemResult> search(String query, int page, int rows) {
        List<TbItemResult> tbItemResults = Lists.newArrayList();

        SolrQuery solrQuery = new SolrQuery();

        solrQuery.setQuery(query);

        solrQuery.setStart((page - 1) * rows);
        solrQuery.setRows(rows);

        solrQuery.set("df", "tb_item_keywords");

        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<span style='color:red;'>");
        solrQuery.setHighlightSimplePost("</span>");

        try {
            QueryResponse queryResponse = solrClient.query(solrQuery);
            SolrDocumentList results = queryResponse.getResults();
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();
            for (SolrDocument result : results) {
                TbItemResult tbItemResult = new TbItemResult();

                tbItemResult.setId(Long.parseLong(String.valueOf(result.get("id"))));
                tbItemResult.setTbItemCid(Long.parseLong(String.valueOf(result.get("tb_item_cid"))));
                tbItemResult.setTbItemCname((String) result.get("tb_item_cname"));
                tbItemResult.setTbItemTitle((String) result.get("tb_item_title"));
                tbItemResult.setTbItemSellPoint((String) result.get("tb_item_sell_point"));
                tbItemResult.setTbItemDesc((String) result.get("tb_item_desc"));

                String tbItemTitle = "";
                List<String> highlightResult = highlighting.get(result.get("id")).get("tb_item_title");
                if (highlightResult != null && !highlightResult.isEmpty()) {
                    tbItemTitle = highlightResult.get(0);
                    tbItemResult.setTbItemTitle(tbItemTitle);
                }

                tbItemResults.add(tbItemResult);
            }
        } catch (SolrServerException | IOException e) {
            e.printStackTrace();
        }

        return tbItemResults;
    }
}
