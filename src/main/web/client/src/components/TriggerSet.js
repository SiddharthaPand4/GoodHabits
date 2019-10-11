import React, {Component} from "react";
import {Slider, Button, Input} from "antd";
import TriggerService from "../services/TriggerService";

export default class TriggerSet extends Component {

    constructor(props) {
        super(props);
        this.days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
        this.onChange0 = this.onChange0.bind(this);
        this.onChange1 = this.onChange1.bind(this);
        this.onChange2 = this.onChange2.bind(this);
        this.onChange3 = this.onChange3.bind(this);
        this.onChange4 = this.onChange4.bind(this);
        this.onChange5 = this.onChange5.bind(this);
        this.onChange6 = this.onChange6.bind(this);
        this.onChangeValue = this.onChangeValue.bind(this);
        this.saveTrigger = this.saveTrigger.bind(this);
        this.deleteTrigger = this.deleteTrigger.bind(this);
        this.setName = this.setName.bind(this);

        this.state = {
            trigger : this.props.trigger,
            changed : false
        }
    }

    onChange0(value) { this.onChangeValue(0, value)    }
    onChange1(value) { this.onChangeValue(1, value)    }
    onChange2(value) { this.onChangeValue(2, value)    }
    onChange3(value) { this.onChangeValue(3, value)    }
    onChange4(value) { this.onChangeValue(4, value)    }
    onChange5(value) { this.onChangeValue(5, value)    }
    onChange6(value) { this.onChangeValue(6, value)    }

    onChangeValue(idx, value) {
        let trigger = this.state.trigger;
        trigger.TriggerDays[idx].Start = value[0];
        trigger.TriggerDays[idx].End = value[1];
        this.setState({trigger:trigger, changed: true})
    }

    saveTrigger() {

        if (this.state.trigger.new) {
            TriggerService.createTrigger(this.state.trigger)
        } else {
            TriggerService.saveTrigger(this.state.trigger)
        }

    }

    NewTrigger() {
        let trigger = {name:"New", TriggerDays: [], new:true};
        for (let i=0;i<7;i++) {
            trigger.TriggerDays.push({
                Start:0,
                End:24,
                Day:i,
            })
        }
        this.setState({trigger:trigger, changed: true})
    }

    setName(e, value) {
        let trigger = this.state.trigger;
        trigger.name = e.target.value;
        this.setState({trigger:trigger, changed: true});
    }

    deleteTrigger() {
        TriggerService.deleteTrigger(this.state.trigger)
    }

    ChangeTrigger(trigger) {
        this.setState({trigger:trigger, changed: false})
    }

    render() {
        let selected = this.state.trigger;
        let changed = this.state.changed;

        if (!selected) return (<div>Waiting..</div>);
        let marks = {};
        for (let i = 12; i <= 24; i++) {
            let j = i - 12;
            marks[j] = i + ':00'
        }

        for (let i = 0; i <= 12; i++) {
            let j = i + 12;
            marks[j] = (i < 10 ? '0' : '') + i + ':00'
        }


        selected.TriggerDays.map((day, idx) => {
            let start = day.Start;
            let end = day.End;
            if (start > end) {
                day.Start = start - 12;
                day.End = end + 12
            }
        });

        let dlist = selected.TriggerDays;

        return (
            <div>
                {selected.new ? (<Input placeholder="Name" onChange={this.setName}/>) : (<span>{selected.ID} - {selected.name}</span>)}
                <div>
                    <span>{this.days[dlist[0].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[0].Start, dlist[0].End]} onChange={this.onChange0}/>
                </div>

                <div>
                    <span>{this.days[dlist[1].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[1].Start, dlist[1].End]} onChange={this.onChange1}/>
                </div>

                <div>
                    <span>{this.days[dlist[2].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[2].Start, dlist[2].End]} onChange={this.onChange2}/>
                </div>

                <div>
                    <span>{this.days[dlist[3].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[3].Start, dlist[3].End]} onChange={this.onChange3}/>
                </div>

                <div>
                    <span>{this.days[dlist[4].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[4].Start, dlist[4].End]} onChange={this.onChange4}/>
                </div>

                <div>
                    <span>{this.days[dlist[5].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[5].Start, dlist[5].End]} onChange={this.onChange5}/>
                </div>

                <div>
                    <span>{this.days[dlist[6].Day]}</span>
                    <Slider min={0} max={24} range marks={marks} value={[dlist[6].Start, dlist[6].End]} onChange={this.onChange6}/>
                </div>

                <Button type="primary" disabled={!changed} onClick={this.saveTrigger}>Save</Button>
                <Button type="link" disabled={changed} onClick={this.deleteTrigger}>Delete</Button>
            </div>
        )
    }

}