import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import { connect } from 'react-redux'
import PropTypes from "prop-types"
import { fetchLabs } from '../utils/securityUtils'
import { Button } from 'react-bootstrap'
import "./Landing.css"

class Landing extends Component {
    componentDidMount() {
        const user = this.props.security.user
        if (user && JSON.stringify(user) !== '{}') {
            fetchLabs()
            this.props.history.push('/chem-items')
        }
    }
    render() {
        return (
            <div className="landing">
                <div className="light-overlay landing-inner text-dark">
                    <div className="container">
                        <div className="row">
                            <div className="col-md-12 text-center">
                                <h1 className="display-3 mb-4">chemstore</h1>
                                <p className="lead">
                                    If you are tired of administrating chemicals and eluents on paper
                                </p>
                                <br/>
                                <p className="lead">
                                    <b>Log in</b>, to manage chamicals and register eluents in your lab
                                </p>
                                <hr />
                                <Link to="/login" className="lgbtn btn btn-primary mt-2">
                                    Login
                                </Link>
                                <Link to="/user-manual">
                                    <Button variant="outline-primary" className="lgbtn btn-block mt-2">
                                        <i className="fas fa-question-circle"></i>
                                        Help
                                    </Button>
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

Landing.propTypes = {
    security: PropTypes.object.isRequired
}

const mapStateToProps = state => ({
    security: state.security
})

export default connect(mapStateToProps) (Landing)
