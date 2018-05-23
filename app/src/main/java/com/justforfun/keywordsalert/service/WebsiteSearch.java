package com.justforfun.keywordsalert.service;

import android.util.Log;

import com.justforfun.keywordsalert.activity.MainActivity;
import com.justforfun.keywordsalert.entity.Result;
import com.justforfun.keywordsalert.entity.Setting;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class WebsiteSearch {
    private static final String TAG= "WebsiteSearch";

    private Set<String> keywords;
    private Set<String> websites;

    public WebsiteSearch() {
        syncWithMain();
    }

    private void syncWithMain(){
        Setting setting = MainActivity.getSetting();
        keywords = setting.getKeywords();
        websites = setting.getWebsites();
    }

    private Set<Result> checkWebsites() {

        Set<Result> newResult= new HashSet<>();
        for (String url: this.websites)
        {
            if(url.length()==0)
                continue;
            try {
                Log.i("try url", url);
                Document doc=Jsoup.connect(url).get();
                Elements elements=doc.select("a");
                Log.i("success try",String.valueOf(elements.size()));
                for(Element ele: elements) {
                    for (String keyword : this.keywords) {
                        if(keyword.length()==0)
                            continue;
                        if (ele.text().toLowerCase().contains(keyword.toLowerCase())) {
                            String articleName = ele.text().trim();
                            String articleUrl = ele.absUrl("href");
                            newResult.add(new Result(articleName, articleUrl));
                        }
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.e(TAG,"Crawling exception");
            }
        }

        return newResult;
    }

    public void updateResult() {

        syncWithMain();

        Log.d(TAG,">>>WBS: Starting search websites...");

        Set<Result> newResult = checkWebsites();
        MainActivity.setNewResult(newResult);

        Log.d(TAG,">>>new result size: " + newResult.size());

        // compare with old result list and update the differential result list

        Set<Result> oldResults = MainActivity.getOldResult();
        Set<Result> diffResults = new HashSet<>();

        for(Result result : newResult){
            if(!oldResults.contains(result))
                diffResults.add(result);
        }

        MainActivity.setDiffResult(diffResults);
        MainActivity.setOldResult(newResult);

    }


}