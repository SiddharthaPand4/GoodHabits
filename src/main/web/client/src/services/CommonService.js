

class CommonService {

    static Instance() {
        return new CommonService()
    }

    ifExist(array, attr, value) {
        for (let i = 0; i < array.length; i += 1) {
            if (array[i][attr] === value) {
                return true;
            }
        }
        return false;
    }

    findIndex(array, attr, value) {
        if (!array) {
            return -1;
        }
        for (let i = 0; i < array.length; i += 1) {
            if (array[i][attr] === value) {
                return i;
            }
        }
        return -1;
    }
}

export default CommonService.Instance()