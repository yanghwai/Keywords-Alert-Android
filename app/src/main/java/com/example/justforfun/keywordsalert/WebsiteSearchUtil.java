package com.example.justforfun.keywordsalert;

import android.util.Log;

import com.example.justforfun.keywordsalert.models.Result;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class WebsiteSearchUtil {
    private ArrayList<Result> checkWebsites(Set<String> keywords, Set<String> websites){
        ArrayList<Result> resList  = new ArrayList<>();

        for(String url:websites){
            try{
                Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(3000).get();
                Elements eles = doc.select("a[href]");
                for(Element ele:eles){
                    for(String keyword:keywords){
                        if(ele.text().toLowerCase().contains(keyword.toLowerCase())){
                            String title = ele.text().trim();
                            String webLink = ele.absUrl("href");
                            resList.add(new Result(title,webLink));
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return resList;
    }

    public int updateResults(ArrayList<Result> oldResList,Set<String> keywords, Set<String> websites){
        ArrayList<Result> newResList = checkWebsites(keywords,websites);
        Log.e("qiao","search and compare");
        int newNum = 0;
        for(Result res:newResList){
            if(!oldResList.contains(res)){
                newNum++;
            }
        }
        if(newNum !=0){
            oldResList.clear();
            oldResList.addAll(newResList);
        }
        return newNum;
    }
}
