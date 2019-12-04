import axios from "axios";

function forceLogout() {
    localStorage.clear();
    window.location = '/login';
}

class HttpService {

    static Instance() {

        let ax = new axios.create({
            baseURL: "/",
            timeout: 5000,
            headers: {
                'Content-Type': 'application/json'
            }
        });

        ax.interceptors.request.use(config => {
            let token = JSON.parse(localStorage.getItem('syntoken'));
            if (token) {
                config.headers.Authorization = "Bearer " + token.token
            }
            return config
        });

        axios.interceptors.response.use(undefined, err => {
            if (err.response.config.url.includes('/login'))
                return Promise.reject(err);

            if (err.response.status === 403) {
                console.log('I am at 403');
                return forceLogout();
            }
            if (err.response.status !== 401) {
                console.log('I am at 401');
                return Promise.reject(err);
            }
        });

        return ax;
    }

}
//https://gist.github.com/alfonmga/96474f6adb6ed8dee8bc8bf8627c0ae1
export default HttpService.Instance()