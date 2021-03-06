package com.single.mvvm.service;

import com.single.mvvm.entity.StoriesBean;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by kelin on 16-4-26.
 */
public interface TopNewsService {
    @GET("/api/4/news/latest")
    public Observable<News> getTopNewsList();

    public class News {

        private String date;

        private List<StoriesBean> stories;

        private List<TopStoriesBean> top_stories;

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

        public List<TopStoriesBean> getTop_stories() {
            return top_stories;
        }

        public void setTop_stories(List<TopStoriesBean> top_stories) {
            this.top_stories = top_stories;
        }

        public static class TopStoriesBean {
            private String image;
            private int type;
            private long id;
            private String ga_prefix;
            private String title;

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getGa_prefix() {
                return ga_prefix;
            }

            public void setGa_prefix(String ga_prefix) {
                this.ga_prefix = ga_prefix;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
