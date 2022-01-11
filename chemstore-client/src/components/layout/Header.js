import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import PropTypes from "prop-types"
import { connect } from 'react-redux'
import { logoutDispatch, refreshTokenAndUser } from '../../securityUtils/securityUtils'
import 'semantic-ui-css/semantic.min.css'
import Select from 'react-select'
import store from '../../store'
import { SELECT_LAB } from '../../actions/types'


class Header extends Component {
    constructor() {
        super()
        this.state = {
            selectedLab: {}
        }
        this.onChange=this.onChange.bind(this)
    }

    logout(){
        this.props.logoutDispatch()
        window.location.href = '/'
    }

    onChange(justSelected) {
        this.setState({selectedLab: justSelected})
        store.dispatch({
            type: SELECT_LAB,
            payload: justSelected
        });
    }

    componentWillReceiveProps(nextProps){
        console.log("in componentWillReceiveProps " + JSON.stringify(nextProps.selectedLab.selectedLab))
        if (nextProps.selectedLab && JSON.stringify(nextProps.selectedLab.selectedLab) !== "{}") {
            this.setState({selectedLab: nextProps.selectedLab.selectedLab});
        }
    }


    render() {
        const { user } = this.props.security;
        const userISAuthenticated = (                    
            <div className="collapse navbar-collapse" id="mobile-nav">
                <ul className="navbar-nav mr-auto">
                    <li className="nav-item">
                        <Link className="nav-link" to="/chem-items">
                            Chem items
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link " to="/users">
                            Users
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link " to="/labs">
                            Labs
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link " to="/manufacturers">
                            Manufacturers
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link" to="/categories">
                            Categories
                        </Link>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link " to="/chemicals">
                            Chemicals
                        </Link>
                    </li>
                </ul>

                <ul className="navbar-nav ms-auto">
                    <li className="nav-item" key="react-select">
                        <Select 
                            name="form-field-name"
                            value={this.selectedLab}
                            onChange={this.onChange}
                            options={this.props.labs} 
                            placeholder="Select lab"

                        />
                    </li>

                    <li className="nav-item">
                        <Link className="nav-link" to='/change-password'>
                            Change Password
                        </Link>
                    </li>
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
    security: state.security,
    selectedLab: state.selectedLab,
    labs: state.labs
})

export default connect(mapStateToProps, {logoutDispatch}) (Header)
