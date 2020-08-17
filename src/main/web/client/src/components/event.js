const EventBus  = {
    events : {},
    publish: function (event, data) {
        if (!this.events[event]) return;
        this.events[event].forEach(callback => callback(data))
    },
    subscribe: function (event, callback) {
        console.log('subs called:', event);
        if (!this.events[event]) this.events[event] = [];
        this.events[event].push(callback)
    }
};

module.exports = {EventBus};