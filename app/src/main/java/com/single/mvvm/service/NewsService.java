package com.single.mvvm.service;

import com.single.mvvm.entity.StoriesBean;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by kelin on 16-4-26.
 */
public interface NewsService {
    @GET("/api/4/news/before/{date}")
    public Observable<News> getNewsList(@Path("date") String date);

    public class News {

        @Override
        public String toString() {
            return "News{" +
                    "date='" + date + '\'' +
                    ", stories=" + stories +
                    '}';
        }

        private String date;

        private List<StoriesBean> stories;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<StoriesBean> getStories() {
            return stories;
        }

        public void setStories(List<StoriesBean> stories) {
            this.stories = stories;
        }
    }
}
