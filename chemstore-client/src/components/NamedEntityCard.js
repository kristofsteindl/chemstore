import React, { Component } from 'react'
import DuButtons from './UI/DuButtons'

export default class NamedEntityCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteNamedEntity = props.deleteNamedEntity
    }


    render() {
        const {namedEntity} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                    <div className="row" >
                        <div className="col-sm-9">
                        <h4>{namedEntity.name}</h4>
                        </div>
                        <div className="col-sm-3">
                            {this.props.isAdmin && 
                                <DuButtons 
                                    updateFormTo={`${this.props.updateUrl}/${namedEntity.id}`}
                                    onDelete={() => this.deleteNamedEntity(namedEntity)}
                                /> 
                            }
                            </div>
                    </div>
                </div>
            </div>
        )
    }
}
