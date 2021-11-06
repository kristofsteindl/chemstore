import React, { Component } from 'react'
import ChemItem from './ChemItem'
import RedirectFormButton from '../RedirectFormButton'

class ChemItemDashboard extends Component {
    render() {
        return (
            <div className="projects">
                <div className="container">
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Chem items</h1>
                            <br />
                            <RedirectFormButton formRoute="/add-chem-item" buttonLabel="Create Chem item"/>
                            <br />
                            <hr />
                            <ChemItem />
                        </div>
                    </div>
                </div>
            </div>
        )   
    }
}

export default ChemItemDashboard