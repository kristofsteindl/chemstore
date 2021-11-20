import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import NamedEntityCard from '../NamedEntityCard'
import ChemicalCard from './ChemicalCard'

export default class LabDashboard extends Component {
    constructor() {
        super()
        this.state = {
            labs: [],
            errors: {deleted : {}}
        }
        this.deleteLab=this.deleteLab.bind(this)
    }


    async deleteLab(lab) {
        const id = lab.id
        if (window.confirm(`Are you sure you want to delete \'${lab.name}\' (${lab.key})?`)) {
            try {
                await axios.delete(`/api/account/lab/${id}`)
                const refreshedLabs = this.state.labs.filter(labFromList => labFromList.id !== id)
                this.setState({labs: refreshedLabs})
            } catch (error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }

        }
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/account/lab').then(result => this.setState({labs: result.data}))
    }

    render() {
        return (
            <div className="labs">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Labs</h1>
                            <br />
                            <RedirectFormButton formRoute="/add-chemical" buttonLabel="Add Chemical"/>
                            <br />
                            <hr />
                            {this.state.chemicals.map(chemical => (
                                <ChemicalCard 
                                    chemical={chemical} 
                                    key={chemical.id} 
                                    deleteChemical={this.deleteLab}
                                    errors={this.state.errors.deleted["id" + chemical.id] ? this.state.errors.deleted["id" + chemical.id] : {}}
                                />
                            ))
                                
                            }
                           
                        </div>
                    </div>
                </div>
            </div>
        ) 
    }
}

