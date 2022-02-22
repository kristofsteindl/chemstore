import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { createChemItem } from '../../actions/chemItemActions'
import { check, checkIfAdmin } from '../../utils/securityUtils'
import axios from 'axios'
import Select from 'react-dropdown-select'

class AddChemItem extends Component {
    constructor(){
        super()
        this.state={
            chemicals: [],
            manufacturers: [],
            units: [],
            chemicalShortName: "",
            manufacturerId: "",
            unit: "", 
            amount: 1,
            quantity: 0,
            batchNumber: "",
            expirationDateBeforeOpened: "",
            arrivalDate: new Date().toISOString().split('T')[0],
            errors: {}
        }
        this.onChange=this.onChange.bind(this)
        this.onSubmit=this.onSubmit.bind(this)
    }
    onChange(e) {
        check()
        this.setState({ [e.target.name]: e.target.value})
    }

    componentDidMount() {
        const selectedLab = this.props.selectedLab
        this.handleChange(selectedLab)
    }

    componentWillReceiveProps(nextProps) {
        const selectedLab = nextProps.selectedLab
        this.handleChange(selectedLab)
    }

    async onSubmit(e) {
        check()
        e.preventDefault()
        const newChemItem = {
            labKey: this.props.selectedLab.key,
            chemicalShortName: this.state.chemicalShortName,
            manufacturerId: this.state.manufacturerId,
            unit: this.state.unit,
            amount: this.state.amount,
            quantity: this.state.quantity,
            batchNumber: this.state.batchNumber,
            expirationDateBeforeOpened: this.state.expirationDateBeforeOpened,
            arrivalDate: this.state.arrivalDate
        }
        try {
            await axios.post(`/api/chem-item`, newChemItem)
            this.props.history.push('/chem-items')
        } catch (error) {
            this.setState({errors: error.response.data})
        }
    }


    handleChange(selectedLab) {
        check()
        if (checkIfAdmin(selectedLab, this.props.user)) {
            this.loadDropDowns(selectedLab)
        } else {
            this.props.history.push("/chem-items")
        }
    }

    loadDropDowns(selectedLab) {
        if (selectedLab && selectedLab.key) {
            axios.get(`/api/logged-in/chemical/${selectedLab.key}`)
                .then(result => this.setState({chemicals: result.data}))
                .catch(error =>  this.setState({ errors: {...this.state.errors, chemicalsErrorStatus: error.response.status}}))
            axios.get(`/api/logged-in/manufacturer`)
                .then(result => this.setState({manufacturers: result.data}))
                .catch(error =>  this.setState({ errors: {...this.state.errors, manufacturersErrorStatus: error.response.status}}))
            axios.get(`/api/chem-item/unit`)
                .then(result => this.setState({units: result.data.map(unit => {return {"unit": unit}})}))
                .catch(error =>  this.setState({ errors: {...this.state.errors, unitsErrorStatus: error.response.status}}))
            
        }
    }



    render() {
        
        return (
            <div className="container">
                <div className="row">
                    <div className="col-md-8 me-auto">
                        <h5 className="display-4 text-center">{`Register chemical into ${this.props.selectedLab.name}`}</h5>
                        <hr />
                        <br/>
                        <form onSubmit={this.onSubmit}>
                        

                            <div className="form-group row mb-3">
                                <label htmlFor="chemical" className="col-sm-4 col-form-label">chemical</label>
                                <div className="col-sm-8">
                                    <Select
                                        options={this.state.chemicals}
                                        labelField="shortName"
                                        valueField="shortName"
                                        placeholder="chemical"
                                        searchable="true"
                                        searchBy="shortName"
                                        clearable="true"
                                        style={{height: "42px", fontSize: "16px"}}
                                        onChange={(items) => this.setState({chemicalShortName: items[0].shortName })}
                                    />
                                </div>
                            </div>


                            <div className="form-group row mb-3">
                                <label htmlFor="manufacturer" className="col-sm-4 col-form-label">manufacturer</label>
                                <div className="col-sm-8">
                                    <Select
                                        options={this.state.manufacturers}
                                        labelField="name"
                                        valueField="name"
                                        placeholder="manufacturer"
                                        searchable="true"
                                        searchBy="name"
                                        clearable="true"
                                        style={{height: "42px", fontSize: "16px"}}
                                        onChange={(items) => this.setState({manufacturerId: items[0].id })}
                                    />
                                </div>
                            </div>

                            <div className="form-group row mb-3">
                                <label htmlFor="batchNumber" className="col-sm-4 col-form-label">batch number</label>
                                <div className="col-sm-8">
                                   <input 
                                        name="batchNumber" 
                                        value={this.state.batchNumber}
                                        onChange={this.onChange}
                                        type="text" 
                                        className="form-control form-control-lg " 
                                        placeholder="batch number" />
                                </div>
                            </div>

                            <div className="form-group row mb-3">
                                <label htmlFor="quantity" className="col-sm-4 col-form-label">quantity</label>
                                <div className="col-sm-8">
                                   <input 
                                        name="quantity" 
                                        value={this.state.quantity}
                                        onChange={this.onChange}
                                        type="text" 
                                        className="form-control form-control-lg " 
                                        placeholder="quantity" />
                                </div>
                            </div>

                            <div className="form-group row mb-3">
                                <label htmlFor="unit" className="col-sm-4 col-form-label">unit</label>
                                <div className="col-sm-8">
                                    <Select
                                        options={this.state.units}
                                        labelField="unit"
                                        placeholder="unit"
                                        valueField="unit"
                                        searchable="true"
                                        clearable="true"
                                        style={{height: "42px", fontSize: "16px"}}
                                        onChange={(items) => this.setState({unit: items[0].unit })}
                                    />
                                </div>
                            </div>

                            <div className="form-group row mb-3">
                                <label htmlFor="amount" className="col-sm-4 col-form-label">amount</label>
                                <div className="col-sm-8">
                                   <input 
                                        name="amount" 
                                        value={this.state.amount}
                                        onChange={this.onChange}
                                        type="text" 
                                        className="form-control form-control-lg " 
                                        placeholder="amount" />
                                </div>
                            </div>

                            <div className="form-group row mb-3">
                                <label htmlFor="expirationDateBeforeOpened" className="col-sm-4 col-form-label">Expiration date (before opened)</label>
                                <div className="col-sm-8">
                                   <input 
                                        name="expirationDateBeforeOpened" 
                                        value={this.state.expirationDateBeforeOpened}
                                        onChange={this.onChange}
                                        type="date" 
                                        className="form-control form-control-lg " 
                                     />
                                </div>
                            </div>

                            <div className="form-group row mb-3">
                                <label htmlFor="arrivalDate" className="col-sm-4 col-form-label">Arrival date</label>
                                <div className="col-sm-8">
                                   <input 
                                        name="arrivalDate" 
                                        value={this.state.arrivalDate}
                                        onChange={this.onChange}
                                        type="date" 
                                        className="form-control form-control-lg " 
                                     />
                                </div>
                            </div>


                            <input type="submit" className="btn btn-primary btn-block mt-4" />
                        </form>
                    </div>
                </div>
            </div>

        )
    }
}

AddChemItem.propTypes = {
    createChemItem: PropTypes.func.isRequired,
    errors: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    errors: state.errors,
    selectedLab: state.selectedLab,
    user: state.security.user
})

export default connect(mapStateToProps, { createChemItem }) (AddChemItem)
