import React from 'react'
import { Link } from 'react-router-dom'

function RedirectFormButton(props) {
    return (
        <Link to={props.formRoute} className="btn btn-lg btn-info">
            {props.buttonLabel}
        </Link>

    )
}

export default RedirectFormButton
