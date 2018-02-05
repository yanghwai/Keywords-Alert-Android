package com.example.justforfun.keywordsalert;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Created by yangh on 2018-02-03.
 */

public class WebsiteSearch {
    HashMap<String, String> updateDict (HashMap<String,String> oldDict, HashMap<String,String> newDict)
    {
        HashMap<String, String> res= new HashMap<>();

        for (Map.Entry<String, String> entry : newDict.entrySet()) {
            if (oldDict.get(entry.getKey()) == null) {
                res.put(entry.getKey(), entry.getValue());
            }
        }
        return res;
    }

    HashMap<String, String> checkWebsites(HashSet<String> keywords, HashSet<String> websites)
    {
        HashMap<String, String> updatedTopic= new HashMap<String, String>();
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

    boolean isValidEmail(String emailAddr)
    {
        //TODO email address validate
        return emailAddr.length()>=5;
    }

    void sendEmailAlert(HashMap<String, String> contentMap, String emailAddr)
    {

        GMailSender sender= new GMailSender("lithiumHack2018", "hack6666");

        String mssg="";

        Integer index=1;

        for( Map.Entry<String, String> entry: contentMap.entrySet())
        {
            mssg+= index.toString()+ ". "+ entry.getKey()+ "\n" + entry.getValue()+ "\n\n";
            index++;
        }

        try {
            sender.sendMail("Topic updates", mssg, "lithiumHack2018@gmail.com", emailAddr);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    ArrayList<HashMap<String,String>> updateAlert( HashMap<String,String> oldDict, ArrayList<String> keywords, ArrayList<String> websites,boolean emailOption, boolean popupOption, String emailAddr)
    {
        HashSet<String> keywordsSet= new HashSet<>(keywords);
        HashSet<String> websitesSet= new HashSet<>(websites);

        HashMap<String,String> newArticleDict= checkWebsites(keywordsSet, websitesSet);
        //  old Dictionary data read and store
        HashMap<String,String> updatedEntries= updateDict(oldDict, newArticleDict);

        ArrayList<HashMap<String,String>> res= new ArrayList<>();

        res.add(newArticleDict);
        res.add(updatedEntries);

        if (updatedEntries.size()>0)
        {
            if (emailOption && isValidEmail(emailAddr))
                System.out.println("call send email "+String.valueOf(emailOption));
            sendEmailAlert(updatedEntries, emailAddr);

        }

        return res;
    }


}