import axios from 'axios'
import classNames from 'classnames'
import React, { Component } from 'react'
import { check, checkIfAdmin } from '../../utils/securityUtils'
import Select from 'react-dropdown-select';
import { connect } from 'react-redux';
import PropTypes from "prop-types";

class UpdateChemical extends Component {
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
        axios.get(`/api/lab-admin/chemical/${this.props.match.params.id}`).then(result => this.setState({
            shortName: result.data.shortName,
            exactName: result.data.exactName,
            categoryId: result.data.category.id
        } ))
    }

    handleChange(selectedLab) {
        check()
        if (checkIfAdmin(selectedLab, this.props.user)) {
            this.loadCategories(selectedLab)
        } else {
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
        check()
        e.preventDefault()
        const updatedChemical = {
            labKey: this.props.selectedLab.key,
            shortName: this.state.shortName,
            exactName: this.state.exactName,
            categoryId: this.state.categoryId
        }
        try {
            await axios.put(`/api/lab-admin/chemical/${this.props.match.params.id}`, updatedChemical)
            this.props.history.push("/chemicals")
        } catch(error) {
            this.setState({errors: error.response.data})
        }
    }
    render() {
        const {errors} = this.state
        return (
            <div className="container">
                
                
                    <div className="col-md-8 m-auto">
                        <h1 className="display-4 text-center">Update Chemical</h1>
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
                                <label htmlFor="exactName" className="col-sm-4 col-form-label">category</label>
                                <div className="col-sm-8">
                                    <Select
                                            options={this.state.categories}
                                            labelField="name"
                                            values={this.state.categories.filter(category => category.id === this.state.categoryId)}
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
                            
                            <button type="submit" className="btn btn-info btn-block mt-4">Update Chemical</button>
                            
                        </form>
                    </div>
                
            </div>
        )
    }
}

UpdateChemical.propTypes = {
    selectedLab: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    selectedLab: state.selectedLab,
    user: state.security.user
})

export default connect(mapStateToProps) (UpdateChemical)
