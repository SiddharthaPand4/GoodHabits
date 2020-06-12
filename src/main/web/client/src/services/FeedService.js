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
        return axios.get('/api/feed/' + url);
    }

    addFeed(feed) {
        return axios.post('/api/feed/', feed)
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
