import React, { Component } from 'react'
import { Link } from 'react-router-dom'
import { connect } from 'react-redux'
import PropTypes from "prop-types"

class Landing extends Component {
    componentDidMount() {
        const user = this.props.security.user
        if (user && JSON.stringify(user) !== '{}') {
            //TODO do we really want to navigate here?
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
                                    <b>Log in</b>, to manage chamicals and register eluents in your lab
                                </p>
                                <hr />
                                <Link to="/login" className="btn btn-lg btn-primary me-2">
                                    Login
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
