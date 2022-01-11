import axios from 'axios'
import React, { Component } from 'react'
import { refreshTokenAndUser } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import NamedEntityCard from '../NamedEntityCard'
import ChemicalCard from './ChemicalCard'

export default class ChemicalDashboard extends Component {
    constructor() {
        super()
        this.state = {
            chemicals: [],
            errors: {deleted : {}}
        }
        this.deleteChemical=this.deleteChemical.bind(this)
    }


    async deleteChemical(chemical) {
        const id = chemical.id
        if (window.confirm(`Are you sure you want to delete \'${chemical.shortName}\' (${chemical.exactName})?`)) {
            try {
                await axios.delete(`/api/lab-admin/chemical/${id}`)
                const refreshedChemical = this.state.chemicals.filter(chemicalFromList => chemicalFromList.id !== id)
                this.setState({chemicals: refreshedChemical})
            } catch (error) {
                this.setState({ errors: {deleted: {["id" + id]: error.response.data}}})
            }

        }
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/lab-admin/chemical').then(result => this.setState({chemicals: result.data}))
    }

    render() {
        return (
            <div className="chemicals">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Chemicals</h1>
                            <br />
                            <RedirectFormButton formRoute="/add-chemical" buttonLabel="Add Chemical"/>
                            <br />
                            <hr />
                            {this.state.chemicals.map(chemical => (
                                <ChemicalCard 
                                    chemical={chemical} 
                                    key={chemical.id} 
                                    deleteChemical={this.deleteChemical}
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

