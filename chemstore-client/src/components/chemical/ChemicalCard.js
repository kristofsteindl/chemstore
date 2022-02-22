import React, { Component } from 'react'
import DuButtons from '../UI/DuButtons'

export default class ChemicalCard extends Component {
    constructor(props) {
        super()
        this.state = {
            errors: {}
        }
        this.deleteChemical = props.deleteChemical
    }


    render() {
        const {chemical} = this.props
        return (
            <div className="container">
                <div className="card card-body bg-light mb-3" style={{padding: "10px"}}>
                    <div className="row" >
                        <div className="col-2">
                            <span className="mx-auto">{chemical.shortName}</span>
                        </div>
                        <div className="col-sm-7">
                            <h4>{chemical.exactName}</h4>
                            { this.drawCategory(chemical)}
                        </div>
                        <div className="col-sm-3">
                            { this.props.isAdmin && 
                                <DuButtons 
                                    updateFormTo={`update-chemical/${chemical.id}`}
                                    onDelete={() => this.deleteChemical(chemical)}
                                /> 
                               
                            }
                        </div>
                    </div>
                </div>
            </div>
        )
    }

    drawCategory(chemical) {
        if (chemical.category) {
            return (<i>{chemical.category.name}</i>)
        }
        return (<i>n.a</i>)
    }
}
