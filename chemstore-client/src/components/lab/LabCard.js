import React, { Component } from 'react'
import DuButtons from '../UI/DuButtons'

export default class LabCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteLab = props.deleteLab
    }


    render() {
        const {lab} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                    <div className="row" >
                        <div className="col-2">
                            <span className="mx-auto">{lab.key}</span>
                        </div>
                        <div className="col-sm-7">
                            <h4>{lab.name}</h4>
                            <i>({lab.labManagers.map(manager => manager.fullName).join(",")})</i>
                        </div>
                        <div className="col-sm-3">
                            {this.props.isAccountManager &&
                                <DuButtons 
                                    updateFormTo={`update-lab/${lab.id}`}
                                    onDelete={() => this.deleteLab(lab)}
                                /> 
                            }
                        </div>
                        
                    </div>
                </div>
            </div>
        )
    }

}
