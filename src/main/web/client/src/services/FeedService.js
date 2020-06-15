import axios from './axios';
import {authHeader} from "../helpers/auth-header";
import {config} from "../helpers/config";
import FeedView from "../views/FeedView";

class FeedService {

    static Instance() {
        return new FeedService()
    }

    getFeeds() {
        return axios.get('/api/feed/list');
    }

    getFeed(url) {
        return axios.get('/api/feed/' +url);
    }

    addFeed(feed,flag)
    {
        const requestBody={
            url:feed.url,
            location:feed.location,
            site:feed.site,
            name:feed.name
        };
        const request = {
            method: 'POST',
            headers: authHeader(),
            data: JSON.stringify(requestBody),
            url: config.apiUrl + 'api/feed/'
        };
        if(flag){
            request.method='PUT';
        }
        return axios(request);
    }



    removeFeed(url) {
        return axios.delete('/api/feed/?url='+url);
    }


    startFeed(feed) {
        return axios.get('/api/feed/' +feed.ID+ '/start');
    }

    stopFeed(feed) {
        return axios.get('/api/feed/' +feed.ID+ '/stop');
    }
}

export default FeedService.Instance()
