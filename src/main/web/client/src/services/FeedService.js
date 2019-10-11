import axios from 'axios';

class FeedService {

    static Instance() {
        return new FeedService()
    }

    getFeeds() {
        return axios.get('/api/feeds');
    }

    getFeed(feed) {
        return axios.get('/api/feed/' + feed.ID);
    }

    addFeed(feed) {
        return axios.post('/api/feed/', feed)
    }

    removeFeed(feed) {
        return axios.delete('/api/feed/' + feed.ID);
    }

    startFeed(feed) {
        return axios.get('/api/feed/' +feed.ID+ '/start');
    }

    stopFeed(feed) {
        return axios.get('/api/feed/' +feed.ID+ '/stop');
    }
}

export default FeedService.Instance()
