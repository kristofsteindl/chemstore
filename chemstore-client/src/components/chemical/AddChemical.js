import React, { Component } from 'react'
import classNames from "classnames";
import axios from 'axios';
import Select from 'react-dropdown-select';
import { checkExpiry } from '../../utils/securityUtils';
import { connect } from 'react-redux';
import PropTypes from "prop-types";


class AddChemical extends Component {
    constructor() {
        super()
        this.state = {
            categories: [],
            shortName: "",
            exactName: "",
            categoryId: 0,
            errors: {}
        }
        this.onChange = this.onChange.bind(this)
        this.onSubmit = this.onSubmit.bind(this)
    }

    onChange(e) {
        this.setState({[e.target.name]: e.target.value})
    }


    async componentWillReceiveProps(nextProps){
        const selectedLab = nextProps.selectedLab
        this.handleChange(selectedLab)
    }

    componentDidMount() {
        const selectedLab = this.props.selectedLab
        this.handleChange(selectedLab)
    }

    handleChange(selectedLab) {
        checkExpiry()
        this.checkIfAdmin(selectedLab)
        this.loadCategories(selectedLab)
    }

    checkIfAdmin(selectedLab) {
        console.log(selectedLab.key)
        const isAdmin = (selectedLab.key) && 
                        (this.props.user.labsAsAdmin.includes(selectedLab.value) || 
                        selectedLab.labManagers.map(manager => manager.username).includes(this.props.user.username))
        if (!isAdmin) {
            this.props.history.push("/chemicals")
        }
    }

    async loadCategories(selectedLab) {
        if (selectedLab && selectedLab.key) {
            try {
                await axios.get(`/api/logged-in/chem-category/${selectedLab.value}`).then(result => this.setState({categories: result.data}))
            } catch (error) {
                console.log("error in get chem-categories: " + error)
                this.setState({ errors: {...this.state.errors, categoriesErrorStatus: error.response.status}})
            }
        }
    }

    async onSubmit(e) {
        checkExpiry()
        e.preventDefault()
        const newChemical = {
            labKey: this.props.selectedLab.key,
            shortName: this.state.shortName,
            exactName: this.state.exactName,
            categoryId: this.state.categoryId
        }
        try {
            await axios.post('/api/lab-admin/chemical', newChemical)
            this.props.history.push("/chemicals")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }

    render() {
        const {errors} = this.state
        return (
            <div className="add-chemical">
                <div className="container">
                    <div className="row">                        
                        <div className="col-md-8 m-auto">

                            <h1 className="display-4 text-center">Add Chemical</h1>
                            <p className="lead text-center">Create a chemical (for the whole account/company)</p>
                            <br/>
                            {
                                (errors.message && 
                                <div className="form-group row mb-3 invalid-feedback">
                                    {errors.message}
                                </div>)
                            }
                            <form onSubmit={this.onSubmit}>
                                <div className="form-group row mb-3">
                                    <label htmlFor="shortName" className="col-sm-4 col-form-label">short name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="shortName"
                                            value={this.state.shortName}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.shortName})} 
                                            placeholder="short name" 
                                        />
                                        {
                                            (errors.shortName && <div className="invalid-feedback">{errors.shortName}</div>)
                                        }
                                       
                                    </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="exactName" className="col-sm-4 col-form-label">exact name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="exactName"
                                            value={this.state.exactName}
                                            onChange={this.onChange}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.exactName})} 
                                            placeholder="exact name" 
                                        />
                                        {
                                            (errors.exactName && <div className="invalid-feedback">{errors.exactName}</div>)
                                        }
                                       
                                    </div>
                                </div>

                                <div className="form-group row mb-3">
                                    <label htmlFor="category" className="col-sm-4 col-form-label">category</label>
                                    <div className="col-sm-8">
                                      <Select
                                            options={this.state.categories}
                                            labelField="name"
                                            valueField="id"
                                            placeholder="category"
                                            searchable="true"
                                            searchBy="name"
                                            clearable="true"
                                            style={{height: "42px", fontSize: "16px"}}
                                            onChange={(items) => this.setState({categoryId: items[0].id })}
                                      />
                                        {
                                            (errors.chemType && <div className="invalid-feedback">{errors.chemType}</div>)
                                        }
                                       
                                    </div>
                                </div>    
                                <button type="submit" className="btn btn-info btn-block mt-4">Add Chemical</button>
                                
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

AddChemical.propTypes = {
    selectedLab: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab,
    user: state.security.user
})

export default connect(mapStateToProps) (AddChemical)

