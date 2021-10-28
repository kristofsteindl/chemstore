import React, { Component } from 'react'
import ChemItem from './chemitem/ChemItem'
import CreateChemItemButton from './chemitem/CreateChemItemButton'

class Dashboard extends Component {
    render() {
        return (
            <div className="projects">
                <div className="container">
                    <div className="row">
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Projects</h1>
                            <br />
                            <CreateChemItemButton />
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

export default Dashboard