export function authHeader() {
    // return authorization header with jwt token
    let token = JSON.parse(localStorage.getItem('syntoken'));

    if (token) {
        return {
            'Authorization': 'Bearer ' + token,
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        };
    } else {
        return {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        };
    }
}