import React, { Component } from 'react'
import PropTypes from "prop-types"
import { connect } from "react-redux"
import classNames from 'classnames'
import { login } from '../../actions/securityActions'

class Login extends Component {
    constructor() {
        super()
        this.state = {
            username: "",
            password: ""
        }
        this.onChange=this.onChange.bind(this)
        this.onSubmit=this.onSubmit.bind(this)
    }
    onChange(event) {
        this.setState({[event.target.name]: event.target.value})
    }
    onSubmit(event) {
        event.preventDefault()
        const loginRequest = {
            username: this.state.username,
            password: this.state.password
        }
        this.props.login(loginRequest, this.props.history)
    }

    componentWillReceiveProps(nextProps){
        if (nextProps.security.validToken) {
            this.props.history.push("/dashboard")
        }
        this.setState({errors: nextProps.errors});
    }

    render() {
        return (
            <div className="login">
                <div className="container">
                    <div className="row">
                        <div className="col-md-8 m-auto">
                            <h1 className="display-4 text-center">Log In</h1>
                            <form onSubmit={this.onSubmit}>
                                <div className="form-group mb-3">
                                    <input 
                                        name="username" 
                                        value={this.state.username}
                                        onChange={this.onChange}
                                        type="email" 
                                        className="form-control form-control-lg"
                                        placeholder="Email Address" 
                                    />
                                </div>
                                <div className="form-group mb-3">
                                    <input 
                                        name="password"
                                        value={this.state.password}
                                        onChange={this.onChange}
                                        type="password" 
                                        className="form-control form-control-lg" 
                                        placeholder="Password" 
                                     />
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

Login.propTypes = {
    login: PropTypes.func.isRequired,
    errors: PropTypes.object.isRequired
}

const mapSateToProps = state => ({
    security: state.security,
    errors: state.errors
})

export default connect(mapSateToProps, {login}) (Login) 
