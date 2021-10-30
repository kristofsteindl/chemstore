import React, { Component } from 'react'
import { Link } from 'react-router-dom'

class Landing extends Component {
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

export default Landing
