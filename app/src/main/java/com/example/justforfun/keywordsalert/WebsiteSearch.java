package com.example.justforfun.keywordsalert;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class WebsiteSearch {
    private Map<String, String> updateDict (Map<String,String> oldDict, Map<String,String> newDict) {
        Map<String, String> res= new HashMap<>();

        for (Map.Entry<String, String> entry : newDict.entrySet()) {
            if (oldDict.get(entry.getKey()) == null) {
                res.put(entry.getKey(), entry.getValue());
            }
        }
        return res;
    }

    private Map<String, String> checkWebsites(Set<String> keywords, Set<String> websites) {
        Map<String, String> updatedTopic= new HashMap<>();
        for (String url: websites)
        {
            if(url.length()==0)
                continue;
            try {
                Log.i("try url", url);
                Document doc=Jsoup.connect(url).get();
                Elements eles=doc.select("a");
                Log.i("success try",String.valueOf(eles.size()));
                for(Element ele: eles) {
                    for (String keyword : keywords) {
                        if(keyword.length()==0)
                            continue;
                        if (ele.text().toLowerCase().contains(keyword.toLowerCase())) {
                            String articleName = ele.text().trim();
                            String articleUrl = ele.absUrl("href");
                            updatedTopic.put(articleName, articleUrl);
                        }
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.e("try crawl error","");
            }
        }
        System.out.println(updatedTopic.size());
        return updatedTopic;
    }




    List<Map<String,String>> updateAlert(Map<String,String> oldDict, List<String> keywords, List<String> websites) {
        Set<String> keywordsSet= new HashSet<>(keywords);
        Set<String> websitesSet= new HashSet<>(websites);

        Map<String,String> newArticleDict= checkWebsites(keywordsSet, websitesSet);
        //  old Dictionary data read and store
        Map<String,String> updatedEntries= updateDict(oldDict, newArticleDict);

        List<Map<String,String>> res= new ArrayList<>();

        res.add(newArticleDict);
        res.add(updatedEntries);

        return res;
    }

}