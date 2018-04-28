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
import java.util.regex.Pattern;


/**
 * Created by yangh on 2018-02-03.
 * modified on 03-25
 */

class WebsiteSearch {
    private Map<String, String> updateDict (Map<String,String> oldDict, Map<String,String> newDict)
    {
        Map<String, String> res= new HashMap<>();

        for (Map.Entry<String, String> entry : newDict.entrySet()) {
            if (oldDict.get(entry.getKey()) == null) {
                res.put(entry.getKey(), entry.getValue());
            }
        }
        return res;
    }

    private Map<String, String> checkWebsites(Set<String> keywords, Set<String> websites)
    {
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

    private boolean isValidEmail(String emailAddr)
    {
        final String emailPattern="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return Pattern.compile(emailPattern).matcher(emailAddr).matches();
    }

    private void sendEmailAlert(Map<String, String> contentMap, String emailAddr)
    {

        GMailSender sender= new GMailSender("lithiumHack2018", "hack6666");

        StringBuilder mssgStr=new StringBuilder();
        //String mssg="";

        Integer index=1;

        for( Map.Entry<String, String> entry: contentMap.entrySet())
        {
            mssgStr.append(index).append(". ").append(entry.getKey()).append("\n").append(entry.getValue()).append("\n\n");
          //  mssg+= index.toString()+ ". "+ entry.getKey()+ "\n" + entry.getValue()+ "\n\n";
            index++;
        }

        try {
            sender.sendMail("Topic updates", mssgStr.toString(), "lithiumHack2018@gmail.com", emailAddr);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    List<Map<String,String>> updateAlert(Map<String,String> oldDict, List<String> keywords, List<String> websites, boolean emailOption, String emailAddr)
    {
        Set<String> keywordsSet= new HashSet<>(keywords);
        Set<String> websitesSet= new HashSet<>(websites);

        Map<String,String> newArticleDict= checkWebsites(keywordsSet, websitesSet);
        //  old Dictionary data read and store
        Map<String,String> updatedEntries= updateDict(oldDict, newArticleDict);

        List<Map<String,String>> res= new ArrayList<>();

        res.add(newArticleDict);
        res.add(updatedEntries);

        if (updatedEntries.size()>0)
        {
            if (emailOption && isValidEmail(emailAddr))
            {
                System.out.println("call send email ");
                sendEmailAlert(updatedEntries, emailAddr);
            }
        }

        return res;
    }

}