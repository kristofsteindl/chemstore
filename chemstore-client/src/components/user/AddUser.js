import Multiselect from 'multiselect-react-dropdown'
import React, { Component } from 'react'
import { createNewUser } from '../../actions/accountAdminActions'
import PropTypes from "prop-types"
import { connect } from 'react-redux'
import classNames from "classnames";
import axios from "axios";
import { refreshTokenAndUser } from '../../securityUtils/securityUtils'


class AddUser extends Component {
    constructor() {
        super()
        this.state = {
            username: "",
            fullName: "",
            labsAsUser: [], 
            labsAsAdmin: [],
            roles: [],
            labs: [],
            errors: {}
        }
        this.onChangeBasicInputs=this.onChangeBasicInputs.bind(this)
        this.labsAsUserOnChange=this.labsAsUserOnChange.bind(this)
        this.labsAsAdminOnChange=this.labsAsAdminOnChange.bind(this)
        this.rolesOnChange=this.rolesOnChange.bind(this)
        this.onSubmit=this.onSubmit.bind(this)
    }

    componentDidMount() {
        refreshTokenAndUser()
        axios.get('/api/account/lab').then((results) => this.setState({ labs: results.data }));
        axios.get('/api/logged-in/role').then((results) => this.setState({ roles: results.data }));
    }

    componentWillReceiveProps(nextProps){
        refreshTokenAndUser()
        console.log(nextProps.errors)
        this.setState({errors: nextProps.errors});
    }

    onChangeBasicInputs(e) {
        this.setState({ [e.target.name]: e.target.value})
    }

    labsAsUserOnChange(selectedList, selectedItem) {
        
        this.setState({labsAsUser:selectedList})
    }

    labsAsAdminOnChange(selectedList, selectedItem) {
        console.log(this.state.labs)
        this.setState({labsAsAdmin:selectedList})
    }

    rolesOnChange(selectedList, selectedItem) {
        this.setState({roles:selectedList})
        console.log(this.state)
    }

    onSubmit(e) {
        e.preventDefault()
        const newUser = {
            username: this.state.username,
            fullName: this.state.fullName,
            labKeysAsUser: this.state.labsAsUser.map(lab => lab.key), 
            labKeysAsAdmin: this.state.labsAsAdmin.map(lab => lab.key),
            roles: this.state.roles
        }
        console.log(newUser)
         this.props.createNewUser(newUser, this.props.history)
    }


    render() {
        const {errors} = this.state
        return (
            <div className="register">
                <div className="container">
                    <div className="row">
                    
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Register</h1>
                            <p className="lead text-center">Add new user</p>
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
                                    <label htmlFor="username" className="col-sm-4 col-form-label">username</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="username"
                                            value={this.state.username}
                                            onChange={this.onChangeBasicInputs}
                                            type="email" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.username})} 
                                            placeholder="username (email)" 
                                            
                                        />
                                        
                                        {
                                            (errors.username && <div className="invalid-feedback">{errors.username}</div>)
                                        }
                                       
                                    </div>

                                         
                                </div>
                                      
                                
                                <div className="form-group row mb-3">
                                    <label htmlFor="fullName" className="col-sm-4 col-form-label">Full name</label>
                                    <div className="col-sm-8">
                                        <input 
                                            name="fullName"
                                            value={this.state.fullName}
                                            onChange={this.onChangeBasicInputs}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.fullName})} 
                                            placeholder="full name"  
                                        />
                                    {errors.fullName && (
                                        <div className="invalid-feedback">{errors.fullName}</div>
                                    )}
                                    </div>

                                </div>

                                <div className="form-group row mb-3">
                                    <label htmlFor="labsAsUser" className="col-sm-4 col-form-label">Labs, where user can administrate (open, use, etc) chemicals</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='labs as user'
                                            onRemove={this.labsAsUserOnChange}
                                            onSearch={function noRefCheck(){}}
                                            onSelect={this.labsAsUserOnChange}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.labs}
                                            showCheckbox
                                            />
                                        </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="labsAsAdmin" className="col-sm-4 col-form-label">Labs, where user is administrator</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='admin in labs'
                                            onRemove={this.labsAsAdminOnChange}
                                            onSearch={function noRefCheck(){}}
                                            onSelect={this.labsAsAdminOnChange}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.labs}
                                            showCheckbox
                                        />
                                        </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="roles" className="col-sm-4 col-form-label">Additional roles</label>
                                    <div className="col-sm-8">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='roles'
                                            onRemove={this.rolesOnChange}
                                            onSearch={function noRefCheck(){}}
                                            onSelect={this.rolesOnChange}
                                            closeOnSelect={false}
                                            style={{searchBox: {"fontSize": "20px"}}}
                                            options={this.state.roles}
                                            showCheckbox
                                        />
                                    </div>
                                </div>
                                
                                <input type="submit" className="btn btn-info btn-block mt-4" />
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

AddUser.propTypes = {
    createNewUser: PropTypes.func.isRequired,
    errors: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    errors: state.errors
})

export default connect (mapStateToProps, {createNewUser}) (AddUser)
