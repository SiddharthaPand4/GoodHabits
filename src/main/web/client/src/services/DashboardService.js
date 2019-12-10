import axios from "./axios";
import moment from 'moment';

class DashboardService {

    static Instance() {
        return new DashboardService()
    }

    getTotalNoOfVehiclesBetweenTwoDates(filter) {
        return axios.post('/api/dashboard/vehiclescount/datefilter', filter);
    }

    getTotalNoOfVehiclesBySelectedDate(filter) {
        return axios.post('/api/dashboard/vehiclescount/date', filter);
    }

    getTotalNoOfVehiclesForLastMonths(filter) {
        return axios.post('/api/dashboard/vehiclescount/month', filter);
    }

    getAtccVehicleCount(fromDate, toDate, xAxis) {
        let filter = {
            from: moment(fromDate).format('YYYY-MM-DD HH:mm'),
            to: moment(toDate).format('YYYY-MM-DD HH:mm'),
            xAxis: xAxis
        };
        return axios.post('/api/dashboard/atcc/vehicle/count', filter);
    }

    enumerateDaysBetweenDates(startDate, endDate) {
        let dates = [];

        let currDate = moment(startDate).startOf('day');
        let lastDate = moment(endDate).startOf('day');

        let addition = 0;

        while (currDate.add(addition, 'days').diff(lastDate) <= 0) {

            dates.push(currDate.clone().format("YYYY-MM-DD"));
            addition = 1;
        }
        return dates;
    };

    getColor(index) {
        let color = "";
        let colors = [
            '#e83e8c',
            '#dc3545',
            '#007bff',
            '#6610f2',
            '#fd7e14',
            '#ffc107',
            '#28a745',
            '#20c997',
            '#17a2b8',
            '#6c757d',
            '#343a40',
            '#6f42c1'];

        color = colors[index];
        if (color) {
            return color;
        }
        return this.getRandomColor();

    }


    getRandomColor() {
        let letters = '0123456789ABCDEF';
        let color = '#';
        for (let i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }


    extractFromToDate(selectedCustomDateRange) {

        let baseDate;
        let from_date;
        let to_date;
        switch (selectedCustomDateRange) {
            case  "Today":
                baseDate = moment();
                from_date = baseDate.startOf('day').toDate();
                to_date = baseDate.endOf('day').toDate();
                break;
            case  "Yesterday":
                baseDate = moment().subtract(1, 'days');
                from_date = baseDate.startOf('day').toDate();
                to_date = baseDate.endOf('day').toDate();
                break;
            case  "This week":
                baseDate = moment();
                from_date = baseDate.startOf('week').toDate();
                to_date = baseDate.endOf('week').toDate();
                break;
            case  "This month":
                baseDate = moment();
                from_date = baseDate.startOf('month').toDate();
                to_date = baseDate.endOf('month').toDate();
                break;
            case  "This quarter":
                baseDate = moment();
                from_date = baseDate.startOf('quarter').toDate();
                to_date = baseDate.endOf('quarter').toDate();
                break;
            case  "This year":
                baseDate = moment();
                from_date = baseDate.startOf('year').toDate();
                to_date = baseDate.endOf('year').toDate();
                break;
            case  "Last week":
                baseDate = moment().subtract(1, 'weeks');
                from_date = baseDate.startOf('week').toDate();
                to_date = baseDate.endOf('week').toDate();
                break;
            case  "Last month":
                baseDate = moment().subtract(1, 'months');
                from_date = baseDate.startOf('month').toDate();
                to_date = baseDate.endOf('month').toDate();
                break;
            case  "Last quarter":
                baseDate = moment().subtract(1, 'quarters');
                from_date = baseDate.startOf('quarter').toDate();
                to_date = baseDate.endOf('quarter').toDate();
                break;
            case  "Last year":
                baseDate = moment().subtract(1, 'years');
                from_date = baseDate.startOf('year').toDate();
                to_date = baseDate.endOf('year').toDate();
                break;
            case  "Second Last week":
                baseDate = moment().subtract(2, 'weeks');
                from_date = baseDate.startOf('week').toDate();
                to_date = baseDate.endOf('week').toDate();
                break;
            case  "Second Last month":
                baseDate = moment().subtract(2, 'months');
                from_date = baseDate.startOf('month').toDate();
                to_date = baseDate.endOf('month').toDate();
                break;
            case  "Second Last quarter":
                baseDate = moment().subtract(2, 'quarters');
                from_date = baseDate.startOf('quarter').toDate();
                to_date = baseDate.endOf('quarter').toDate();
                break;
            case  "Second Last year":
                baseDate = moment().subtract(2, 'years');
                from_date = baseDate.startOf('year').toDate();
                to_date = baseDate.endOf('year').toDate();
                break;
        }

        return {
            from_date: from_date,
            to_date: to_date
        }
    }

}

export default DashboardService.Instance()