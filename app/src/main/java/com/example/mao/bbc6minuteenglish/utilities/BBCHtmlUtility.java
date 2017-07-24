package com.example.mao.bbc6minuteenglish.utilities;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.example.mao.bbc6minuteenglish.data.BBCContentContract;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCHtmlUtility {

    // Base url of bbc
    private static final String BBC_URL = "http://www.bbc.co.uk";

    // Url of 6 minute English home page
    private static final String BBC_6_MINUTE_ENGLISH_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/6-minute-english";

    private BBCHtmlUtility(){}

    /**
     * Connect to bbc 6 minute English to get the newest document html.
     * Use Jsoup to parse the html to Elements which contains all contents.
     * @return list of all contents
     */
    @Nullable
    public static Elements getContentsList() {
        Elements elements;
        try{
            Document document = Jsoup.connect(BBC_6_MINUTE_ENGLISH_URL).get();
            elements = document.select(".widget-progress-enabled");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Elements contents = new Elements();
        contents.add(elements.first());
        contents.addAll(elements.get(1).select("li"));
        return contents;
    }

    /**
     * Get content's title
     * @param content the Element inside getContentsList()
     * @return title
     */
    public static String getTitle(Element content) {
        Elements texts = content.select(".text a");
        return texts.first().text();
    }

    /**
     * Get content's content's hyper link
     * @param content the Element inside getContentsList()
     * @return content's hyper link
     */
    public static String getArticleHref(Element content) {
        Elements texts = content.select(".text a");
        return BBC_URL + texts.attr("href");
    }

    /**
     * Get content's thumbnail's hyper link
     * @param content the Element inside getContentsList()
     * @return thumbnail's hyper link
     */
    public static String getImageHref(Element content) {
        Elements img = content.select(".img img");
        return img.attr("src");
    }

    /**
     * Get content's time
     * @param content the Element inside getContentsList()
     * @return time
     */
    public static String getTime(Element content){
        Elements details = content.select(".details");
        return details.select("h3").text();
    }

    /**
     * Get content's short description
     * @param content the Element inside getContentsList()
     * @return description
     */
    public static String getDescription(Element content){
        Elements details = content.select(".details");
        return details.select("p").text();
    }

    @Nullable
    public static Document getSpecificContent(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return document;
    }

    /**
     * Parse the article form specific content document
     * @param document
     * @return article's html format string
     */
    public static String getArticleHtml(Document document) {
        Elements article = document.select(".widget.widget-richtext.6 .text").first().children();
        return article.toString().replaceAll("h3", "h1");
    }

    /**
     * Parse the mp3 link from specific content document
     * @param document
     * @return mp3 link string
     */
    public static String getMp3Href(Document document) {
        Elements mp3Href = document.select(".download.bbcle-download-extension-mp3");
        return mp3Href.attr("href");
    }

    public static long getTimeStamp(Element content) {
        String time = getTime(content);
        //Timestamp timestamp = null;
        long timestamp = -1;
        try {
            time = time.split("/")[1].trim();
            DateFormat format = new SimpleDateFormat("dd MMM yyyy");
            //timestamp = new Timestamp(format.parse(time).getTime());
            timestamp = format.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public ContentValues getContentValues(Element content) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TITLE,
                BBCHtmlUtility.getTitle(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIME,
                BBCHtmlUtility.getTime(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_DESCRIPTION,
                BBCHtmlUtility.getDescription(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_HREF,
                BBCHtmlUtility.getArticleHref(content));
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_TIMESTAMP,
                BBCHtmlUtility.getTimeStamp(content));
        String imgHref = BBCHtmlUtility.getImageHref(content);
        Bitmap bitmap = DbBitmapUtility.getBitmapFromURL(imgHref);
        contentValues.put(BBCContentContract.BBC6MinuteEnglishEntry.COLUMN_THUMBNAIL,
                DbBitmapUtility.getBytes(bitmap));
        return contentValues;
    }
}
