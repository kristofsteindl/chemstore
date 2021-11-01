import Multiselect from 'multiselect-react-dropdown'
import React, { Component } from 'react'
import { createNewUser } from '../../actions/securityActions'
import PropTypes from "prop-types"
import { connect } from 'react-redux'
import classNames from "classnames";

class Register extends Component {
    constructor() {
        super()
        this.state = {
            username: "",
            fullName: "",
            labsAsUser: [], 
            labsAsAdmin: [],
            roles: [],
            password: "",
            password2: "",
            errors: {}
        }
        this.onChangeBasicInputs=this.onChangeBasicInputs.bind(this)
        this.labsAsUserOnChange=this.labsAsUserOnChange.bind(this)
        this.labsAsAdminOnChange=this.labsAsAdminOnChange.bind(this)
        this.rolesOnChange=this.rolesOnChange.bind(this)
        this.onSubmit=this.onSubmit.bind(this)
    }

    componentWillReceiveProps(nextProps){
        this.setState({errors: nextProps.errors});
    }

    onChangeBasicInputs(e) {
        this.setState({ [e.target.name]: e.target.value})
    }

    labsAsUserOnChange(selectedList, selectedItem) {
        this.setState({labsAsUser:selectedList})
    }

    labsAsAdminOnChange(selectedList, selectedItem) {
        this.setState({labsAsAdmin:selectedList})
    }

    rolesOnChange(selectedList, selectedItem) {
        this.setState({roles:selectedList})
        console.log(this.state)
    }

