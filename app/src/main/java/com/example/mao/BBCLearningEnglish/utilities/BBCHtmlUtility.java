package com.example.mao.BBCLearningEnglish.utilities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.mao.BBCLearningEnglish.R;
import com.example.mao.BBCLearningEnglish.data.BBCArticleSection;
import com.example.mao.BBCLearningEnglish.data.BBCCategory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MAO on 7/17/2017.
 */

public class BBCHtmlUtility {

    // Base url of bbc
    private static final String BBC_URL = "http://www.bbc.co.uk";

    // Url of 6 minute English home page
    public static final String BBC_6_MINUTE_ENGLISH_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/6-minute-english";

    public static final String BBC_NEWS_REPORT_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/news-report";

    public static final String BBC_THE_ENGLISH_WE_SPEAK_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/the-english-we-speak";

    public static final String BBC_ENGLISH_AT_UNIVERSITY_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/english-at-university";

    public static final String BBC_LINGO_HACK_URL =
            "http://www.bbc.co.uk/learningenglish/english/features/lingohack";

    /**
     * Connect to bbc 6 minute English to get the newest document html.
     * Use Jsoup to parse the html to Elements which contains all contents.
     * @return list of all contents
     */
    @Nullable
    public static Elements getContentsList(String contentHtml) {
        Document document = Jsoup.parse(contentHtml);
        Elements elements = document.select(".widget-progress-enabled");
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
    @NonNull
    public static String getTime(Element content){
        Elements details = content.select(".details");
        String time = details.select("h3").text();
        return time.split("/")[1].trim();
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

    /**
     * Parse the article form specific content document
     * @param document
     * @return article's html format string
     */
    public static String getArticleHtml(Document document) {
        Elements article = document.select(".widget.widget-richtext.6 .text").first().children();
        return article.toString();
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
        return TimeUtility.getTimeStamp(time);
    }

    public static Document getArticleDocument(String articleHtml) {
        return Jsoup.parse(articleHtml);
    }

    public static List<BBCArticleSection> getArticleSection(Context context,
                                                            String html,
                                                            String category) {
        List<BBCArticleSection> list = new ArrayList<>();
        String[] splitResult = html.split("</?h3>");
        try{
            switch (category){
                case BBCCategory.CATEGORY_6_MINUTE_ENGLISH:
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_question))
                            .setArticle(splitResult[2])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_vocabulary))
                            .setArticle(splitResult[4])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_transcript))
                            .setArticle(splitResult[6])
                    );
                    break;
                case BBCCategory.CATEGORY_THE_ENGLISH_WE_SPEAK:
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_summary))
                            .setArticle(splitResult[2])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_transcript))
                            .setArticle(splitResult[4])
                    );
                    break;
                case BBCCategory.CATEGORY_NEWS_REPORT:
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_question))
                            .setArticle(splitResult[2])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_key_words))
                            .setArticle(splitResult[4])
                    );
                    String newsReport = splitResult[6];
                    if (splitResult.length > 8) {
                        newsReport += "\n<p><Strong>"
                                + splitResult[7]
                                + "</strong></p>\n"
                                + splitResult[8];
                    }
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_transcript))
                            .setArticle(newsReport)
                    );
                    break;
                case BBCCategory.CATEGORY_ENGLISH_AT_UNIVERSITY:
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_summary))
                            .setArticle(splitResult[2])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_focus))
                            .setArticle(splitResult[4])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_vocabulary))
                            .setArticle(splitResult[6])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_transcript))
                            .setArticle(splitResult[8])
                    );
                    break;
                case BBCCategory.CATEGORY_LINGO_HACK:
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_headlines))
                            .setArticle(splitResult[2])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_vocabulary))
                            .setArticle(splitResult[6])
                    );
                    list.add(new BBCArticleSection()
                            .setTitle(context.getString(R.string.article_transcript))
                            .setArticle(splitResult[4])
                    );
                    //more
                    break;
                default:
                    break;
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return list;
    }
}
