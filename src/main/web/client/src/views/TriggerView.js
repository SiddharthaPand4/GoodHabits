import React, {Component} from "react";
import {Select, Button} from 'antd';
import TriggerService from "../services/TriggerService";
import TriggerSet from "../components/TriggerSet";

const {Option} = Select;
export default class TriggerView extends Component {

    constructor(props) {
        super(props);

        this.state = {
            loading: true,
            triggers: []
        };

        this.tsRef = React.createRef();
        this.handleChange = this.handleChange.bind(this);
        this.newTrigger = this.newTrigger.bind(this);
    }

    componentDidMount() {
        TriggerService.getTriggers().then(request => {
            this.setState({
                triggers: request.data,
                selected: request.data[0].ID,
                selectedt: request.data[0],
                loading: false
            })
        });
    }

    newTrigger() {
        this.tsRef.current.NewTrigger();
    }

    handleChange(id) {

        console.log("changing to ...", id);

        for (let i = 0; i < this.state.triggers.length; i++) {
            console.log("checking:", this.state.triggers[i].ID);
            if (id == this.state.triggers[i].ID) {
                console.log("selected:", id);
                this.tsRef.current.ChangeTrigger(this.state.triggers[i]);
                this.setState({selected: id, selectedt: this.state.triggers[i]});
                return
            }
        }

        console.log("cant select:(")
    }

    render() {

        let selected = this.state.selected;
        let selectedt = this.state.selectedt;
        let triggers = this.state.triggers;

        if (this.state.loading) {
            triggers = [];
        }

        let selectedname = "";
        if (selectedt) {
            selectedname = selectedt.Name
        }

        console.log("SN", selectedname, selected, selectedt);
        let selector = (

            <div>
                <Select style={{width: 120}} onChange={this.handleChange}>
                    {
                        triggers.map((trigger, index) =>
                            <Option key={trigger.ID}>{trigger.name}</Option>
                        )
                    }

                </Select>
                &nbsp;
                <Button type="primary" onClick={this.newTrigger}>New</Button>
            </div>
        );

        if (selectedt) {
            return (
                <div>
                    {selector}
                    <TriggerSet trigger={selectedt} ref={this.tsRef}/>
                </div>

            )
        }

        return (
            selector
        )
    }
}