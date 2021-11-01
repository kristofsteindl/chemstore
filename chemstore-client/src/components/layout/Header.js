import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import PropTypes from "prop-types"
import { connect } from 'react-redux'
import { logoutDispatch } from '../../securityUtils/securityUtils'

class Header extends Component {
    logout(){
        this.props.logoutDispatch()
        window.location.href = '/'
    }
    render() {
        const { user } = this.props.security;
        const userISAuthenticated = (                    
            <div className="collapse navbar-collapse" id="mobile-nav">
                <ul className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <Link className="nav-link" to="/dashboard">
                            Chem items
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link " to="/register">
                            Add User
                        </Link>
                    </li>
                </ul>

                <ul className="navbar-nav ms-auto">
                    <li className="nav-item">
                        <Link className="nav-link" to='/logout' onClick={this.logout.bind(this)}>
                            Logout
                        </Link>
                    </li>
                </ul>
            </div>
        )
        const userIsNOTAuthenticated = (
            <div className="collapse navbar-collapse" id="mobile-nav">
                <ul className="navbar-nav ms-auto">
                    <li className="nav-item">
                        <Link className="nav-link" to='/login'>
                            Login
                        </Link>
                    </li>
                </ul>
            </div>
        )

        let headerLinks = user && JSON.stringify(user) !== '{}' ? userISAuthenticated : userIsNOTAuthenticated;

        if (user)

        return (
            <nav className="navbar navbar-expand-sm navbar-dark bg-primary mb-4">
                <div className="container">
                    <Link className="navbar-brand" to="/">
                        chemstore
                    </Link>
                    {
                        headerLinks
                    }
                </div>
            </nav>
        )
    }
}

Header.propTypes = {
    logoutDispatch: PropTypes.func.isRequired,
    security: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    security: state.security
})

export default connect(mapStateToProps, {logoutDispatch}) (Header)
