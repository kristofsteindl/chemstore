import axios from 'axios'
import React, { Component } from 'react'
import { checkExpiry, refreshTokenAndUser } from '../../utils/securityUtils'
import RedirectFormButton from '../RedirectFormButton'
import NamedEntityCard from '../NamedEntityCard'
import ChemicalCard from './ChemicalCard'
import { connect } from 'react-redux'
import PropTypes from "prop-types";

class ChemicalDashboard extends Component {
    constructor() {
        super()
        this.state = {
            chemicals: [],
            errors: {
                deleted : {},
                chemicalsErrorStatus: ""}
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

    async componentDidMount() {
        checkExpiry()
        const selectedLab = this.props.selectedLab
        this.loadChemicals(selectedLab)
    }

    async componentWillReceiveProps(nextProps){
        const selectedLab = nextProps.selectedLab
        console.log("in componentWillReceiveProps " + JSON.stringify(selectedLab))
        this.loadChemicals(selectedLab)
    }


    async loadChemicals(selectedLab) {
        if (selectedLab && JSON.stringify(selectedLab) !== "{}") {
            try {
                await axios.get(`/api/logged-in/chemical/${selectedLab.value}`).then(result => this.setState({chemicals: result.data}))
            } catch (error) {
                console.log("error in get chemicals: " + error)
                this.setState({ errors: {...this.state.errors, chemicalsErrorStatus: error.response.status}})
            }

            console.log("in componentWillReceiveProps " + JSON.stringify(selectedLab))
        }
    }


    render() {
        const isAdmin = (this.props.selectedLab.key) && 
                        (this.props.user.labsAsAdmin.includes(this.props.selectedLab.value) || 
                        this.props.selectedLab.labManagers.map(manager => manager.username).includes(this.props.user.username))
        return (
            <div className="chemicals">
                <div className="container">
                    <div className="row"> 
                        <div className="col-md-12">
                            <h1 className="display-4 text-center">Chemicals</h1>
                            <p className="lead text-center">With the help of this pre-defined list of chemicals, you can easily register the concrete chemical items into your lab.</p>
                            <br />
                            { isAdmin && 
                                (<RedirectFormButton formRoute="/add-chemical" buttonLabel="Add Chemical"/>)
                            }
                            <br />
                            <hr />
                            {this.props.selectedLab ? 
                                this.getCards(isAdmin) :
                                <p className="lead"><i>Please select a lab</i></p>
                            }
                               
                           
                        </div>
                    </div>
                </div>
            </div>
        ) 
    }

    getCards(isAdmin) {
        return this.state.chemicals.map(chemical => (
                <ChemicalCard 
                    lab={this.props.selectedLab}
                    isAdmin={isAdmin}
                    chemical={chemical} 
                    key={chemical.id} 
                    deleteChemical={this.deleteChemical}
                    errors={this.state.errors.deleted["id" + chemical.id] ? this.state.errors.deleted["id" + chemical.id] : {}}
                />))
    }
}



ChemicalDashboard.propTypes = {
    selectedLab: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab,
    user: state.security.user
})

export default connect(mapStateToProps) (ChemicalDashboard)

