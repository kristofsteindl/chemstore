import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { connect } from 'react-redux'
import { createChemItem } from '../../actions/chemItemActions'

const labKey = "first-lab"

class AddChemItem extends Component {
    constructor(){
        super()
        this.state={
            chemicalShortName: "",
            manufacturerId: "",
            unit: "", 
            amount: 0,
            quantity: 0,
            batchNumber: "",
            expirationDateBeforeOpened: "",
            arrivalDate: new Date().toISOString().split('T')[0],
            errors: {}
        }
        this.onChange=this.onChange.bind(this)
        this.onSubmit=this.onSubmit.bind(this)
        console.log("TodaY is: " + this.state.arrivalDate)
    }
    onChange(e) {
        this.setState({ [e.target.name]: e.target.value})
    }

    onSubmit(e) {
        e.preventDefault()
        const newChemItem = {
            chemicalShortName: this.state.chemicalShortName,
            manufacturerId: this.state.manufacturerId,
            unit: this.state.unit,
            amount: this.state.amount,
            quantity: this.state.quantity,
            batchNumber: this.state.batchNumber,
            expirationDateBeforeOpened: this.state.expirationDateBeforeOpened,
            arrivalDate: this.state.arrivalDate
        }
        console.log(newChemItem)
        this.props.createChemItem(newChemItem, labKey, this.props.history)
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.errors){
            this.setState({errors:nextProps.errors})
        }
    }

    render() {
        const {errors} = this.state
        
        return (
            <div>
            
                <div className="project">
                    <div className="container">
                        <div className="row">
                            <div className="col-md-8 me-auto">
                                <h5 className="display-4 text-center">Add Chem Item</h5>
                                <hr />
                                <form onSubmit={this.onSubmit}>
                                

                                    <div className="form-group">
                                        <input 
                                            name="chemicalName" 
                                            value={this.state.chemicalName}
                                            onChange={this.onChange}
                                            type="text" 
                                            className="form-control form-control-lg " 
                                            placeholder="chemical short name" />
                                    </div>
                                    <div className="form-group">
                                        <input 
                                            name="manufacturerId" 
                                            value={this.state.manufacturerId}
                                            onChange={this.onChange}
                                            type="text" 
                                            className="form-control form-control-lg " 
                                            placeholder="manufacturer id" />
                                    </div>
                                    <div className="form-group">
                                        <input 
                                            name="batchNumber" 
                                            value={this.state.batchNumber}
                                            onChange={this.onChange}
                                            type="text" 
                                            className="form-control form-control-lg " 
                                            placeholder="batch number" />
                                    </div>
                                    <div className="form-group">
                                        <input 
                                            name="quantity" 
                                            value={this.state.quantity}
                                            onChange={this.onChange}
                                            type="number" 
                                            className="form-control form-control-lg " 
                                            placeholder="quantity" />
                                    </div>
                                    <div className="form-group">
                                        <input 
                                            name="unit" 
                                            value={this.state.unit}
                                            onChange={this.onChange}
                                            type="text" 
                                            className="form-control form-control-lg " 
                                            placeholder="unit" />
                                    </div>
                                    <div className="form-group">
                                        <input 
                                            name="amount" 
                                            value={this.state.amount}
                                            onChange={this.onChange}
                                            type="number" 
                                            className="form-control form-control-lg " 
                                            placeholder="amount" />
                                            <p>{errors.amount}</p>
                                    </div>
                                    <h6>Expiration date (before opened)</h6>
                                    <div className="form-group">
                                        <input 
                                            name="expirationDateBeforeOpened" 
                                            value={this.state.expirationDateBeforeOpened}
                                            onChange={this.onChange}
                                            type="date" 
                                            className="form-control form-control-lg" 
                                         />
                                    </div>
                                    <div className="form-group">
                                    <h6>Arrival date </h6>
                                    <input
                                            name="arrivalDate" 
                                            value={this.state.arrivalDate}
                                            onChange={this.onChange}
                                            type="date" 
                                            className="form-control form-control-lg" 
                                         />
                                    </div>


                                    <input type="submit" className="btn btn-primary btn-block mt-4" />
                                </form>
                            </div>
                        </div>
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
    errors: state.errors
})

export default connect(mapStateToProps, { createChemItem }) (AddChemItem)