    onSubmit(e) {
        e.preventDefault()
        console.log(this.state)
        console.log(this.state.username)
        const newUser = {
            username: this.state.username,
            fullName: this.state.fullName,
            labKeysAsUser: this.state.labsAsUser.map(lab => lab.key), 
            labKeysAsAdmin: this.state.labsAsAdmin.map(lab => lab.key),
            roles: this.state.roles,
            password: this.state.password,
            password2: this.state.password2
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
                            <form onSubmit={this.onSubmit}>
                                <div className="form-group row mb-3">
                                    <label htmlFor="username" className="col-sm-2 col-form-label">username</label>
                                    <div class="col-sm-10">
                                        <input 
                                            name="username"
                                            value={this.state.username}
                                            onChange={this.onChangeBasicInputs}
                                            type="email" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.username})} 
                                            placeholder="username (email)" 
                                        />
                                        {
                                            errors.username && <div className="invalid-feedback">{errors.username}</div>
                                        }
                                    </div>
                                         
                                </div>
                                      
                                
                                <div className="form-group row mb-3">
                                    <label htmlFor="fullName" className="col-sm-2 col-form-label">Full name</label>
                                    <div class="col-sm-10">
                                        <input 
                                            name="fullName"
                                            value={this.state.fullName}
                                            onChange={this.onChangeBasicInputs}
                                            type="text" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.fullName})} 
                                            className="form-control form-control-lg" 
                                            placeholder="full name" 
                                            
                                            required 
                                            
                                        />
                                        {
                                            errors.fullName && <div className="invalid-feedback">{errors.fullName}</div>
                                        }
                                    </div>
                                </div>

                                
                                <div className="form-group row mb-3">
                                    <label htmlFor="password" class="col-sm-2 col-form-label">Password</label>
                                    <div class="col-sm-10">
                                        <input 
                                            name="password"
                                            value={this.state.password}
                                            onChange={this.onChangeBasicInputs}
                                            type="password" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.password})} 
                                            placeholder="password" 
                                        />
                                    </div>
                                    {
                                        errors.password && <div  className="invalid-feedback">{errors.password}</div>
                                    }
                                </div>

                                <div className="form-group row mb-5">
                                    <label htmlFor="password2" class="col-sm-2 col-form-label">Password</label>
                                    <div class="col-sm-10">
                                        <input 
                                            name="password2"
                                            value={this.state.password2}
                                            onChange={this.onChangeBasicInputs}
                                            type="password" 
                                            className={classNames("form-control form-control-lg", {"is-invalid": errors.password2})} 
                                            placeholder="password2" 
                                        />
                                    </div>
                                    {
                                        errors.password2 && <div className="invalid-feedback">{errors.password2}</div>
                                    }

                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="labsAsUser" class="col-sm-5 col-form-label">Labs, where user can administrate (open, use, etc) chemicals</label>
                                    <div class="col-sm-7">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='labs as user'
                                            onRemove={this.labsAsUserOnChange}
                                            onSearch={function noRefCheck(){}}
                                            onSelect={this.labsAsUserOnChange}
                                            closeOnSelect={false}
                                            options={[
                                                    {
                                                        "id": 3,
                                                        "key": "alab",
                                                        "name": "Alpha Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-18",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "alabman@account.com",
                                                            "ablabman@account.com"
                                                        ]
                                                    },
                                                    {
                                                        "id": 4,
                                                        "key": "blab",
                                                        "name": "Beta Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-18",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "blabman@account.com",
                                                            "ablabman@account.com"
                                                        ]
                                                    },
                                                    {
                                                        "id": 1,
                                                        "key": "first-lab",
                                                        "name": "First Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-05",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "kristof.steindl@account.com"
                                                        ]
                                                    },
                                                    {
                                                        "id": 2,
                                                        "key": "foo-lab",
                                                        "name": "Foo Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-05",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "foo@account.com"
                                                        ]
                                                    }
                                                ]}
                                            showCheckbox
                                            />
                                        </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="labsAsAdmin" className="col-sm-5 col-form-label">Labs, where user is administrator</label>
                                    <div class="col-sm-7">
                                        <Multiselect
                                            displayValue="name"
                                            placeholder='Admin in labs'
                                            onRemove={this.labsAsAdminOnChange}
                                            onSearch={function noRefCheck(){}}
                                            onSelect={this.labsAsAdminOnChange}
                                            closeOnSelect={false}
                                            options={[
                                                    {
                                                        "id": 3,
                                                        "key": "alab",
                                                        "name": "Alpha Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-18",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "alabman@account.com",
                                                            "ablabman@account.com"
                                                        ]
                                                    },
                                                    {
                                                        "id": 4,
                                                        "key": "blab",
                                                        "name": "Beta Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-18",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "blabman@account.com",
                                                            "ablabman@account.com"
                                                        ]
                                                    },
                                                    {
                                                        "id": 1,
                                                        "key": "first-lab",
                                                        "name": "First Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-05",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "kristof.steindl@account.com"
                                                        ]
                                                    },
                                                    {
                                                        "id": 2,
                                                        "key": "foo-lab",
                                                        "name": "Foo Lab",
                                                        "deleted": false,
                                                        "createdAt": "2021-09-05",
                                                        "updatedAt": null,
                                                        "labManagerUsernames": [
                                                            "foo@account.com"
                                                        ]
                                                    }
                                                ]}
                                            showCheckbox
                                        />
                                        </div>
                                </div>
                                <div className="form-group row mb-3">
                                    <label htmlFor="roles" class="col-sm-5 col-form-label">Additional roles</label>
                                    <div class="col-sm-7">
                                        <Multiselect
                                            displayValue="value"
                                            placeholder='Roles'
                                            onRemove={this.rolesOnChange}
                                            onSearch={function noRefCheck(){}}
                                            onSelect={this.rolesOnChange}
                                            closeOnSelect={false}
                                            options={[
                                                    {
                                                        key: 'ACCOUNT_ADMIN',
                                                        value: 'Account Admin'
                                                    },
                                                    {
                                                        key: 'FOO_ADMIN',
                                                        value: 'Foo Admin'
                                                    }
                                                ]}
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

Register.propTypes = {
    createNewUser: PropTypes.func.isRequired,
    errors: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    errors: state.errors
})

export default connect (mapStateToProps, {createNewUser}) (Register)
