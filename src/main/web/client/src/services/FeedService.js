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

    getFeed(FeedId) {
        return axios.get('/api/feed/' +FeedId);
    }

    addFeed(feed,flag)
    {
        const requestBody={
            url:feed.url,
            location:feed.location,
            site:feed.site,
            name:feed.name,
            id:feed.id
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



    removeFeed(FeedId) {
        return axios.delete('/api/feed/'+FeedId);
    }


   startFeed(feedId) {
       return axios.get('/api/feed/start?feedId='+ feedId);
   }

   stopFeed(feedId) {
       return axios.get('/api/feed/stop?feedId='+ feedId);
   }
}

export default FeedService.Instance()
