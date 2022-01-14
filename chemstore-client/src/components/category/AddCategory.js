import React, { Component } from 'react'
import classNames from "classnames";
import axios from 'axios';
import PropTypes from "prop-types";
import { connect } from 'react-redux';
import Select from 'react-dropdown-select';
import { checkExpiry, refreshTokenAndUser } from '../../utils/securityUtils';

class AddCategory extends Component {
    constructor() {
        super()
        this.state = {
            name: "",
            amount: 0,
            unit: "",
            errors: {}
        }
        this.onChange = this.onChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
        this.unitOnChanged = this.unitOnChanged.bind(this)
        this.unitOptions = [
            {
                value: "d",
                label: "day"
            },
            {
                value: "m",
                label: "month"
            },
            {
                value: "y",
                label: "year"
            },
        ]
    }

    componentDidMount() {
        checkExpiry()
    }

    unitOnChanged(justSelected) {
        this.setState({unit: justSelected[0]})
    }

    onChange(e) {
        this.setState({[e.target.name]: e.target.value})
    }

    async onSubmit(e) {
        e.preventDefault()
        const newCategory = {
            labKey: this.props.selectedLab.key,
            name: this.state.name,
            amount: this.state.amount,
            unit: this.state.unit.value,
        }
        try {
            await axios.post('/api/lab-admin/chem-category', newCategory)
            this.props.history.push("/categories")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }

    render() {
        const {errors} = this.state
        return (
            <div className="add-category">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Add chemical category</h1>
                            <p className="lead text-center">Create a chemical category with shelf life for the given lab</p>
                            <br/>
                            {
                                (errors.message && <h5 className="invalid-input">{errors.message}</h5>)
                            }
                            {
                                (errors.message && 
                                <div className="form-group row mb-3 invalid-feedback">
                                    {errors.message}
                                </div>)
                            }
                            <form onSubmit={this.onSubmit}>
                                <div className="form-group row mb-3">
                                    <label htmlFor="name" className="col-sm-4 col-form-label">name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="name"
                                            value={this.state.name}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.name})} 
                                            placeholder="chemical category name (e.g. salt)" 
                                        />
                                        {
                                            (errors.name && <div className="invalid-feedback">{errors.name}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                
                                <div className="row" >
                                    <label htmlFor="amount" className="col-sm-4 col-form-label">shelf life</label>
                                    
                                    <div className="col-sm-4">
                                        <input 
                                            name="amount"
                                            value={this.state.amount}
                                            onChange={this.onChange}
                                            type="number" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.amount})} 
                                            placeholder="chemical category name (e.g. salt)" 
                                        />
                                    </div>
                                    <div className="col-sm-4">
                                        <Select 
                                            name="amount"
                                            onChange={this.unitOnChanged}
                                            options={this.unitOptions} 
                                            placeholder="unit"
                                            style={{height: "42px", fontSize: "16px"}}

                                        />
                                    </div>
     
                                </div>
                                

                                <button type="submit" className="btn btn-info btn-block mt-4">Add Category</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

AddCategory.propTypes = {
    selectedLab: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab,
    user: state.security.user
})

export default connect(mapStateToProps) (AddCategory)
